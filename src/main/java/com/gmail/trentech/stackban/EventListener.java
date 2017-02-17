package com.gmail.trentech.stackban;

import java.util.Optional;

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
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.AffectSlotEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.World;

import com.gmail.trentech.stackban.utils.Action;
import com.gmail.trentech.stackban.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class EventListener {

	@Listener
	public void onLoadWorldEvent(LoadWorldEvent event) {
		String worldName = event.getTargetWorld().getName();
		
		ConfigManager.init(worldName);
	}

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Place event, @Root Player player) {
		if (player.hasPermission("stackban.admin")) {
			return;
		}

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot snapshot = transaction.getFinal();

			if (snapshot.getState().getType().equals(BlockTypes.AIR)) {
				continue;
			}

			ItemStack itemStack;
			try {
				itemStack = ItemStack.builder().fromBlockSnapshot(snapshot).build();
			} catch (Exception e) {
				return;
			}			

			if (isBanned(player, itemStack, Action.PLACE)) {
				log(player, itemStack, Action.PLACE);

				player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));

				event.setCancelled(true);
			}
		}
	}

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Modify event, @Root Player player) {
		if (player.hasPermission("stackban.admin")) {
			return;
		}

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot snapshot = transaction.getFinal();

			if (snapshot.getState().getType().equals(BlockTypes.AIR)) {
				continue;
			}

			ItemStack itemStack;
			try {
				itemStack = ItemStack.builder().fromBlockSnapshot(snapshot).build();
			} catch (Exception e) {
				return;
			}

			if (isBanned(player, itemStack, Action.MODIFY)) {
				log(player, itemStack, Action.MODIFY);

				player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));

				event.setCancelled(true);
			}
		}
	}

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Break event, @Root Player player) {
		if (player.hasPermission("stackban.admin")) {
			return;
		}

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot snapshot = transaction.getOriginal();

			if (snapshot.getState().getType().equals(BlockTypes.AIR)) {
				continue;
			}

			ItemStack itemStack;
			try {
				itemStack = ItemStack.builder().fromBlockSnapshot(snapshot).build();
			} catch (Exception e) {
				return;
			}

			if (isBanned(player, itemStack, Action.BREAK)) {
				log(player, itemStack, Action.BREAK);

				player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));

				event.setCancelled(true);
			}
		}
	}

	@Listener(order = Order.POST)
	public void onAffectSlotEvent(AffectSlotEvent event, @Root Player player) {
		if (player.hasPermission("stackban.admin")) {
			return;
		}
		
		for(SlotTransaction transaction : event.getTransactions()) {
			Slot slot = transaction.getSlot();
			Optional<ItemStack> optionalItem = slot.peek();
			
			if(optionalItem.isPresent()) {
				if (isBanned(player, optionalItem.get(), Action.CRAFT)) {
					log(player, optionalItem.get(), Action.CRAFT);

					player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));

					transaction.setValid(false);
				}
			}
		}
	}
	
	@Listener
	public void onChangeInventoryEvent(ChangeInventoryEvent.Held event, @Root Player player) {
		if (player.hasPermission("stackban.admin")) {
			return;
		}

		for (SlotTransaction transaction : event.getTransactions()) {
			ItemStack itemStack = transaction.getFinal().createStack();

			if (itemStack.getItem().equals(ItemTypes.NONE)) {
				continue;
			}

			if (isBanned(player, itemStack, Action.HOLD)) {
				log(player, itemStack, Action.HOLD);

				player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));

				Sponge.getScheduler().createTaskBuilder().delayTicks(2).execute(c -> transaction.getSlot().clear()).submit(Main.getPlugin());
			}
		}
	}

	@Listener(order = Order.POST)
	public void onChangeInventoryEvent(ChangeInventoryEvent.Pickup event, @Root Player player) {
		if (player.hasPermission("stackban.admin")) {
			return;
		}

		for (SlotTransaction transaction : event.getTransactions()) {
			ItemStack itemStack = transaction.getFinal().createStack();

			if (itemStack.getItem().equals(ItemTypes.NONE)) {
				continue;
			}

			if (isBanned(player, itemStack, Action.PICKUP)) {
				log(player, itemStack, Action.PICKUP);

				player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));

				Sponge.getScheduler().createTaskBuilder().delayTicks(2).execute(c -> transaction.getSlot().clear()).submit(Main.getPlugin());
			}
		}
	}

	@Listener
	public void onDropItemEvent(DropItemEvent.Pre event, @Root Player player) {
		for (ItemStackSnapshot snapshot : event.getDroppedItems()) {
			ItemStack itemStack = snapshot.createStack();

			if (itemStack.getItem().equals(ItemTypes.NONE)) {
				continue;
			}

			if (isBanned(player, itemStack, Action.DROP)) {
				log(player, itemStack, Action.DROP);

				player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));

				event.setCancelled(true);
			}
		}
	}
	
	// NOT WORKING
	@Listener
	public void onUseItemStackEvent(UseItemStackEvent.Start event, @Root Player player) {
		System.out.println("USE");
		
		if (player.hasPermission("stackban.admin")) {
			return;
		}

		ItemStack itemStack = event.getItemStackInUse().createStack();

		if (itemStack.getItem().equals(ItemTypes.NONE)) {
			return;
		}

		if (isBanned(player, itemStack, Action.USE)) {
			log(player, itemStack, Action.USE);

			player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));

			event.setCancelled(true);
		}
	}

	private boolean isBanned(Player player, ItemStack itemStack, Action action) {
		World world = player.getWorld();
		String itemType = itemStack.getItem().getId();

		DataContainer container = itemStack.toContainer();
		DataQuery query = DataQuery.of('/', "UnsafeDamage");

		if (player.hasPermission("stackban.bypass." + itemType + ":" + container.get(query).get().toString()) || player.hasPermission("itemStack.bypass." + itemType)) {
			return false;
		}
		
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

		Main.instance().getLog().info(message);

		for (Player player : Sponge.getServer().getOnlinePlayers()) {
			if (player.hasPermission("stackban.log")) {
				player.sendMessage(Text.of(TextStyles.ITALIC, TextColors.GRAY, message));
			}
		}
	}
}
