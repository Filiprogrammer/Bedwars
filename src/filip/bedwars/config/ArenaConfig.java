package filip.bedwars.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;

import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.arena.Base;
import filip.bedwars.utils.ArenaDeserializer;
import filip.bedwars.utils.ArenaSerializer;
import filip.bedwars.utils.MessageSender;

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
	
	public int getArenaCount() {
		return arenas.size();
	}
	
	public void addArena(Arena arena) {
		arenas.add(arena);
	}
	
	public boolean removeArena(Arena arena) {
		return arenas.remove(arena);
	}
	
	public boolean removeArena(String mapName) {
		Arena arena = getArena(mapName);
		
		if (arena == null)
			return false;
		
		return arenas.remove(arena);
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
			
			for (Object serializedArena : serializedArenas) {
				Arena arena = ArenaDeserializer.deserializeArena(serializedArena);
				
				if(isArenaValid(arena))
					arenas.add(arena); // add the arena to the list only if it is valid
			}
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

	/**
	 * Check if an arena fulfills all requirements, if not return false and print a warning in the console.
	 * @param arena Arena to check
	 * @return true if arena is valid; false if arena is not valid
	 */
	private boolean isArenaValid(Arena arena) {
		boolean ret = true;
		String mapName = arena.getMapName();
		
		if(mapName == null) {
			ret = false;
			MessageSender.sendWarning("§eOne arena could not be loaded! The arena does not have a name! Please delete the arena from the config and set it up with the ingame-commands again!");
			mapName = "arena-name-not-found";
		}
		
		if(arena.getBases().size() < 2) {
			ret = false;
			MessageSender.sendWarning("§eArena §6\"" + mapName + "\" §ecould not be loaded! There must be at least 2 bases! Please delete the arena from the config and set it up with the ingame-commands again!");
		}
		
		if(arena.getMinPlayersToStart() < 2) {
			ret = false;
			MessageSender.sendWarning("§eArena §6\"" + mapName + "\" §ecould not be loaded! The minimum start players count must be at least 2! Please delete the arena from the config and set it up with the ingame-commands again!");
		}
		
		if(arena.getPlayersPerTeam() < 1) {
			ret = false;
			MessageSender.sendWarning("§eArena §6\"" + mapName + "\" §ecould not be loaded! The players per team count must be at least 1! Please delete the arena from the config and set it up with the ingame-commands again!");
		}
		
		if(!Bukkit.getWorlds().contains(arena.getWorld())) {
			ret = false;
			MessageSender.sendWarning("§eArena §6\"" + mapName + "\" §ecould not be loaded! The world could not be found on the server! Please delete the arena from the config and set it up with the ingame-commands again!");
		}
		
		for(Base base : arena.getBases()) {
			if(base.getTeamColor() == null) {
				ret = false;
				MessageSender.sendWarning("§eArena §6\"" + mapName + "\" §ecould not be loaded! In one base the team color is null! Please delete the arena from the config and set it up with the ingame-commands again!");
				continue;
			}
			
			if(base.getBedTop(null) == null) {
				ret = false;
				MessageSender.sendWarning("§eArena §6\"" + mapName + "\" §ecould not be loaded! In base §6" + base.getTeamColor().toString() + "§e the bed top was null! Please delete the arena from the config and set it up with the ingame-commands again!");
			}
			
			if(base.getBedBottom(null) == null) {
				ret = false;
				MessageSender.sendWarning("§eArena §6\"" + mapName + "\" §ecould not be loaded! In base §6" + base.getTeamColor().toString() + "§e the bed bottom was null! Please delete the arena from the config and set it up with the ingame-commands again!");
			}
			
			if(base.getItemShop(null) == null) {
				ret = false;
				MessageSender.sendWarning("§eArena §6\"" + mapName + "\" §ecould not be loaded! In base §6" + base.getTeamColor().toString() + "§e the item shop was null! Please delete the arena from the config and set it up with the ingame-commands again!");
			}
			
			if(base.getSpawn(null) == null) {
				ret = false;
				MessageSender.sendWarning("§eArena §6\"" + mapName + "\" §ecould not be loaded! In base §6" + base.getTeamColor().toString() + "§e the spawn point was null! Please delete the arena from the config and set it up with the ingame-commands again!");
			}
		}
		
		return ret;
	}
}
