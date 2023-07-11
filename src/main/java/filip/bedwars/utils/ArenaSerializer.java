package filip.bedwars.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.arena.Base;
import filip.bedwars.game.arena.Spawner;

public class ArenaSerializer {
	
	@SuppressWarnings("serial")
	public static Map<String, Object> serializeArena(Arena arena) {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("mapName", arena.getMapName());
		ret.put("minPlayersToStart", arena.getMinPlayersToStart());
		ret.put("playersPerTeam", arena.getPlayersPerTeam());
		ret.put("world", arena.getWorld().getName());
		
		Location spectatorSpawn = arena.getSpectatorSpawn(null);
		
		if (spectatorSpawn != null) {
			ret.put("spectatorSpawn", new HashMap<String, Double>() {{
				put("x", spectatorSpawn.getX());
				put("y", spectatorSpawn.getY());
				put("z", spectatorSpawn.getZ());
				put("yaw", (double) spectatorSpawn.getYaw());
				put("pitch", (double) spectatorSpawn.getPitch());
			}});
		}
		
		List<Map<String, Object>> spawnerList = new ArrayList<Map<String, Object>>();
		
		for (Spawner spawner : arena.getSpawner()) {
			Map<String, Object> spawnerInfo = new HashMap<String, Object>() {{
				put("ticksPerSpawn", spawner.getTicksPerSpawn());
				put("location", new HashMap<String, Integer>() {{
					Location loc = spawner.getLocation(null);
					put("x", loc.getBlockX());
					put("y", loc.getBlockY());
					put("z", loc.getBlockZ());
				}});
				put("itemMaterial", spawner.getItemStack().getType().toString());
				put("itemName", spawner.getItemStack().getItemMeta().getDisplayName().replace('§', '&'));
			}};
			spawnerList.add(spawnerInfo);
		}
		
		ret.put("spawner", spawnerList);
		
		List<Map<String, Object>> baseList = new ArrayList<Map<String, Object>>();
		
		for (Base base : arena.getBases()) {
			Map<String, Object> baseInfo = new HashMap<String, Object>() {{
				put("spawn", new HashMap<String, Integer>() {{
					Location loc = base.getSpawn(null);
					put("x", loc.getBlockX());
					put("y", loc.getBlockY());
					put("z", loc.getBlockZ());
					put("yaw", (int) loc.getYaw());
					put("pitch", (int) loc.getPitch());
				}});
				put("teamColor", base.getTeamColor().toString());
				put("bedBottom", new HashMap<String, Integer>() {{
					Location loc = base.getBedBottom(null);
					put("x", loc.getBlockX());
					put("y", loc.getBlockY());
					put("z", loc.getBlockZ());
				}});
				put("bedTop", new HashMap<String, Integer>() {{
					Location loc = base.getBedTop(null);
					put("x", loc.getBlockX());
					put("y", loc.getBlockY());
					put("z", loc.getBlockZ());
				}});
				put("itemShop", new HashMap<String, Integer>() {{
					Location loc = base.getItemShop(null);
					put("x", loc.getBlockX());
					put("y", loc.getBlockY());
					put("z", loc.getBlockZ());
				}});
				
				if(base.getTeamShop(null) != null) { // only write the team shop if it exists
					put("teamShop", new HashMap<String, Integer>() {{
						Location loc = base.getTeamShop(null);
						put("x", loc.getBlockX());
						put("y", loc.getBlockY());
						put("z", loc.getBlockZ());
					}});
				}
			}};
			baseList.add(baseInfo);
		}
		
		ret.put("bases", baseList);
		
		return ret;
	}
	
}
