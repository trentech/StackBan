package com.gmail.trentech.stackban.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.stackban.Main;

public class CMDLog implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!args.hasAny("true|false")) {
			Help help = Help.get("sban log").get();
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		boolean value = args.<Boolean>getOne("true|false").get();

		ConfigManager configManager = ConfigManager.get(Main.getPlugin());

		configManager.getConfig().getNode("console_log").setValue(value);
		configManager.save();

		src.sendMessage(Text.of(TextColors.GREEN, "Set ban logging to ", value));

		return CommandResult.success();
	}

}
