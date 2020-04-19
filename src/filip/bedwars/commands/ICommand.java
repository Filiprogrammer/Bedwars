package filip.bedwars.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

public interface ICommand {
	
	boolean execute(CommandSender sender, String[] args);
	
	String getPermission();
	
	String getName();
	
	String[] getArguments();
	
	default List<String> getSuggestions(int argIndex, String arg) {
		return null;
	}
	
}
