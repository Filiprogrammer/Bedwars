package filip.bedwars.game.arena.setup;

import org.bukkit.Location;
import org.bukkit.Material;

import filip.bedwars.game.arena.Spawner;

public class SpawnerBuilder {
	
	private Location loc;
	private int ticksPerSpawn;
	private Material item;
	private String itemName;
	
	public SpawnerBuilder setLocation(Location location) {
		loc = location;
		return this;
	}
	
	public SpawnerBuilder setTicks(int ticks) {
		ticksPerSpawn = ticks;
		return this;
	}
	
	public SpawnerBuilder setItem(Material item) {
		this.item = item;
		return this;
	}
	
	public SpawnerBuilder setItemName(String name) {
		this.itemName = name;
		return this;
	}
	
	public Spawner build() {
		return new Spawner(loc, ticksPerSpawn, item, itemName);
	}

}
