package filip.bedwars.game.arena.setup;

import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.arena.Spawner;

public class SpawnerBuilder {
	
	private Location loc;
	private int ticksPerSpawn;
	private Material item;
	private String itemName;
	
	public SpawnerBuilder setLocation(@NotNull Location location) {
		loc = location;
		return this;
	}
	
	public SpawnerBuilder setTicksPerSpawn(int ticksPerSpawn) {
		this.ticksPerSpawn = ticksPerSpawn;
		return this;
	}
	
	public SpawnerBuilder setItem(@NotNull Material item) {
		this.item = item;
		return this;
	}
	
	public SpawnerBuilder setItemName(@NotNull String name) {
		this.itemName = name;
		return this;
	}
	
	public Spawner build() {
		return new Spawner(loc, ticksPerSpawn, item, itemName);
	}

}
