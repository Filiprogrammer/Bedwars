package filip.bedwars.config;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Sound;

public class SoundsConfig {

	private static SoundsConfig instance = null;
	
	private Map<String, Sound> soundValues = new HashMap<String, Sound>();
	
	private SoundsConfig() {
		// TODO: load config files
		// TODO: if file doesn't exist generate default config
	}
	
	public Sound getSoundValue(String key) {
		return soundValues.get(key);
	}
	
	public void setSoundValue(String key, Sound value) {
		// TODO Ahjo
	}
	
	public static SoundsConfig getInstance() {
		if (instance == null)
			instance = new SoundsConfig();
		
		return instance;
	}

}
