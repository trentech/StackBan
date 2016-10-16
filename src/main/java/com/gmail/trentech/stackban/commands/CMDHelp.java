package com.gmail.trentech.stackban.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import com.gmail.trentech.stackban.utils.Help;

public class CMDHelp implements CommandExecutor {

	public CMDHelp() {
		new Help("sban help", "help", "Get help with all commands in StackBan", false)
			.setPermission("stackban.cmd.sban")
			.setUsage("/sban help <rawCommand>")
			.setExample("/sban help sban create")
			.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help help = args.<Help> getOne("rawCommand").get();
		help.execute(src);
		
		return CommandResult.success();
	}
}
