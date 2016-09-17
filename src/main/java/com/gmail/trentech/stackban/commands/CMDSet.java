package com.gmail.trentech.stackban.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.stackban.utils.ConfigManager;
import com.gmail.trentech.stackban.utils.Help;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDSet implements CommandExecutor {

	public CMDSet() {
		Help help = new Help("set", "set", " Set item in ban list. All actions are banned by default. To unban action add corresponding flag. set <world> to 'global' to ban in all worlds");
		help.setSyntax(" /sban set <world> <modid:itemType[:id]> [--break] [--craft] [--drop] [--modify] [--pickup] [--place] [--use]\n /b s <world> <modid:itemType[:id]> [--break] [--craft] [--drop] [--modify] [--pickup] [--place] [--use]");
		help.setExample(" /sban set world minecraft:stone\n /sban set minecraft:wool:5");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String worldName = args.<String>getOne("world").get();

		if(!Sponge.getServer().getWorld(worldName).isPresent() && !worldName.equalsIgnoreCase("global")) {
			throw new CommandException(Text.of(TextColors.RED, worldName, " does not exist"), false);
		}

		String itemType = args.<String>getOne("item").get();

		String[] check = itemType.split(":");

		if (check.length < 2) {
			throw new CommandException(Text.of(TextColors.RED, "Not a valid ItemType"), true);
		}

		if (!Sponge.getRegistry().getType(ItemType.class, check[0] + ":" + check[1]).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, "Not a valid ItemType"), true);
		}

		ConfigManager configManager = ConfigManager.get(worldName);
		ConfigurationNode config = configManager.getConfig();

		config.getNode("items", itemType, "place").setValue(args.hasAny("place"));
		config.getNode("items", itemType, "break").setValue(args.hasAny("break"));
		config.getNode("items", itemType, "use").setValue(args.hasAny("use"));
		config.getNode("items", itemType, "craft").setValue(args.hasAny("craft"));
		config.getNode("items", itemType, "modify").setValue(args.hasAny("modify"));
		config.getNode("items", itemType, "pickup").setValue(args.hasAny("pickup"));
		config.getNode("items", itemType, "drop").setValue(args.hasAny("drop"));
		config.getNode("items", itemType, "hold").setValue(args.hasAny("hold"));

		configManager.save();

		src.sendMessage(Text.of(TextColors.GREEN, itemType));

		src.sendMessage(Text.of(TextColors.YELLOW, "  - break: ", TextColors.WHITE, args.hasAny("break")));
		src.sendMessage(Text.of(TextColors.YELLOW, "  - craft: ", TextColors.WHITE, args.hasAny("craft")));
		src.sendMessage(Text.of(TextColors.YELLOW, "  - drop: ", TextColors.WHITE, args.hasAny("drop")));
		src.sendMessage(Text.of(TextColors.YELLOW, "  - hold: ", TextColors.WHITE, args.hasAny("hold")));
		src.sendMessage(Text.of(TextColors.YELLOW, "  - modify: ", TextColors.WHITE, args.hasAny("modify")));
		src.sendMessage(Text.of(TextColors.YELLOW, "  - pickup: ", TextColors.WHITE, args.hasAny("pickup")));
		src.sendMessage(Text.of(TextColors.YELLOW, "  - place: ", TextColors.WHITE, args.hasAny("place")));
		src.sendMessage(Text.of(TextColors.YELLOW, "  - use: ", TextColors.WHITE, args.hasAny("use")));

		return CommandResult.success();
	}

}
