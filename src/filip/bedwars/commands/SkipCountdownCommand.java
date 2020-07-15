package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.Game;
import filip.bedwars.game.GameManager;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class SkipCountdownCommand implements ICommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != getArguments().length)
			return false;
		
		if(!(sender instanceof Player)) {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(MainConfig.getInstance().getLanguage(), "you-must-be-player"));
			return true;
		}
		
		Player player = (Player) sender;
		String locale = player.getLocale();
		Game game = GameManager.getInstance().getGameOfPlayer(player);
		
		if(game == null) {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(locale, "you-are-not-ingame"));
			SoundPlayer.playSound("error", player);
			return true;
		}
		
		if (game.isRunning()) {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(locale, "game-already-running"));
			SoundPlayer.playSound("error", player);
			return true;
		}
		
		game.getLobby().skipLobbyCountdown(player);
		
		return true;
	}

	public String getPermission() {
		return "lobby.skip";
	}

	public String getName() {
		return "start";
	}
	
	public String[] getArguments() {
		return new String[0];
	}
	
}
