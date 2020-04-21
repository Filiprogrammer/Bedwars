package filip.bedwars.config;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import filip.bedwars.utils.MessageSender;

public class MainConfig extends SingleConfig {

	private static MainConfig instance = null;
	
	private String language = "en_us";
	private Location mainLobby;
	private Location gameLobby;
	private int gameLobbyCountdown;
	private String gameWorldPrefix = "bw_game_";
	private boolean hunger = false;
	private String itemShopName = "§2Item Shop";
	private String teamShopName = "§1Team Shop";
	private String baseSpawnPointName = "§dSpawn-Point";
	private String[] joinSignLines = new String[4];
	
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
	
	public int getGameLobbyCountdown() {
		return gameLobbyCountdown;
	}
	
	public String getGameWorldPrefix() {
		return gameWorldPrefix;
	}
	
	public boolean getHunger() {
		return hunger;
	}
	
	public String getItemShopName() {
		return itemShopName;
	}
	
	public String getTeamShopName() {
		return teamShopName;
	}
	
	public String getBaseSpawnPointName() {
		return baseSpawnPointName;
	}
	
	public String getJoinSignLine(int line) {
		if(line < 0 || line > joinSignLines.length - 1)
			return null;
		
		return joinSignLines[line];
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
		config.set("game-lobby-countdown", gameLobbyCountdown);
		config.set("game-world-prefix", gameWorldPrefix);
		config.set("hunger", hunger);
		config.set("item-shop-name", itemShopName.replace('§', '&'));
		config.set("team-shop-name", teamShopName.replace('§', '&'));
		config.set("base-spawn-point-name", baseSpawnPointName.replace('§', '&'));
		
		config.set("join-sign-line-one", joinSignLines[0].replace('§', '&'));
		config.set("join-sign-line-two", joinSignLines[1].replace('§', '&'));
		config.set("join-sign-line-three", joinSignLines[2].replace('§', '&'));
		config.set("join-sign-line-four", joinSignLines[3].replace('§', '&'));
		
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
		gameLobbyCountdown = config.getInt("game-lobby-countdown", 60);
		gameWorldPrefix = config.getString("game-world-prefix", "bw_game_").replace("/", "").replace("\\", "");
		hunger = config.getBoolean("hunger", false);
		itemShopName = config.getString("item-shop-name", "&2Item Shop").replace('&', '§');
		teamShopName = config.getString("team-shop-name", "&1Team Shop").replace('&', '§');
		baseSpawnPointName = config.getString("base-spawn-point-name", "&dSpawn-Point").replace('&', '§');
		
		joinSignLines[0] = config.getString("join-sign-line-one", "&a[BedWars]").replace('&', '§');
		joinSignLines[1] = config.getString("join-sign-line-two", "&d%arenaname%").replace('&', '§');
		joinSignLines[2] = config.getString("join-sign-line-three", "").replace('&', '§');
		joinSignLines[3] = config.getString("join-sign-line-four", "&b<Join Arena>").replace('&', '§');
		
		if (itemShopName.length() > 16) {
			MessageSender.sendWarning("item-shop-name must not be longer than 16 characters. Check your config.yml!");
			itemShopName = "§2Item Shop";
		}
		
		if (teamShopName.length() > 16) {
			MessageSender.sendWarning("team-shop-name must not be longer than 16 characters. Check your config.yml!");
			teamShopName = "§1Team Shop";
		}
		
		if (baseSpawnPointName.length() > 16) {
			MessageSender.sendWarning("base-spawn-point-name must not be longer than 16 characters. Check your config.yml!");
			baseSpawnPointName = "§dSpawn-Point";
		}
		
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
