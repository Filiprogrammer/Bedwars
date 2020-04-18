package filip.bedwars.game;

import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.config.ItemShopConfig;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.config.TeamShopConfig;
import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.arena.Base;
import filip.bedwars.game.arena.Spawner;
import filip.bedwars.inventory.ClickableInventory;
import filip.bedwars.inventory.IClickable;
import filip.bedwars.listener.player.IPacketListener;
import filip.bedwars.listener.player.UseEntityPacketListener;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.PlayerUtils;
import filip.bedwars.utils.SoundPlayer;
import filip.bedwars.utils.TeamColorConverter;
import filip.bedwars.utils.VillagerNPC;
import filip.bedwars.world.GameWorld;

public class GameLogic implements Listener {

	private Game game;
	private Arena arena;
	private GameWorld gameWorld;
	private BukkitRunnable gameTicker;
	private List<VillagerNPC> itemShopNPCs = new ArrayList<VillagerNPC>();
	private List<VillagerNPC> teamShopNPCs = new ArrayList<VillagerNPC>();
	private List<UseEntityPacketListener> itemShopNPCListeners = new ArrayList<UseEntityPacketListener>();
	private List<UseEntityPacketListener> teamShopNPCListeners = new ArrayList<UseEntityPacketListener>();
	private List<IClickable> itemShopClickables = new ArrayList<IClickable>();
	private Map<UUID, Integer> selectedItemShopCategory = new HashMap<UUID, Integer>();
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
					List<UUID> syncPlayersList = game.getPlayers();
					synchronized (syncPlayersList) {
						for (UUID uuid : syncPlayersList) {
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
					}
					
					return false;
				}
				
				return true;
			}
		};
		
		BedwarsPlugin.getInstance().getServer().getPluginManager().registerEvents(this, BedwarsPlugin.getInstance());
		
		List<UUID> syncPlayersList = game.getPlayers();
		Player[] players;
		synchronized (syncPlayersList) {
			players = new Player[game.getPlayers().size()];
			int i = 0;
			
			for (UUID uuid : syncPlayersList) {
				Player player = Bukkit.getPlayer(uuid);
				players[i] = player;
				// Add a packet listener
				BedwarsPlugin.getInstance().addPacketListener(player, packetListener);
				// Teleport player to the spawnpoint of their base
				teleportToSpawn(player);
				// Notify the player that the game has started.
				MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "game-started"));
				SoundPlayer.playSound("game-started", player);
				++i;
			}
		}
		
		for (Base base : arena.getBases()) {
			// Setup item shop NPC
			VillagerNPC itemShopNPC = new VillagerNPC(base.getItemShop(gameWorld.getWorld()).clone().add(0.5, 0, 0.5), "DESERT", "ARMORER", MainConfig.getInstance().getItemShopName(), players);
			itemShopNPCs.add(itemShopNPC);
			
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
			
			for (Player player : players) {
				itemShopClickables.add(new ClickableInventory(ItemShopConfig.getInstance().getShop().getCategoryListInventory(), player) {
					@Override
					public void click(InventoryClickEvent event) {
						HumanEntity player = event.getWhoClicked();
						selectedItemShopCategory.put(player.getUniqueId(), ItemShopConfig.getInstance().getShop().handleClick(selectedItemShopCategory.getOrDefault(player.getUniqueId(), -1), event));
					}

					@Override
					public void drag(InventoryDragEvent event) {}
				});
				
				BedwarsPlugin.getInstance().addPacketListener(player, itemShopNPCListener);
			}
			
			// Setup team shop NPC, if it is not null
			if (base.getTeamShop(gameWorld.getWorld()) != null) {
				VillagerNPC teamShopNPC = new VillagerNPC(base.getTeamShop(gameWorld.getWorld()).clone().add(0.5, 0, 0.5), "SNOW", "CLERIC", MainConfig.getInstance().getTeamShopName(), players);
				teamShopNPCs.add(teamShopNPC);
				
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
		
		for (VillagerNPC itemShopNPC : itemShopNPCs)
			itemShopNPC.respawn(player);
		
		for (VillagerNPC teamShopNPC : teamShopNPCs)
			teamShopNPC.respawn(player);
	}
	
	public void leavePlayer(Player player) {
		player.teleport(MainConfig.getInstance().getMainLobby());
		for (UseEntityPacketListener itemShopNPCListener : itemShopNPCListeners)
			BedwarsPlugin.getInstance().removePacketListener(player, itemShopNPCListener);
		
		for (UseEntityPacketListener teamShopNPCListener : teamShopNPCListeners)
			BedwarsPlugin.getInstance().removePacketListener(player, teamShopNPCListener);
		
		for (IClickable itemShopClickable : itemShopClickables) {
			if (itemShopClickable.getPlayer().equals(player)) {
				BedwarsPlugin.getInstance().removeClickable(itemShopClickable);
				break;
			}
		}
		
		BedwarsPlugin.getInstance().removePacketListener(player, packetListener);
	}
	
	public GameWorld getGameWorld() {
		return gameWorld;
	}
	
	public void cleanup() {
		gameTicker.cancel();
		
		List<UUID> syncPlayersList = game.getPlayers();
		synchronized (syncPlayersList) {
			for (UUID uuid : syncPlayersList)
				leavePlayer(Bukkit.getPlayer(uuid));
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
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		Player player = (Player) event.getEntity();
		
		if (!game.getPlayers().contains(player.getUniqueId()))
			return;
		
		if (!MainConfig.getInstance().getHunger()) {
			event.setCancelled(true);
			event.setFoodLevel(20);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		
		if (!game.getPlayers().contains(player.getUniqueId()))
			return;
		
		Location blockLocation = event.getBlock().getLocation();
		Team teamOfPlayer = game.getTeamOfPlayer(player.getUniqueId());
		
		// Check if the broken block is a bed
		if (event.getBlock().getBlockData() instanceof Bed) {
			for (Team team : game.getTeams()) {
				// Check if the team still has a bed
				if (!team.hasBed())
					continue;
				
				Base base = team.getBase();
				Location bedBottom = base.getBedBottom(gameWorld.getWorld());
				Location bedTop = base.getBedTop(gameWorld.getWorld());
				
				// Check if the broken block was the bed of the team
				if ((blockLocation.getBlockX() == bedBottom.getBlockX()
				  && blockLocation.getBlockY() == bedBottom.getBlockY()
				  && blockLocation.getBlockZ() == bedBottom.getBlockZ())
				 || (blockLocation.getBlockX() == bedTop.getBlockX()
				  && blockLocation.getBlockY() == bedTop.getBlockY()
				  && blockLocation.getBlockZ() == bedTop.getBlockZ())) {
					if (teamOfPlayer.getId() == team.getId()) {
						event.setCancelled(true);
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "you-cant-destroy-own-bed"));
					} else {
						team.setHasBed(false);
						event.setCancelled(true);
						gameWorld.getWorld().getBlockAt(bedBottom).setType(Material.AIR);
						gameWorld.getWorld().getBlockAt(bedTop).setType(Material.AIR);
						
						for (Player p : gameWorld.getWorld().getPlayers()) {
							String colorStr = TeamColorConverter.convertTeamColorToStringForMessages(team.getBase().getTeamColor(), p.getLocale());
							
							MessageSender.sendMessage(p,
									MessagesConfig.getInstance().getStringValue(p.getLocale(), "bed-destroyed")
									.replace("%player%", player.getName())
									.replace("%teamcolor%", colorStr));
							
							SoundPlayer.playSound("bed-destroyed", p);
						}
					}
					
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		
		// Check if the player is in the game world
		if (player.getWorld().getName().equals(getGameWorld().getWorld().getName())) {
			String deathMsg = event.getDeathMessage();
			event.setDeathMessage(null);
			Team team = game.getTeamOfPlayer(player.getUniqueId());
			
			if (team == null) {
				
			} else {
				for (Player p : gameWorld.getWorld().getPlayers())
					MessageSender.sendMessage(p, deathMsg);
				
				EntityDamageEvent entityDamageEvent = player.getLastDamageCause();
				
				if (entityDamageEvent != null) {
					if (entityDamageEvent.getCause() == DamageCause.PROJECTILE) {
						Player killer = player.getKiller();
						
						if (killer != null) {
							for (ItemStack itemStack : player.getInventory().getContents()) {
								if (itemStack == null)
									continue;
								
								if (!event.getItemsToKeep().contains(itemStack)) {
									event.getDrops().remove(itemStack);
									HashMap<Integer, ItemStack> didNotFit = killer.getInventory().addItem(itemStack);
									
									for (ItemStack is : didNotFit.values())
							    		killer.getWorld().dropItemNaturally(killer.getLocation(), is).setVelocity(killer.getLocation().getDirection().multiply(0.5));
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		
		// Check if the player is in the game world
		if (player.getWorld().getName().equals(getGameWorld().getWorld().getName())) {
			Team team = game.getTeamOfPlayer(player.getUniqueId());
			
			if (team == null) {
				// TODO: do something about this
			} else {
				event.setRespawnLocation(team.getBase().getSpawn(gameWorld.getWorld()));
			}
		}
	}
	
	@EventHandler
	public void onPlayerPostRespawn(PlayerPostRespawnEvent event) {
		Player player = event.getPlayer();
		
		// Check if the player is in the game world
		if (player.getWorld().getName().equals(getGameWorld().getWorld().getName())) {
			for (VillagerNPC itemShopNPC : itemShopNPCs)
				itemShopNPC.respawn(player);
			
			for (VillagerNPC teamShopNPC : teamShopNPCs)
				teamShopNPC.respawn(player);
		}
	}
	
}
