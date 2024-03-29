package filip.bedwars.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.configuration.file.YamlConfiguration;

public class MessagesConfig extends MultipleConfig {

	private static MessagesConfig instance = null;
	
	private Map<String, Map<String, String>> messages = new HashMap<String, Map<String, String>>();
	
	@SuppressWarnings("serial")
	private final static Map<String, String> configFileNames = new HashMap<String, String>() {{
		put("en_au", "messages-en.yml");
		put("en_ca", "messages-en.yml");
		put("en_gb", "messages-en.yml");
		put("en_nz", "messages-en.yml");
		put("en_7s", "messages-en.yml");
		put("en_ud", "messages-en.yml");
		put("en_us", "messages-en.yml");
		put("enp",   "messages-en.yml");
		put("en_ws", "messages-en.yml");
		put("de_at", "messages-de.yml");
		put("de_ch", "messages-de.yml");
		put("de_de", "messages-de.yml");
	}};
	
	private MessagesConfig() {
		super(configFileNames);
		reloadConfig();
	}
	
	public String getStringValue(String language, String key) {
		Map<String, String> msgs = messages.get(language);
		String ret = null;
		
		if (msgs == null)
			ret = messages.get(MainConfig.getInstance().getLanguage()).get(key); // If the language was not found, use the default language from the config
		else
			ret = msgs.get(key);
		
		if (ret == null)
			ret = "";
		
		return ret;
	}
	
	public static MessagesConfig getInstance() {
		if (instance == null)
			instance = new MessagesConfig();
		
		return instance;
	}
	
	public void reloadConfig() {
		createAndLoadConfigFileIfNotExistent(false);
		
		for (String langKey : configs.keySet()) {
			Map<String, String> msgs = new HashMap<String, String>();
			messages.put(langKey, msgs);
			
			YamlConfiguration config = configs.get(langKey);
			Set<String> keys = config.getKeys(false);
			
			for (String key : keys) {
				String msg = config.getString(key).replace('&', '§');
				String msgOut = msg;
		        Matcher matcher = Pattern.compile("\\\\u\\d{1,4}").matcher(msg);
				
				while (matcher.find()) {
				    String str = msg.substring(matcher.start(), matcher.end());
				    char c = (char) Integer.parseInt(str.substring(2, str.length()), 16);
				    msgOut = msgOut.replace(str, "" + c);
				}
				
				msgs.put(key, msgOut);
			}
		}
	}
	
	public boolean saveConfig() {
		if (!createAndLoadConfigFileIfNotExistent(true))
			return false;
		
		for (String langKey : messages.keySet()) {
			Map<String, String> msgs = messages.get(langKey);
			
			Set<String> keys = msgs.keySet();
			YamlConfiguration config = configs.get(langKey);
			
			for (String key : keys)
				config.set(key, msgs.get(key));
			
			try {
				config.save(configFiles.get(langKey));
			} catch (IOException e) {
				return false;
			}
		}
		
		return true;
	}

}
