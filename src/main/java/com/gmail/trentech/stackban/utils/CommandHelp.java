package com.gmail.trentech.stackban.utils;

import com.gmail.trentech.helpme.help.Argument;
import com.gmail.trentech.helpme.help.Help;
import com.gmail.trentech.helpme.help.Usage;

public class CommandHelp {

	public static void init() {
		
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
		
		Help sban = new Help("sban", "sban", "Base command for Stack Ban")
				.setPermission("stackban.cmd.sban")
				.addChild(sbanSet)
				.addChild(sbanRemove)
				.addChild(sbanLog)
				.addChild(sbanList);
		
		Help.register(sban);
	}
}
