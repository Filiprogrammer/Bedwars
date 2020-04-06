package filip.bedwars.config;

import java.util.HashMap;
import java.util.Map;

public class MessagesConfig extends MultipleConfig {

	private static MessagesConfig instance = null;
	
	private Map<String, String> stringValues = new HashMap<String, String>();
	
	private final static Map<String, String> configFileNames  = new HashMap<String, String>() {{
		put("en", "messages-en.yml");
		put("de", "messages-de.yml");
	}};
	
	private MessagesConfig() {
		super(configFileNames);
		reloadConfig();
	}
	
	public String getStringValue(String key) {
		return stringValues.get(key);
	}

	public void setStringValue(String key, String value) {
		// TODO Ahjo
	}
	
	public static MessagesConfig getInstance() {
		if (instance == null)
			instance = new MessagesConfig();
		
		return instance;
	}
	
	public void reloadConfig() {
		// TODO: load config files
		// TODO: if file doesn't exist generate default config
	}
	
	private void saveConfig() {
		// TODO: Ahjo
	}

}
