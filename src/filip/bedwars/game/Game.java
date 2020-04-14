package filip.bedwars.game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.arena.Base;
import filip.bedwars.game.lobby.Lobby;
import filip.bedwars.listener.player.PlayerChangedWorldHandler;
import filip.bedwars.listener.player.PlayerQuitHandler;
import filip.bedwars.world.GameWorld;
import filip.bedwars.world.GameWorldManager;

public class Game {

	private Lobby lobby;
	private Arena arena;
	private GameLogic gameLogic;
	private List<UUID> players = new ArrayList<UUID>();
	private List<Team> teams = new ArrayList<Team>();
	private PlayerChangedWorldHandler playerChangedWorldHandler;
	private PlayerQuitHandler playerQuitHandler;
	
	public Game(@NotNull Arena arena) {
		this.arena = arena;
		// TODO: Lobby setup and get lobby spawn point from config
		this.lobby = new Lobby(new Location(Bukkit.getWorld("bw_lobby"), 0, 65, 0), this);
		
		for (int i = 0; i < arena.getBases().size(); ++i) {
			Base base = arena.getBase(i);
			teams.add(new Team(i, base, new ArrayList<UUID>()));
		}
		
		playerChangedWorldHandler = new PlayerChangedWorldHandler() {
			@Override
			public void onChangedWorld(PlayerChangedWorldEvent event) {
				Player player = event.getPlayer();
				
				if (players.contains(player.getUniqueId())) {
					if (isRunning()) {
						if (!player.getWorld().getName().equals(gameLogic.getGameWorld().getWorld().getName()))
							leavePlayer(player); // Player left the game world and therefore leaves the game
					} else if (!player.getWorld().getName().equals(lobby.getSpawnPoint().getWorld().getName())) {
						leavePlayer(player); // Player left the game lobby and therefore leaves the game
					}
				}
			}
		};
		
		playerQuitHandler = new PlayerQuitHandler() {
			@Override
			public void onQuit(PlayerQuitEvent event) {
				Player player = event.getPlayer();
				
				if (players.contains(player.getUniqueId()))
					leavePlayer(player);
			}
		};
		
		BedwarsPlugin.getInstance().addPlayerChangedWorldHandler(playerChangedWorldHandler);
		BedwarsPlugin.getInstance().addPlayerQuitHandler(playerQuitHandler);
	}
	
	/**
	 * Starts the game if there are enough players
	 * @return false if not enough players
	 */
	public boolean startGame() {
		if (players.size() < arena.getMinPlayersToStart())
			return false; // Not enough players
		
		// Assign players who didn't choose a team to a team automatically.
		for (UUID uuid : players)
			if (!playerHasTeam(uuid))
				getSmallestTeam().addMember(uuid);
		
		GameWorld gameWorld = GameWorldManager.getInstance().claimGameWorld(arena.getWorld());
		lobby = null;
		gameLogic = new GameLogic(teams, this, arena, gameWorld);
		return true;
	}
	
	/**
	 * Cleans everything up after a game
	 */
	public void endGame() {
		BedwarsPlugin.getInstance().removePlayerChangedWorldHandler(playerChangedWorldHandler);
		BedwarsPlugin.getInstance().removePlayerQuitHandler(playerQuitHandler);
		GameManager.getInstance().removeGame(this);
	}
	
	public boolean isRunning() {
		return (lobby == null); // Because lobby is set to null at start game
	}
	
	// TODO: Add reconnect function
	
	public void joinPlayer(Player player) {
		if (players.contains(player.getUniqueId()))
			return; // Player is already in this game
		
		if (isRunning()) {
			gameLogic.joinSpectator(player);
		} else {
			players.add(player.getUniqueId());
			lobby.joinPlayer(player);
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
	
}
