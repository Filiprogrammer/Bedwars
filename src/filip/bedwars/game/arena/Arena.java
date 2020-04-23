package filip.bedwars.game.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class Arena implements Cloneable {
	
	private List<Spawner> spawner = new ArrayList<Spawner>();
	private List<Base> bases = new ArrayList<Base>();
	private final String mapName;	
	private World world;
	private final int minPlayersToStart;
	private final int playersPerTeam;

	public Arena(@NotNull String mapName, int minPlayersToStart, int playersPerTeam, @NotNull World world, @NotNull List<Spawner> spawner, @NotNull List<Base> bases) {
		this.spawner = spawner;
		this.bases = bases;
		this.mapName = mapName;
		this.minPlayersToStart = minPlayersToStart;
		this.playersPerTeam = playersPerTeam;
		this.world = world;
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
	
	public int getMinPlayersToStart() {
		return minPlayersToStart;
	}
	
	public int getPlayersPerTeam() {
		return playersPerTeam;
	}
	
	public Arena clone() {
		List<Spawner> newSpawner = new ArrayList<>();
		List<Base> newBases = new ArrayList<>();
		
		for (Spawner spawner : this.spawner)
			newSpawner.add(spawner.clone());
		
		for (Base base : this.bases)
			newBases.add(base.clone());
		
		return new Arena(mapName, minPlayersToStart, playersPerTeam, world, newSpawner, newBases);
	}
	
}
