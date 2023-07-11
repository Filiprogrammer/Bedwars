package filip.bedwars.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundSetting;

public class SoundsConfig extends SingleConfig {

	private static SoundsConfig instance = null;
	
	private Map<String, SoundSetting> soundValues = new HashMap<String, SoundSetting>();
	
	private SoundsConfig() {
		super("sounds.yml");
		reloadConfig();
	}
	
	public SoundSetting getSoundValue(String key) {
		return soundValues.get(key);
	}
	
	public void setSoundValue(String key, SoundSetting value) {
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
		
		for (String key : keys) {
			ConfigurationSection section = config.createSection(key);
			section.set("sound", soundValues.get(key).getSound().toString());
			section.set("pitch", soundValues.get(key).getPitch());
			section.set("volume", soundValues.get(key).getVolume());
		}
		
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
			ConfigurationSection section = config.getConfigurationSection(key);
			
			Sound sound = null;
			
			try {
				String soundStr = section.getString("sound", null);
				
				if (soundStr != null)
					sound = Sound.valueOf(section.getString("sound")); // if no sound is specified in the config, then play no sound
			} catch(Exception e) {
				// if the inputted sound IS NOT a sound, print a warning in the console
				MessageSender.sendWarning("The inputted sound for §c" + key + " §ewas not found! Please check your sounds.yml! Be sure to only use the correct minecraft-spigot-sounds from this page: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html");
			}
			
			float pitch = (float) section.getDouble("pitch", 1.0);
			float volume = (float) section.getDouble("volume", 1.0);
			soundValues.put(key, new SoundSetting(sound, pitch, volume));
		}
	}

}
