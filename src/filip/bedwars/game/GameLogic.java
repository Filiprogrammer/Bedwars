package filip.bedwars.game;

import java.util.List;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Callable;

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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.config.ItemShopConfig;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.config.TeamShopConfig;
import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.arena.Base;
import filip.bedwars.game.arena.Spawner;
import filip.bedwars.listener.player.IPacketListener;
import filip.bedwars.listener.player.UseEntityPacketListener;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.PlayerUtils;
import filip.bedwars.utils.SoundPlayer;
import filip.bedwars.utils.VillagerNPC;
import filip.bedwars.world.GameWorld;

public class GameLogic implements Listener {

	private Game game;
	private Arena arena;
	private GameWorld gameWorld;
	private BukkitRunnable gameTicker;
	private List<UseEntityPacketListener> itemShopNPCListeners = new ArrayList<UseEntityPacketListener>();
	private List<UseEntityPacketListener> teamShopNPCListeners = new ArrayList<UseEntityPacketListener>();
	private IPacketListener packetListener;
	
	public GameLogic(Game game, Arena arena, GameWorld gameWorld) {
		this.game = game;
		this.arena = arena;
		this.gameWorld = gameWorld;
		
		// Setup game ticker for spawners
		gameTicker = new BukkitRunnable() {
			{
				runTaskTimer(BedwarsPlugin.getInstance(), 1L, 1L);
			}
			
			@Override
			public void run() {
				for (Spawner spawner : arena.getSpawner()) {
					spawner.update(gameWorld.getWorld());
				}
			}
		};
		
		packetListener = new IPacketListener() {
			public boolean writePacket(Object packet, Player player) {
				if (packet.getClass().getSimpleName().equals("PacketPlayOutNamedEntitySpawn")) {
					for (UUID uuid : game.getPlayers()) {
						Player p = Bukkit.getPlayer(uuid);
						try {
							Field aField = packet.getClass().getDeclaredField("a");
							aField.setAccessible(true);
							if (p.getEntityId() == aField.getInt(packet))
								return true;
						} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
					}
					
					return false;
				}
				
				return true;
			}
		};
		
		BedwarsPlugin.getInstance().getServer().getPluginManager().registerEvents(this, BedwarsPlugin.getInstance());
		
		for (UUID uuid : game.getPlayers()) {
			Player player = Bukkit.getPlayer(uuid);
			// Add a packet listener
			BedwarsPlugin.getInstance().addPacketListener(player, packetListener);
			// Teleport player to the spawnpoint of their base
			teleportToSpawn(player);
			// Notify the player that the game has started.
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "game-started"));
			SoundPlayer.playSound("game-started", player);
		}
		
		// Convert UUIDs to players
		Player[] players = new Player[game.getPlayers().size()];
		
		for (int i = 0; i < game.getPlayers().size(); ++i)
			players[i] = Bukkit.getPlayer(game.getPlayers().get(i));
		
		for (Base base : arena.getBases()) {
			// Setup item shop NPC
			VillagerNPC itemShopNPC = new VillagerNPC(base.getItemShop(gameWorld.getWorld()).clone().add(0.5, 0, 0.5), "DESERT", "ARMORER", "Item Shop", players);
			
			UseEntityPacketListener itemShopNPCListener = new UseEntityPacketListener(itemShopNPC.getEntityId()) {
				@Override
				public void onUse(String action, Player player) {
					if (action.equals("INTERACT")) {
						// Call that on the main thread
						Bukkit.getScheduler().callSyncMethod(BedwarsPlugin.getInstance(), new Callable<Void>() {
							@Override
							public Void call() throws Exception {
								player.openInventory(ItemShopConfig.getInstance().getShop().getCategoryListInventory());
								return null;
							}
						});
					}
				}
			};
			
			itemShopNPCListeners.add(itemShopNPCListener);
			
			for (Player player : players)
				BedwarsPlugin.getInstance().addPacketListener(player, itemShopNPCListener);
			
			// Setup team shop NPC, if it is not null
			if (base.getTeamShop(gameWorld.getWorld()) != null) {
				VillagerNPC teamShopNPC = new VillagerNPC(base.getTeamShop(gameWorld.getWorld()).clone().add(0.5, 0, 0.5), "SNOW", "CLERIC", "Team Shop", players);
				
				UseEntityPacketListener teamShopNPCListener = new UseEntityPacketListener(teamShopNPC.getEntityId()) {
					@Override
					public void onUse(String action, Player player) {
						if (action.equals("INTERACT")) {
							// Call that on the main thread
							Bukkit.getScheduler().callSyncMethod(BedwarsPlugin.getInstance(), new Callable<Void>() {
								@Override
								public Void call() throws Exception {
									player.openInventory(TeamShopConfig.getInstance().getShop().getCategoryListInventory());
									return null;
								}
							});
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
		
		Location spawnLoc = game.getTeamOfPlayer(player.getUniqueId()).getBase().getSpawn(gameWorld.getWorld());
		player.teleport(spawnLoc);
		PlayerUtils.playerReset(player);
	}
	
	public void joinSpectator(Player player) {
		// TODO: Add spectator spawn point
		player.teleport(arena.getBase(0).getSpawn(gameWorld.getWorld()));
		PlayerUtils.playerReset(player);
		player.setGameMode(GameMode.SPECTATOR);
		MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "joined-game-as-spectator"));
	}
	
	public void leavePlayer(Player player) {
		player.teleport(MainConfig.getInstance().getMainLobby());
		for (UseEntityPacketListener itemShopNPCListener : itemShopNPCListeners)
			BedwarsPlugin.getInstance().removePacketListener(player, itemShopNPCListener);
		
		for (UseEntityPacketListener teamShopNPCListener : teamShopNPCListeners)
			BedwarsPlugin.getInstance().removePacketListener(player, teamShopNPCListener);
		
		BedwarsPlugin.getInstance().removePacketListener(player, packetListener);
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
		
		HandlerList.unregisterAll(this);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		// Check if the player is a spectator
		if (!game.getPlayers().contains(player.getUniqueId()) && player.getWorld().getName().equals(getGameWorld().getWorld().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
		// Check if the player is a part of this game
		if (game.getPlayers().contains(event.getPlayer().getUniqueId()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerPickupItem(EntityPickupItemEvent event) {
		if (event.getEntityType() != EntityType.PLAYER)
			return;
		
		Player player = (Player) event.getEntity();
		
		// Check if the player is a spectator
		if (!game.getPlayers().contains(player.getUniqueId()) && player.getWorld().getName().equals(getGameWorld().getWorld().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		
		// Check if the player is a spectator
		if (!game.getPlayers().contains(player.getUniqueId()) && player.getWorld().getName().equals(getGameWorld().getWorld().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager().getType() != EntityType.PLAYER)
			return;
		
		Player damager = (Player) event.getDamager();
		
		if (game.getPlayers().contains(damager.getUniqueId())) {
			if (event.getEntity().getType() != EntityType.PLAYER)
				return;
			
			Player player = (Player) event.getEntity();
			
			if (game.getTeamOfPlayer(damager.getUniqueId()) == game.getTeamOfPlayer(player.getUniqueId()))
				event.setCancelled(true);
		} else {
			if (damager.getWorld().getName().equals(getGameWorld().getWorld().getName()))
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntityType() != EntityType.PLAYER)
			return;
		
		Player player = (Player) event.getEntity();
		
		// Check if the player is a spectator
		if (!game.getPlayers().contains(player.getUniqueId()) && player.getWorld().getName().equals(getGameWorld().getWorld().getName()))
			event.setCancelled(true);
	}
	
}
