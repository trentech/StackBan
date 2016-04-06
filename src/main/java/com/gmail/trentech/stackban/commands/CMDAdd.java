package com.gmail.trentech.stackban.commands;

import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.stackban.Main;
import com.gmail.trentech.stackban.utils.ConfigManager;
import com.gmail.trentech.stackban.utils.Help;

public class CMDAdd implements CommandExecutor {

	public CMDAdd(){
		Help help = new Help("add", "add", " Add item to ban list in the format of MODID:ITEMTYPE[:VARIENTID]");
		help.setSyntax(" /sban add <item>\n /b a <item>");
		help.setExample(" /sban add minecraft:stone\n /sban add minecraft:wool:5");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("item")) {
			src.sendMessage(Text.of(TextColors.YELLOW, "/sban add <item>"));
			return CommandResult.empty();
		}	
		String itemType = args.<String>getOne("item").get();
		
		String[] check = itemType.split(":");
		
		if(check.length < 2){
			src.sendMessage(Text.of(TextColors.YELLOW, "/sban add <item>"));
			return CommandResult.empty();
		}

		if(!Main.getGame().getRegistry().getType(ItemType.class, check[0] + ":" + check[1]).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Not a valid ItemType"));
			return CommandResult.empty();
		}

		if(Main.getItems().contains(itemType)){
			src.sendMessage(Text.of(TextColors.YELLOW, itemType, " already exists"));
			return CommandResult.empty();
		}
		
		List<String> items = Main.getItems();
		
		items.add(itemType);

		ConfigManager configManager = new ConfigManager();
		
		configManager.getConfig().getNode("items").setValue(items);
		
		configManager.save();
		
		src.sendMessage(Text.of(TextColors.GREEN, "Added ", itemType, " to ban list"));
		
		return CommandResult.success();
	}

}
