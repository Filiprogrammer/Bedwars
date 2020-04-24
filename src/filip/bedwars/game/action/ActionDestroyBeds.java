package filip.bedwars.game.action;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.destroystokyo.paper.Title;

import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.Game;
import filip.bedwars.game.GameLogic;
import filip.bedwars.game.Team;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class ActionDestroyBeds extends Action {
	
	@Override
	public void execute(@NotNull Game game, @NotNull GameLogic gameLogic) {
		for (Team team : game.getTeams()) {
			team.destroyBed(gameLogic.getGameWorld().getWorld());
			gameLogic.scoreboardManager.update();
			
			for (UUID uuid : team.getMembers()) {
				Player p = Bukkit.getPlayer(uuid);
				Title title = new Title(MessagesConfig.getInstance().getStringValue(p.getLocale(), "your-bed-destroyed"), MessagesConfig.getInstance().getStringValue(p.getLocale(), "you-cant-respawn-anymore"));
				p.sendTitle(title);
			}
		}
		
		for (Player p : gameLogic.getGameWorld().getWorld().getPlayers()) {
			MessageSender.sendMessage(p, "§4All beds have been destroyed");
			SoundPlayer.playSound("bed-destroyed", p);
		}
	}

	public static String[] getArgumentNames() {
		return new String[0];
	}

}
