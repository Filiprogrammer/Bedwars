package filip.bedwars.game.arena.setup;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.arena.Base;
import filip.bedwars.game.arena.Spawner;

public class ArenaBuilder {
	
	private String mapName;
	private List<Base> bases = new ArrayList<Base>();
	private List<Spawner> spawner = new ArrayList<Spawner>();
	private World world;
	private int minPlayersToStart;
	private int playersPerTeam;
	private Location spectatorSpawn;

	@NotNull
	public ArenaBuilder addBase(@NotNull Base base) {
		bases.add(base);
		return this;
	}

	@NotNull
	public ArenaBuilder addSpawner(@NotNull Spawner spawner) {
		this.spawner.add(spawner);
		return this;
	}

	@NotNull
	public ArenaBuilder setMapName(@NotNull final String mapName) {
		this.mapName = mapName;
		return this;
	}

	@NotNull
	public ArenaBuilder setMinPlayersToStart(final int minPlayersToStart) {
		this.minPlayersToStart = minPlayersToStart;
		return this;
	}

	@NotNull
	public ArenaBuilder setPlayersPerTeam(final int playersPerTeam) {
		this.playersPerTeam = playersPerTeam;
		return this;
	}

	@NotNull
	public ArenaBuilder setWorld(@NotNull World world) {
		this.world = world;
		return this;
	}

	@NotNull
	public ArenaBuilder removeSpawner(final int index) {
		this.spawner.remove(index);
		return this;
	}

	@NotNull
	public ArenaBuilder setSpectatorSpawn(@Nullable Location location) {
		this.spectatorSpawn = location;
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

	@NotNull
	public Arena build() {
		if (mapName == null)
			throw new NullPointerException("mapName must not be null");

		if (world == null)
			throw new NullPointerException("world must not be null");

		return new Arena(mapName, minPlayersToStart, playersPerTeam, world, spawner, bases, spectatorSpawn);
	}

}
