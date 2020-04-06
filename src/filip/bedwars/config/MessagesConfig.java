package filip.bedwars.config;

import java.util.HashMap;
import java.util.Map;

public class MessagesConfig extends Config {

	private static MessagesConfig instance = null;
	
	private Map<String, String> stringValues = new HashMap<String, String>();
	
	private MessagesConfig() {
		// TODO: add a MainConfig class and get the language from that class
		super("messages-en.yml");
		reloadConfigFile();
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
	
	public void reloadConfigFile() {
		// TODO: load config files
		// TODO: if file doesn't exist generate default config
	}
	
	private void saveConfigFile() {
		// TODO: Ahjo
	}

}
