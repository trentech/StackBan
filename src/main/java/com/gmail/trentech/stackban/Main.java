package com.gmail.trentech.stackban;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import com.gmail.trentech.stackban.commands.CommandManager;
import com.gmail.trentech.stackban.utils.ConfigManager;
import com.gmail.trentech.stackban.utils.Resource;

import me.flibio.updatifier.Updatifier;

@Updatifier(repoName = "StackBan", repoOwner = "TrenTech", version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, authors = Resource.AUTHOR, url = Resource.URL, dependencies = { @Dependency(id = "Updatifier", optional = true) })
public class Main {

	private static Game game;
	private static Logger log;
	private static PluginContainer plugin;

	private static List<String> items = new ArrayList<>();
	private static boolean isLog = false;

	@Listener
	public void onPreInitialization(GamePreInitializationEvent event) {
		game = Sponge.getGame();
		plugin = getGame().getPluginManager().getPlugin(Resource.ID).get();
		log = getPlugin().getLogger();
	}

	@Listener
	public void onInitialization(GameInitializationEvent event) {
		getGame().getEventManager().registerListeners(this, new EventListener());
		getGame().getCommandManager().register(this, new CommandManager().cmdSBan, "sban", "sb");
		getGame().getCommandManager().register(this, new CommandManager().cmdWhatsThis, "whatsthis", "wt");
	}

	@Listener
	public void onStartedServer(GameStartedServerEvent event) {
		new ConfigManager().init();
	}

	public static Logger getLog() {
		return log;
	}

	public static Game getGame() {
		return game;
	}

	public static PluginContainer getPlugin() {
		return plugin;
	}

	public static List<String> getItems() {
		return items;
	}

	public static void setItems(List<String> items) {
		Main.items = items;
	}

	public static boolean isLog() {
		return isLog;
	}

	public static void setLog(boolean isLog) {
		Main.isLog = isLog;
	}
}