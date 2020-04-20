package filip.bedwars.game;

import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.destroystokyo.paper.Title;
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
import filip.bedwars.world.GameWorldManager;

public class GameLogic implements Listener {

	private Game game;
	private Arena arena;
	private GameWorld gameWorld;
	private GameState gameState;
	private ArrayDeque<GameState> gameStates = new ArrayDeque<GameState>();
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
		
		gameStates.add(new GameState("Phase 1", new Countdown(60 * 2) {
			@Override
			public void onTick() {
				int secondsLeft = getSecondsLeft();
				
				if (secondsLeft != 0 && (secondsLeft % 60) == 0) {
					for (Player p : gameWorld.getWorld().getPlayers())
						MessageSender.sendMessage(p, "§7Beds will be destroyed in §3" + (secondsLeft / 60) + " minutes");
				}
			}
			
			@Override
			public void onStart() {}
			
			@Override
			public boolean onFinish() {
				// Destroy all beds
				List<Team> teamsSyncList = game.getTeams();
				synchronized (teamsSyncList) {
					for (Team team : teamsSyncList) {
						team.destroyBed(gameWorld.getWorld());
						
						for (UUID uuid : team.getMembers()) {
							Player p = Bukkit.getPlayer(uuid);
							Title title = new Title(MessagesConfig.getInstance().getStringValue(p.getLocale(), "your-bed-destroyed"), MessagesConfig.getInstance().getStringValue(p.getLocale(), "you-cant-respawn-anymore"));
							p.sendTitle(title);
						}
					}
				}
				
				for (Player p : gameWorld.getWorld().getPlayers()) {
					MessageSender.sendMessage(p, "§4All beds have been destroyed");
					SoundPlayer.playSound("bed-destroyed", p);
				}
				
				// Initiate Phase 2
				gameState = gameStates.remove();
				gameState.initiate();
				return false;
			}
			
			@Override
			public void onCancel() {}
		}));
		
		gameStates.add(new GameState("Phase 2", new Countdown(60 * 1) {
			@Override
			public void onTick() {
				int secondsLeft = getSecondsLeft();
				
				if (secondsLeft != 0 && (secondsLeft % 60) == 0) {
					for (Player p : gameWorld.getWorld().getPlayers())
						MessageSender.sendMessage(p, "§7Game will end in §3" + (secondsLeft / 60) + " minutes");
				}
			}
			
			@Override
			public void onStart() {}
			
			@Override
			public boolean onFinish() {
				// Initiate Game End phase
				gameState = gameStates.getLast(); // Must not be .remove() because it would invalidate the game over checks
				gameState.initiate();
				
				// No Team wins
				for (Player p : gameWorld.getWorld().getPlayers())
					MessageSender.sendMessage(p, "The game is over and noone wins");
				
				return false;
			}
			
			@Override
			public void onCancel() {}
		}));
		
		gameStates.add(new GameState("Game End", new Countdown(10) {
			@Override
			public void onTick() {}
			
			@Override
			public void onStart() {}
			
			@Override
			public boolean onFinish() {
				game.endGame();
				return false;
			}
			
			@Override
			public void onCancel() {}
		}));
		
		gameState = gameStates.remove();
		gameState.initiate();
		
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
			players = new Player[syncPlayersList.size()];
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
		player.spigot().respawn();
		player.teleport(MainConfig.getInstance().getMainLobby());
		removePlayerListeners(player);
		checkGameOver();
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
		
		for (Player p : gameWorld.getWorld().getPlayers())
			p.teleport(MainConfig.getInstance().getMainLobby());
		
		HandlerList.unregisterAll(this);
		
		GameWorldManager.getInstance().removeGameWorld(gameWorld);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		// Check if the player is a spectator
		if (!game.containsPlayer(player.getUniqueId()) && player.getWorld().getName().equals(getGameWorld().getWorld().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
		// Check if the player is a part of this game
		if (game.containsPlayer(event.getPlayer().getUniqueId()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerPickupItem(EntityPickupItemEvent event) {
		if (event.getEntityType() != EntityType.PLAYER)
			return;
		
		Player player = (Player) event.getEntity();
		
		// Check if the player is a spectator
		if (!game.containsPlayer(player.getUniqueId()) && player.getWorld().getName().equals(getGameWorld().getWorld().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		
		// Check if the player is a spectator
		if (!game.containsPlayer(player.getUniqueId()) && player.getWorld().getName().equals(getGameWorld().getWorld().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager().getType() != EntityType.PLAYER)
			return;
		
		Player damager = (Player) event.getDamager();
		
		if (game.containsPlayer(damager.getUniqueId())) {
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
		
		// Check if the player is in the game world
		if (player.getWorld().getName().equals(getGameWorld().getWorld().getName())) {
			if (game.containsPlayer(player.getUniqueId())) {
				// Player is a game player
				if (gameState == gameStates.getLast())
					event.setCancelled(true);
			} else {
				// Player is a spectator
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		Player player = (Player) event.getEntity();
		
		if (!game.containsPlayer(player.getUniqueId()))
			return;
		
		if (!MainConfig.getInstance().getHunger()) {
			event.setCancelled(true);
			event.setFoodLevel(20);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		
		if (!game.containsPlayer(player.getUniqueId()))
			return;
		
		Block block = event.getBlock();
		
		if (block.getType() == Material.TNT) {
			Location loc = block.getLocation();
			loc.getWorld().spawnEntity(loc.clone().add(0.5, 0, 0.5), EntityType.PRIMED_TNT);
			block.setType(Material.AIR);
			return;
		}
		
		block.setMetadata("bedwars_placed", new FixedMetadataValue(BedwarsPlugin.getInstance(), true));
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		
		if (!game.containsPlayer(player.getUniqueId()))
			return;
		
		Location blockLocation = event.getBlock().getLocation();
		Team teamOfPlayer = game.getTeamOfPlayer(player.getUniqueId());
		
		// Check if the broken block is a bed
		if (event.getBlock().getBlockData() instanceof Bed) {
			List<Team> teamsSyncList = game.getTeams();
			synchronized (teamsSyncList) {
				for (Team team : teamsSyncList) {
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
							team.destroyBed(gameWorld.getWorld());
							event.setCancelled(true);
							broadcastBedDestroyed(player, team);
						}
						
						break;
					}
				}
			}
		}
		
		if (!event.getBlock().hasMetadata("bedwars_placed"))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		// Check if the explosion is in the game world
		if (event.getLocation().getWorld().getName().equals(getGameWorld().getWorld().getName())) {
			List<Block> blockListCopy = new ArrayList<Block>();
	        blockListCopy.addAll(event.blockList());
	        
	        for (Block block : blockListCopy) {
	        	if (block.getBlockData() instanceof Bed)
	        		event.blockList().remove(block);
	        	
	        	if (!block.hasMetadata("bedwars_placed"))
	        		event.blockList().remove(block);
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
			
			if (team != null) {
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
				
				boolean isFinalKill = false;
				
				if (!team.hasBed()) {
					removePlayerListeners(player);
					team.removeMember(player.getUniqueId());
					isFinalKill = true;
					
					List<UUID> syncPlayersList = game.getPlayers();
					synchronized (syncPlayersList) {
						syncPlayersList.remove(player.getUniqueId());
					}
					
					checkGameOver();
					
					Bukkit.getScheduler().scheduleSyncDelayedTask(BedwarsPlugin.getInstance(), () -> {
						player.spigot().respawn();
						joinSpectator(player);
					}, 1L);
				}
				
				for (Player p : gameWorld.getWorld().getPlayers()) {
					String deathMessage = deathMsg;
					
					if(isFinalKill)
						deathMessage += MessagesConfig.getInstance().getStringValue(p.getLocale(), "final-kill");
					
					MessageSender.sendMessage(p, deathMessage);
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
			
			if (team == null)
				// TODO: use spectator specific respawn location
				event.setRespawnLocation(game.getTeams().get(0).getBase().getSpawn(gameWorld.getWorld()));
			else
				event.setRespawnLocation(team.getBase().getSpawn(gameWorld.getWorld()));
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
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		// Check if the player is in the game world
		if (player.getWorld().getName().equals(getGameWorld().getWorld().getName())) {
			if (event.getTo().getY() < 0) {
				// Check if the player is a game player
				if (game.containsPlayer(player.getUniqueId()))
					PlayerUtils.damagePlayer(player, "OUT_OF_WORLD", 999);
				else
					// TODO: use spectator specific respawn location
					player.teleport(game.getTeams().get(0).getBase().getSpawn(gameWorld.getWorld()));
			}
		}
	}
	
	private void removePlayerListeners(Player player) {
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
	
	private void checkGameOver() {
		if (gameState == gameStates.getLast())
			// Game is already over
			return;
		
		Team winnerTeam = game.isOver();
		
		if (winnerTeam != null)
			// Game is over
			runGameOver(winnerTeam);
	}
	
	private void runGameOver(Team winnerTeam) {
		for (Player p : gameWorld.getWorld().getPlayers()) {
			String teamHasWonMsg = MessagesConfig.getInstance().getStringValue(p.getLocale(), "team-has-won").replace("%teamcolor%", TeamColorConverter.convertTeamColorToStringForMessages(winnerTeam.getBase().getTeamColor(), p.getLocale()));
			MessageSender.sendMessage(p, teamHasWonMsg);
			SoundPlayer.playSound("victory", p);
			
			if(winnerTeam.containsMember(p.getUniqueId())) {
				Title title = new Title(MessagesConfig.getInstance().getStringValue(p.getLocale(), "victory"), teamHasWonMsg);
				p.sendTitle(title);
			}
		}
		
		// Make sure that the countdown of the current game state is cancelled
		gameState.getCountdown().cancel();
		
		// Initiate game over phase
		gameState = gameStates.getLast();
		gameState.initiate();
	}
	
	private void broadcastBedDestroyed(Player destroyer, Team team) {
		for (Player p : gameWorld.getWorld().getPlayers()) {
			String colorStr = TeamColorConverter.convertTeamColorToStringForMessages(team.getBase().getTeamColor(), p.getLocale());
			
			MessageSender.sendMessage(p,
					MessagesConfig.getInstance().getStringValue(p.getLocale(), "bed-destroyed")
					.replace("%player%", destroyer.getName())
					.replace("%teamcolor%", colorStr));
			
			SoundPlayer.playSound("bed-destroyed", p);
			
			if(team.containsMember(p.getUniqueId())) {
				Title title = new Title(MessagesConfig.getInstance().getStringValue(p.getLocale(), "your-bed-destroyed"), MessagesConfig.getInstance().getStringValue(p.getLocale(), "you-cant-respawn-anymore"));
				p.sendTitle(title);
			}
		}
	}
	
}
