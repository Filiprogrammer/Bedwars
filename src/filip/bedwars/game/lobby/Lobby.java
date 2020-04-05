package filip.bedwars.game.lobby;

import org.bukkit.Location;

public class Lobby {
	
	private Location spawnPoint;

	public Lobby(Location spawnPoint) {
		this.spawnPoint = spawnPoint;
	}
	
	public Location getSpawnPoint() {
		return spawnPoint;
	}
	
}
