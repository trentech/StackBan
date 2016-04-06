package com.gmail.trentech.stackban.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList.Builder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.stackban.Main;
import com.gmail.trentech.stackban.utils.Help;

public class CMDList implements CommandExecutor {

	public CMDList(){
		Help help = new Help("list", "list", " List all banned items");
		help.setSyntax(" /sban list\n /sb l");
		help.setExample(" /sban list");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Builder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.AQUA, "items")).build());
		
		List<Text> list = new ArrayList<>();
		
		int i = 0;
		for(String item : Main.getItems()){
			list.add(Text.of(TextColors.GREEN,"[",i,"] ", TextColors.RESET, item));
			i++;
		}
		
		pages.contents(list);
		
		pages.sendTo(src);
		
		return CommandResult.success();
	}

}
