package filip.bedwars.world;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.World;

public class GameWorld {

	private World world;
	
	public GameWorld(World loadFrom) {
		loadWorld(loadFrom);
	}
	
	public World getWorld() {
		return world;
	}
	
	private void loadWorld(World loadFrom) {
		// TODO: get game world name prefix from config
		throw new NotImplementedException();
	}
	
}
