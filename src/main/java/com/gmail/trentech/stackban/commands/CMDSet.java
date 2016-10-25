package com.gmail.trentech.stackban.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.stackban.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDSet implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String worldName = args.<String>getOne("world").get();

		String itemType = args.<String>getOne("item").get();

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
