package com.gmail.trentech.stackban.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.stackban.utils.ConfigManager;
import com.gmail.trentech.stackban.utils.Help;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDRemove implements CommandExecutor {

	public CMDRemove() {
		new Help("remove", "remove", "Remove item from ban list. set <world> to 'global' to remove from global config", false)
			.setUsage("/sban remove <modid:itemType[:id]>\n /b r <modid:itemType[:id]>")
			.setExample("/sban remove minecraft:stone")
			.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String worldName = args.<String>getOne("world").get();

		if(!Sponge.getServer().getWorld(worldName).isPresent() && !worldName.equalsIgnoreCase("global")) {
			throw new CommandException(Text.of(TextColors.RED, worldName, " does not exist"), false);
		}

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
