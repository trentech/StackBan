package com.gmail.trentech.stackban.commands.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.stackban.utils.Help;

public class HelpElement extends CommandElement {

    public HelpElement(Text key) {
        super(key);
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        final StringBuilder ret = new StringBuilder(args.next());
        
        while (args.hasNext()) {
            ret.append(' ').append(args.next());
        }
        String rawCommand = ret.toString().toLowerCase();

        Optional<Help> optionalHelp = Help.get(rawCommand);

        if(optionalHelp.isPresent()) {
        	return optionalHelp.get();
        }
        
		throw args.createError(Text.of(TextColors.RED, "Command not found"));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
    	List<String> list = new ArrayList<>();

    	Optional<String> next = args.nextIfPresent();
    	
    	if(next.isPresent()) {
            for(Help help : Help.getAll()) {
            	if(help.getRawCommand().startsWith(next.get().toLowerCase())) {
            		list.add(help.getRawCommand());
            	}
            }
    	} else {
            for(Help help : Help.getAll()) {
            	list.add(help.getRawCommand());
            }
    	}

        return list;
    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of(getKey());
    }
}
