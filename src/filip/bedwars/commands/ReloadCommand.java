package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import filip.bedwars.config.ArenaConfig;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.config.SoundsConfig;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class ReloadCommand implements ICommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != getArguments().length)
			return false;
		
		MainConfig.getInstance().reloadConfig();
		MessagesConfig.getInstance().reloadConfig();
		ArenaConfig.getInstance().reloadConfig();
		SoundsConfig.getInstance().reloadConfig();
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(player.getLocale(), "config-reloaded"));
			SoundPlayer.playSound("success", player);
		} else {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(MainConfig.getInstance().getLanguage(), "config-reloaded"));
		}
		
		return true;
	}

	public String getPermission() {
		return "reload";
	}

	public String getName() {
		return "reload";
	}
	
	public String[] getArguments() {
		return new String[0];
	}
	
}
