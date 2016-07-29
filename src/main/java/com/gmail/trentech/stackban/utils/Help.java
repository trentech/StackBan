package com.gmail.trentech.stackban.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.pagination.PaginationList.Builder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class Help {

	private final String id;
	private final String command;
	private final String description;
	private Optional<String> syntax = Optional.empty();
	private Optional<String> example = Optional.empty();

	private static List<Help> list = new ArrayList<>();

	public Help(String id, String command, String description) {
		this.id = id;
		this.command = command;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public Optional<String> getSyntax() {
		return syntax;
	}

	public void setSyntax(String syntax) {
		this.syntax = Optional.of(syntax);
	}

	public Optional<String> getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = Optional.of(example);
	}

	public String getCommand() {
		return command;
	}

	public void save() {
		list.add(this);
	}

	public static Consumer<CommandSource> getHelp(String input) {
		return (CommandSource src) -> {
			for (Help help : list) {
				if (help.getId().equalsIgnoreCase(input)) {
					Builder pages = Sponge.getServiceManager().provide(PaginationService.class).get().builder();
					pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, help.getCommand().toLowerCase())).build());

					List<Text> list = new ArrayList<>();

					list.add(Text.of(TextColors.GREEN, "Description:"));
					list.add(Text.of(TextColors.WHITE, help.getDescription()));

					if (help.getSyntax().isPresent()) {
						list.add(Text.of(TextColors.GREEN, "Syntax:"));
						list.add(Text.of(TextColors.WHITE, help.getSyntax().get()));
					}
					if (help.getExample().isPresent()) {
						list.add(Text.of(TextColors.GREEN, "Example:"));
						list.add(Text.of(TextColors.WHITE, help.getExample().get(), TextColors.DARK_GREEN));
					}

					pages.contents(list);

					pages.sendTo(src);
					break;
				}
			}

		};
	}
}
