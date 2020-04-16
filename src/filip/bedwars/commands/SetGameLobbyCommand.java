package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class SetGameLobbyCommand implements ICommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != getArguments().length)
			return false;
			
		if(sender instanceof Player) {
			Player player = (Player) sender;
				
			MainConfig.getInstance().setGameLobby(player.getLocation());
			MainConfig.getInstance().saveConfig();
				
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(player.getLocale(), "lobby-set"));
			SoundPlayer.playSound("success", player);
		} else {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(MainConfig.getInstance().getLanguage(), "you-must-be-player"));
		}
			
		return true;
	}

	public String getPermission() {
		return "setup";
	}

	public String getName() {
		return "setgamelobby";
	}
		
	public String[] getArguments() {
		return new String[0];
	}
		
}
