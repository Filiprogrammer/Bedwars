package filip.bedwars.game.action;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.Game;
import filip.bedwars.game.GameLogic;
import filip.bedwars.utils.MessageSender;

public class ActionSendMessage extends Action {

	private final String message;
	private final boolean includeSpectators;
	
	public ActionSendMessage(@NotNull String message, @NotNull Boolean includeSpectators) {
		this.message = message;
		this.includeSpectators = includeSpectators;
	}

	@Override
	public void execute(@NotNull Game game, @NotNull GameLogic gameLogic) {
		if (includeSpectators) {
			for (Player p : gameLogic.getGameWorld().getWorld().getPlayers())
				MessageSender.sendMessage(p, MessagesConfig.getInstance().getStringValue(p.getLocale(), message));
		} else {
			for (UUID uuid : game.getPlayers()) {
				Player p = Bukkit.getPlayer(uuid);
				
				if (p != null)
					MessageSender.sendMessage(p, MessagesConfig.getInstance().getStringValue(p.getLocale(), message));
			}
		}
	}

	public static String[] getArgumentNames() {
		return new String[] { "message", "include-spectators" };
	}

}
