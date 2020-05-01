package filip.bedwars.game;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GamePlayer {

	public final UUID uuid;
	public final Game game;
	
	public GamePlayer(UUID uuid, Game game) {
		this.uuid = uuid;
		this.game = game;
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}
	
	public Team getTeam() {
		List<Team> teams = game.getTeams();
		
		synchronized (teams) {
			for (Team team : teams)
				if (team.containsMember(uuid))
					return team;
		}
		
		return null;
	}
	
}
