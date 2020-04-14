package filip.bedwars.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class GameWorldManager {

	private static GameWorldManager instance = null;
	
	private List<GameWorld> gameWorlds = new ArrayList<GameWorld>();
	
	private GameWorldManager() {}
	
	public static GameWorldManager getInstance() {
		if (instance == null)
			instance = new GameWorldManager();
		
		return instance;
	}
	
	public GameWorld claimGameWorld(World loadFrom) {
		return new GameWorld(loadFrom);
	}
	
	public void removeGameWorld(@NotNull GameWorld gameWorld) {
		gameWorlds.remove(gameWorld);
		gameWorld.unloadWorld();
	}
	
}
