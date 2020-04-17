package filip.bedwars.config;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class MainConfig extends SingleConfig {

	private static MainConfig instance = null;
	
	private String language = "en_us";
	private Location mainLobby;
	private Location gameLobby;
	private String gameWorldPrefix = "bw_game_";
	private boolean hunger = false;
	
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
	
	public Location getGameLobby() {
		return gameLobby;
	}
	
	public String getGameWorldPrefix() {
		return gameWorldPrefix;
	}
	
	public boolean getHunger() {
		return hunger;
	}
	
	public void setMainLobby(Location loc) {
		mainLobby = loc;
	}
	
	public void setGameLobby(Location loc) {
		gameLobby = loc;
	}
	
	@Override
	public boolean saveConfig() {
		createAndLoadConfigFileIfNotExistent(true);
		
		config.set("language", language);
		config.set("game-world-prefix", gameWorldPrefix);
		config.set("hunger", hunger);
		ConfigurationSection mainLobbySection = config.getConfigurationSection("main-lobby");
		
		if (mainLobbySection == null)
			mainLobbySection = config.createSection("main-lobby");
		
		mainLobbySection.set("w", mainLobby.getWorld().getName());
		mainLobbySection.set("x", mainLobby.getX());
		mainLobbySection.set("y", mainLobby.getY());
		mainLobbySection.set("z", mainLobby.getZ());
		mainLobbySection.set("yaw", mainLobby.getYaw());
		mainLobbySection.set("pitch", mainLobby.getPitch());
		
		ConfigurationSection gameLobbySection = config.getConfigurationSection("game-lobby");
		
		if (gameLobbySection == null)
			gameLobbySection = config.createSection("game-lobby");
		
		gameLobbySection.set("w", gameLobby.getWorld().getName());
		gameLobbySection.set("x", gameLobby.getX());
		gameLobbySection.set("y", gameLobby.getY());
		gameLobbySection.set("z", gameLobby.getZ());
		gameLobbySection.set("yaw", gameLobby.getYaw());
		gameLobbySection.set("pitch", gameLobby.getPitch());
		
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
		gameWorldPrefix = config.getString("game-world-prefix", "bw_game_").replace("/", "").replace("\\", "");
		hunger = config.getBoolean("hunger", false);
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
		
		ConfigurationSection gameLobbySection = config.getConfigurationSection("game-lobby");
		
		if (gameLobbySection == null)
			gameLobbySection = config.createSection("game-lobby");
			
		String gameLobbyWorld = gameLobbySection.getString("w", "world");
		double gameLobbyX = gameLobbySection.getDouble("x", 0.0);
		double gameLobbyY = gameLobbySection.getDouble("y", 65.0);
		double gameLobbyZ = gameLobbySection.getDouble("z", 0.0);
		float gameLobbyYaw = (float) gameLobbySection.getDouble("yaw", 0.0);
		float gameLobbyPitch = (float) gameLobbySection.getDouble("pitch", 0.0);
		gameLobby = new Location(Bukkit.getWorld(gameLobbyWorld), gameLobbyX, gameLobbyY, gameLobbyZ, gameLobbyYaw, gameLobbyPitch);
	}
	
	public static MainConfig getInstance() {
		if (instance == null)
			instance = new MainConfig();
		
		return instance;
	}

}
