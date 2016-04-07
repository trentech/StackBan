package com.gmail.trentech.stackban.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.stackban.utils.Help;

public class CMDWhatsThis implements CommandExecutor {

	public CMDWhatsThis(){
		Help help = new Help("whatsthis", "whatsthis", " If you are not sure the itemtype or varient of an item, use this command to find out the item that is in players hand");
		help.setSyntax(" /whatsthis\n /wt");
		help.setExample(" /whatsthis");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;
		
		Optional<ItemStack> optionalItemStack = player.getItemInHand();
		
		if(!optionalItemStack.isPresent()){
			src.sendMessage(Text.of(TextColors.YELLOW, "You must be holding an item"));
			return CommandResult.empty();
		}
		ItemStack itemStack = optionalItemStack.get();
		
		DataContainer cont = itemStack.toContainer();
        DataQuery query = DataQuery.of('/',"UnsafeDamage");

        int unsafeDamage = Integer.parseInt(cont.get(query).get().toString());
        
        String item = itemStack.getItem().getName();
        		
        if(unsafeDamage != 0){
        	item = item + ":" + unsafeDamage;
        }

        player.sendMessage(Text.of(TextColors.GREEN, item));

		return CommandResult.success();
	}

}
