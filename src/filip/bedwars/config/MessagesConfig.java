package filip.bedwars.config;

import java.util.HashMap;
import java.util.Map;

public class MessagesConfig {

	private static MessagesConfig instance = null;
	
	private Map<String, String> stringValues = new HashMap<String, String>();
	
	private MessagesConfig() {
		// TODO: load config files
		// TODO: if file doesn't exist generate default config
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

}
