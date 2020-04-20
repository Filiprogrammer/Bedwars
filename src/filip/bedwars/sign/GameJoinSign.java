package filip.bedwars.sign;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

public class GameJoinSign {

	Location location;
	String mapName;
	
	public GameJoinSign(Location location, String mapName) {
		this.location = location;
		this.mapName = mapName;
	}
	
	public Sign getSign() {
		BlockState state = location.getBlock().getState();
		
		if (!(state instanceof Sign))
			return null;
		
		return (Sign) state;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public String getMapName() {
		return mapName;
	}
	
}
