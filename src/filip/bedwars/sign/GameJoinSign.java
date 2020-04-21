package filip.bedwars.sign;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import filip.bedwars.config.MainConfig;

public class GameJoinSign {

	Location location;
	String mapName;
	
	public GameJoinSign(Location location, String mapName) {
		this.location = location;
		this.mapName = mapName;
		
		Sign sign = getSign();
		
		if (sign != null) {
			sign.setLine(0, MainConfig.getInstance().getJoinSignLine(0).replace("%arenaname%", mapName));
			sign.setLine(1, MainConfig.getInstance().getJoinSignLine(1).replace("%arenaname%", mapName));
			sign.setLine(2, MainConfig.getInstance().getJoinSignLine(2).replace("%arenaname%", mapName));
			sign.setLine(3, MainConfig.getInstance().getJoinSignLine(3).replace("%arenaname%", mapName));
			sign.update();
		}
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
