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
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.arena.Base;
import filip.bedwars.game.arena.Spawner;
import filip.bedwars.listener.player.UseEntityPacketListener;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;
import filip.bedwars.utils.VillagerNPC;
import filip.bedwars.world.GameWorld;

public class GameLogic {

	private Game game;
	private Arena arena;
	private GameWorld gameWorld;
	private List<Team> teams;
	private BukkitRunnable gameTicker;
	private List<UseEntityPacketListener> itemShopNPCListeners = new ArrayList<UseEntityPacketListener>();
	private List<UseEntityPacketListener> teamShopNPCListeners = new ArrayList<UseEntityPacketListener>();
	
	public GameLogic(List<Team> teams, Game game, Arena arena, GameWorld gameWorld) {
		this.game = game;
		this.arena = arena;
		this.gameWorld = gameWorld;
		this.teams = teams;
		
		// Setup game ticker for spawners
		gameTicker = new BukkitRunnable() {
			{
				runTaskTimer(BedwarsPlugin.getInstance(), 1L, 1L);
			}
			
			@Override
			public void run() {
				for (Spawner spawner : arena.getSpawner())
					spawner.update();
			}
		};
		
		// Teleport every player to the spawnpoint of their base and send a message that the game started
		for (UUID uuid : game.getPlayers()) {
			teleportToSpawn(Bukkit.getPlayer(uuid));
			MessageSender.sendMessageUUID(uuid, MessagesConfig.getInstance().getStringValue(Bukkit.getPlayer(uuid).getLocale(), "game-started"));
			SoundPlayer.playSound("game-started", Bukkit.getPlayer(uuid));
		}
		
		// Convert UUIDs to players
		Player[] players = new Player[game.getPlayers().size()];
		
		for (int i = 0; i < game.getPlayers().size(); ++i)
			players[i] = Bukkit.getPlayer(game.getPlayers().get(i));
		
		for (Base base : arena.getBases()) {
			// Setup item shop NPC
			VillagerNPC itemShopNPC = new VillagerNPC(base.getItemShop().clone().add(0.5, 0, 0.5), "DESERT", "ARMORER", "Item Shop", players);
			
			UseEntityPacketListener itemShopNPCListener = new UseEntityPacketListener(itemShopNPC.getEntityId()) {
				@Override
				public void onUse(String action) {
					if (action.equals("INTERACT")) {
						// TODO: Open the item shop
					}
				}
			};
			
			itemShopNPCListeners.add(itemShopNPCListener);
			
			for (Player player : players)
				BedwarsPlugin.getInstance().addPacketListener(player, itemShopNPCListener);
			
			// Setup team shop NPC, if it is not null
			if (base.getTeamShop() != null) {
				VillagerNPC teamShopNPC = new VillagerNPC(base.getTeamShop().clone().add(0.5, 0, 0.5), "SNOW", "CLERIC", "Team Shop", players);
				
				UseEntityPacketListener teamShopNPCListener = new UseEntityPacketListener(teamShopNPC.getEntityId()) {
					@Override
					public void onUse(String action) {
						if (action.equals("INTERACT")) {
							// TODO: Open the team shop
						}
					}
				};
				
				teamShopNPCListeners.add(teamShopNPCListener);
				
				for (Player player : players)
					BedwarsPlugin.getInstance().addPacketListener(player, teamShopNPCListener);
			}
		}
	}
	
	private void teleportToSpawn(Player player) {
		if (player == null)
			return;
		
		Location spawnLoc = game.getTeamOfPlayer(player.getUniqueId()).getBase().getSpawn().clone();
		spawnLoc.setWorld(gameWorld.getWorld());
		player.teleport(spawnLoc);
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
	
	public void cleanup() {
		gameTicker.cancel();
		
		for (UUID uuid : game.getPlayers()) {
			Player player = Bukkit.getPlayer(uuid);
			
			for (UseEntityPacketListener itemShopNPCListener : itemShopNPCListeners)
				BedwarsPlugin.getInstance().removePacketListener(player, itemShopNPCListener);
			
			for (UseEntityPacketListener teamShopNPCListener : teamShopNPCListeners)
				BedwarsPlugin.getInstance().removePacketListener(player, teamShopNPCListener);
		}
	}
	
}
