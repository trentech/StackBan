package com.gmail.trentech.stackban.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.helpme.help.Help;

public class CMDSBan implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (Sponge.getPluginManager().isLoaded("helpme")) {
			Help.executeList(src, Help.get("sban").get().getChildren());
			
			return CommandResult.success();
		}

		List<Text> list = new ArrayList<>();

		if (src.hasPermission("stackban.cmd.sban.list")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/stackban:sban list")).append(Text.of(" /sban list")).build());
		}
		if (src.hasPermission("stackban.cmd.sban.log")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/stackban:sban log")).append(Text.of(" /sban log")).build());
		}
		if (src.hasPermission("stackban.cmd.sban.remove")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/stackban:sban remove")).append(Text.of(" /sban remove")).build());
		}
		if (src.hasPermission("stackban.cmd.sban.set")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/stackban:sban set")).append(Text.of(" /sban set")).build());
		}

		if (src instanceof Player) {
			PaginationList.Builder pages = Sponge.getServiceManager().provide(PaginationService.class).get().builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Command List")).build());

			pages.contents(list);

			pages.sendTo(src);
		} else {
			for (Text text : list) {
				src.sendMessage(text);
			}
		}
		return CommandResult.success();
	}

}
