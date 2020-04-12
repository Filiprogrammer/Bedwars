package filip.bedwars.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import filip.bedwars.game.arena.Arena;

public class GameManager {

	private static GameManager instance = null;
	
	private List<Game> games = new ArrayList<Game>();
	
	private GameManager() {}
	
	public static GameManager getInstance() {
		if (instance == null)
			instance = new GameManager();
		
		return instance;
	}
	
	/**
	 * Tries to find a game for the given parameters.
	 * @param arena
	 * @param players
	 * @return the game or null if the arena does not fit that many players
	 */
	public Game joinGame(Arena arena, Player... players) {
		int maxPlayers = arena.getBases().size() * arena.getPlayersPerTeam();
		int playerCount = players.length;
		
		// Arena does not fit that many players
		if (playerCount > maxPlayers)
			return null;
		
		for (Game game : games) {
			// Check if the game has the arena we are looking for
			if (!game.getArena().equals(arena))
				continue;
			
			// Check if the game is not already running
			if (game.isRunning())
				continue;
			
			// Check if the game has enough space for the new players
			if ((game.getPlayers().size() + playerCount) <= maxPlayers) {
				game.joinPlayers(players);
				return game;
			}
		}
		
		// if no game was found, create a new one
		Game game = new Game(arena);
		games.add(game);
		game.joinPlayers(players);
		return game;
	}
	
	public void removeGame(Game game) {
		games.remove(game);
	}
	
}
