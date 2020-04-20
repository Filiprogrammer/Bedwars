package filip.bedwars.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import filip.bedwars.sign.GameJoinSign;

public class JoinSignConfig extends SingleConfig {

	private static JoinSignConfig instance = null;
	
	List<GameJoinSign> joinSigns = new ArrayList<GameJoinSign>();
	
	private JoinSignConfig() {
		super("joinsignes.yml");
		reloadConfig();
	}

	public List<GameJoinSign> getGameJoinSigns() {
		return joinSigns;
	}
	
	public GameJoinSign getGameJoinSignAt(Location joinSignLoc) {
		for (GameJoinSign js : joinSigns) {
			Location jsLoc = js.getLocation();
			
			if (jsLoc.getBlockX() == joinSignLoc.getBlockX()
			 && jsLoc.getBlockY() == joinSignLoc.getBlockY()
			 && jsLoc.getBlockZ() == joinSignLoc.getBlockZ()
			 && jsLoc.getWorld().getName().equals(joinSignLoc.getWorld().getName())) {
				return js;
			}
		}
		
		return null;
	}
	
	public void addJoinSign(GameJoinSign gameJoinSign) {
		joinSigns.add(gameJoinSign);
	}
	
	public boolean removeJoinSign(GameJoinSign gameJoinSign) {
		return joinSigns.remove(gameJoinSign);
	}
	
	public static JoinSignConfig getInstance() {
		if (instance == null)
			instance = new JoinSignConfig();
		
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public void reloadConfig() {
		createAndLoadConfigFileIfNotExistent(true);
		
		joinSigns = new ArrayList<GameJoinSign>();
		
		if (config.isList("joinsigns")) {
			List<Map<String, Object>> serializedJoinSigns = (List<Map<String, Object>>) config.getList("joinsigns");
			
			for (Map<String, Object> serializedJoinSign : serializedJoinSigns) {
				String mapName = (String) serializedJoinSign.get("mapName");
				Map<String, Object> serializedLocation = (Map<String, Object>) serializedJoinSign.get("location");
				Location location = new Location(
						Bukkit.getWorld((String) serializedLocation.get("w")),
						(int) serializedLocation.get("x"),
						(int) serializedLocation.get("y"),
						(int) serializedLocation.get("z"));
				
				joinSigns.add(new GameJoinSign(location, mapName));
			}
		}
	}
	
	public boolean saveConfig() {
		createAndLoadConfigFileIfNotExistent(true);
		
		List<Map<String, Object>> serializedJoinSigns = new ArrayList<Map<String, Object>>();
		
		for (GameJoinSign joinSign : joinSigns) {
			Map<String, Object> serializedJoinSign = new HashMap<String, Object>();
			serializedJoinSign.put("mapName", joinSign.getMapName());
			Map<String, Object> serializedLocation = new HashMap<String, Object>();
			serializedLocation.put("x", joinSign.getLocation().getBlockX());
			serializedLocation.put("y", joinSign.getLocation().getBlockY());
			serializedLocation.put("z", joinSign.getLocation().getBlockZ());
			serializedLocation.put("w", joinSign.getLocation().getWorld().getName());
			serializedJoinSigns.add(serializedJoinSign);
		}
		
		config.set("joinsigns", serializedJoinSigns);
		
		try {
			config.save(configFile);
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
}
