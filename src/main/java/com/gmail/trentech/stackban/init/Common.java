package com.gmail.trentech.stackban.init;

import org.spongepowered.api.item.ItemTypes;

import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pjc.help.Argument;
import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjc.help.Usage;
import com.gmail.trentech.stackban.Main;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class Common {

	public static void init() {
		initConfig(Main.getPlugin().getId());
		initHelp();
	}
	
	public static void initHelp() {
		Help whatsThis = new Help("whatsthis", "whatsthis", "If you are not sure the itemtype or varient of an item, use this command to find out the item that is in players hand")
				.setPermission("stackban.cmd.whatsthis")		
				.addExample("/whatsthis");
		
		Help.register(whatsThis);
		
		Usage usageList = new Usage(Argument.of("<world>", "Specifies the name of the targetted world or 'global' to effect all worlds"));
		
		Help sbanList = new Help("sban list", "list", "List all banned items")
				.setPermission("stackban.cmd.sban.list")
				.setUsage(usageList)
				.addExample("/sban list world");

		Usage usageLog = new Usage(Argument.of("<true|false>"));
		
		Help sbanLog = new Help("sban log", "log", "Log to console when player triggers banned item event")
				.setPermission("stackban.cmd.sban.log")
				.setUsage(usageLog)
				.addExample("/sban log true");
		
		Usage usageRemove = new Usage(Argument.of("<world>", "Specifies the name of the targetted world or 'global' to effect all worlds"))
				.addArgument(Argument.of("<itemType[:id]>", "Specifies the ItemType normally formatted <modid:itemName>. Optionally you can specify a raw data Id formatted <modid:itemName:id>"));
			
		Help sbanRemove = new Help("sban remove", "remove", "Remove item from ban list. set <world> to 'global' to remove from global config")
				.setPermission("stackban.cmd.sban.remove")
				.setUsage(usageRemove)
				.addExample("/sban remove minecraft:stone");
		
		Usage usageSet = new Usage(Argument.of("<world>", "Specifies the name of the targetted world or 'global' to effect all worlds"))
				.addArgument(Argument.of("<itemType[:id]>", "Specifies the ItemType normally formatted <modid:itemName>. Optionally you can specify a raw data Id formatted <modid:itemName:id>"))
				.addArgument(Argument.of("[--break]", "Allows breaking banned item"))
				.addArgument(Argument.of("[--craft]", "Allow crafting banned item"))
				.addArgument(Argument.of("[--drop]", "Allow dropping banned item"))
				.addArgument(Argument.of("[--modify]", "Allow modifying banned item"))
				.addArgument(Argument.of("[--pickup]", "Allow pickup of banned item"))
				.addArgument(Argument.of("[--place]", "Allowing placing banned item"))
				.addArgument(Argument.of("[--use]", "Allowing interacting with banned item"));
		
		Help sbanSet = new Help("sban set", "set", "Set item in ban list. All actions are banned by default. To unban action add corresponding flag. set <world> to 'global' to ban in all worlds")
				.setPermission("stackban.cmd.sban.set")
				.setUsage(usageSet)
				.addExample("/sban set world minecraft:stone")
				.addExample("/sban set minecraft:wool:5");
		
		Help sbanWhatsThis = new Help("sban whatsthis", "whatsthis", "Displays the itemType of the ItemStack player is holding")
				.setPermission("stackban.cmd.sban.whatsthis")
				.addExample("/sban whatsthis");

		Help sban = new Help("sban", "sban", "Base command for Stack Ban")
				.setPermission("stackban.cmd.sban")
				.addChild(sbanSet)
				.addChild(sbanRemove)
				.addChild(sbanLog)
				.addChild(sbanWhatsThis)
				.addChild(sbanList);
		
		Help.register(sban);
	}
	
	public static void initConfig(String configName) {
		ConfigManager configManager = ConfigManager.init(Main.getPlugin(), configName);
		CommentedConfigurationNode config = configManager.getConfig();

		if(configName.equalsIgnoreCase(Main.getPlugin().getId())) {
			if (config.getNode("console_log").isVirtual()) {
				config.getNode("console_log").setValue(false).setComment("Log to console when player triggers banned item event");
			}
			if (config.getNode("log_message").isVirtual()) {
				config.getNode("log_message").setValue("%PLAYER% attempted to %ACTION% banned item: %ITEM%");
			}
			if (config.getNode("player_message").isVirtual()) {
				config.getNode("player_message").setValue("&cCannot %ACTION% banned item: &e%ITEM%");
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
	}
}
