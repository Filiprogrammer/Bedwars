package filip.bedwars.game;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.config.MainConfig;
import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.arena.Base;
import filip.bedwars.game.arena.Spawner;
import filip.bedwars.listener.player.UseEntityPacketListener;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.VillagerNPC;
import filip.bedwars.world.GameWorld;

public class GameLogic {

	private Game game;
	private Arena arena;
	private GameWorld gameWorld;
	private List<Team> teams;
	private int currentGameStateIndex;
	private List<GameState> gameStates = new ArrayList<GameState>();
	private BukkitRunnable gameTicker;
	
	public GameLogic(List<Team> teams, Game game, Arena arena, GameWorld gameWorld) {
		this.game = game;
		this.arena = arena;
		this.gameWorld = gameWorld;
		this.teams = teams;
		
		/*new GameState("Phase 1", new Countdown(300) {
			@Override
			public void onTick() {
				if ((getSecondsLeft() % 60) == 0) {
					GameState nextGameState = getNextGameState();
					String nextGameStateName = null;
					
					if (nextGameState != null)
						nextGameStateName = nextGameState.getName();
					
					for (UUID uuid : game.getPlayers()) {
						Player player = Bukkit.getPlayer(uuid);
						
						if (nextGameStateName == null)
							MessageSender.sendMessage(player, (getSecondsLeft() / 60) + " minutes left the game ends");
						else
							MessageSender.sendMessage(player, (getSecondsLeft() / 60) + " minutes left until " + nextGameStateName);
					}
				}
			}
			
			@Override
			public void onStart() {}
			
			@Override
			public boolean onFinish() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
			}
		});
		
		this.currentGameStateIndex = 0;
		getCurrentGameState().initiate();*/
		
		// Teleport every player to the spawnpoint of their base
		for (UUID uuid : game.getPlayers())
			teleportToSpawn(Bukkit.getPlayer(uuid));
		
		// Setup game ticker for spawners
		gameTicker = new BukkitRunnable() {
			{
				runTaskTimer(BedwarsPlugin.getInstance(), 1L, 1L);
			}
			
			@Override
			public void run() {
				for (Spawner spawner : arena.getSpawner()) {
					spawner.update();
				}
			}
		};
		
		Player[] players = new Player[game.getPlayers().size()];
		
		for (Base base : arena.getBases()) {
			for (int i = 0; i < game.getPlayers().size(); ++i)
				players[i] = Bukkit.getPlayer(game.getPlayers().get(i));
			
			VillagerNPC itemShopNPC = new VillagerNPC(base.getItemShop(), "DESERT", "ARMORER", "Item Shop", players);
			
			new UseEntityPacketListener(itemShopNPC.getEntityId()) {
				@Override
				public void onUse(String action) {
					if (action.equals("INTERACT")) {
						// TODO: Open the item shop
					}
				}
			};
			
		}
	}
	
	private void teleportToSpawn(Player player) {
		if (player == null)
			return;
		
		Location spawnLoc = game.getTeamOfPlayer(player.getUniqueId()).getBase().getSpawn().clone();
		spawnLoc.setWorld(gameWorld.getWorld());
		player.teleport(spawnLoc);
	}
	
	private GameState getCurrentGameState() {
		return gameStates.get(currentGameStateIndex);
	}
	
	private GameState getNextGameState() {
		if ((currentGameStateIndex + 1) >= (gameStates.size()))
			return null;
		
		return gameStates.get(currentGameStateIndex + 1);
	}
	
	public void joinSpectator(Player player) {
		// TODO: Add spectator spawn point
		player.teleport(arena.getBase(0).getSpawn());
		player.setGameMode(GameMode.SPECTATOR);
	}
	
	public void leavePlayer(Player player) {
		player.teleport(MainConfig.getInstance().getMainLobby());
	}
	
	public GameWorld getGameWorld() {
		return gameWorld;
	}
	
}
