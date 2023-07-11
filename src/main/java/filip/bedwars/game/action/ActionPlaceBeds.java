package filip.bedwars.game.action;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.Game;
import filip.bedwars.game.GameLogic;
import filip.bedwars.game.GamePlayer;
import filip.bedwars.game.Team;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class ActionPlaceBeds extends Action {

	@Override
	public void execute(@NotNull Game game, @NotNull GameLogic gameLogic) {
		for (Team team : game.getTeams()) {
			team.restoreBed(gameLogic.getGameWorld().getWorld());
			gameLogic.scoreboardManager.update();
			
			for (GamePlayer gamePlayer : team.getMembers()) {
				Player p = gamePlayer.getPlayer();
				p.sendTitle(MessagesConfig.getInstance().getStringValue(p.getLocale(), "your-bed-restored"), "", 10, 70, 20);
			}
		}
		
		for (Player p : gameLogic.getGameWorld().getWorld().getPlayers()) {
			MessageSender.sendMessage(p, MessagesConfig.getInstance().getStringValue(p.getLocale(), "all-beds-restored"));
			SoundPlayer.playSound("bed-restored", p);
		}
		
		gameLogic.allBedsPermDestroyed = false;
	}

	public static String[] getArgumentNames() {
		return new String[0];
	}

}
