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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

public class WorldElement extends CommandElement {

    public WorldElement(Text key) {
        super(key);
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
    	final String worldName = args.next();

		if(!Sponge.getServer().getWorld(worldName).isPresent() && !worldName.equalsIgnoreCase("global")) {
			throw args.createError(Text.of(TextColors.RED, worldName, " does not exist"));
		}

		return worldName;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
    	List<String> list = new ArrayList<>();

    	Optional<String> next = args.nextIfPresent();
    	
    	if(next.isPresent()) {
    		if("global".startsWith(next.get())) {
    			list.add("global");
    		}
            for(WorldProperties properties : Sponge.getServer().getAllWorldProperties()) {
            	if(properties.getWorldName().startsWith(next.get())) {
            		list.add(properties.getWorldName());
            	}
            }
    	} else {
    		list.add("global");
    		
    		for(WorldProperties properties : Sponge.getServer().getAllWorldProperties()) {
            	list.add(properties.getWorldName());
            }
    	}

        return list;
    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of(getKey());
    }
}
