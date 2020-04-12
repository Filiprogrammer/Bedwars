package filip.bedwars.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;

public class GameWorldManager {

	private static GameWorldManager instance = null;
	
	private List<GameWorld> gameWorlds = new ArrayList<GameWorld>();
	
	private GameWorldManager() {
		
	}
	
	public static GameWorldManager getInstance() {
		if (instance == null)
			instance = new GameWorldManager();
		
		return instance;
	}
	
	public GameWorld claimGameWorld(World loadFrom) {
		
	}
	
	public void removeGameWorld(GameWorld gameWorld) {
		
	}
	
}
