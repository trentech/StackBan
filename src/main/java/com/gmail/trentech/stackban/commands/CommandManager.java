package com.gmail.trentech.stackban.commands;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandManager {

	private CommandSpec cmdSet = CommandSpec.builder()
			.permission("stackban.cmd.sban.set")
			.arguments(GenericArguments.optional(GenericArguments.string(Text.of("world"))), GenericArguments.optional(GenericArguments.string(Text.of("item"))),
					GenericArguments.flags().flag("break", "craft", "drop", "hold", "modify", "pickup", "place", "use").setAcceptsArbitraryLongFlags(true).buildWith(GenericArguments.none()))
			.executor(new CMDSet())
			.build();

	private CommandSpec cmdRemove = CommandSpec.builder()
			.permission("stackban.cmd.sban.remove")
			.arguments(GenericArguments.optional(GenericArguments.string(Text.of("world"))), GenericArguments.optional(GenericArguments.string(Text.of("item"))))
			.executor(new CMDRemove())
			.build();

	private CommandSpec cmdList = CommandSpec.builder()
			.permission("stackban.cmd.sban.list")
			.arguments(GenericArguments.optional(GenericArguments.string(Text.of("world"))))
			.executor(new CMDList())
			.build();

	private CommandSpec cmdLog = CommandSpec.builder()
			.permission("stackban.cmd.sban.log")
			.arguments(GenericArguments.optional(GenericArguments.string(Text.of("boolean"))))
			.executor(new CMDLog())
			.build();

	public CommandSpec cmdSBan = CommandSpec.builder()
			.permission("stackban.cmd.sban")
			.child(cmdSet, "set", "s")
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
