package com.gmail.trentech.stackban.commands;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.gmail.trentech.stackban.utils.Help;

public class CommandManager {

	private CommandSpec cmdSet = CommandSpec.builder()
			.permission("stackban.cmd.sban.set")
			.arguments(GenericArguments.string(Text.of("world")), GenericArguments.string(Text.of("item")),
					GenericArguments.flags().flag("break", "craft", "drop", "hold", "modify", "pickup", "place", "use").setAcceptsArbitraryLongFlags(true).buildWith(GenericArguments.none()))
			.executor(new CMDSet())
			.build();

	private CommandSpec cmdRemove = CommandSpec.builder()
			.permission("stackban.cmd.sban.remove")
			.arguments(GenericArguments.string(Text.of("world")), GenericArguments.string(Text.of("item")))
			.executor(new CMDRemove())
			.build();

	private CommandSpec cmdList = CommandSpec.builder()
			.permission("stackban.cmd.sban.list")
			.arguments(GenericArguments.string(Text.of("world")))
			.executor(new CMDList())
			.build();

	private CommandSpec cmdLog = CommandSpec.builder()
			.permission("stackban.cmd.sban.log")
			.arguments(GenericArguments.bool(Text.of("boolean")))
			.executor(new CMDLog())
			.build();

	public CommandSpec cmdWhatsThis = CommandSpec.builder()
			.permission("stackban.cmd.sban.whatsthis")
			.executor(new CMDWhatsThis())
			.build();
	
	private CommandSpec cmdHelp = CommandSpec.builder()
		    .description(Text.of(" I need help with StackBan"))
		    .permission("stackban.cmd.sban")
		    .arguments(GenericArguments.choices(Text.of("command"), Help.all()))
		    .executor(new CMDHelp())
		    .build();
	
	public CommandSpec cmdSBan = CommandSpec.builder()
			.permission("stackban.cmd.sban")
			.child(cmdSet, "set", "s")
			.child(cmdRemove, "remove", "r")
			.child(cmdList, "list", "ls")
			.child(cmdLog, "log", "l")
			.child(cmdWhatsThis, "whatsthis", "wt")
			.child(cmdHelp, "help", "h")
			.executor(new CMDSBan())
			.build();


}
