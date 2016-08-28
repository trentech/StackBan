package com.gmail.trentech.stackban;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.World;

import com.gmail.trentech.stackban.commands.CommandManager;
import com.gmail.trentech.stackban.utils.ConfigManager;
import com.gmail.trentech.stackban.utils.Resource;

import me.flibio.updatifier.Updatifier;

@Updatifier(repoName = Resource.NAME, repoOwner = Resource.AUTHOR, version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, description = Resource.DESCRIPTION, authors = Resource.AUTHOR, url = Resource.URL, dependencies = { @Dependency(id = "Updatifier", optional = true) })
public class Main {

	private static Logger log;
	private static PluginContainer plugin;

	@Listener
	public void onPreInitialization(GamePreInitializationEvent event) {
		plugin = Sponge.getPluginManager().getPlugin(Resource.ID).get();
		log = getPlugin().getLogger();
	}

	@Listener
	public void onInitialization(GameInitializationEvent event) {
		ConfigManager.init();
		ConfigManager.init("global");
		
		Sponge.getEventManager().registerListeners(this, new EventListener());
		Sponge.getCommandManager().register(this, new CommandManager().cmdSBan, "sban", "sb");
		Sponge.getCommandManager().register(this, new CommandManager().cmdWhatsThis, "whatsthis", "wt");
	}

	@Listener
	public void onReloadEvent(GameReloadEvent event) {
		ConfigManager.init();
		ConfigManager.init("global");

		for (World world : Sponge.getServer().getWorlds()) {
			ConfigManager.init(world.getName());
		}
	}

	public static Logger getLog() {
		return log;
	}

	public static PluginContainer getPlugin() {
		return plugin;
	}
}