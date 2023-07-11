package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.Game;
import filip.bedwars.game.GameManager;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class LeaveCommand implements ICommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != getArguments().length)
			return false;
		
		if (!(sender instanceof Player)) {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(MainConfig.getInstance().getLanguage(), "you-must-be-player"));
			return true;
		}
		
		Player player = (Player) sender;
		Game game = GameManager.getInstance().getGameOfPlayer(player);
		
		if (game == null) {
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "you-are-not-ingame"));
			SoundPlayer.playSound("error", player);
			return true;
		}
		
		game.leavePlayer(player);
		MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "player-left").replace("%player%", player.getName()));
		SoundPlayer.playSound("success", player);
		
		return true;
	}

	@Override
	public String getPermission() {
		return "play.command";
	}

	@Override
	public String getName() {
		return "leave";
	}

	@Override
	public String[] getArguments() {
		return new String[0];
	}
	
}
