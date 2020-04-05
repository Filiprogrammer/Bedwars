package filip.bedwars.config;

public interface IConfig {

	String getStringValue(String key);
	
	void setStringValue(String key, String value);
	
	int getIntValue(String key);
	
	void setIntValue(String key, int value);
	
}
