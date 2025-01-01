package filip.bedwars.game.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Arena implements Cloneable {
	
	private final List<Spawner> spawner;
	private final List<Base> bases;
	private final String mapName;	
	private final World world;
	private final int minPlayersToStart;
	private final int playersPerTeam;
	private final Location spectatorSpawn;

	public Arena(@NotNull String mapName, int minPlayersToStart, int playersPerTeam, @NotNull World world, @NotNull List<Spawner> spawner, @NotNull List<Base> bases, @Nullable Location spectatorSpawn) {
		this.spawner = spawner;
		this.bases = bases;
		this.mapName = mapName;
		this.minPlayersToStart = minPlayersToStart;
		this.playersPerTeam = playersPerTeam;
		this.world = world;
		this.spectatorSpawn = spectatorSpawn;
	}

	@Nullable
	public Base getBase(int id) {
        if (id < bases.size())
            return bases.get(id); //only if id is one of the bases
        
        return null;
    }

	@NotNull
	public List<Base> getBases() {
		return bases;
	}

	@NotNull
	public List<Spawner> getSpawner() {
		return spawner;
	}

	@NotNull
	public World getWorld() {
		return world;
	}

	@NotNull
	public String getMapName() {
		return mapName;
	}
	
	public int getMinPlayersToStart() {
		return minPlayersToStart;
	}
	
	public int getPlayersPerTeam() {
		return playersPerTeam;
	}

	@Nullable
	public Location getSpectatorSpawn(@Nullable World world) {
		if (spectatorSpawn == null)
			return null;
		
		return new Location(world, spectatorSpawn.getX(), spectatorSpawn.getY(), spectatorSpawn.getZ(), spectatorSpawn.getYaw(), spectatorSpawn.getPitch());
	}
	
	public Arena clone() {
		List<Spawner> newSpawner = new ArrayList<>();
		List<Base> newBases = new ArrayList<>();
		
		for (Spawner spawner : this.spawner)
			newSpawner.add(spawner.clone());
		
		for (Base base : this.bases)
			newBases.add(base.clone());
		
		Location newSpectatorSpawn = null;
		
		if (spectatorSpawn != null)
			newSpectatorSpawn = spectatorSpawn.clone();
		
		return new Arena(mapName, minPlayersToStart, playersPerTeam, world, newSpawner, newBases, newSpectatorSpawn);
	}
	
}
