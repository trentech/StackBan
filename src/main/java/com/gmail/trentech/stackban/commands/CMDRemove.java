package com.gmail.trentech.stackban.commands;

import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.stackban.Main;
import com.gmail.trentech.stackban.utils.ConfigManager;
import com.gmail.trentech.stackban.utils.Help;

public class CMDRemove implements CommandExecutor {

	public CMDRemove() {
		Help help = new Help("remove", "remove", " Remove message from broadcast list. use /sban list to view list index");
		help.setSyntax(" /sban remove <index>\n /b r <index>");
		help.setExample(" /sban remove 4");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!args.hasAny("index")) {
			src.sendMessage(Text.of(TextColors.YELLOW, "/sban remove <index>"));
			return CommandResult.empty();
		}
		int index;

		try {
			index = Integer.parseInt(args.<String> getOne("index").get());
		} catch (Exception e) {
			src.sendMessage(Text.of(TextColors.YELLOW, "/sban remove <index>"));
			return CommandResult.empty();
		}

		List<String> items = Main.getItems();

		String itemType = items.get(index);

		items.remove(itemType);

		ConfigManager configManager = new ConfigManager();

		configManager.getConfig().getNode("items").setValue(items);

		configManager.save();

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Removed ", TextColors.YELLOW, itemType, TextColors.DARK_GREEN, " from ban list"));

		return CommandResult.success();
	}

}
