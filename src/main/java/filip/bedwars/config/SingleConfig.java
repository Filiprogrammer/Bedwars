package filip.bedwars.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import filip.bedwars.BedwarsPlugin;

public abstract class SingleConfig implements IConfig {
	
	private String configFileName = null;
	protected File configFile = null;
	protected YamlConfiguration config = null;
	
	protected SingleConfig(String configFileName) {
		this.configFileName = configFileName;
	}
	
	protected boolean createAndLoadConfigFileIfNotExistent(boolean empty) {
		File dataFolder = BedwarsPlugin.getInstance().getDataFolder();
		
		if (!dataFolder.exists())
			dataFolder.mkdir();
		
		// Only one config file is used
		if (configFile == null)
			configFile = new File(BedwarsPlugin.getInstance().getDataFolder(), configFileName);
		
		if (!configFile.exists()) {
			if (empty) {
				try {
					configFile.createNewFile();
				} catch (IOException e) {
					return false;
				}
			} else {
				BedwarsPlugin.getInstance().saveResource(configFileName, false);
			}
		}
		
		config = YamlConfiguration.loadConfiguration(configFile);
		
		return true;
	}
	
}
