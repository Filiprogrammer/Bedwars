package filip.bedwars.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.arena.Base;
import filip.bedwars.game.lobby.Lobby;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;
import filip.bedwars.utils.TeamColorConverter;
import filip.bedwars.world.GameWorld;
import filip.bedwars.world.GameWorldManager;

public class Game implements Listener {

	private Lobby lobby;
	private Arena arena;
	private GameLogic gameLogic;
	private List<GamePlayer> players = Collections.synchronizedList(new ArrayList<GamePlayer>());
	private List<Team> teams = Collections.synchronizedList(new ArrayList<Team>());
	private boolean isStarting = false;
	
	public Game(@NotNull Arena arena) {
		this.arena = arena.clone();
		this.lobby = new Lobby(MainConfig.getInstance().getGameLobby(), this);
		
		for (int i = 0; i < arena.getBases().size(); ++i) {
			Base base = arena.getBase(i);
			teams.add(new Team(i, base));
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
		
		lobby.cleanup();
		
		// Assign players who didn't choose a team to a team automatically.
		assignLonelyPlayersToTeamsAutomatically();
		
		// Make sure there are at least two teams that contain at least one player.
		int filledTeamsCount = 0;
		
		synchronized (teams) {
			for (Team team : teams)
				if (team.getMembers().size() > 0)
					++filledTeamsCount;
		}
		
		if (filledTeamsCount < 2) {
			synchronized (teams) {
				for (Team team : teams)
					team.clearMembers();
			}
			
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
		for (GamePlayer gamePlayer : new ArrayList<GamePlayer>(players))
			leavePlayer(gamePlayer.getPlayer());
		
		HandlerList.unregisterAll(this);
		
		if (gameLogic != null)
			gameLogic.cleanup();
		
		GameManager.getInstance().removeGame(this);
	}
	
	public boolean isRunning() {
		return (gameLogic != null);
	}
	
	public Team isOver() {
		List<Team> aliveTeams = new ArrayList<Team>();
		
		synchronized (teams) {
			for (Team team : teams)
				if (team.getMembers().size() != 0)
					aliveTeams.add(team);
		}
		
		if (aliveTeams.size() == 1)
			return aliveTeams.get(0);
		
		return null;
	}
	
	// TODO: Add reconnect function
	
	public void joinPlayer(Player player) {
		if (containsPlayer(player.getUniqueId())) {
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "already-in-this-game"));
			SoundPlayer.playSound("error", player);
			return; // Player is already in this game 
		}
		
		if (isRunning()) {
			gameLogic.joinSpectator(player);
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "joined-game-as-spectator"));
			SoundPlayer.playSound("success", player);
		} else {
			players.add(new GamePlayer(player.getUniqueId(), this));
			lobby.joinPlayer(player);
			
			for(GamePlayer gp : players) {
				Player p = gp.getPlayer();
				MessageSender.sendMessage(p, MessagesConfig.getInstance().getStringValue(p.getLocale(), "player-joined").replace("%player%", player.getName()));
			}
			
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
		Optional<GamePlayer> gamePlayerOptional = players.stream().filter(gp -> gp.uuid.equals(player.getUniqueId())).findFirst().map(gp -> {
			players.remove(gp);
			return gp;
		});
		
		if (gamePlayerOptional.isPresent()) {
			GamePlayer gamePlayer = gamePlayerOptional.get();
			
			synchronized (teams) {
				for (Team team : teams)
					if (team.removeMember(gamePlayer)) {
						if (team.getMembers().size() == 0 && isRunning())
							team.destroyBed(gameLogic.getGameWorld().getWorld());
						
						break;
					}
			}
			
			if (isRunning())
				gameLogic.leavePlayer(player);
			else
				lobby.leavePlayer(player);
			
			gamePlayer.cleanup();
			
			for(GamePlayer gp : players) {
				Player p = gp.getPlayer();
				MessageSender.sendMessage(p, MessagesConfig.getInstance().getStringValue(p.getLocale(), "player-left").replace("%player%", player.getName()));
			}
			
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
	
	public List<GamePlayer> getPlayers() {
		return players;
	}
	
	public boolean containsPlayer(UUID uuid) {
		synchronized (players) {
			return players.stream().anyMatch(gp -> gp.uuid.equals(uuid));
		}
	}
	
	public GamePlayer getGamePlayer(UUID uuid) {
		synchronized (players) {
			Optional<GamePlayer> optionalGamePlayer = players.stream().filter(gp -> gp.uuid.equals(uuid)).findFirst();
			
			if (optionalGamePlayer.isPresent())
				return optionalGamePlayer.get();
			
			return null;
		}
	}
	
	public Team getTeamOfPlayer(UUID uuid) {
		GamePlayer gamePlayer = getGamePlayer(uuid);
		
		if (gamePlayer != null)
			return gamePlayer.getTeam();
		
		return null;
	}
	
	private Team getSmallestTeam() {
		Team ret = null;
		
		synchronized (teams) {
			for(Team team : teams){
				if(ret == null) {
					ret = team;
					continue;
				}
				
				if(team.getMembers().size() < ret.getMembers().size())
					ret = team;
			}
		}
		
		return ret;
	}
	
	private void assignLonelyPlayersToTeamsAutomatically() {
		synchronized (players) {
			for (GamePlayer gamePlayer : players) {
				if (gamePlayer.getTeam() == null) {
					Team team = getSmallestTeam();
					team.addMember(gamePlayer);
					
					Player p = gamePlayer.getPlayer();
					String colorStr = TeamColorConverter.convertTeamColorToStringForMessages(team.getBase().getTeamColor(), p.getLocale());
					MessageSender.sendMessage(p, MessagesConfig.getInstance().getStringValue(p.getLocale(), "team-changed").replace("%teamcolor%", colorStr));
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if (containsPlayer(player.getUniqueId()))
			leavePlayer(player);
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		
		if (containsPlayer(player.getUniqueId())) {
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
		
		if (containsPlayer(player.getUniqueId())) {
			if (isStarting) {
				event.setCancelled(true);
				return;
			}
			
			if (!isRunning() && !player.getWorld().getName().equals(lobby.getSpawnPoint().getWorld().getName())) {
				if (player.getGameMode() != GameMode.CREATIVE)
					event.setCancelled(true);
			}
		}
	}
	
}
