package filip.bedwars.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.World;

import filip.bedwars.game.arena.Arena;
import filip.bedwars.utils.ArenaDeserializer;
import filip.bedwars.utils.ArenaSerializer;

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
	
	public Arena getArena(World world) {
		for (Arena arena : arenas)
			if (arena.getWorld().getName().equals(world.getName()))
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
	
	public static ArenaConfig getInstance() {
		if (instance == null)
			instance = new ArenaConfig();
		
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public void reloadConfig() {
		createAndLoadConfigFileIfNotExistent(true);
		
		arenas = new ArrayList<Arena>();
		
		if (config.isList("arenas")) {
			List<Object> serializedArenas = (List<Object>) config.getList("arenas");
			
			for (Object serializedArena : serializedArenas)
				arenas.add(ArenaDeserializer.deserializeArena(serializedArena));
		}
	}
	
	public boolean saveConfig() {
		createAndLoadConfigFileIfNotExistent(true);
		
		List<Map<String, Object>> serializedArenas = new ArrayList<Map<String, Object>>();
		
		for (Arena arena : arenas)
			serializedArenas.add(ArenaSerializer.serializeArena(arena));
		
		config.set("arenas", serializedArenas);
		
		try {
			config.save(configFile);
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}

}
