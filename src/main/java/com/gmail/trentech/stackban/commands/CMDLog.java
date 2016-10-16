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
		new Help("add", "add", "Log to console when player triggers banned item event", false)
			.setUsage("/sban log <boolean>\n /b a <boolean>")
			.setExample("/sban log true\n /sban add false")
			.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		boolean value = args.<Boolean>getOne("boolean").get();

		ConfigManager configManager = ConfigManager.get();

		configManager.getConfig().getNode("console_log").setValue(value);
		configManager.save();

		src.sendMessage(Text.of(TextColors.GREEN, "Set ban logging to ", value));

		return CommandResult.success();
	}

}
