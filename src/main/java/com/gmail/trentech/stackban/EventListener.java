package com.gmail.trentech.stackban;

import java.util.List;
import java.util.Map.Entry;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.command.TabCompleteEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import com.gmail.trentech.stackban.utils.Action;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class EventListener {

	@Listener
	public void onTabCompleteEvent(TabCompleteEvent event) {
		String rawMessage = event.getRawMessage();

		String[] args = rawMessage.split(" ");

		List<String> list = event.getTabCompletions();

		if ((args[0].equalsIgnoreCase("sban") || args[0].equalsIgnoreCase("sb")) && args.length > 1) {
			if ((args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("s")) && (args.length == 2 || args.length == 3)) {
				for (ItemType itemType : Sponge.getRegistry().getAllOf(ItemType.class)) {
					String id = itemType.getId();

					if (args.length == 3) {
						if (id.contains(args[2].toLowerCase()) && !id.equalsIgnoreCase(args[2])) {
							list.add(id);
						}
					} else if (rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")) {
						list.add(id);
					}
				}
			} else if ((args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r")) && (args.length == 2 || args.length == 3)) {
				for (Entry<Object, ? extends CommentedConfigurationNode> item : Main.getConfigManager().getConfig().getNode("items").getChildrenMap().entrySet()) {
					String id = item.getValue().getKey().toString();

					if (args.length == 3) {
						if (id.contains(args[2].toLowerCase()) && !id.equalsIgnoreCase(args[2])) {
							list.add(id);
						}
					} else if (rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")) {
						list.add(id);
					}
				}
			}
		}
	}

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Place event, @First Player player) {
		if (player.hasPermission("stackban.admin")) {
			return;
		}

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot snapshot = transaction.getFinal();

			if (snapshot.getState().getType().equals(BlockTypes.AIR)) {
				continue;
			}

			ItemStack itemStack = ItemStack.builder().fromBlockSnapshot(snapshot).build();

			if (isBanned(itemStack, Action.PLACE)) {
				if (Main.getConfigManager().getConfig().getNode("console_log").getBoolean()) {
					log(itemStack, player.getName() + " attempted to place banned item: %ITEM%");
				}

				player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));

				event.setCancelled(true);
			}
		}
	}

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Modify event, @First Player player) {
		if (player.hasPermission("stackban.admin")) {
			return;
		}

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot snapshot = transaction.getFinal();

			if (snapshot.getState().getType().equals(BlockTypes.AIR)) {
				continue;
			}

			ItemStack itemStack = ItemStack.builder().fromBlockSnapshot(snapshot).build();

			if (isBanned(itemStack, Action.MODIFY)) {
				if (Main.getConfigManager().getConfig().getNode("console_log").getBoolean()) {
					log(itemStack, player.getName() + " attempted to modify banned item: %ITEM%");
				}

				player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));

				event.setCancelled(true);
			}
		}
	}

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Break event, @First Player player) {
		if (player.hasPermission("stackban.admin")) {
			return;
		}

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot snapshot = transaction.getOriginal();

			if (snapshot.getState().getType().equals(BlockTypes.AIR)) {
				continue;
			}

			ItemStack itemStack = ItemStack.builder().fromBlockSnapshot(snapshot).build();

			if (isBanned(itemStack, Action.BREAK)) {
				if (Main.getConfigManager().getConfig().getNode("console_log").getBoolean()) {
					log(itemStack, player.getName() + " attempted to break banned item: %ITEM%");
				}

				player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));

				event.setCancelled(true);
			}
		}
	}

	// NOT WORKING
	@Listener
	@Exclude({ ClickInventoryEvent.Drop.class })
	public void onClickInventoryEvent(ClickInventoryEvent event, @First Player player) {
		if (player.hasPermission("stackban.admin")) {
			return;
		}

		for (SlotTransaction transaction : event.getTransactions()) {
			ItemStack itemStack = transaction.getFinal().createStack();

			if (itemStack.getItem().equals(ItemTypes.NONE)) {
				continue;
			}

			if (isBanned(itemStack, Action.CRAFT)) {
				if (Main.getConfigManager().getConfig().getNode("console_log").getBoolean()) {
					log(itemStack, player.getName() + " attempted to craft/click banned item: %ITEM%");
				}

				player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));

				Sponge.getScheduler().createTaskBuilder().delayTicks(2).execute(c -> transaction.getSlot().clear()).submit(Main.getPlugin());
			}
		}
	}

	// NOT WORKING
	@Listener
	public void onDropItemEvent(DropItemEvent.Pre event, @First Player player) {
		System.out.println("DROP");

		for (ItemStackSnapshot snapshot : event.getDroppedItems()) {
			ItemStack itemStack = snapshot.createStack();

			if (itemStack.getItem().equals(ItemTypes.NONE)) {
				continue;
			}

			if (isBanned(itemStack, Action.DROP)) {
				if (Main.getConfigManager().getConfig().getNode("console_log").getBoolean()) {
					log(itemStack, player.getName() + " attempted to drop banned item: %ITEM%");
				}

				player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));

				event.setCancelled(true);
			}
		}
	}

	@Listener
	public void onChangeInventoryEvent(ChangeInventoryEvent.Held event, @First Player player) {
		if (player.hasPermission("stackban.admin")) {
			return;
		}

		for (SlotTransaction transaction : event.getTransactions()) {
			ItemStack itemStack = transaction.getFinal().createStack();

			if (itemStack.getItem().equals(ItemTypes.NONE)) {
				continue;
			}

			if (isBanned(itemStack, Action.HOLD)) {
				if (Main.getConfigManager().getConfig().getNode("console_log").getBoolean()) {
					log(itemStack, player.getName() + " attempted to hold banned item: %ITEM%");
				}

				player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));

				Sponge.getScheduler().createTaskBuilder().delayTicks(2).execute(c -> transaction.getSlot().clear()).submit(Main.getPlugin());
			}
		}
	}

	@Listener(order = Order.POST)
	public void onChangeInventoryEvent(ChangeInventoryEvent.Pickup event, @First Player player) {
		if (player.hasPermission("stackban.admin")) {
			return;
		}

		for (SlotTransaction transaction : event.getTransactions()) {
			ItemStack itemStack = transaction.getFinal().createStack();

			if (itemStack.getItem().equals(ItemTypes.NONE)) {
				continue;
			}

			if (isBanned(itemStack, Action.PICKUP)) {
				if (Main.getConfigManager().getConfig().getNode("console_log").getBoolean()) {
					log(itemStack, player.getName() + " attempted to pickup banned item: %ITEM%");
				}

				player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));

				Sponge.getScheduler().createTaskBuilder().delayTicks(2).execute(c -> transaction.getSlot().clear()).submit(Main.getPlugin());
			}
		}
	}

	@Listener
	public void onUseItemStackEvent(UseItemStackEvent.Start event, @First Player player) {
		if (player.hasPermission("stackban.admin")) {
			return;
		}

		ItemStack itemStack = event.getItemStackInUse().createStack();

		if (itemStack.getItem().equals(ItemTypes.NONE)) {
			return;
		}

		if (isBanned(itemStack, Action.USE)) {
			if (Main.getConfigManager().getConfig().getNode("console_log").getBoolean()) {
				log(itemStack, player.getName() + " attempted to use banned item: %ITEM%");
			}

			player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));

			event.setCancelled(true);
		}
	}

	private boolean isBanned(ItemStack itemStack, Action action) {
		String itemType = itemStack.getItem().getId();

		ConfigurationNode config = Main.getConfigManager().getConfig();

		DataContainer container = itemStack.toContainer();
		DataQuery query = DataQuery.of('/', "UnsafeDamage");

		if (!config.getNode("items", itemType + ":" + container.get(query).get().toString()).isVirtual()) {
			return !config.getNode("items", itemType + ":" + container.get(query).get().toString(), action.getName()).getBoolean();
		}

		if (!config.getNode("items", itemType).isVirtual()) {
			return !config.getNode("items", itemType, action.getName()).getBoolean();
		}

		return false;
	}

	public void log(ItemStack itemStack, String message) {
		String itemType = itemStack.getItem().getId();

		DataContainer container = itemStack.toContainer();
		DataQuery query = DataQuery.of('/', "UnsafeDamage");

		int unsafeDamage = Integer.parseInt(container.get(query).get().toString());

		if (unsafeDamage != 0) {
			message = message.replaceAll("%ITEM%", itemType + ":" + container.get(query).get().toString());
		} else {
			message = message.replaceAll("%ITEM%", itemType);
		}

		Main.getLog().info(message);

		for (Player player : Sponge.getServer().getOnlinePlayers()) {
			if (player.hasPermission("stackban.log")) {
				player.sendMessage(Text.of(TextStyles.ITALIC, TextColors.GRAY, message));
			}
		}
	}
}
