package filip.bedwars.game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.lobby.Lobby;

public class Game {

	private Arena currentArena;
	private Lobby currentLobby;
	private GameState currentGameState;
	private List<UUID> ingamePlayer = new ArrayList<UUID>();
	
	public Game(Arena arena, Lobby lobby) {
		currentArena = arena;
		currentLobby = lobby;
	}
	
	public Arena getArena() {
		return currentArena;
	}
	
	public Lobby getLobby() {
		return currentLobby;
	}
	
	public void addPlayer(UUID uuid) {
		ingamePlayer.add(uuid);
	}
	
	public void removePlayer(UUID uuid) {
		ingamePlayer.remove(uuid);
	}
	
	public List<UUID> getPlayers() {
		return ingamePlayer;
	}
	
	public UUID getPlayer(int index) {
		return ingamePlayer.get(index);
	}
	
	public GameState getGameState() {
		return currentGameState;
	}
	
	public void setGameState(GameState state) {
		currentGameState = state;
	}
}
