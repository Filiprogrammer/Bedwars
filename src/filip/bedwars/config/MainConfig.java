package filip.bedwars.config;

import java.io.IOException;

public class MainConfig extends SingleConfig {

	private static MainConfig instance = null;
	
	String language = "en_us";
	
	protected MainConfig() {
		super("config.yml");
		reloadConfig();
	}

	public String getLanguage() {
		return language;
	}
	
	@Override
	public boolean saveConfig() {
		createAndLoadConfigFileIfNotExistent(true);
		
		config.set("language", language);
		
		try {
			config.save(configFile);
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}

	@Override
	public void reloadConfig() {
		createAndLoadConfigFileIfNotExistent(false);
		
		language = config.getString("language");
	}
	
	public static MainConfig getInstance() {
		if (instance == null)
			instance = new MainConfig();
		
		return instance;
	}

}
