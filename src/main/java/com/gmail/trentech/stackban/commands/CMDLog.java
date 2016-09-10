package com.gmail.trentech.stackban.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.stackban.utils.ConfigManager;
import com.gmail.trentech.stackban.utils.Help;

public class CMDLog implements CommandExecutor {

	public CMDLog() {
		Help help = new Help("add", "add", " Log to console when player triggers banned item event");
		help.setSyntax(" /sban log <boolean>\n /b a <boolean>");
		help.setExample(" /sban log true\n /sban add false");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!args.hasAny("boolean")) {
			throw new CommandException(Text.of(TextColors.YELLOW, "/sban log <boolean>"));
		}
		String value = args.<String>getOne("boolean").get();

		if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
			throw new CommandException(Text.of(TextColors.YELLOW, "/sban log <boolean>"));
		}

		ConfigManager configManager = ConfigManager.get();

		configManager.getConfig().getNode("console_log").setValue(Boolean.valueOf(value));
		configManager.save();

		src.sendMessage(Text.of(TextColors.GREEN, "Set ban logging to ", value));

		return CommandResult.success();
	}

}
