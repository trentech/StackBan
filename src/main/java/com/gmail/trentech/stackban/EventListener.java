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
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.World;

import com.gmail.trentech.stackban.utils.Action;
import com.gmail.trentech.stackban.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class EventListener {

	@Listener
	public void onLoadWorldEvent(LoadWorldEvent event) {
		String worldName = event.getTargetWorld().getName();
		
		ConfigManager.init(worldName);
	}
	
	@Listener
	public void onTabCompleteEvent(TabCompleteEvent event) {
		String rawMessage = event.getRawMessage();

		String[] args = rawMessage.split(" ");

		List<String> list = event.getTabCompletions();

		if ((args[0].equalsIgnoreCase("sban") || args[0].equalsIgnoreCase("sb")) && args.length > 1) {
			if ((args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("s"))) {
				if(args.length == 2 || args.length == 3) {
					if((args.length == 2 && rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")) || (args.length == 3 && "global".contains(args[2].toLowerCase()) && !"global".equalsIgnoreCase(args[2]))) {
						list.add("global");
					}
					
					for(World world : Sponge.getServer().getWorlds()) {
						String name = world.getName();
						
						if (args.length == 3) {
							if (name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
								list.add(name);
							}
						} else if (rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")) {
							list.add(name);
						}
					}
				}
				if(args.length == 3 || args.length == 4) {
					for (ItemType itemType : Sponge.getRegistry().getAllOf(ItemType.class)) {
						String id = itemType.getId();

						if (args.length == 4) {
							if (id.contains(args[3].toLowerCase()) && !id.equalsIgnoreCase(args[3])) {
								list.add(id);
							}
						} else if (rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")) {
							list.add(id);
						}
					}
				}

			} else if ((args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r"))) {
				if(args.length == 2 || args.length == 3) {
					if((args.length == 2 && rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")) || (args.length == 3 && "global".contains(args[2].toLowerCase()) && !"global".equalsIgnoreCase(args[2]))) {
						list.add("global");
					}
					
					for(World world : Sponge.getServer().getWorlds()) {
						String name = world.getName();
						
						if (args.length == 3) {
							if (name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
								list.add(name);
							}
						} else if (rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")) {
							list.add(name);
						}
					}
				}
				if(args.length == 3 || args.length == 4) {					
					if(Sponge.getServer().getWorld(args[2]).isPresent() || args[2].equalsIgnoreCase("global")) {
						for (Entry<Object, ? extends CommentedConfigurationNode> item : ConfigManager.get(args[2]).getConfig().getNode("items").getChildrenMap().entrySet()) {
							String id = item.getValue().getKey().toString();

							if (args.length == 4) {
								if (id.contains(args[3].toLowerCase()) && !id.equalsIgnoreCase(args[3])) {
									list.add(id);
								}
							} else if (rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")) {
								list.add(id);
							}
						}
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

			if (isBanned(player.getWorld(), itemStack, Action.PLACE)) {
				log(player, itemStack, Action.PLACE);

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

			if (isBanned(player.getWorld(), itemStack, Action.MODIFY)) {		
				log(player, itemStack, Action.MODIFY);

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

			if (isBanned(player.getWorld(), itemStack, Action.BREAK)) {
				log(player, itemStack, Action.BREAK);

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

			if (isBanned(player.getWorld(), itemStack, Action.CRAFT)) {
				log(player, itemStack, Action.CRAFT);

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

			if (isBanned(player.getWorld(), itemStack, Action.DROP)) {
				log(player, itemStack, Action.DROP);

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

			if (isBanned(player.getWorld(), itemStack, Action.HOLD)) {
				log(player, itemStack, Action.HOLD);

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

			if (isBanned(player.getWorld(), itemStack, Action.PICKUP)) {
				log(player, itemStack, Action.PICKUP);

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

		if (isBanned(player.getWorld(), itemStack, Action.USE)) {
			log(player, itemStack, Action.USE);

			player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));

			event.setCancelled(true);
		}
	}

	private boolean isBanned(World world, ItemStack itemStack, Action action) {
		String itemType = itemStack.getItem().getId();

		DataContainer container = itemStack.toContainer();
		DataQuery query = DataQuery.of('/', "UnsafeDamage");
		
		ConfigurationNode config = ConfigManager.get(world.getName()).getConfig();

		if (!config.getNode("items", itemType + ":" + container.get(query).get().toString()).isVirtual()) {
			return !config.getNode("items", itemType + ":" + container.get(query).get().toString(), action.getName()).getBoolean();
		}

		if (!config.getNode("items", itemType).isVirtual()) {
			return !config.getNode("items", itemType, action.getName()).getBoolean();
		}

		config = ConfigManager.get("global").getConfig();

		if (!config.getNode("items", itemType + ":" + container.get(query).get().toString()).isVirtual()) {
			return !config.getNode("items", itemType + ":" + container.get(query).get().toString(), action.getName()).getBoolean();
		}

		if (!config.getNode("items", itemType).isVirtual()) {
			return !config.getNode("items", itemType, action.getName()).getBoolean();
		}
		
		return false;
	}

	public void log(Player user, ItemStack itemStack, Action action) {
		if (!ConfigManager.get().getConfig().getNode("console_log").getBoolean()) {
			return;
		}
		
		String itemType = itemStack.getItem().getId();

		DataContainer container = itemStack.toContainer();
		DataQuery query = DataQuery.of('/', "UnsafeDamage");

		int unsafeDamage = Integer.parseInt(container.get(query).get().toString());

		String message = action.getMessage().replaceAll("%PLAYER%", user.getName());
		
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
