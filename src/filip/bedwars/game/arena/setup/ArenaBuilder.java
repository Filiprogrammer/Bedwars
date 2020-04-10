package filip.bedwars.game.arena.setup;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.arena.Base;
import filip.bedwars.game.arena.Spawner;

public class ArenaBuilder {
	
	private String mapName;
	private List<Base> bases = new ArrayList<Base>();
	private List<Spawner> spawner = new ArrayList<Spawner>();
	private World world;
	
	public ArenaBuilder addBase(@NotNull Base base) {
		bases.add(base);
		return this;
	}
	
	public ArenaBuilder addSpawner(@NotNull Spawner spawner) {
		this.spawner.add(spawner);
		return this;
	}
	
	public ArenaBuilder setMapName(@NotNull String mapName) {
		this.mapName = mapName;
		return this;
	}
	
	public ArenaBuilder setWorld(@NotNull World world) {
		this.world = world;
		return this;
	}
	
	public ArenaBuilder removeSpawner(int index) {
		this.spawner.remove(index);
		return this;
	}
	
	public boolean hasBase(@NotNull Base base) {
		return bases.contains(base);
	}
	
	public int getBaseCount() {
		return bases.size();
	}
	
	public int getSpawnerCount() {
		return spawner.size();
	}
	
	public String getMapName() {
		return mapName;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Arena build() {
		return new Arena(mapName, world, spawner, bases);
	}

}
