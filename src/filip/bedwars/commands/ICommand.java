package filip.bedwars.commands;

import org.bukkit.command.CommandSender;

public interface ICommand {
	
	boolean execute(CommandSender sender, String[] args);
	
	String getPermission();
	
	String getName();
	
	String[] getArguments();
	
}
