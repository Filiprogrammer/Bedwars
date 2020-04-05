package filip.bedwars.config;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Sound;

public class SoundsConfig implements IConfig {

	private static SoundsConfig instance = null;
	
	Map<String, String> stringValues = new HashMap<String, String>();
	Map<String, Integer> intValues = new HashMap<String, Integer>();
	Map<String, Sound> soundValues = new HashMap<String, Sound>();
	
	private SoundsConfig() {
		// TODO: load config files
		// TODO: if file doesn't exist generate default config
	}
	
	@Override
	public String getStringValue(String key) {
		return stringValues.get(key);
	}

	@Override
	public void setStringValue(String key, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getIntValue(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setIntValue(String key, int value) {
		// TODO Auto-generated method stub
		
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
