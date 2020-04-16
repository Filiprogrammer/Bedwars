package filip.bedwars.game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.arena.Base;
import filip.bedwars.game.lobby.Lobby;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;
import filip.bedwars.world.GameWorld;
import filip.bedwars.world.GameWorldManager;

public class Game implements Listener {

	private Lobby lobby;
	private Arena arena;
	private GameLogic gameLogic;
	private List<UUID> players = new ArrayList<UUID>();
	private List<Team> teams = new ArrayList<Team>();
	private boolean isStarting = false;
	
	public Game(@NotNull Arena arena) {
		this.arena = arena;
		// TODO: Lobby setup and get lobby spawn point from config
		this.lobby = new Lobby(new Location(Bukkit.getWorld("bw_lobby"), 0, 65, 0), this);
		
		for (int i = 0; i < arena.getBases().size(); ++i) {
			Base base = arena.getBase(i);
			teams.add(new Team(i, base, new ArrayList<UUID>()));
		}
		
		BedwarsPlugin.getInstance().getServer().getPluginManager().registerEvents(this, BedwarsPlugin.getInstance());
	}
	
	/**
	 * Starts the game if there are enough players
	 * @return false if not enough players
	 */
	public boolean startGame() {
		if (players.size() < arena.getMinPlayersToStart())
			return false; // Not enough players
		
		// Assign players who didn't choose a team to a team automatically.
		assignLonelyPlayersToTeamsAutomatically();
		
		// Make sure there are at least two teams that contain at least one player.
		int filledTeamsCount = 0;
		
		for (Team team : teams)
			if (team.getMembers().size() > 0)
				++filledTeamsCount;
		
		if (filledTeamsCount < 2) {
			for (Team team : teams)
				team.clearMembers();
			
			assignLonelyPlayersToTeamsAutomatically();
		}
		
		isStarting = true;
		GameWorld gameWorld = GameWorldManager.getInstance().claimGameWorld(arena.getWorld());
		lobby = null;
		gameLogic = new GameLogic(this, arena, gameWorld);
		isStarting = false;
		return true;
	}
	
	/**
	 * Cleans everything up after a game
	 */
	public void endGame() {
		for (UUID uuid : new ArrayList<UUID>(players))
			leavePlayer(Bukkit.getPlayer(uuid));
		
		HandlerList.unregisterAll(this);
		gameLogic.cleanup();
		GameManager.getInstance().removeGame(this);
	}
	
	public boolean isRunning() {
		return (gameLogic != null);
	}
	
	// TODO: Add reconnect function
	
	public void joinPlayer(Player player) {
		if (players.contains(player.getUniqueId())) {
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "already-in-this-game"));
			SoundPlayer.playSound("error", player);
			return; // Player is already in this game 
		}
		
		if (isRunning()) {
			gameLogic.joinSpectator(player);
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "joined-game-as-spectator"));
			SoundPlayer.playSound("success", player);
		} else {
			players.add(player.getUniqueId());
			lobby.joinPlayer(player);
			
			for(UUID uuid : players)
				MessageSender.sendMessageUUID(uuid, MessagesConfig.getInstance().getStringValue(Bukkit.getPlayer(uuid).getLocale(), "player-joined").replace("%player%", player.getName()));
			
			SoundPlayer.playSound("success", player);
		}
	}
	
	public void joinPlayers(Player... players) {
		for (Player player : players)
			joinPlayer(player);
	}
	
	/**
	 * Remove a player or spectator from the game
	 * @param player
	 * @return
	 */
	public boolean leavePlayer(Player player) {
		if (players.remove(player.getUniqueId())) {
			for (Team team : teams)
				if (team.removeMember(player.getUniqueId()))
					break;
			
			if (isRunning())
				gameLogic.leavePlayer(player);
			else
				lobby.leavePlayer(player);
			
			for(UUID uuid : players) 
				MessageSender.sendMessageUUID(uuid, MessagesConfig.getInstance().getStringValue(Bukkit.getPlayer(uuid).getLocale(), "player-left").replace("%player%", player.getName()));
			
			return true;
		} else {
			if (isRunning()) {
				gameLogic.leavePlayer(player);
				return true;
			}
		}
		
		return false;
	}
	
	public Arena getArena() {
		return arena;
	}
	
	public List<Team> getTeams() {
		return teams;
	}
	
	public List<UUID> getPlayers() {
		return players;
	}
	
	private boolean playerHasTeam(UUID uuid) {
		for (Team team : teams)
			if (team.containsMember(uuid))
				return true;
		
		return false;
	}
	
	public Team getTeamOfPlayer(UUID uuid) {
		for (Team team : teams)
			if (team.containsMember(uuid))
				return team;
		
		return null;
	}
	
	private Team getSmallestTeam() {
		Team ret = null;
		
		for(Team team : teams){
			if(ret == null) {
				ret = team;
				continue;
			}
			
			if(team.getMembers().size() < ret.getMembers().size())
				ret = team;
		}
		
		return ret;
	}
	
	private void assignLonelyPlayersToTeamsAutomatically() {
		for (UUID uuid : players) {
			if (!playerHasTeam(uuid)) {
				Team team = getSmallestTeam();
				team.addMember(uuid);
				
				String colorConfigKey = "color-" + team.getBase().getTeamColor().toString().toLowerCase().replace("_", "-");
				MessagesConfig msgConfig = MessagesConfig.getInstance();
				Player player = Bukkit.getPlayer(uuid);
				String colorStr = msgConfig.getStringValue(player.getLocale(), colorConfigKey);
				MessageSender.sendMessageUUID(uuid, msgConfig.getStringValue(player.getLocale(), "team-changed").replace("%teamcolor%", colorStr));
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if (players.contains(player.getUniqueId()))
				leavePlayer(player);
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		
		if (players.contains(player.getUniqueId())) {
			if (isStarting)
				return;
			
			if (isRunning()) {
				if (!player.getWorld().getName().equals(gameLogic.getGameWorld().getWorld().getName()))
					leavePlayer(player); // Player left the game world and therefore leaves the game
			} else if (!player.getWorld().getName().equals(lobby.getSpawnPoint().getWorld().getName())) {
				leavePlayer(player); // Player left the game lobby and therefore leaves the game
			}
		} else {
			if (isRunning()) {
				if (player.getWorld().getName().equals(gameLogic.getGameWorld().getWorld().getName()))
					gameLogic.joinSpectator(player);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if (players.contains(player.getUniqueId())) {
			if (isStarting) {
				event.setCancelled(true);
				return;
			}
			
			if (isRunning()) {
				
			} else if (!player.getWorld().getName().equals(lobby.getSpawnPoint().getWorld().getName())) {
				if (player.getGameMode() != GameMode.CREATIVE)
					event.setCancelled(true);
			}
		} else {
			if (isRunning()) {
				if (player.getWorld().getName().equals(gameLogic.getGameWorld().getWorld().getName()))
					event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
		Player player = event.getPlayer();
		
		if (players.contains(player.getUniqueId())) {
			if (isStarting || isRunning())
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(EntityPickupItemEvent event) {
		if (event.getEntityType() != EntityType.PLAYER)
			return;
		
		Player player = (Player) event.getEntity();
		
		if (!players.contains(player.getUniqueId())) {
			if (isRunning()) {
				if (player.getWorld().getName().equals(gameLogic.getGameWorld().getWorld().getName()))
					event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager().getType() != EntityType.PLAYER)
			return;
		
		Player damager = (Player) event.getDamager();
		
		if (players.contains(damager.getUniqueId())) {
			if (event.getEntity().getType() != EntityType.PLAYER)
				return;
			
			Player player = (Player) event.getEntity();
			
			if (getTeamOfPlayer(damager.getUniqueId()) == getTeamOfPlayer(player.getUniqueId()))
				event.setCancelled(true);
		} else {
			if (isRunning()) {
				if (damager.getWorld().getName().equals(gameLogic.getGameWorld().getWorld().getName()))
					event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		
		if (!players.contains(player.getUniqueId())) {
			if (isRunning()) {
				if (player.getWorld().getName().equals(gameLogic.getGameWorld().getWorld().getName()))
					event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntityType() != EntityType.PLAYER)
			return;
		
		Player player = (Player) event.getEntity();
		
		if (!players.contains(player.getUniqueId())) {
			if (isRunning()) {
				if (player.getWorld().getName().equals(gameLogic.getGameWorld().getWorld().getName()))
					event.setCancelled(true);
			}
		}
	}
	
}
