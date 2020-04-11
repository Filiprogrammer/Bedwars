package filip.bedwars.commands;

import org.bukkit.command.CommandSender;

import filip.bedwars.config.ArenaConfig;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.config.SoundsConfig;
import filip.bedwars.utils.MessageSender;

public class ReloadCommand implements ICommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != 0)
			return false;
		
		MainConfig.getInstance().reloadConfig();
		MessagesConfig.getInstance().reloadConfig();
		ArenaConfig.getInstance().reloadConfig();
		SoundsConfig.getInstance().reloadConfig();
		
		// TODO: Read message from config file
		MessageSender.sendMessage(sender, "Config files reloaded");
		
		return true;
	}

	@Override
	public String getPermission() {
		return "reload";
	}

	@Override
	public String getName() {
		return "reload";
	}
	
}
