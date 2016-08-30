package com.gmail.trentech.stackban.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.item.ItemTypes;

import com.gmail.trentech.stackban.Main;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigManager {

	private Path path;
	private CommentedConfigurationNode config;
	private ConfigurationLoader<CommentedConfigurationNode> loader;
	
	private static ConcurrentHashMap<String, ConfigManager> configManagers = new ConcurrentHashMap<>();

	private ConfigManager(String configName) {
		try {
			path = Main.instance().getPath().resolve(configName + ".conf");
			
			if (!Files.exists(path)) {		
				Files.createFile(path);
				Main.instance().getLog().info("Creating new " + path.getFileName() + " file...");
			}		
		} catch (IOException e) {
			e.printStackTrace();
		}

		load();
	}
	
	public static ConfigManager get(String configName) {
		return configManagers.get(configName);
	}
	
	public static ConfigManager get() {
		return configManagers.get("config");
	}
	
	public static ConfigManager init() {
		return init("config");
	}
	
	public static ConfigManager init(String configName) {
		ConfigManager configManager = new ConfigManager(configName);
		CommentedConfigurationNode config = configManager.getConfig();
		
		if(configName.equalsIgnoreCase("config")) {
			CommentedConfigurationNode node = config.getNode("console_log");

			if (node.isVirtual()) {
				node.setValue(false).setComment("Log to console when player triggers banned item event");
			}
		} else if(configName.equalsIgnoreCase("global")) {
			CommentedConfigurationNode node = config.getNode("items");

			if (node.isVirtual()) {
				node.getNode(ItemTypes.STONE.getId(), "place").setValue(false);
				node.getNode(ItemTypes.STONE.getId(), "break").setValue(false);
				node.getNode(ItemTypes.STONE.getId(), "use").setValue(false);
				node.getNode(ItemTypes.STONE.getId(), "craft").setValue(false);
				node.getNode(ItemTypes.STONE.getId(), "modify").setValue(false);
				node.getNode(ItemTypes.STONE.getId(), "hold").setValue(false);
				node.getNode(ItemTypes.STONE.getId(), "pickup").setValue(false);
				node.getNode(ItemTypes.STONE.getId(), "drop").setValue(false);
			}
		}
		
		configManager.save();
		
		configManagers.put(configName, configManager);

		return configManager;
	}
	
	public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
		return loader;
	}

	public CommentedConfigurationNode getConfig() {
		return config;
	}

	private void load() {
		loader = HoconConfigurationLoader.builder().setPath(path).build();
		
		try {
			config = loader.load();
		} catch (IOException e) {
			Main.instance().getLog().error("Failed to load config");
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			loader.save(config);
		} catch (IOException e) {
			Main.instance().getLog().error("Failed to save config");
			e.printStackTrace();
		}
	}
}
