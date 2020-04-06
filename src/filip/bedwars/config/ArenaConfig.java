package filip.bedwars.config;

import java.util.ArrayList;
import java.util.List;

import filip.bedwars.game.arena.Arena;

public class ArenaConfig {

private static ArenaConfig instance = null;
	
	List<Arena> arenas = new ArrayList<Arena>();
	
	private ArenaConfig() {
		// TODO: load config files
		// TODO: if file doesn't exist generate default config
	}

	public Arena getArena(String mapName) {
		for (Arena arena : arenas)
			if (arena.getMapName().equals(mapName))
				return arena;
		
		return null;
	}
	
	public Arena getArena(int index) {
		return arenas.get(index);
	}
	
	public void addArena(Arena arena) {
		arenas.add(arena);
	}
	
	public void removeArena(Arena arena) {
		arenas.remove(arena);
	}
	
	public ArenaConfig getInstance() {
		if (instance == null)
			instance = new ArenaConfig();
		
		return instance;
	}
	
	private void updateConfigFile() {
		// TODO: Ahjo
	}
	
	private void reloadConfigFile() {
		// TODO: Ahjo
	}

}
