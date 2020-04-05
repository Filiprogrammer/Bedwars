package filip.bedwars.game.arena.setup;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.arena.Base;
import filip.bedwars.game.arena.Spawner;

public class ArenaBuilder {
	
	private String mapName;
	private List<Base> bases = new ArrayList<Base>();
	private List<Spawner> spawner = new ArrayList<Spawner>();
	
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
	
	public boolean hasBase(@NotNull Base base) {
		return bases.contains(base);
	}
	
	public Arena build() {
		return new Arena(mapName, spawner, bases);
	}

}
