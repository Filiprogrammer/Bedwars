package filip.bedwars.game.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;

public class Arena {
	
	private List<Spawner> spawner = new ArrayList<Spawner>();
	private List<Base> bases = new ArrayList<Base>();
	private final String mapName;	
	private World world;
	
	public Arena(String mapName, List<Spawner> spawner, List<Base> bases) {
		this.spawner = spawner;
		this.bases = bases;
		this.mapName = mapName;
		
		
		if(this.bases.size() >= 1) {
			this.world = this.bases.get(0).getSpawn().getWorld(); //Sets the world as the first base spawn point
		}
		
	}
	
	public Base getBase(int id) {
        if (id < bases.size())
            return bases.get(id); //only if id is one of the bases
        
        return null;
    }
	
	public List<Base> getBases() {
		return bases;
	}
	
	public List<Spawner> getSpawner() {
		return spawner;
	}
	
	public World getWorld() {
		return world;
	}
	
	public String getMapName() {
		return mapName;
	}
	
}
