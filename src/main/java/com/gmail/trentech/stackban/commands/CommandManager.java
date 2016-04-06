package com.gmail.trentech.stackban.commands;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandManager {

	private CommandSpec cmdAdd = CommandSpec.builder()
		    .permission("stackban.cmd.sban.add")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("item"))))
		    .executor(new CMDAdd())
		    .build();
	
	private CommandSpec cmdRemove = CommandSpec.builder()
		    .permission("stackban.cmd.sban.remove")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("index"))))
		    .executor(new CMDRemove())
		    .build();
	
	private CommandSpec cmdList = CommandSpec.builder()
		    .permission("stackban.cmd.sban.list")
		    .executor(new CMDList())
		    .build();
	
	private CommandSpec cmdLog = CommandSpec.builder()
		    .permission("stackban.cmd.sban.log")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("boolean"))))
		    .executor(new CMDLog())
		    .build();
	
	public CommandSpec cmdSBan = CommandSpec.builder()
		    .permission("stackban.cmd.sban")
		    .child(cmdAdd, "add", "a")
		    .child(cmdRemove, "remove", "r")
		    .child(cmdList, "list", "ls")
		    .child(cmdLog, "log", "l")
		    .executor(new CMDSBan())
		    .build();
	
	public CommandSpec cmdWhatsThis = CommandSpec.builder()
		    .permission("stackban.cmd.whatsthis")
		    .executor(new CMDWhatsThis())
		    .build();
}
