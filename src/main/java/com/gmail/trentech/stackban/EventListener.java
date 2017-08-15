package com.gmail.trentech.stackban;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.stackban.init.Action;
import com.gmail.trentech.stackban.init.Common;

import ninja.leaping.configurate.ConfigurationNode;

public class EventListener {

	private static ConcurrentHashMap<UUID, Action> notifications = new ConcurrentHashMap<>();
	
	@Listener
	public void onLoadWorldEvent(LoadWorldEvent event) {
		String worldName = event.getTargetWorld().getName();
		
		Common.initConfig(worldName);
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

					transaction.setValid(false);
					event.setCancelled(true);
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
			
			if (itemStack.getType().equals(ItemTypes.NONE)) {
				continue;
			}

			if (isBanned(player, itemStack, Action.HOLD)) {
				PlayerInventory inv = player.getInventory().query(PlayerInventory.class);

				for (Inventory item : inv.getHotbar().slots()) {
					Slot slot = (Slot) item;

					Optional<ItemStack> peek = slot.peek();

					if(!peek.isPresent()) {
						continue;
					}
					
					if (isBanned(player, peek.get(), Action.HOLD)) {
						slot.clear();
					}
				}
				
				for (Inventory item : inv.getMain().slots()) {
					Slot slot = (Slot) item;

					Optional<ItemStack> peek = slot.peek();

					if(!peek.isPresent()) {
						continue;
					}
					
					if (isBanned(player, peek.get(), Action.HOLD)) {
						slot.clear();
					}
				}
				
				log(player, itemStack, Action.HOLD);
				
				return;
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

			if (itemStack.getType().equals(ItemTypes.NONE)) {
				continue;
			}

			if (isBanned(player, itemStack, Action.PICKUP)) {
				log(player, itemStack, Action.PICKUP);
				
				event.setCancelled(true);
				
				return;
			}
		}
	}

	@Listener
	public void onDropItemEvent(DropItemEvent.Pre event, @Root Player player) {
		for (ItemStackSnapshot snapshot : event.getDroppedItems()) {
			ItemStack itemStack = snapshot.createStack();

			if (itemStack.getType().equals(ItemTypes.NONE)) {
				continue;
			}

			if (isBanned(player, itemStack, Action.DROP)) {
				log(player, itemStack, Action.DROP);
				
				event.setCancelled(true);
			}
		}
	}

	@Listener
	public void onUseItemStackEvent(UseItemStackEvent.Start event, @Root Player player) {
		if (player.hasPermission("stackban.admin")) {
			return;
		}

		ItemStack itemStack = event.getItemStackInUse().createStack();

		if (itemStack.getType().equals(ItemTypes.NONE)) {
			return;
		}

		if (isBanned(player, itemStack, Action.USE)) {
			log(player, itemStack, Action.USE);

			event.setCancelled(true);
		}
	}

	private boolean isBanned(Player player, ItemStack itemStack, Action action) {
		World world = player.getWorld();
		String itemType = itemStack.getType().getId();

		DataContainer container = itemStack.toContainer();
		DataQuery query = DataQuery.of('/', "UnsafeDamage");

		if (player.hasPermission("stackban.bypass." + itemType + ":" + container.get(query).get().toString()) || player.hasPermission("stackban.bypass." + itemType)) {
			return false;
		}
		
		ConfigurationNode config = ConfigManager.get(Main.getPlugin(), world.getName()).getConfig();

		if (!config.getNode("items", itemType + ":" + container.get(query).get().toString()).isVirtual()) {
			return !config.getNode("items", itemType + ":" + container.get(query).get().toString(), action.getName()).getBoolean();
		}

		if (!config.getNode("items", itemType).isVirtual()) {
			return !config.getNode("items", itemType, action.getName()).getBoolean();
		}

		config = ConfigManager.get(Main.getPlugin(), "global").getConfig();

		if (!config.getNode("items", itemType + ":" + container.get(query).get().toString()).isVirtual()) {
			return !config.getNode("items", itemType + ":" + container.get(query).get().toString(), action.getName()).getBoolean();
		}

		if (!config.getNode("items", itemType).isVirtual()) {
			return !config.getNode("items", itemType, action.getName()).getBoolean();
		}

		return false;
	}
	
	public static void log(Player player, ItemStack itemStack, Action action) {
		UUID uuid = player.getUniqueId();

		if(!notifications.containsKey(uuid)) {
			notifications.put(uuid, action);

			ConfigurationNode config = ConfigManager.get(Main.getPlugin()).getConfig();
			
			player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(config.getNode("player_message").getString().replaceAll("%ITEM%", itemStack.getTranslation().get()).replaceAll("%ACTION%", action.getName())));

			if (config.getNode("console_log").getBoolean()) {
				String itemType = itemStack.getType().getId();

				DataContainer container = itemStack.toContainer();
				DataQuery query = DataQuery.of('/', "UnsafeDamage");

				int unsafeDamage = Integer.parseInt(container.get(query).get().toString());

				String message = config.getNode("log_message").getString().replaceAll("%PLAYER%", player.getName()).replaceAll("%ACTION%", action.getName());
				
				if (unsafeDamage != 0) {
					message = message.replaceAll("%ITEM%", itemType + ":" + container.get(query).get().toString());
				} else {
					message = message.replaceAll("%ITEM%", itemType);
				}

				Main.instance().getLog().info(message);

				for (Player p : Sponge.getServer().getOnlinePlayers()) {
					if (p.hasPermission("stackban.log")) {
						p.sendMessage(Text.of(TextStyles.ITALIC, TextColors.GRAY, message));
					}
				}
			}

			Task.builder().delayTicks(40).execute(c -> { notifications.remove(uuid);}).submit(Main.getPlugin());
		}
	}
}
