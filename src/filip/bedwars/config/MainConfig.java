package filip.bedwars.config;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class MainConfig extends SingleConfig {

	private static MainConfig instance = null;
	
	private String language = "en_us";
	private Location mainLobby;
	private String gameWorldPrefix = "bw_game_";
	
	protected MainConfig() {
		super("config.yml");
		reloadConfig();
	}

	public String getLanguage() {
		return language;
	}
	
	public Location getMainLobby() {
		return mainLobby;
	}
	
	public String getGameWorldPrefix() {
		return gameWorldPrefix;
	}
	
	@Override
	public boolean saveConfig() {
		createAndLoadConfigFileIfNotExistent(true);
		
		config.set("language", language);
		config.set("game-world-prefix", gameWorldPrefix);
		ConfigurationSection mainLobbySection = config.getConfigurationSection("main-lobby");
		
		if (mainLobbySection == null)
			mainLobbySection = config.createSection("main-lobby");
		
		mainLobbySection.set("w", mainLobby.getWorld().getName());
		mainLobbySection.set("x", mainLobby.getX());
		mainLobbySection.set("y", mainLobby.getY());
		mainLobbySection.set("z", mainLobby.getZ());
		mainLobbySection.set("yaw", mainLobby.getYaw());
		mainLobbySection.set("pitch", mainLobby.getPitch());
		
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
		
		language = config.getString("language", "en_us");
		gameWorldPrefix = config.getString("game-world-prefix", "bw_game_");
		ConfigurationSection mainLobbySection = config.getConfigurationSection("main-lobby");
		
		if (mainLobbySection == null)
			mainLobbySection = config.createSection("main-lobby");
			
		String mainLobbyWorld = mainLobbySection.getString("w", "world");
		double mainLobbyX = mainLobbySection.getDouble("x", 0.0);
		double mainLobbyY = mainLobbySection.getDouble("y", 65.0);
		double mainLobbyZ = mainLobbySection.getDouble("z", 0.0);
		float mainLobbyYaw = (float) mainLobbySection.getDouble("yaw", 0.0);
		float mainLobbyPitch = (float) mainLobbySection.getDouble("pitch", 0.0);
		mainLobby = new Location(Bukkit.getWorld(mainLobbyWorld), mainLobbyX, mainLobbyY, mainLobbyZ, mainLobbyYaw, mainLobbyPitch);
	}
	
	public static MainConfig getInstance() {
		if (instance == null)
			instance = new MainConfig();
		
		return instance;
	}

}
