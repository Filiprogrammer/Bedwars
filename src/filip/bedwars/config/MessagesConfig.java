package filip.bedwars.config;

import java.util.HashMap;
import java.util.Map;

public class MessagesConfig implements IConfig {

	private static MessagesConfig instance = null;
	
	Map<String, String> stringValues = new HashMap<String, String>();
	Map<String, Integer> intValues = new HashMap<String, Integer>();
	
	private MessagesConfig() {
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
	
	public static MessagesConfig getInstance() {
		if (instance == null)
			instance = new MessagesConfig();
		
		return instance;
	}

}
