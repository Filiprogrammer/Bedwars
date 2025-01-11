package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.Game;
import filip.bedwars.game.GameManager;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class LeaveCommand implements ICommand {

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull final String[] args) {
		if (args.length != getArguments().length)
			return false;

		if (!(sender instanceof Player)) {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(MainConfig.getInstance().getLanguage(), "you-must-be-player"));
			return true;
		}

		Player player = (Player) sender;
		Game game = GameManager.getInstance().getGameOfPlayer(player);
		final String locale = player.getLocale();

		if (game == null) {
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(locale, "you-are-not-ingame"));
			SoundPlayer.playSound("error", player);
			return true;
		}

		game.leavePlayer(player);
		MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(locale, "player-left").replace("%player%", player.getName()));
		SoundPlayer.playSound("success", player);

		return true;
	}

	@Override
	@NotNull
	public String getPermission() {
		return "play.command";
	}

	@Override
	@NotNull
	public String getName() {
		return "leave";
	}

	@Override
	@NotNull
	public String[] getArguments() {
		return new String[0];
	}

}
