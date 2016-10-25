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

public class CMDRemove implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String worldName = args.<String>getOne("world").get();

		String itemType = args.<String>getOne("item").get();

		ConfigManager configManager = ConfigManager.get(worldName);
		ConfigurationNode config = configManager.getConfig();

		if (config.getNode("items", itemType).isVirtual()) {
			throw new CommandException(Text.of(TextColors.RED, itemType, " does not exist"), false);
		}

		config.getNode("items").removeChild(itemType);

		configManager.save();

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Removed ", TextColors.YELLOW, itemType, TextColors.DARK_GREEN, " from ban list"));

		return CommandResult.success();
	}

}
