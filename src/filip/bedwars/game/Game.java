package filip.bedwars.game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.lobby.Lobby;
import filip.bedwars.world.GameWorld;
import filip.bedwars.world.GameWorldManager;

public class Game {

	private Lobby lobby;
	private Arena arena;
	private GameLogic gameLogic;
	private List<UUID> players = new ArrayList<UUID>();
	private List<Team> teams = new ArrayList<Team>();
	private List<UUID> spectators = new ArrayList<UUID>();
	
	public Game(@NotNull Arena arena) {
		this.arena = arena;
		// TODO: Lobby setup and get lobby spawn point from config
		this.lobby = new Lobby(new Location(Bukkit.getWorld("bw_lobby"), 0, 65, 0));
		
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
		GameManager.getInstance().removeGame(this);
	}
	
	public boolean isRunning() {
		return (lobby == null); // Because lobby is set to null at start game
	}
	
	// TODO: Add reconnect function
	
	public void joinPlayer(Player player) {
		// TODO: Teleport players
		
		if (isRunning()) {
			spectators.add(player.getUniqueId());
		} else {
			players.add(player.getUniqueId());
			lobby.joinPlayer(player);
		}
	}
	
	public void joinPlayers(Player... players) {
		for (Player player : players)
			joinPlayer(player);
	}
	
	public void leavePlayer(Player player) {
		
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
