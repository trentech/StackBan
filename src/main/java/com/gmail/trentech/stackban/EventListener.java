package com.gmail.trentech.stackban;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class EventListener {

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

			if (isBanned(itemStack)) {
				if (Main.isLog()) {
					Main.getLog().info(player.getName() + " attempted to place banned item: " + itemStack.getItem().getName());
				}

				player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));
				snapshot.getLocation().get().setBlock(BlockTypes.AIR.getDefaultState(), Cause.of(NamedCause.source(Main.getPlugin())));
			}
		}
	}

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Break event, @First Player player) {
		if (player.hasPermission("stackban.admin")) {
			return;
		}

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot snapshot = transaction.getFinal();

			if (snapshot.getState().getType().equals(BlockTypes.AIR)) {
				continue;
			}

			ItemStack itemStack = ItemStack.builder().fromBlockSnapshot(snapshot).build();

			if (isBanned(itemStack)) {
				if (Main.isLog()) {
					Main.getLog().info(player.getName() + " attempted to break banned item: " + itemStack.getItem().getName());
				}

				player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));
				event.setCancelled(true);
			}
		}
	}

	@Listener
	@Exclude({ ClickInventoryEvent.Drop.class })
	public void onClickInventoryEvent(ClickInventoryEvent event, @First Player player) {
		if (player.hasPermission("stackban.admin")) {
			return;
		}

		ItemStack itemStack = event.getCursorTransaction().getOriginal().createStack();

		if (isBanned(itemStack)) {
			if (!event.getTransactions().isEmpty()) {
				if (Main.isLog()) {
					Main.getLog().info(player.getName() + " attempted to interact with banned item: " + itemStack.getItem().getName());
				}

				player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));
				event.getTransactions().get(0).getSlot().clear();
			}
		}
	}

	@Listener
	public void onDropItemEvent(DropItemEvent.Dispense event, @First Player player) {
		for (Entity entity : event.getEntities()) {
			ItemStack itemStack = ((Item) entity).getItemData().item().get().createStack();

			if (isBanned(itemStack)) {
				if (Main.isLog()) {
					Main.getLog().info(player.getName() + " attempted to drop banned item: " + itemStack.getItem().getName());
				}

				player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));
				entity.remove();
			}
		}
	}

	@Listener
	public void onChangeInventoryEvent(ChangeInventoryEvent.Held event, @First Player player) {
		if (player.hasPermission("stackban.admin")) {
			return;
		}

		for (SlotTransaction transaction : event.getTransactions()) {
			ItemStackSnapshot snapshot = transaction.getOriginal();
			ItemStack itemStack = snapshot.createStack();

			if (isBanned(itemStack)) {
				if (Main.isLog()) {
					Main.getLog().info(player.getName() + " was holding a banned item: " + itemStack.getItem().getName());
				}

				player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));
				transaction.getSlot().clear();
			}
		}
	}

	@Listener
	public void onUseItemStackEvent(UseItemStackEvent.Start event, @First Player player) {
		if (player.hasPermission("stackban.admin")) {
			return;
		}

		ItemStackSnapshot snapshot = event.getItemStackInUse();
		ItemStack itemStack = snapshot.createStack();

		if (isBanned(itemStack)) {
			if (Main.isLog()) {
				Main.getLog().info(player.getName() + " attempted to use banned item: " + itemStack.getItem().getName());
			}

			player.sendMessage(Text.of(TextColors.GOLD, "This item is banned"));
			event.setCancelled(true);
		}
	}

	private boolean isBanned(ItemStack itemStack) {
		String itemType = itemStack.getItem().getName();

		if (Main.getItems().contains(itemType)) {
			return true;
		}

		DataContainer cont = itemStack.toContainer();
		DataQuery query = DataQuery.of('/', "UnsafeDamage");

		if (Main.getItems().contains(itemType + ":" + cont.get(query).get().toString())) {
			return true;
		}

		return false;
	}
}
