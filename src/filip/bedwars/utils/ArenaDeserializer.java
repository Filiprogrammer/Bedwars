package filip.bedwars.utils;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import filip.bedwars.game.TeamColor;
import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.arena.setup.ArenaBuilder;
import filip.bedwars.game.arena.setup.BaseBuilder;
import filip.bedwars.game.arena.setup.SpawnerBuilder;

public class ArenaDeserializer {
	
	public static Arena deserializeArena(Object serializedArena) {
		Map<String, Object> mapOfElements = (Map<String, Object>) serializedArena;
		
		ArenaBuilder arenaBuilder = new ArenaBuilder();
		
		arenaBuilder.setMapName((String) mapOfElements.get("mapName"));
		arenaBuilder.setWorld(Bukkit.getWorld((String) mapOfElements.get("world")));
		
		List<Map<String, Object>> spawnerList = (List<Map<String, Object>>) mapOfElements.get("spawner");
		
		for (Map<String, Object> serializedSpawner : spawnerList) {
			SpawnerBuilder spawnerBuilder = new SpawnerBuilder();
			spawnerBuilder.setTicksPerSpawn((int) serializedSpawner.get("ticksPerSpawn"));
			spawnerBuilder.setItem(Material.valueOf((String) serializedSpawner.get("itemMaterial")));
			spawnerBuilder.setItemName((String) serializedSpawner.get("itemName"));
			
			Map<String, Integer> spawnerLocationMap = (Map<String, Integer>) serializedSpawner.get("location");
			Location spawnerLocation = new Location(arenaBuilder.getWorld(), spawnerLocationMap.get("x"), spawnerLocationMap.get("y"), spawnerLocationMap.get("z"));
			spawnerBuilder.setLocation(spawnerLocation);
			
			arenaBuilder.addSpawner(spawnerBuilder.build());
		}
		
		List<Map<String, Object>> basesList = (List<Map<String, Object>>) mapOfElements.get("bases");
		
		for (Map<String, Object> serializedBase : basesList) {
			BaseBuilder baseBuilder = new BaseBuilder();
			
			Map<String, Integer> spawnLocationMap = (Map<String, Integer>) serializedBase.get("spawn");
			Location spawnLocation = new Location(arenaBuilder.getWorld(), spawnLocationMap.get("x"), spawnLocationMap.get("y"), spawnLocationMap.get("z"));
			baseBuilder.setSpawn(spawnLocation);
			
			Map<String, Integer> bedTopLocationMap = (Map<String, Integer>) serializedBase.get("bedTop");
			Location bedTopLocation = new Location(arenaBuilder.getWorld(), bedTopLocationMap.get("x"), bedTopLocationMap.get("y"), bedTopLocationMap.get("z"));
			baseBuilder.setBedTop(bedTopLocation);
			
			Map<String, Integer> bedBottomLocationMap = (Map<String, Integer>) serializedBase.get("bedBottom");
			Location bedBottomLocation = new Location(arenaBuilder.getWorld(), bedBottomLocationMap.get("x"), bedBottomLocationMap.get("y"), bedBottomLocationMap.get("z"));
			baseBuilder.setBedBottom(bedBottomLocation);
			
			Map<String, Integer> itemShopLocationMap = (Map<String, Integer>) serializedBase.get("itemShop");
			Location itemShopLocation = new Location(arenaBuilder.getWorld(), itemShopLocationMap.get("x"), itemShopLocationMap.get("y"), itemShopLocationMap.get("z"));
			baseBuilder.setItemShop(itemShopLocation);
			
			Map<String, Integer> teamShopLocationMap = (Map<String, Integer>) serializedBase.get("teamShop");
			Location teamShopLocation = new Location(arenaBuilder.getWorld(), teamShopLocationMap.get("x"), teamShopLocationMap.get("y"), teamShopLocationMap.get("z"));
			baseBuilder.setTeamShop(teamShopLocation);
			
			baseBuilder.setTeamColor(TeamColor.valueOf((String) serializedBase.get("teamColor")));
			
			arenaBuilder.addBase(baseBuilder.build());
		}
		
		return arenaBuilder.build();
	}

}
