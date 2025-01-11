package filip.bedwars.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ICommand {

	boolean execute(@NotNull CommandSender sender, @NotNull String[] args);

	@NotNull
	String getPermission();

	@NotNull
	String getName();

	@NotNull
	String[] getArguments();

	@Nullable
	default List<String> getSuggestions(int argIndex, @NotNull String arg) {
		return null;
	}

}
