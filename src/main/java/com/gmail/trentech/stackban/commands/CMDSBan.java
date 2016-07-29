package com.gmail.trentech.stackban.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList.Builder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.stackban.utils.Help;

public class CMDSBan implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Builder pages = Sponge.getServiceManager().provide(PaginationService.class).get().builder();

		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Command List")).build());

		List<Text> list = new ArrayList<>();

		if (src.hasPermission("stackban.cmd.sban.add")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(Help.getHelp("add"))).append(Text.of(" /sban add")).build());
		}
		if (src.hasPermission("stackban.cmd.sban.remove")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(Help.getHelp("remove"))).append(Text.of(" /sban remove")).build());
		}
		if (src.hasPermission("stackban.cmd.sban.list")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(Help.getHelp("list"))).append(Text.of(" /sban list")).build());
		}
		if (src.hasPermission("stackban.cmd.sban.log")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(Help.getHelp("log"))).append(Text.of(" /sban log")).build());
		}
		if (src.hasPermission("stackban.cmd.whatsthis")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(Help.getHelp("whatsthis"))).append(Text.of(" /whatsthis")).build());
		}
		pages.contents(list);

		pages.sendTo(src);

		return CommandResult.success();
	}

}
