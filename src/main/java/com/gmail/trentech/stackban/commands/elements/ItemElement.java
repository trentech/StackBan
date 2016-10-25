package com.gmail.trentech.stackban.commands.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ItemElement extends CommandElement {

    public ItemElement(Text key) {
        super(key);
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
    	final String itemName = args.next();

		String[] check = itemName.split(":");

		if (check.length < 2 || check.length > 3) {
			throw args.createError(Text.of(TextColors.RED, "Not a valid ItemType"));
		}

		Optional<ItemType> optionalItemType = Sponge.getRegistry().getType(ItemType.class, check[0] + ":" + check[1]);
		
		if (!optionalItemType.isPresent()) {
			throw args.createError(Text.of(TextColors.RED, "Not a valid ItemType"));
		}

		if(check.length == 3) {
			try{
				Integer.parseInt(check[2]);
			} catch(Exception e) {
				throw args.createError(Text.of(TextColors.RED, "Not a valid Data Id. Must be a number"));
			}
		}
		
		return itemName;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
    	List<String> list = new ArrayList<>();

    	Optional<String> next = args.nextIfPresent();
    	
    	if(next.isPresent()) {
            for(ItemType itemType : Sponge.getRegistry().getAllOf(ItemType.class)) {
            	if(itemType.getId().startsWith(next.get())) {
            		list.add(itemType.getId());
            	}
            }
    	} else {
    		for(ItemType itemType : Sponge.getRegistry().getAllOf(ItemType.class)) {
            	list.add(itemType.getId());
            }
    	}

        return list;
    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of(getKey());
    }
}
