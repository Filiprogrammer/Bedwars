package filip.bedwars.game.arena.setup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.BedwarsPlugin.FinishArenaSetupResponse;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.TeamColor;
import filip.bedwars.game.arena.Arena;
import filip.bedwars.inventory.ClickableInventory;
import filip.bedwars.inventory.IClickable;
import filip.bedwars.inventory.IPlacable;
import filip.bedwars.inventory.IUsable;
import filip.bedwars.inventory.ItemBuilder;
import filip.bedwars.inventory.PlacableItem;
import filip.bedwars.inventory.UsableItem;
import filip.bedwars.listener.player.IPacketListener;
import filip.bedwars.listener.player.UseEntityPacketListener;
import filip.bedwars.utils.ArmorStandItemNPC;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.PlayerNPC;
import filip.bedwars.utils.SoundPlayer;
import filip.bedwars.utils.VillagerNPC;

public class ArenaSetup {

	private final List<IUsable> usables = new ArrayList<IUsable>();
	private final List<IPlacable> placables = new ArrayList<IPlacable>();
	private final List<IClickable> clickables = new ArrayList<IClickable>();
	
	private SpawnerBuilder spawnerBuilder;
	private BaseBuilder baseBuilder;
	private ArenaBuilder arenaBuilder;
	private final Player setuper;
	private ItemStack spawnerItem;
	private ItemStack itemShopItem;
	private ItemStack teamShopItem;
	private ItemStack spawnItem;
	private ItemStack bedItem;
	private ItemStack createBaseItem;
	private ItemStack cancelSetupItem;
	private ItemStack finishSetupItem;
	private VillagerNPC itemShopNPC;
	private VillagerNPC teamShopNPC;
	private PlayerNPC spawnNPC;
	private Map<ArmorStandItemNPC, IPacketListener> spawnerNPCs = new HashMap<ArmorStandItemNPC, IPacketListener>();
	
	public ArenaSetup(@NotNull String mapName, int minPlayersToStart, int playersPerTeam, @NotNull Player setuper) {
		spawnerBuilder = new SpawnerBuilder();
		baseBuilder = new BaseBuilder();
		arenaBuilder = new ArenaBuilder();
		arenaBuilder.setMapName(mapName);
		arenaBuilder.setMinPlayersToStart(minPlayersToStart);
		arenaBuilder.setPlayersPerTeam(playersPerTeam);
		arenaBuilder.setWorld(setuper.getWorld());
		this.setuper = setuper;
		
		createSetupItems();
		giveSetupItems();
		
		// TODO: Read stuff like spawner ticks and item names from config files
		
		// An inventory menu to select the spawner item and add the spawner to the arena
		IClickable spawnerSelector = new ClickableInventory(Bukkit.createInventory(null, 9 * 3, MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "select-spawner-item")), setuper) {
			
			{
				inventory.setItem(9 + 2, new ItemBuilder().setName("�r�aBronze Spawner setzen").setMaterial(Material.BRICK).build());
                inventory.setItem(9 + 4, new ItemBuilder().setName("�r�7Eisen Spawner setzen").setMaterial(Material.IRON_INGOT).build());
                inventory.setItem(9 + 6, new ItemBuilder().setName("�r�bGold Spawner setzen").setMaterial(Material.GOLD_INGOT).build());
			}
			
			@Override
			public void click(@NotNull InventoryClickEvent event) {
				Player player = (Player) event.getWhoClicked();
				
				// Check if the player who initiated the event is the setuper of the arena
				if (player != setuper)
					return;
				
				int slot = event.getSlot();
				
				switch (slot) {
				case (9 + 2): // Bronze
					{
						spawnerBuilder
								.setItem(Material.BRICK)
								.setItemName("Bronze")
								.setTicksPerSpawn(10);
						
						addSpawner();
						event.getView().close();
					}
					break;
				case (9 + 4): // Eisen
					{
						spawnerBuilder
								.setItem(Material.IRON_INGOT)
								.setItemName("Eisen")
								.setTicksPerSpawn(40);
				
						addSpawner();
						event.getView().close();
					}
					break;
				case (9 + 6): // Gold
					{
						spawnerBuilder
								.setItem(Material.GOLD_INGOT)
								.setItemName("Gold")
								.setTicksPerSpawn(100);
				
						addSpawner();
						event.getView().close();
					}
					break;
				}
				
			}
		};
		
		clickables.add(spawnerSelector);
		
		// An inventory menu to select the team color and add the base to the arena
		IClickable teamColorSelector = new ClickableInventory(Bukkit.createInventory(null, 9 * 3, MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "select-team-color").replace("&", "�")), setuper) {
			
			{
				inventory.setItem(0, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "color-white").replace("&", "�")).setMaterial(Material.WHITE_WOOL).build());
				inventory.setItem(1, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "color-orange").replace("&", "�")).setMaterial(Material.ORANGE_WOOL).build());
				inventory.setItem(2, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "color-magenta").replace("&", "�")).setMaterial(Material.MAGENTA_WOOL).build());
				inventory.setItem(3, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "color-light-blue").replace("&", "�")).setMaterial(Material.LIGHT_BLUE_WOOL).build());
				inventory.setItem(4, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "color-yellow").replace("&", "�")).setMaterial(Material.YELLOW_WOOL).build());
				inventory.setItem(5, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "color-lime").replace("&", "�")).setMaterial(Material.LIME_WOOL).build());
				inventory.setItem(6, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "color-pink").replace("&", "�")).setMaterial(Material.PINK_WOOL).build());
				inventory.setItem(7, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "color-gray").replace("&", "�")).setMaterial(Material.GRAY_WOOL).build());
				inventory.setItem(8, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "color-light-gray").replace("&", "�")).setMaterial(Material.LIGHT_GRAY_WOOL).build());
				inventory.setItem(9, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "color-cyan").replace("&", "�")).setMaterial(Material.CYAN_WOOL).build());
				inventory.setItem(10, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "color-blue").replace("&", "�")).setMaterial(Material.BLUE_WOOL).build());
				inventory.setItem(11, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "color-purple").replace("&", "�")).setMaterial(Material.PURPLE_WOOL).build());
				inventory.setItem(12, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "color-green").replace("&", "�")).setMaterial(Material.GREEN_WOOL).build());
				inventory.setItem(13, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "color-brown").replace("&", "�")).setMaterial(Material.BROWN_WOOL).build());
				inventory.setItem(14, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "color-red").replace("&", "�")).setMaterial(Material.RED_WOOL).build());
				inventory.setItem(15,new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "color-black").replace("&", "�")).setMaterial(Material.BLACK_WOOL).build());
			}
			
			@Override
			public void click(@NotNull InventoryClickEvent event) {
				Player player = (Player) event.getWhoClicked();
				
				// Check if the player who initiated the event is the setuper of the arena
				if (player != setuper)
					return;
				
				int slot = event.getSlot();
				
				if (slot < 16) {
					baseBuilder.setTeamColor(TeamColor.values()[slot]);
					addBase();
					event.getView().close();
				}
			}
		};
		
		clickables.add(teamColorSelector);
		
		// Do the following when an item with the custom name is right clicked on a block:
		// Set the location of the spawner to the blocks' position through the spawnerBuilder.
		// Open an inventory menu to select the spawner type.
		placables.add(new PlacableItem(spawnerItem, setuper) {

			@Override
			public void place(@NotNull BlockPlaceEvent event) {
				Player player = event.getPlayer();
				
				// Check if the player who initiated the event is the setuper of the arena
				if (player != setuper)
					return;
				
				Block block = event.getBlockPlaced();
				
				if (block != null) {
					spawnerBuilder.setLocation(block.getLocation());
					player.openInventory(spawnerSelector.getInventory());
				}
			}
		});
		
		// Do the following when an item with the custom name is right clicked on a block:
		// Set the item shop of the base at the blocks' position through the baseBuilder.
		placables.add(new PlacableItem(itemShopItem, setuper) {

			@Override
			public void place(@NotNull BlockPlaceEvent event) {
				Player player = event.getPlayer();
				
				// Check if the player who initiated the event is the setuper of the arena
				if (player != setuper)
					return;
				
				Block block = event.getBlockPlaced();
				
				if (block != null) {
					baseBuilder.setItemShop(block.getLocation());
					spawnItemShopNPC(block.getLocation());
					MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "shop-set").replace("%type%", "Item"));
					SoundPlayer.playSound("arena-setup", setuper);
				}
			}
		});
		
		// Do the following when an item with the custom name is right clicked on a block:
		// Set the team shop of the base at the blocks' position through the baseBuilder.
		placables.add(new PlacableItem(teamShopItem, setuper) {

			@Override
			public void place(@NotNull BlockPlaceEvent event) {
				Player player = event.getPlayer();
				
				// Check if the player who initiated the event is the setuper of the arena
				if (player != setuper)
					return;
				
				Block block = event.getBlockPlaced();
				
				if (block != null) {
					baseBuilder.setTeamShop(block.getLocation());
					spawnTeamShopNPC(block.getLocation());
					MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "shop-set").replace("%type%", "Team"));
					SoundPlayer.playSound("arena-setup", setuper);
				}
			}
		});
		
		// Do the following when an item with the custom name is right clicked on a block:
		// Set the spawn of the base at the blocks' position through the baseBuilder.
		placables.add(new PlacableItem(spawnItem, setuper) {

			@Override
			public void place(@NotNull BlockPlaceEvent event) {
				Player player = event.getPlayer();
				
				// Check if the player who initiated the event is the setuper of the arena
				if (player != setuper)
					return;
				
				Block block = event.getBlockPlaced();
				
				if (block != null) {
					baseBuilder.setSpawn(block.getLocation());
					spawnSpawnNPC(block.getLocation());
					MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "spawn-set"));
					SoundPlayer.playSound("arena-setup", setuper);
				}
			}
		});
		
		usables.add(new UsableItem(bedItem, setuper) {
			
			@Override
			public void use(@NotNull PlayerInteractEvent event) {
				Player player = event.getPlayer();
				
				// Check if the player who initiated the event is the setuper of the arena
				if (player != setuper)
					return;
				
				if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					Block block = event.getClickedBlock();
					
					if (block.getBlockData() instanceof Bed) {
						World w = block.getWorld();
						Location loc = block.getLocation();
						baseBuilder.setBedTop(loc);
						baseBuilder.setBedBottom(null);
						
						List<Block> surroundingBlocks = new ArrayList<Block>();
						surroundingBlocks.add(w.getBlockAt(loc.clone().add(1, 0, 0)));
						surroundingBlocks.add(w.getBlockAt(loc.clone().add(0, 0, 1)));
						surroundingBlocks.add(w.getBlockAt(loc.clone().add(-1, 0, 0)));
						surroundingBlocks.add(w.getBlockAt(loc.clone().add(0, 0, -1)));
						
						for (Block b : surroundingBlocks) {
							if (b.getBlockData() instanceof Bed) {
								baseBuilder.setBedBottom(b.getLocation());
								break;
							}
						}
						
						if (baseBuilder.getBedBottom() == null) {
							MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "bed-bottom-error"));
							SoundPlayer.playSound("error", player);
							return;
						}
						
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "bed-set"));
						SoundPlayer.playSound("arena-setup", player);
					} else {
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "bed-set-error"));
						SoundPlayer.playSound("error", player);
					}
				}
			}
		});
		
		// Do the following when an item with the custom name is right clicked:
		// Set the team color of the base by checking the wool color the player is holding.
		// Build a base with base builder.
		// Add it to arenaBuilder.
		// Create a new BaseBuilder.
		usables.add(new UsableItem(createBaseItem, setuper) {
			
			@Override
			public void use(@NotNull PlayerInteractEvent event) {
				Player player = event.getPlayer();
				
				// Check if the player who initiated the event is the setuper of the arena
				if (player != setuper)
					return;
				
				if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
					player.openInventory(teamColorSelector.getInventory());
			}
		});
		
		usables.add(new UsableItem(cancelSetupItem, setuper) {
			
			@Override
			public void use(PlayerInteractEvent event) {
				Player player = event.getPlayer();
				
				// Check if the player who initiated the event is the setuper of the arena
				if (player != setuper)
					return;
				
				if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					BedwarsPlugin.getInstance().cancelArenaSetup(player);
					MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "arena-setup-cancelled"));
				}
			}
		});
		
		usables.add(new UsableItem(finishSetupItem, setuper) {
			
			@Override
			public void use(PlayerInteractEvent event) {
				Player player = event.getPlayer();
				
				// Check if the player who initiated the event is the setuper of the arena
				if (player != setuper)
					return;
				
				if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					FinishArenaSetupResponse finishArenaSetupResponse = BedwarsPlugin.getInstance().finishArenaSetup(player);
					
					switch (finishArenaSetupResponse) {
					case ARENA_CREATED:
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "arena-setup-finish"));
						SoundPlayer.playSound("arena-setup", player);
						break;
					case NO_ARENA_SETTING_UP:
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "arena-you-were-not-setup"));
						SoundPlayer.playSound("error", player);
						break;
					case NOT_ENOUGH_BASES:
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "arena-not-enough-bases"));
						SoundPlayer.playSound("error", player);
						break;
					}
				}
			}
		});
	}
	
	public void addBase() {
		if (baseBuilder.getBedBottom() == null || baseBuilder.getBedTop() == null) {
			MessageSender.sendMessage(setuper, "Cannot add base because the bed is not set up correctly");
			return;
		}
		else if (baseBuilder.getItemShop() == null) {
			MessageSender.sendMessage(setuper, "Cannot add base because item shop location is not set");
			return;
		}
		else if (baseBuilder.getTeamShop() == null) {
			MessageSender.sendMessage(setuper, "Cannot add base because team shop location is not set");
			return;
		}
		else if (baseBuilder.getSpawn() == null) {
			MessageSender.sendMessage(setuper, "Cannot add base because spawn location is not set");
			return;
		}
		else if (baseBuilder.getTeamColor() == null) {
			// How did we get here?
			MessageSender.sendMessage(setuper, "Cannot add base because team color is not set");
			return;
		}
		
		arenaBuilder.addBase(baseBuilder.build());
		
		despawnItemShopNPC();
		despawnTeamShopNPC();
		despawnSpawnNPC();
		String colorConfigKey = "color-" + baseBuilder.getTeamColor().toString().toLowerCase().replace("_", "-");
		MessagesConfig msgConfig = MessagesConfig.getInstance();
		String colorStr = msgConfig.getStringValue(setuper.getLocale(), colorConfigKey);
		MessageSender.sendMessage(setuper, MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "base-added").replace("%teamcolor%", colorStr));
		SoundPlayer.playSound("arena-setup", setuper);
		
		baseBuilder = new BaseBuilder();
	}
	
	public void addSpawner() {
		arenaBuilder.addSpawner(spawnerBuilder.build());
		spawnSpawnerNPC(spawnerBuilder.getLocation(), spawnerBuilder.getMaterial(), spawnerBuilder.getItemName());
		MessageSender.sendMessage(setuper, MessagesConfig.getInstance().getStringValue(setuper.getLocale(), "spawner-set").replace("%itemname%", spawnerBuilder.getItemName()));
		SoundPlayer.playSound("arena-setup", setuper);
		spawnerBuilder = new SpawnerBuilder();
	}
	
	public Arena finish() {
		if(arenaBuilder.getBaseCount() < 2) // if there are not enough bases, return null so the setup does NOT finish
			return null;
		
		cleanup();
		World w = arenaBuilder.getWorld();
		MVWorldManager mvWorldManager = BedwarsPlugin.getInstance().getMultiverse().getMVWorldManager();
    	MultiverseWorld mvWorld = mvWorldManager.getMVWorld(w);
    	mvWorld.setAllowAnimalSpawn(false);
    	mvWorld.setAllowMonsterSpawn(false);
    	mvWorld.setHunger(false);
    	mvWorld.setBedRespawn(false);
    	mvWorld.setEnableWeather(false);
    	w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
    	w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
    	w.setFullTime(6000);
		return arenaBuilder.build();
	}
	
	public void cancel() {
		cleanup();
	}
	
	public String getMapName() {
		return arenaBuilder.getMapName();
	}
	
	public World getWorld() {
		return arenaBuilder.getWorld();
	}
	
	public Player getSetuper() {
		return setuper;
	}
	
	private void createSetupItems() {
		spawnerItem = new ItemBuilder().setName("�rSpawner").setMaterial(Material.SPAWNER).build();
		itemShopItem = new ItemBuilder().setName("�rItem Shop").setMaterial(Material.EMERALD_BLOCK).build();
		teamShopItem = new ItemBuilder().setName("�rTeam Shop").setMaterial(Material.DIAMOND_BLOCK).build();
		spawnItem = new ItemBuilder().setName("�rSpawn").setMaterial(Material.BEACON).build();
		bedItem = new ItemBuilder().setName("�rBed").setMaterial(Material.WHITE_BED).build();
		createBaseItem = new ItemBuilder().setName("�rCreate Base").setMaterial(Material.WHITE_WOOL).build();
		cancelSetupItem = new ItemBuilder().setName("�rCancel Setup").setMaterial(Material.BARRIER).build();
		finishSetupItem = new ItemBuilder().setName("�rFinish Setup").setMaterial(Material.GREEN_DYE).build();
	}
	
	private void giveSetupItems() {
		setuper.getInventory().setItem(0, spawnerItem);
		setuper.getInventory().setItem(1, itemShopItem);
		setuper.getInventory().setItem(2, teamShopItem);
		setuper.getInventory().setItem(3, spawnItem);
		setuper.getInventory().setItem(4, bedItem);
		setuper.getInventory().setItem(5, createBaseItem);
		setuper.getInventory().setItem(6, cancelSetupItem);
		setuper.getInventory().setItem(7, finishSetupItem);
	}
	
	private void cleanup() {
		BedwarsPlugin plugin = BedwarsPlugin.getInstance();
		
		for (IClickable clickable : clickables)
			plugin.removeClickable(clickable);
		
		for (IUsable usable : usables)
			plugin.removeUsable(usable);
		
		for (IPlacable placable : placables)
			plugin.removePlacable(placable);
		
		despawnItemShopNPC();
		despawnTeamShopNPC();
		despawnSpawnNPC();
		despawnAllSpawnerNPCs();
	}
	
	private void spawnItemShopNPC(Location loc) {
		if (itemShopNPC == null)
			itemShopNPC = new VillagerNPC(loc.clone().add(0.5, 0, 0.5), "DESERT", "ARMORER", "Item Shop", setuper);
		else
			itemShopNPC.teleport(loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5, setuper);
	}
	
	private void despawnItemShopNPC() {
		if (itemShopNPC != null) {
			itemShopNPC.despawn(setuper);
			itemShopNPC = null;
		}
	}
	
	private void spawnTeamShopNPC(Location loc) {
		if (teamShopNPC == null)
			teamShopNPC = new VillagerNPC(loc.clone().add(0.5, 0, 0.5), "SNOW", "CLERIC", "Team Shop", setuper);
		else
			teamShopNPC.teleport(loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5, setuper);
	}
	
	private void despawnTeamShopNPC() {
		if (teamShopNPC != null) {
			teamShopNPC.despawn(setuper);
			teamShopNPC = null;
		}
	}
	
	private void spawnSpawnNPC(Location loc) {
		if (spawnNPC == null)
			spawnNPC = new PlayerNPC(loc.clone().add(0.5, 0, 0.5), "Spawn", setuper);
		else
			spawnNPC.teleport(loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5, setuper);
	}
	
	private void despawnSpawnNPC() {
		if (spawnNPC != null) {
			spawnNPC.despawn(setuper);
			spawnNPC = null;
		}
	}
	
	private void spawnSpawnerNPC(Location loc, Material material, String itemName) {
		ArmorStandItemNPC npc = new ArmorStandItemNPC(new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getBlockY() -0.5, loc.getBlockZ() + 0.5), itemName + " - Spawner", material, setuper);
		int index = spawnerNPCs.size();
		
		UseEntityPacketListener listener = new UseEntityPacketListener(npc.getEntityId()) {
			@Override
			public void onUse(String action) {
				if (!action.equals("ATTACK"))
					return;
				
				arenaBuilder.removeSpawner(index);
				// TODO: Get message from config
				MessageSender.sendMessage(setuper, "Spawner was removed");
				
				// Delay Spawner despawn because otherwise the client throws an exception for some reason
				Bukkit.getScheduler().scheduleSyncDelayedTask(BedwarsPlugin.getInstance(), new Runnable() {
					@Override
					public void run() {
						despawnSpawnerNPC(npc);
					}
				}, 1L);
			}
		};
		
		BedwarsPlugin.getInstance().addPacketListener(setuper, listener);
		spawnerNPCs.put(npc, listener);
	}
	
	private void despawnSpawnerNPC(ArmorStandItemNPC npc) {
		IPacketListener packetListener = spawnerNPCs.get(npc);
		BedwarsPlugin.getInstance().removePacketListener(setuper, packetListener);
		npc.despawn(setuper);
		spawnerNPCs.remove(npc);
	}
	
	private void despawnAllSpawnerNPCs() {
		for (ArmorStandItemNPC npc : spawnerNPCs.keySet()) {
			IPacketListener packetListener = spawnerNPCs.get(npc);
			BedwarsPlugin.getInstance().removePacketListener(setuper, packetListener);
			npc.despawn(setuper);
		}
		
		spawnerNPCs = new HashMap<ArmorStandItemNPC, IPacketListener>();
	}
	
}
