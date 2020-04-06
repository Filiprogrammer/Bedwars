package filip.bedwars.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.arena.Base;
import filip.bedwars.game.arena.Spawner;

public class ArenaSerializer {
	
	@SuppressWarnings("serial")
	public static Map<String, Object> serializeArena(Arena arena) {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("mapName", arena.getMapName());
		ret.put("world", arena.getWorld().getName());
		
		List<Map<String, Object>> spawnerList = new ArrayList<Map<String, Object>>();
		
		for (Spawner spawner : arena.getSpawner()) {
			Map<String, Object> spawnerInfo = new HashMap<String, Object>() {{
				put("ticksPerSpawn", spawner.getTicksPerSpawn());
				put("location", new HashMap<String, Integer>() {{
					put("x", spawner.getLocation().getBlockX());
					put("y", spawner.getLocation().getBlockY());
					put("z", spawner.getLocation().getBlockZ());
				}});
				put("itemStack", spawner.getItemStack());
			}};
			spawnerList.add(spawnerInfo);
		}
		
		ret.put("spawner", spawnerList);
		
		List<Map<String, Object>> baseList = new ArrayList<Map<String, Object>>();
		
		for (Base base : arena.getBases()) {
			Map<String, Object> baseInfo = new HashMap<String, Object>() {{
				put("spawn", new HashMap<String, Integer>() {{
					put("x", base.getSpawn().getBlockX());
					put("y", base.getSpawn().getBlockY());
					put("z", base.getSpawn().getBlockZ());
				}});
				put("teamColor", base.getTeamColor());
				put("bedBottom", new HashMap<String, Integer>() {{
					put("x", base.getBedBottom().getBlockX());
					put("y", base.getBedBottom().getBlockY());
					put("z", base.getBedBottom().getBlockZ());
				}});
				put("bedTop", new HashMap<String, Integer>() {{
					put("x", base.getBedTop().getBlockX());
					put("y", base.getBedTop().getBlockY());
					put("z", base.getBedTop().getBlockZ());
				}});
				put("itemShop", new HashMap<String, Integer>() {{
					put("x", base.getItemShop().getBlockX());
					put("y", base.getItemShop().getBlockY());
					put("z", base.getItemShop().getBlockZ());
				}});
				put("teamShop", new HashMap<String, Integer>() {{
					put("x", base.getTeamShop().getBlockX());
					put("y", base.getTeamShop().getBlockY());
					put("z", base.getTeamShop().getBlockZ());
				}});
			}};
			baseList.add(baseInfo);
		}
		
		ret.put("bases", baseList);
		
		return ret;
	}
	
}
