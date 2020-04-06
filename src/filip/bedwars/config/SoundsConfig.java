package filip.bedwars.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Sound;

public class SoundsConfig extends SingleConfig {

	private static SoundsConfig instance = null;
	
	private Map<String, Sound> soundValues = new HashMap<String, Sound>();
	
	private SoundsConfig() {
		super("sounds.yml");
		reloadConfig();
	}
	
	public Sound getSoundValue(String key) {
		return soundValues.get(key);
	}
	
	public void setSoundValue(String key, Sound value) {
		soundValues.put(key, value);
	}
	
	public static SoundsConfig getInstance() {
		if (instance == null)
			instance = new SoundsConfig();
		
		return instance;
	}
	
	public boolean saveConfig() {
		if (!createAndLoadConfigFileIfNotExistent(true))
			return false;
		
		Set<String> keys = soundValues.keySet();
		
		for (String key : keys)
			config.set(key, soundValues.get(key).toString());
		
		try {
			config.save(configFile);
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
	public void reloadConfig() {
		createAndLoadConfigFileIfNotExistent(false);
		
		Set<String> keys = config.getKeys(false);
		
		for (String key : keys) {
			Sound sound = Sound.valueOf(config.getString(key));
			soundValues.put(key, sound);
		}
	}

}
