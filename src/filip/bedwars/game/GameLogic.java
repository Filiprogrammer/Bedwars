package filip.bedwars.game;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import filip.bedwars.game.arena.Arena;
import filip.bedwars.world.GameWorld;

public class GameLogic {

	private Game game;
	private Arena arena;
	private GameWorld gameWorld;
	private List<Team> teams;
	private GameState gameState;
	
	public GameLogic(List<Team> teams, Game game, Arena arena, GameWorld gameWorld) {
		this.game = game;
		this.arena = arena;
		this.gameWorld = gameWorld;
		this.teams = teams;
		this.gameState = new GameState("PHASE_1", new Countdown(300) {
			
			@Override
			public void onTick() {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
			}
			
			@Override
			public boolean onFinish() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
			}
		}) {
			
			@Override
			public void onInitiate() {
				
			}
		};
		this.gameState.initiate();
		
		// Teleport every player to the spawnpoint of their base
		for (UUID uuid : game.getPlayers())
			teleportToSpawn(Bukkit.getPlayer(uuid));
	}
	
	private void teleportToSpawn(Player player) {
		if (player == null)
			return;
		
		Location spawnLoc = game.getTeamOfPlayer(player.getUniqueId()).getBase().getSpawn().clone();
		spawnLoc.setWorld(gameWorld.getWorld());
		player.teleport(spawnLoc);
	}
	
	public void joinSpectator(Player player) {
		player.teleport();
		player.setGameMode(GameMode.SPECTATOR);
	}
	
	public void leavePlayer(Player player) {
		// TODO: Teleport to main lobby
	}
	
}
