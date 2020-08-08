package filip.bedwars.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

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
	private boolean dropOnlySpawnerResourcesOnDeath = true;
	private boolean attackCooldown = false;
	private boolean lobbyBossbar = true;
	private BarColor lobbyBossbarColor = BarColor.YELLOW;
	private int respawnDelay = 5;
	private boolean bedwarsChat = true;
	private int lobbySkipCountdown = 5;
	private List<ItemStack> spawnItems;
	
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
	
	public boolean getDropOnlySpawnerResourcesOnDeath() {
		return dropOnlySpawnerResourcesOnDeath;
	}
	
	public boolean getAttackCooldown() {
		return attackCooldown;
	}
	
	public boolean getLobbyBossBar() {
		return lobbyBossbar;
	}
	
	public BarColor getLobbyBossBarColor() {
		return lobbyBossbarColor;
	}
	
	public int getRespawnDelay() {
		return respawnDelay;
	}
	
	public boolean getBedwarsChat() {
		return bedwarsChat;
	}
	
	public int getLobbySkipCountdown() {
		return lobbySkipCountdown;
	}
	
	public List<ItemStack> getSpawnItems() {
		return spawnItems;
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
		config.set("item-shop-name", itemShopName.replace('§', '&'));
		config.set("team-shop-name", teamShopName.replace('§', '&'));
		config.set("base-spawn-point-name", baseSpawnPointName.replace('§', '&'));
		config.set("join-sign-line-one", joinSignLines[0].replace('§', '&'));
		config.set("join-sign-line-two", joinSignLines[1].replace('§', '&'));
		config.set("join-sign-line-three", joinSignLines[2].replace('§', '&'));
		config.set("join-sign-line-four", joinSignLines[3].replace('§', '&'));
		config.set("lobby-bossbar", lobbyBossbar);
		config.set("lobby-bossbar-color", lobbyBossbarColor.toString());
		config.set("hunger", hunger);
		config.set("drop-only-spawner-resources-on-death", dropOnlySpawnerResourcesOnDeath);
		config.set("attack-cooldown", attackCooldown);
		config.set("respawn-delay", respawnDelay);
		config.set("bedwars-chat", bedwarsChat);
		config.set("lobby-skip-countdown", lobbySkipCountdown);
		config.set("spawn-items", spawnItems);
		
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

	@SuppressWarnings("unchecked")
	@Override
	public void reloadConfig() {
		createAndLoadConfigFileIfNotExistent(false);
		
		language = config.getString("language", "en_us");
		gameLobbyCountdown = config.getInt("game-lobby-countdown", 60);
		gameWorldPrefix = config.getString("game-world-prefix", "bw_game_").replace("/", "").replace("\\", "");
		itemShopName = config.getString("item-shop-name", "&2Item Shop").replace('&', '§');
		teamShopName = config.getString("team-shop-name", "&1Team Shop").replace('&', '§');
		baseSpawnPointName = config.getString("base-spawn-point-name", "&dSpawn-Point").replace('&', '§');
		joinSignLines[0] = config.getString("join-sign-line-one", "&1[BedWars]").replace('&', '§');
		joinSignLines[1] = config.getString("join-sign-line-two", "&5%arenaname%").replace('&', '§');
		joinSignLines[2] = config.getString("join-sign-line-three", "").replace('&', '§');
		joinSignLines[3] = config.getString("join-sign-line-four", "&1<Join Arena>").replace('&', '§');
		lobbyBossbar = config.getBoolean("lobby-bossbar", true);
		respawnDelay = config.getInt("respawn-delay", 5);
		bedwarsChat = config.getBoolean("bedwars-chat", true);
		lobbySkipCountdown = config.getInt("lobby-skip-countdown", 5);
		List<?> spawnItemsList = config.getList("spawn-items");
		
		if (spawnItemsList.size() > 0 && spawnItemsList.get(0) instanceof ItemStack)
			spawnItems = (List<ItemStack>) spawnItemsList;
		else
			spawnItems = new ArrayList<ItemStack>();
		
		try {
			lobbyBossbarColor = BarColor.valueOf(config.getString("lobby-bossbar-color", "YELLOW"));
		} catch (IllegalArgumentException e) {
			MessageSender.sendWarning("lobby-bossbar-color has an invalid value.");
		}
		
		hunger = config.getBoolean("hunger", false);
		dropOnlySpawnerResourcesOnDeath = config.getBoolean("drop-only-spawner-resources-on-death", true);
		hunger = config.getBoolean("attack-cooldown", false);
		
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
