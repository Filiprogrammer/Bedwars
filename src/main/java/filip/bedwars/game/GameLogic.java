package filip.bedwars.game;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import com.destroystokyo.paper.Title;
import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.config.GameStatesConfig;
import filip.bedwars.config.ItemShopConfig;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.config.SpawnerConfig;
import filip.bedwars.config.TeamShopConfig;
import filip.bedwars.game.Team.TeamUpgradeType;
import filip.bedwars.game.action.Action;
import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.arena.Base;
import filip.bedwars.game.arena.Spawner;
import filip.bedwars.game.arena.SpawnerType;
import filip.bedwars.game.events.BedwarsBedBrokenByPlayerEvent;
import filip.bedwars.game.events.BedwarsKillEvent;
import filip.bedwars.game.events.BedwarsVictoryEvent;
import filip.bedwars.game.scoreboard.ScoreboardManager;
import filip.bedwars.game.state.GameState;
import filip.bedwars.game.state.GameStateSetting;
import filip.bedwars.inventory.ClickableInventory;
import filip.bedwars.inventory.IClickable;
import filip.bedwars.utils.EnderDragonController;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.PlayerUtils;
import filip.bedwars.utils.SoundPlayer;
import filip.bedwars.utils.TeamColorConverter;
import filip.bedwars.utils.VillagerNPC;
import filip.bedwars.world.GameWorld;
import filip.bedwars.world.GameWorldManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.nbt.CompoundTag;

public class GameLogic implements Listener {

	private Game game;
	private Arena arena;
	private GameWorld gameWorld;
	private GameState gameState;
	private ArrayDeque<GameState> gameStates = new ArrayDeque<GameState>();
	private BukkitRunnable gameTicker;
	private List<VillagerNPC> itemShopNPCs = new ArrayList<VillagerNPC>();
	private List<VillagerNPC> teamShopNPCs = new ArrayList<VillagerNPC>();
	private List<IClickable> itemShopClickables = new ArrayList<IClickable>();
	private List<IClickable> teamShopClickables = new ArrayList<IClickable>();
	private Map<UUID, Integer> selectedItemShopCategory = new HashMap<UUID, Integer>();
	private BukkitTask bukkitTask;
	private BukkitRunnable bukkitRunnable;
	public final Set<EnderDragonController> enderDragonControllers = new HashSet<>();
	public final ScoreboardManager scoreboardManager;
	public boolean allBedsPermDestroyed = false;
	
	public GameLogic(Game game, Arena arena, GameWorld gameWorld) {
		this.game = game;
		this.arena = arena;
		this.gameWorld = gameWorld;
		scoreboardManager = new ScoreboardManager(game, this);
		
		// Destroy beds of empty teams
		for (Team team : game.getTeams())
			if (team.getMembers().size() == 0)
				team.destroyBed(gameWorld.getWorld());
		
		for (GameStateSetting gameStateSetting : GameStatesConfig.getInstance().getGameStateSettings())
			gameStates.add(new GameState(gameStateSetting, game, this));
		
		Action gameEndStartAction = new Action() {
			@Override
			public void execute(@NotNull Game game, @NotNull GameLogic gameLogic) {
				Team winnerTeam = game.isOver();
				
				if (winnerTeam == null) {
					// No Team wins
					for (Player p : gameWorld.getWorld().getPlayers())
						MessageSender.sendMessage(p, MessagesConfig.getInstance().getStringValue(p.getLocale(), "no-team-wins"));
				} else {
					for (Player p : gameWorld.getWorld().getPlayers()) {
						String teamHasWonMsg = MessagesConfig.getInstance().getStringValue(p.getLocale(), "team-has-won").replace("%teamcolor%", TeamColorConverter.convertTeamColorToStringForMessages(winnerTeam.getBase().getTeamColor(), p.getLocale()));
						MessageSender.sendMessage(p, teamHasWonMsg);
						SoundPlayer.playSound("victory", p);
						
						if(winnerTeam.containsMember(p.getUniqueId()))
							p.sendTitle(MessagesConfig.getInstance().getStringValue(p.getLocale(), "victory"), teamHasWonMsg, 10, 70, 20);
					}
					
					Set<Player> winners = new HashSet<>();
					
					for (GamePlayer gp : winnerTeam.getMembers())
						winners.add(gp.getPlayer());
					
					BedwarsPlugin.getInstance().getServer().getPluginManager().callEvent(new BedwarsVictoryEvent(winners));
				}
			}
		};
		
		Action gameEndEndAction = new Action() {
			@Override
			public void execute(@NotNull Game game, @NotNull GameLogic gameLogic) {
				game.endGame();
			}
		};
		
		GameStateSetting gameEndSetting = new GameStateSetting("Game End", 10, null, null, new ArrayList<Action>() {{
			add(gameEndStartAction);
		}}, new ArrayList<Action>() {{
			add(gameEndEndAction);
		}});
		
		gameStates.add(new GameState(gameEndSetting, game, this));
		
		initiateNextGameState();
		
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

		BedwarsPlugin.getInstance().getServer().getPluginManager().registerEvents(this, BedwarsPlugin.getInstance());
		
		List<GamePlayer> syncPlayersList = game.getPlayers();
		Player[] players;
		int i = 0;
		synchronized (syncPlayersList) {
			players = new Player[syncPlayersList.size()];
			
			for (GamePlayer gamePlayer : syncPlayersList) {
				Player player = gamePlayer.getPlayer();
				players[i] = player;
				// Teleport player to the spawnpoint of their base
				teleportToSpawn(gamePlayer);
				
				if (!MainConfig.getInstance().getAttackCooldown())
					// Disable attack cooldown
					player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(20);
				
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

			// Setup team shop NPC, if it is not null
			if (base.getTeamShop(gameWorld.getWorld()) != null) {
				VillagerNPC teamShopNPC = new VillagerNPC(base.getTeamShop(gameWorld.getWorld()).clone().add(0.5, 0, 0.5), "SNOW", "CLERIC", MainConfig.getInstance().getTeamShopName(), players);
				teamShopNPCs.add(teamShopNPC);
			}
		}
		
		for (GamePlayer gamePlayer : syncPlayersList) {
			itemShopClickables.add(new ClickableInventory(ItemShopConfig.getInstance().getShop().getCategoryListInventory(), gamePlayer.getPlayer()) {
				@Override
				public void click(InventoryClickEvent event) {
					HumanEntity player = event.getWhoClicked();
					selectedItemShopCategory.put(player.getUniqueId(), ItemShopConfig.getInstance().getShop().handleClick(selectedItemShopCategory.getOrDefault(player.getUniqueId(), -1), event, gamePlayer));
				}

				@Override
				public void drag(InventoryDragEvent event) {}
			});
			
			teamShopClickables.add(new ClickableInventory(TeamShopConfig.getInstance().getShop().getCategoryListInventory(), gamePlayer.getPlayer()) {
				@Override
				public void click(InventoryClickEvent event) {
					HumanEntity player = event.getWhoClicked();
					selectedItemShopCategory.put(player.getUniqueId(), TeamShopConfig.getInstance().getShop().handleClick(selectedItemShopCategory.getOrDefault(player.getUniqueId(), -1), event, gamePlayer));
				}

				@Override
				public void drag(InventoryDragEvent event) {}
			});
		}
		
		bukkitRunnable = new BukkitRunnable() {
			@Override
			public void run() {
				for(Team team : game.getTeams()) {
					int healPoolLevel = team.upgrades.get(TeamUpgradeType.HEAL_POOL);
					int miningBoostLevel = team.upgrades.get(TeamUpgradeType.MINING_BOOST);
					int attackBoostLevel = team.upgrades.get(TeamUpgradeType.ATTACK_BOOST);
					int protectionBoostLevel = team.upgrades.get(TeamUpgradeType.PROTECTION_BOOST);
					Location baseSpawn = team.getBase().getSpawn(gameWorld.getWorld());
					
					if (healPoolLevel > 0) {
						for (GamePlayer gp : team.getMembers()) {
							Player p = gp.getPlayer();
							
							if (p.getGameMode() == GameMode.SPECTATOR)
								continue;
							
							// TODO: Do not hard code this value
							if (p.getLocation().distance(baseSpawn) < 20) {
								if (p.hasPotionEffect(PotionEffectType.REGENERATION) &&
									p.getPotionEffect(PotionEffectType.REGENERATION).getAmplifier() <= (healPoolLevel - 1))
									p.removePotionEffect(PotionEffectType.REGENERATION);
								
								p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, healPoolLevel - 1, true, false, true));
							}
						}
					}
					
					if (miningBoostLevel > 0) {
						for (GamePlayer gp : team.getMembers()) {
							Player p = gp.getPlayer();
							
							if (p.getGameMode() == GameMode.SPECTATOR)
								continue;
							
							p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, miningBoostLevel - 1, true, false, true));
						}
					}
					
					if (attackBoostLevel > 0) {
						for (GamePlayer gp : team.getMembers()) {
							Player p = gp.getPlayer();
							
							if (p.getGameMode() == GameMode.SPECTATOR)
								continue;
							
							p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, attackBoostLevel - 1, true, false, true));
						}
					}
					
					if (protectionBoostLevel > 0) {
						for (GamePlayer gp : team.getMembers()) {
							Player p = gp.getPlayer();
							
							if (p.getGameMode() == GameMode.SPECTATOR)
								continue;
							
							p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, protectionBoostLevel - 1, true, false, true));
						}
					}
				}
			}
		};
		
		try {
			bukkitTask = bukkitRunnable.runTaskTimer(BedwarsPlugin.getInstance(), 0, 20L);
		} catch (IllegalPluginAccessException e) {}
		
		scoreboardManager.update();
	}
	
	private void teleportToSpawn(GamePlayer gamePlayer) {
		if (gamePlayer == null)
			return;
		
		Location spawnLoc = gamePlayer.getTeam().getBase().getSpawn(gameWorld.getWorld());
		Player player = gamePlayer.getPlayer();
		player.teleport(spawnLoc);
		PlayerUtils.playerReset(player);
		
		for (ItemStack is : MainConfig.getInstance().getSpawnItems())
			player.getInventory().addItem(is);
	}
	
	public void joinSpectator(Player player) {
		player.teleport(getSpectatorSpawn());
		PlayerUtils.playerReset(player);
		player.setGameMode(GameMode.SPECTATOR);
		MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "joined-game-as-spectator"));
		
		for (VillagerNPC itemShopNPC : itemShopNPCs)
			itemShopNPC.respawn(player);
		
		for (VillagerNPC teamShopNPC : teamShopNPCs)
			teamShopNPC.respawn(player);
		
		for (EnderDragonController enderDragonController : enderDragonControllers)
			enderDragonController.addViewer(player);

		for (Player p : gameWorld.getWorld().getPlayers()) {
			if (game.containsPlayer(p.getUniqueId())) {
				// Show the new spectator all of the game players
				player.showPlayer(BedwarsPlugin.getInstance(), p);
				// Hide the new spectator from the game players
				p.hidePlayer(BedwarsPlugin.getInstance(), player);
			} else {
				// Show the new spectator all of the other spectators
				player.showPlayer(BedwarsPlugin.getInstance(), p);
			}
		}

		scoreboardManager.update(player);
	}
	
	public void leavePlayer(Player player) {
		player.spigot().respawn();
		player.teleport(MainConfig.getInstance().getMainLobby());
		removePlayerListeners(player);
		
		for (EnderDragonController enderDragonController : enderDragonControllers) {
			enderDragonController.removeViewer(player);
			enderDragonController.removeTargetEntity(player);
		}
		
		scoreboardManager.update();
		scoreboardManager.reset(player);
		
		checkGameOver();
	}
	
	public GameWorld getGameWorld() {
		return gameWorld;
	}
	
	public GameState getGameState() {
		return gameState;
	}
	
	public GameState getNextGameState() {
		return gameStates.peek();
	}
	
	public GameState getLastGameEndGameState() {
		return gameStates.peekLast();
	}
	
	public void cleanup() {
		gameTicker.cancel();
		
		List<GamePlayer> syncPlayersList = game.getPlayers();
		synchronized (syncPlayersList) {
			for (GamePlayer gamePlayer : syncPlayersList)
				leavePlayer(gamePlayer.getPlayer());
		}
		
		for (EnderDragonController enderDragonController : enderDragonControllers)
			enderDragonController.stopTask();
		
		for (Player p : gameWorld.getWorld().getPlayers()) {
			PlayerUtils.playerReset(p);
			p.teleport(MainConfig.getInstance().getMainLobby());
		}
		
		HandlerList.unregisterAll(this);
		
		bukkitRunnable.cancel();
		bukkitTask.cancel();
		
		GameWorldManager.getInstance().removeGameWorld(gameWorld);
	}
	
	public void initiateNextGameState() {
		if (gameStates.peek() == gameStates.getLast()) {
			// Initiate game end state
			initiateLastGameEndState();
		} else if (gameState == gameStates.getLast()) {
			// The Game is already over
		} else {
			// Initiate next game state
			gameState = gameStates.remove();
			gameState.initiate();
		}
	}
	
	public void initiateLastGameEndState() {
		// Check if the game is not already over
		if (gameState != gameStates.getLast()) {
			gameState = gameStates.getLast(); // Must not be .remove() because it would invalidate the game over checks
			gameState.initiate();
		}
	}
	
	@EventHandler
	public void onPlayerUseUnknownEntity(PlayerUseUnknownEntityEvent event) {
		Player player = event.getPlayer();

		// Check if the player is part of this game
		if (!game.containsPlayer(player.getUniqueId()))
			return;

		if (!event.isAttack()) {
			for (VillagerNPC npc : itemShopNPCs) {
				if (npc.getEntityId() == event.getEntityId()) {
					player.openInventory(ItemShopConfig.getInstance().getShop().getCategoryListInventory());
					break;
				}
			}

			for (VillagerNPC npc : teamShopNPCs) {
				if (npc.getEntityId() == event.getEntityId()) {
					player.openInventory(TeamShopConfig.getInstance().getShop().getCategoryListInventory());
					break;
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		// Check if the player is a spectator
		if (!game.containsPlayer(player.getUniqueId()) && player.getWorld().getName().equals(getGameWorld().getWorld().getName()))
			event.setCancelled(true);
		
		if (event.getClickedBlock() != null && !event.getClickedBlock().hasMetadata("bedwars_placed")) {
			if (event.getAction() == org.bukkit.event.block.Action.PHYSICAL) {
				if (event.getClickedBlock().getType() == Material.FARMLAND)
					event.setCancelled(true);
			} else if (event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
				if (event.getClickedBlock().getType() == Material.FLOWER_POT || event.getClickedBlock().getType().toString().startsWith("POTTED_"))
					event.setCancelled(true);
			}
		}
		
		if (event.hasItem()) {
			if (event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_AIR || event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
				try {
					net.minecraft.world.item.ItemStack nmsItemStack = (net.minecraft.world.item.ItemStack) BedwarsPlugin.getInstance().reflectionUtils.craftItemStackAsNMSCopyMethod.invoke(null, event.getItem());
					//net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(event.getItem());
					//Object nmsItemStack = BedwarsPlugin.getInstance().reflectionUtils.craftItemStackAsNMSCopyMethod.invoke(null, event.getItem());
					//boolean hasTag = nmsItemStack.hasTag();
					boolean hasTag = (boolean) BedwarsPlugin.getInstance().reflectionUtils.itemStackHasTagMethod.invoke(nmsItemStack);
					boolean hasKey = false;
					if (hasTag) {
						CompoundTag nbtTagCompound = (CompoundTag)BedwarsPlugin.getInstance().reflectionUtils.itemStackGetOrCreateTagMethod.invoke(nmsItemStack);
						hasKey = (boolean)BedwarsPlugin.getInstance().reflectionUtils.compoundTagHasKeyMethod.invoke(nbtTagCompound, "bedwars-fireball");
						//hasKey = nmsItemStack.getTag().contains("bedwars-fireball");
					}

					if (hasTag && hasKey) {
						boolean shouldLaunchFireball = false;
						
						if (event.getHand() == EquipmentSlot.HAND) {
							net.minecraft.world.item.ItemStack nmsOffHandItemStack = (net.minecraft.world.item.ItemStack) BedwarsPlugin.getInstance().reflectionUtils.craftItemStackAsNMSCopyMethod.invoke(null, player.getInventory().getItemInOffHand());
							//net.minecraft.world.item.ItemStack nmsOffHandItemStack = CraftItemStack.asNMSCopy(player.getInventory().getItemInOffHand());
							//Object nmsOffHandItemStack = BedwarsPlugin.getInstance().reflectionUtils.craftItemStackAsNMSCopyMethod.invoke(null, player.getInventory().getItemInOffHand());

							//hasTag = nmsOffHandItemStack.hasTag();
							hasTag = (boolean) BedwarsPlugin.getInstance().reflectionUtils.itemStackHasTagMethod.invoke(nmsOffHandItemStack);
							if (hasTag) {
								CompoundTag nbtTagCompound = (CompoundTag)BedwarsPlugin.getInstance().reflectionUtils.itemStackGetOrCreateTagMethod.invoke(nmsOffHandItemStack);
								hasKey = (boolean)BedwarsPlugin.getInstance().reflectionUtils.compoundTagHasKeyMethod.invoke(nbtTagCompound, "bedwars-fireball");
								//hasKey = nmsOffHandItemStack.getTag().contains("bedwars-fireball");
								//hasKey = (boolean) BedwarsPlugin.getInstance().reflectionUtils.compoundTagHasKeyMethod.invoke(nmsOffHandItemStack.getTag(), "bedwars-fireball");
							}

							if (!(hasTag && hasKey))
								shouldLaunchFireball = true;
						} else {
							shouldLaunchFireball = true;
						}
						
						if (shouldLaunchFireball) {
							Fireball fireball = (Fireball) gameWorld.getWorld().spawnEntity(player.getLocation().clone().add(player.getLocation().getDirection()).add(0, 1, 0), EntityType.FIREBALL);
							fireball.setVelocity(player.getLocation().getDirection());
							fireball.setYield(3);
							event.setCancelled(true);
							event.getItem().subtract();
							SoundPlayer.playSound("fireball-shoot", player);
						}
					}
				} catch (IllegalArgumentException | SecurityException | IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
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
			if (event.getEntity().getType() == EntityType.PLAYER) {
				GamePlayer damagerGamePlayer = game.getGamePlayer(damager.getUniqueId());
				GamePlayer gamePlayer = game.getGamePlayer(event.getEntity().getUniqueId());
				
				if (damagerGamePlayer.getTeam() == gamePlayer.getTeam())
					event.setCancelled(true);
			} else if (event.getEntity().getType() == EntityType.ITEM_FRAME) {
				event.setCancelled(true);
			}
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
		
		for (Base base : game.getArena().getBases()) {
			if (base.getSpawn(gameWorld.getWorld()).getBlock().getLocation().distance(block.getLocation()) <= 2) {
				event.setCancelled(true);
				MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "you-cant-place-blocks-near-spawn"));
				return;
			}
		}
		
		//String serverVersion = BedwarsPlugin.getInstance().getServerVersion();
		try {
			//Class<?> craftItemStackClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".inventory.CraftItemStack");
			//Method asNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
			//Class<?> nmsItemStackClass = Class.forName("net.minecraft.server." + serverVersion + ".ItemStack");
			//Method hasTagMethod = nmsItemStackClass.getMethod("hasTag");
			//Method getTagMethod = nmsItemStackClass.getMethod("getTag");
			//Class<?> nbtTagCompoundClass = Class.forName("net.minecraft.server." + serverVersion + ".NBTTagCompound");
			//Method hasKeyMethod = nbtTagCompoundClass.getMethod("hasKey", String.class);

			net.minecraft.world.item.ItemStack nmsItemStack = (net.minecraft.world.item.ItemStack) BedwarsPlugin.getInstance().reflectionUtils.craftItemStackAsNMSCopyMethod.invoke(null, event.getItemInHand());
			//net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(event.getItemInHand());
			//Object nmsItemStack = asNMSCopyMethod.invoke(null, event.getItemInHand());
			
			//boolean hasTag = nmsItemStack.hasTag();
			boolean hasTag = (boolean) BedwarsPlugin.getInstance().reflectionUtils.itemStackHasTagMethod.invoke(nmsItemStack);
			boolean hasKey = false;
			if (hasTag) {
				CompoundTag nbtTagCompound = (CompoundTag)BedwarsPlugin.getInstance().reflectionUtils.itemStackGetOrCreateTagMethod.invoke(nmsItemStack);
				hasKey = (boolean)BedwarsPlugin.getInstance().reflectionUtils.compoundTagHasKeyMethod.invoke(nbtTagCompound, "bedwars-blast-proof");
				//hasKey = nmsItemStack.getTag().contains("bedwars-blast-proof");
				//hasKey = (boolean) hasKeyMethod.invoke(getTagMethod.invoke(nmsItemStack), "bedwars-blast-proof");
			}

			if (hasTag && hasKey)
				block.setMetadata("bedwars_blast_proof", new FixedMetadataValue(BedwarsPlugin.getInstance(), true));
			else
				block.removeMetadata("bedwars_blast_proof", BedwarsPlugin.getInstance());
		} catch (IllegalArgumentException | SecurityException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
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
							scoreboardManager.update();
							event.setCancelled(true);
							broadcastBedDestroyed(player, team);
							BedwarsPlugin.getInstance().getServer().getPluginManager().callEvent(new BedwarsBedBrokenByPlayerEvent(player));
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
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		List<Block> blocks = event.getBlocks();
		
		if (event.getBlock().hasMetadata("bedwars_placed")) {
			for (Block b : blocks) {
				if (!b.hasMetadata("bedwars_placed")) {
					event.setCancelled(true);
					return;
				}
			}
		}
		
		BlockFace direction = event.getDirection();
		ListIterator<Block> iterator = blocks.listIterator(blocks.size());
		
		while (iterator.hasPrevious()) {
			Block b = iterator.previous();
			
			if (b.hasMetadata("bedwars_placed")) {
				b.removeMetadata("bedwars_placed", BedwarsPlugin.getInstance());
				b.getLocation().clone().add(direction.getDirection()).getBlock().setMetadata("bedwars_placed", new FixedMetadataValue(BedwarsPlugin.getInstance(), true));
				
				if (b.hasMetadata("bedwars_blast_proof")) {
					b.removeMetadata("bedwars_blast_proof", BedwarsPlugin.getInstance());
					b.getLocation().clone().add(direction.getDirection()).getBlock().setMetadata("bedwars_blast_proof", new FixedMetadataValue(BedwarsPlugin.getInstance(), true));
				}
			} else {
				b.getLocation().clone().add(direction.getDirection()).getBlock().removeMetadata("bedwars_placed", BedwarsPlugin.getInstance());
			}
		}
	}
	
	@EventHandler
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		List<Block> blocks = event.getBlocks();
		
		if (event.getBlock().hasMetadata("bedwars_placed")) {
			for (Block b : blocks) {
				if (!b.hasMetadata("bedwars_placed")) {
					event.setCancelled(true);
					return;
				}
			}
		}
		
		BlockFace direction = event.getDirection();
		ListIterator<Block> iterator = blocks.listIterator(blocks.size());
		
		while (iterator.hasPrevious()) {
			Block b = iterator.previous();
			
			if (b.hasMetadata("bedwars_placed")) {
				b.removeMetadata("bedwars_placed", BedwarsPlugin.getInstance());
				b.getLocation().clone().add(direction.getDirection()).getBlock().setMetadata("bedwars_placed", new FixedMetadataValue(BedwarsPlugin.getInstance(), true));
				
				if (b.hasMetadata("bedwars_blast_proof")) {
					b.removeMetadata("bedwars_blast_proof", BedwarsPlugin.getInstance());
					b.getLocation().clone().add(direction.getDirection()).getBlock().setMetadata("bedwars_blast_proof", new FixedMetadataValue(BedwarsPlugin.getInstance(), true));
				}
			} else {
				b.getLocation().clone().add(direction.getDirection()).getBlock().removeMetadata("bedwars_placed", BedwarsPlugin.getInstance());
			}
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		// Check if the explosion is in the game world
		if (event.getLocation().getWorld().getName().equals(getGameWorld().getWorld().getName())) {
			if (event.getEntityType() == EntityType.ENDER_DRAGON)
				return;
			
			List<Block> blockListCopy = new ArrayList<Block>();
	        blockListCopy.addAll(event.blockList());
	        
	        for (Block block : blockListCopy) {
	        	if (block.getBlockData() instanceof Bed)
	        		event.blockList().remove(block);
	        	
	        	if (!block.hasMetadata("bedwars_placed") || block.hasMetadata("bedwars_blast_proof"))
	        		event.blockList().remove(block);
	        }
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		
		// Check if the player is in the game world
		if (player.getWorld().getName().equals(getGameWorld().getWorld().getName())) {
			Component deathMessage = event.deathMessage();
			event.setDeathMessage(null);
			GamePlayer gamePlayer = game.getGamePlayer(player.getUniqueId());
			
			if (gamePlayer == null)
				return;
			
			Team team = gamePlayer.getTeam();
			
			if (MainConfig.getInstance().getDropOnlySpawnerResourcesOnDeath()) {
				// Only drop spawner resources
				Iterator<ItemStack> iter = event.getDrops().iterator();
				
				while (iter.hasNext()) {
					ItemStack itemStack = iter.next();
					boolean removeItem = true;
					
					for (SpawnerType spawnerType : SpawnerConfig.getInstance().getSpawnerTypes()) {
						if (itemStack.getType() == spawnerType.getMaterial() && itemStack.hasItemMeta()) {
							ItemMeta itemMeta = itemStack.getItemMeta();
							
							if (itemMeta.hasDisplayName() && itemMeta.getDisplayName().equals(spawnerType.getName()))
								removeItem = false;
						}
					}
					
					if (removeItem)
						iter.remove();
				}
			}
			
			if (team != null) {
				EntityDamageEvent entityDamageEvent = player.getLastDamageCause();
				
				if (entityDamageEvent != null) {
					Player killer = player.getKiller();
					
					if (killer != null) {
						SoundPlayer.playSound("kill", killer);
						BedwarsPlugin.getInstance().getServer().getPluginManager().callEvent(new BedwarsKillEvent(killer, player));
						
						if (entityDamageEvent.getCause() == DamageCause.PROJECTILE || entityDamageEvent.getCause() == DamageCause.VOID) {
							Iterator<ItemStack> iter = event.getDrops().iterator();
							
							while (iter.hasNext()) {
								ItemStack itemStack = iter.next();
								
								if (itemStack == null)
									continue;
								
								if (!event.getItemsToKeep().contains(itemStack)) {
									iter.remove();
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
					team.removeMember(gamePlayer);
					
					if (team.getMembers().size() == 0)
						team.destroyBed(gameWorld.getWorld());
					
					scoreboardManager.update();
					isFinalKill = true;
					
					List<GamePlayer> syncPlayersList = game.getPlayers();
					synchronized (syncPlayersList) {
						syncPlayersList.remove(gamePlayer);
					}
					
					checkGameOver();

					Bukkit.getScheduler().scheduleSyncDelayedTask(BedwarsPlugin.getInstance(), () -> {
						player.spigot().respawn();
						
						for (EnderDragonController enderDragonController : enderDragonControllers)
							enderDragonController.removeTargetEntity(player);
						
						joinSpectator(player);
					}, 1L);
				} else {
					gamePlayer.scheduleCountdown(new Countdown(MainConfig.getInstance().getRespawnDelay()) {
						@Override
						public void onTick() {
							if (getSecondsLeft() != 0) {
								MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "you-will-respawn-in").replace("%seconds%", "" + getSecondsLeft()));
								player.sendTitle(
										MessagesConfig.getInstance().getStringValue(player.getLocale(), "you-died"),
										MessagesConfig.getInstance().getStringValue(player.getLocale(), "you-will-respawn-in").replace("%seconds%", "" + getSecondsLeft()),
										0, 25, 10);
							}
						}
						
						@Override
						public void onStart() {
							player.setItemOnCursor(null);
							player.spigot().respawn();
							player.setGameMode(GameMode.SPECTATOR);
						}
						
						@Override
						public boolean onFinish() {
							respawnPlayerAtBase(player);
							return false;
						}
						
						@Override
						public void onCancel() {}
					});
				}

				for (Player p : gameWorld.getWorld().getPlayers()) {
					LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
					TextComponent c = serializer.deserialize(MessagesConfig.getInstance().getStringValue(p.getLocale(), "prefix")).append(deathMessage);

					if (isFinalKill)
						c = c.append(serializer.deserialize(MessagesConfig.getInstance().getStringValue(player.getLocale(), "final-kill")));

					p.sendMessage(c);
				}

				// Use for versions below 1.16:
				/*for (Player p : gameWorld.getWorld().getPlayers()) {
					ReflectionUtils reflectionUtils = BedwarsPlugin.getInstance().reflectionUtils;

					try {
						ServerPlayer entityPlayerVictim = reflectionUtils.playerToNMSPlayer(player);
						ServerPlayer entityPlayer = reflectionUtils.playerToNMSPlayer(p);

						net.minecraft.network.chat.MutableComponent deathMessageComponent = (net.minecraft.network.chat.MutableComponent)BedwarsPlugin.getInstance().reflectionUtils.componentNullToEmptyMethod.invoke(null, MessagesConfig.getInstance().getStringValue(p.getLocale(), "prefix"));
						Object combatTracker = BedwarsPlugin.getInstance().reflectionUtils.entityLivingGetCombatTrackerMethod.invoke(entityPlayerVictim);
						Object component = BedwarsPlugin.getInstance().reflectionUtils.combatTrackerGetDeathMessageMethod.invoke(combatTracker);
						BedwarsPlugin.getInstance().reflectionUtils.mutableComponentAppendMethod.invoke(deathMessageComponent, component);

						if(isFinalKill) {
							net.minecraft.network.chat.Component finalKillComponent = (net.minecraft.network.chat.Component)BedwarsPlugin.getInstance().reflectionUtils.componentNullToEmptyMethod.invoke(null, MessagesConfig.getInstance().getStringValue(player.getLocale(), "final-kill"));
							BedwarsPlugin.getInstance().reflectionUtils.mutableComponentAppendMethod.invoke(deathMessageComponent, finalKillComponent);
						}

						BedwarsPlugin.getInstance().reflectionUtils.nmsPlayerSendSystemMessage(entityPlayer, deathMessageComponent);
					} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}*/
			}
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		
		// Check if the player is in the game world
		if (player.getWorld().getName().equals(getGameWorld().getWorld().getName()))
			event.setRespawnLocation(getSpectatorSpawn());
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		// Check if the player is in the game world
		if (player.getWorld().getName().equals(getGameWorld().getWorld().getName())) {
			if (event.getTo().getY() < player.getWorld().getMinHeight()) {
				// Check if the player is a game player
				if (game.containsPlayer(player.getUniqueId()) && player.getGameMode() != GameMode.SPECTATOR)
					PlayerUtils.damagePlayerVoid(player, 999);
				else
					player.teleport(getSpectatorSpawn());
			}
			
			GamePlayer gamePlayer = game.getGamePlayer(player.getUniqueId());
			
			if (gamePlayer != null) {
				Team playerTeam = gamePlayer.getTeam();
				
				for (Team team : game.getTeams()) {
					if (team == playerTeam)
						continue;
					
					Iterator<Trap> iter = team.getTraps().iterator();
					
					while (iter.hasNext()) {
						Trap trap = iter.next();
						
						if (player.getLocation().distance(team.getBase().getBedTop(gameWorld.getWorld())) <= trap.getRange()) {
							SoundPlayer.playSound("trap", player);
							MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "trap-you-triggered"));
							
							for (PotionEffect effect : trap.getEffectsIntruder())
								player.addPotionEffect(effect);
							
							for (GamePlayer gp : team.getMembers()) {
								Player p = gp.getPlayer();
								SoundPlayer.playSound("trap", p);
								String msg = MessagesConfig.getInstance().getStringValue(p.getLocale(), "trap-triggered").replace("%player%", player.getName());
								MessageSender.sendMessage(p, msg);
								p.sendTitle(MessagesConfig.getInstance().getStringValue(p.getLocale(), "trap-alert"), msg, 10, 70, 20);
								
								for (PotionEffect effect : trap.getEffectsTeam())
									p.addPotionEffect(effect);
							}
							
							iter.remove();
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();

		if (event.getFrom().getName().equals(gameWorld.getWorld().getName())) {
			scoreboardManager.reset(player);

			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (p.getWorld().getName().equals(gameWorld.getWorld().getName())) {
					player.hidePlayer(BedwarsPlugin.getInstance(), p);
					p.hidePlayer(BedwarsPlugin.getInstance(), player);
				} else if (GameManager.getInstance().getGameOfPlayer(p) == null && GameManager.getInstance().getGameOfPlayer(player) == null) {
					p.showPlayer(BedwarsPlugin.getInstance(), player);
					player.showPlayer(BedwarsPlugin.getInstance(), p);
				}
			}
		}
	}
	
	@EventHandler
	public void onHangingBreak(HangingBreakEvent event) {
		// Check if the hanging is in the game world
		if (event.getEntity().getWorld().getName().equals(gameWorld.getWorld().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		
		if (entity.getWorld().getName().equals(gameWorld.getWorld().getName())) {
			if (entity.getType() == EntityType.ITEM_FRAME)
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {
		if (event.getView().getPlayer().getWorld().getName().equals(gameWorld.getWorld().getName()))
			event.getInventory().setResult(null);
	}
	
	@EventHandler
	public void onEnchantItem(EnchantItemEvent event) {
		if (event.getEnchanter().getWorld().getName().equals(gameWorld.getWorld().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onPrepareAnvil(PrepareAnvilEvent event) {
		if (event.getView().getPlayer().getWorld().getName().equals(gameWorld.getWorld().getName()))
			event.setResult(null);
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		HumanEntity humanEntity = event.getPlayer();
		
		if (humanEntity.getWorld().getName().equals(gameWorld.getWorld().getName())) {
			switch (event.getInventory().getType()) {
			case ANVIL:
			case BEACON:
			case BLAST_FURNACE:
			case BREWING:
			case CARTOGRAPHY:
			case DISPENSER:
			case DROPPER:
			case ENCHANTING:
			case FURNACE:
			case GRINDSTONE:
			case HOPPER:
			case LECTERN:
			case LOOM:
			case MERCHANT:
			case SMOKER:
			case STONECUTTER:
			case WORKBENCH:
				event.setCancelled(true);
				break;
			case ENDER_CHEST:
				event.setCancelled(true);
				Team team = game.getTeamOfPlayer(humanEntity.getUniqueId());
				
				if (team != null)
					humanEntity.openInventory(team.getTeamChestInventory());
			default:
			}
		}
	}
	
	@EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getPlayer().getWorld().getName().equals(gameWorld.getWorld().getName()) && event.getCause().equals(TeleportCause.SPECTATE))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.getBlock().getWorld().getName().equals(gameWorld.getWorld().getName())) {
			if (event.getBlock().hasMetadata("bedwars_placed"))
				return;
			
			if (event.getChangedType() != Material.AIR) {
				Material material = event.getBlock().getType();
				
				if (Tag.SMALL_FLOWERS.getValues().contains(material) || Tag.SAPLINGS.getValues().contains(material))
					event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		
		if (!player.getWorld().getName().equals(gameWorld.getWorld().getName()))
			return;
		
		event.setCancelled(true);
		String msg = event.getMessage();
		GamePlayer gamePlayer = game.getGamePlayer(player.getUniqueId());
		
		if (gamePlayer == null) {
			for (Player p : gameWorld.getWorld().getPlayers()) {
				if (!game.containsPlayer(p.getUniqueId()))
					p.sendMessage(
							MessagesConfig.getInstance().getStringValue(p.getLocale(), "chat-prefix-spectator")
									.replace("%player%", player.getName())
									.replace("%msg%", msg));
			}
		} else {
			if (msg.startsWith("@")) {
				for (Player p : gameWorld.getWorld().getPlayers())
					p.sendMessage(
							MessagesConfig.getInstance().getStringValue(p.getLocale(), "chat-prefix-all")
									.replace("%player%", player.getName())
									.replace("%msg%", msg.substring(1))
									.replace("%team%", TeamColorConverter.convertTeamColorToStringForMessages(gamePlayer.getTeam().getBase().getTeamColor(), p.getLocale())));
			} else {
				for (GamePlayer gp : gamePlayer.getTeam().getMembers()) {
					Player p = gp.getPlayer();
					p.sendMessage(
							MessagesConfig.getInstance().getStringValue(p.getLocale(), "chat-prefix-team")
									.replace("%player%", player.getName())
									.replace("%msg%", msg)
									.replace("%team%", TeamColorConverter.convertTeamColorToStringForMessages(gamePlayer.getTeam().getBase().getTeamColor(), p.getLocale())));
				}
			}
		}
	}
	
	private void removePlayerListeners(Player player) {
		for (IClickable itemShopClickable : itemShopClickables) {
			if (itemShopClickable.getPlayer().equals(player)) {
				BedwarsPlugin.getInstance().removeClickable(itemShopClickable);
				break;
			}
		}

		for (IClickable teamShopClickable : teamShopClickables) {
			if (teamShopClickable.getPlayer().equals(player)) {
				BedwarsPlugin.getInstance().removeClickable(teamShopClickable);
				break;
			}
		}
	}
	
	private void checkGameOver() {
		if (gameState == gameStates.getLast())
			// Game is already over
			return;
		
		Team winnerTeam = game.isOver();
		
		if (winnerTeam != null)
			// Game is over
			runGameOver();
	}
	
	private void runGameOver() {
		// Make sure that the countdown of the current game state is cancelled
		gameState.getCountdown().cancel();
		
		// Initiate game over phase
		initiateLastGameEndState();
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
	
	private Location getSpectatorSpawn() {
		Location spectatorSpawn = arena.getSpectatorSpawn(gameWorld.getWorld());
		
		if (spectatorSpawn == null)
			return game.getTeams().get(0).getBase().getSpawn(gameWorld.getWorld());
		
		return spectatorSpawn;
	}
	
	private void respawnPlayerAtBase(Player player) {
		PlayerUtils.playerReset(player);
		Team team = game.getTeamOfPlayer(player.getUniqueId());
		
		if (team != null) {
			player.teleport(team.getBase().getSpawn(gameWorld.getWorld()));
			
			if (!MainConfig.getInstance().getAttackCooldown())
				// Disable attack cooldown
				player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(20);
		}
		
		for (VillagerNPC itemShopNPC : itemShopNPCs)
			itemShopNPC.respawn(player);
		
		for (VillagerNPC teamShopNPC : teamShopNPCs)
			teamShopNPC.respawn(player);
		
		for (EnderDragonController enderDragonController : enderDragonControllers)
			enderDragonController.respawn(player);
		
		for (ItemStack is : MainConfig.getInstance().getSpawnItems())
			player.getInventory().addItem(is);
	}
	
}
