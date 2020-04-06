package filip.bedwars.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import filip.bedwars.BedwarsPlugin;

public abstract class MultipleConfig implements IConfig {

	protected Map<String, String> configFileNames = null;
	protected Map<String, File> configFiles = null;
	protected Map<String, YamlConfiguration> configs = null;
	
	protected MultipleConfig(Map<String, String> configFileNames) {
		this.configFileNames = configFileNames;
	}
	
	protected boolean createAndLoadConfigFileIfNotExistent(boolean empty) {
		File dataFolder = BedwarsPlugin.getInstance().getDataFolder();
		
		if (!dataFolder.exists())
			dataFolder.mkdir();
		
		if (configFiles == null) {
			configFiles = new HashMap<String, File>();
			
			for (String key : configFileNames.keySet())
				configFiles.put(key, new File(BedwarsPlugin.getInstance().getDataFolder(), configFileNames.get(key)));
		}
		
		for (String key : configFiles.keySet()) {
			File configFile = configFiles.get(key);
			
			if (!configFile.exists()) {
				if (empty) {
					try {
						configFile.createNewFile();
					} catch (IOException e) {
						return false;
					}
				} else {
					BedwarsPlugin.getInstance().saveResource(configFileNames.get(key), false);
				}
			}
			
			configs.put(key, YamlConfiguration.loadConfiguration(configFile));
		}
		
		return true;
	}

}
