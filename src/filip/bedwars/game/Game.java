package filip.bedwars.game;

import java.util.List;

import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.lobby.Lobby;

public class Game {

	private Arena arena;
	private Lobby lobby;
	private GameState gameState;
	private List<Team> teams;
	
	public Game(Arena arena, Lobby lobby, List<Team> teams) {
		this.arena = arena;
		this.lobby = lobby;
		this.teams = teams;
	}
	
	public Arena getArena() {
		return arena;
	}
	
	public Lobby getLobby() {
		return lobby;
	}
	
	public GameState getGameState() {
		return gameState;
	}
	
	public void setGameState(GameState state) {
		gameState = state;
	}
	
	public List<Team> getTeams() {
		return teams;
	}
}
