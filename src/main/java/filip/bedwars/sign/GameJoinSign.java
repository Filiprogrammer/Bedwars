package filip.bedwars.sign;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.config.MainConfig;

public class GameJoinSign {

	private final Location location;
	private final String mapName;
	
	public GameJoinSign(@NotNull final Location location, final String mapName) {
		this.location = location;
		this.mapName = mapName;
		
		Sign sign = getSign();
		
		if (sign != null) {
			sign.setLine(0, MainConfig.getInstance().getJoinSignLine(0).replace("%arenaname%", mapName));
			sign.setLine(1, MainConfig.getInstance().getJoinSignLine(1).replace("%arenaname%", mapName));
			sign.setLine(2, MainConfig.getInstance().getJoinSignLine(2).replace("%arenaname%", mapName));
			sign.setLine(3, MainConfig.getInstance().getJoinSignLine(3).replace("%arenaname%", mapName));
			sign.setGlowingText(true);
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
