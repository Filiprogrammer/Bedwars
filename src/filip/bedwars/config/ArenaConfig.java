package filip.bedwars.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import filip.bedwars.game.arena.Arena;

public class ArenaConfig extends SingleConfig {

private static ArenaConfig instance = null;
	
	List<Arena> arenas = new ArrayList<Arena>();
	
	private ArenaConfig() {
		super("arenas.yml");
		reloadConfig();
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
	
	@SuppressWarnings("unchecked")
	public void reloadConfig() {
		createAndLoadConfigFileIfNotExistent(true);
		
		if (config.isList("arenas"))
			arenas = (List<Arena>) config.getList("arenas");
		else
			arenas = new ArrayList<Arena>();
	}
	
	public boolean saveConfig() {
		createAndLoadConfigFileIfNotExistent(true);
		
		config.set("arenas", arenas);
		
		try {
			config.save(configFile);
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}

}
