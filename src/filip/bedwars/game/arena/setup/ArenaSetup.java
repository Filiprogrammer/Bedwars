package filip.bedwars.game.arena.setup;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;

import filip.bedwars.config.MessagesConfig;
import filip.bedwars.config.SoundsConfig;
import filip.bedwars.game.TeamColor;
import filip.bedwars.game.arena.Arena;
import filip.bedwars.inventory.ClickableInventory;
import filip.bedwars.inventory.IClickable;
import filip.bedwars.inventory.ItemBuilder;
import filip.bedwars.inventory.UsableItem;
import filip.bedwars.utils.MessageSender;

public class ArenaSetup {

	SpawnerBuilder spawnerBuilder;
	BaseBuilder baseBuilder;
	ArenaBuilder arenaBuilder;
	Player setuper;
	ItemStack spawnerItem;
	ItemStack itemShopItem;
	ItemStack teamShopItem;
	ItemStack spawnItem;
	ItemStack bedItem;
	ItemStack createBaseItem;
	
	public ArenaSetup(@NotNull String mapName, @NotNull Player setuper) {
		spawnerBuilder = new SpawnerBuilder();
		baseBuilder = new BaseBuilder();
		arenaBuilder = new ArenaBuilder();
		arenaBuilder.setMapName(mapName);
		this.setuper = setuper;
		
		createSetupItems();
		giveSetupItems();
		
		// TODO: Read stuff like spawner ticks and item names from config files
		// TODO: Add localizations
		
		// An inventory menu to select the spawner item and add the spawner to the arena
		IClickable spawnerSelector = new ClickableInventory(Bukkit.createInventory(null, 9 * 3, MessagesConfig.getInstance().getStringValue("select-spawner-item"))) {
			
			{
				inventory.setItem(9 + 2, new ItemBuilder().setName("§r§aBronze Spawner setzen").setMaterial(Material.BRICK).build());
                inventory.setItem(9 + 4, new ItemBuilder().setName("§r§7Eisen Spawner setzen").setMaterial(Material.IRON_INGOT).build());
                inventory.setItem(9 + 6, new ItemBuilder().setName("§r§bGold Spawner setzen").setMaterial(Material.GOLD_INGOT).build());
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
						new SpawnerBuilder()
								.setItem(Material.BRICK)
								.setItemName("Bronze")
								.setTicksPerSpawn(10);
						
						addSpawner();
						event.getView().close();
					}
					break;
				case (9 + 4): // Eisen
					{
						new SpawnerBuilder()
								.setItem(Material.IRON_INGOT)
								.setItemName("Eisen")
								.setTicksPerSpawn(40);
				
						addSpawner();
						event.getView().close();
					}
					break;
				case (9 + 6): // Gold
					{
						new SpawnerBuilder()
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
		
		// An inventory menu to select the team color and add the base to the arena
		IClickable teamColorSelector = new ClickableInventory(Bukkit.createInventory(null, 9 * 3, MessagesConfig.getInstance().getStringValue("select-team-color"))) {
			
			{
				inventory.setItem(0, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue("color-white")).setMaterial(Material.WHITE_WOOL).build());
				inventory.setItem(1, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue("color-orange")).setMaterial(Material.ORANGE_WOOL).build());
				inventory.setItem(2, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue("color-magenta")).setMaterial(Material.MAGENTA_WOOL).build());
				inventory.setItem(3, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue("color-light-blue")).setMaterial(Material.LIGHT_BLUE_WOOL).build());
				inventory.setItem(4, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue("color-yellow")).setMaterial(Material.YELLOW_WOOL).build());
				inventory.setItem(5, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue("color-lime")).setMaterial(Material.LIME_WOOL).build());
				inventory.setItem(6, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue("color-pink")).setMaterial(Material.PINK_WOOL).build());
				inventory.setItem(7, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue("color-gray")).setMaterial(Material.GRAY_WOOL).build());
				inventory.setItem(8, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue("color-light-gray")).setMaterial(Material.LIGHT_GRAY_WOOL).build());
				inventory.setItem(9, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue("color-cyan")).setMaterial(Material.CYAN_WOOL).build());
				inventory.setItem(10, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue("color-blue")).setMaterial(Material.BLUE_WOOL).build());
				inventory.setItem(11, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue("color-purple")).setMaterial(Material.PURPLE_WOOL).build());
				inventory.setItem(12, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue("color-green")).setMaterial(Material.GREEN_WOOL).build());
				inventory.setItem(13, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue("color-brown")).setMaterial(Material.BROWN_WOOL).build());
				inventory.setItem(14, new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue("color-red")).setMaterial(Material.RED_WOOL).build());
				inventory.setItem(15,new ItemBuilder().setName(MessagesConfig.getInstance().getStringValue("color-black")).setMaterial(Material.BLACK_WOOL).build());
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
		
		// Do the following when an item with the custom name is right clicked on a block:
		// Set the location of the spawner to the blocks' position through the spawnerBuilder.
		// Open an inventory menu to select the spawner type.
		new UsableItem(spawnerItem) {
			
			@Override
			public void use(@NotNull PlayerInteractEvent event) {}

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
		};
		
		// Do the following when an item with the custom name is right clicked on a block:
		// Set the item shop of the base at the blocks' position through the baseBuilder.
		new UsableItem(itemShopItem) {
			
			@Override
			public void use(@NotNull PlayerInteractEvent event) {}

			@Override
			public void place(@NotNull BlockPlaceEvent event) {
				Player player = event.getPlayer();
				
				// Check if the player who initiated the event is the setuper of the arena
				if (player != setuper)
					return;
				
				Block block = event.getBlockPlaced();
				
				if (block != null) {
					baseBuilder.setItemShop(block.getLocation());
					MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue("shop-set").replace("%type%", "Item"));
					setuper.playSound(player.getLocation(), SoundsConfig.getInstance().getSoundValue("arena-setup"), 1, 1);
				}
			}
		};
		
		// Do the following when an item with the custom name is right clicked on a block:
		// Set the team shop of the base at the blocks' position through the baseBuilder.
		new UsableItem(teamShopItem) {
			
			@Override
			public void use(@NotNull PlayerInteractEvent event) {}

			@Override
			public void place(@NotNull BlockPlaceEvent event) {
				Player player = event.getPlayer();
				
				// Check if the player who initiated the event is the setuper of the arena
				if (player != setuper)
					return;
				
				Block block = event.getBlockPlaced();
				
				if (block != null) {
					baseBuilder.setTeamShop(block.getLocation());
					MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue("shop-set").replace("%type%", "Team"));
					setuper.playSound(player.getLocation(), SoundsConfig.getInstance().getSoundValue("arena-setup"), 1, 1);
				}
			}
		};
		
		// Do the following when an item with the custom name is right clicked on a block:
		// Set the spawn of the base at the blocks' position through the baseBuilder.
		new UsableItem(spawnItem) {
			
			@Override
			public void use(@NotNull PlayerInteractEvent event) {}

			@Override
			public void place(@NotNull BlockPlaceEvent event) {
				Player player = event.getPlayer();
				
				// Check if the player who initiated the event is the setuper of the arena
				if (player != setuper)
					return;
				
				Block block = event.getBlockPlaced();
				
				if (block != null) {
					baseBuilder.setSpawn(block.getLocation());
					MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue("spawn-set"));
					setuper.playSound(player.getLocation(), SoundsConfig.getInstance().getSoundValue("arena-setup"), 1, 1);
				}
			}
		};
		
		new UsableItem(bedItem) {
			
			@Override
			public void use(@NotNull PlayerInteractEvent event) {
				Player player = event.getPlayer();
				
				// Check if the player who initiated the event is the setuper of the arena
				if (player != setuper)
					return;
				
				if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					Block block = event.getClickedBlock();
					
					if (block instanceof Bed) {
						World w = block.getWorld();
						Location loc = block.getLocation();
						baseBuilder.setBedTop(loc);
						
						List<Block> surroundingBlocks = new ArrayList<Block>();
						surroundingBlocks.add(w.getBlockAt(loc.add(1, 0, 0)));
						surroundingBlocks.add(w.getBlockAt(loc.add(0, 0, 1)));
						surroundingBlocks.add(w.getBlockAt(loc.add(-1, 0, 0)));
						surroundingBlocks.add(w.getBlockAt(loc.add(0, 0, -1)));
						
						for (Block b : surroundingBlocks) {
							if (b instanceof Bed) {
								baseBuilder.setBedBottom(b.getLocation());
								break;
							}
						}
						
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue("bed-set"));
						player.playSound(player.getLocation(), SoundsConfig.getInstance().getSoundValue("arena-setup"), 1, 1);
					} else {
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue("bed-set-error"));
						player.playSound(player.getLocation(), SoundsConfig.getInstance().getSoundValue("error"), 1, 1);
					}
				}
			}

			@Override
			public void place(@NotNull BlockPlaceEvent event) {}
		};
		
		// Do the following when an item with the custom name is right clicked:
		// Set the team color of the base by checking the wool color the player is holding.
		// Build a base with base builder.
		// Add it to arenaBuilder.
		// Create a new BaseBuilder.
		new UsableItem(createBaseItem) {
			
			@Override
			public void use(@NotNull PlayerInteractEvent event) {
				Player player = event.getPlayer();
				
				// Check if the player who initiated the event is the setuper of the arena
				if (player != setuper)
					return;
				
				if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
					player.openInventory(teamColorSelector.getInventory());
			}

			@Override
			public void place(@NotNull BlockPlaceEvent event) {}
		};
	}
	
	public void addBase() {
		arenaBuilder.addBase(baseBuilder.build());
		
		String colorConfigKey = "color-" + baseBuilder.getTeamColor().toString().toLowerCase().replace("_", "-");
		MessagesConfig msgConfig = MessagesConfig.getInstance();
		String colorStr = msgConfig.getStringValue(colorConfigKey);
		MessageSender.sendMessage(setuper, MessagesConfig.getInstance().getStringValue("base-added").replace("%teamcolor%", colorStr));
		setuper.playSound(setuper.getLocation(), SoundsConfig.getInstance().getSoundValue("arena-setup"), 1, 1);
		
		baseBuilder = new BaseBuilder();
	}
	
	public void addSpawner() {
		arenaBuilder.addSpawner(spawnerBuilder.build());
		MessageSender.sendMessage(setuper, MessagesConfig.getInstance().getStringValue("spawner-set").replace("%itemname%", spawnerBuilder.getItemName()));
		setuper.playSound(setuper.getLocation(), SoundsConfig.getInstance().getSoundValue("arena-setup"), 1, 1);
		spawnerBuilder = new SpawnerBuilder();
	}
	
	public Arena finish() {
		return arenaBuilder.build();
	}
	
	private void createSetupItems() {
		spawnerItem = new ItemBuilder().setName("§rSpawner").setMaterial(Material.SPAWNER).build();
		itemShopItem = new ItemBuilder().setName("§rItem Shop").setMaterial(Material.EMERALD_BLOCK).build();
		teamShopItem = new ItemBuilder().setName("§rTeam Shop").setMaterial(Material.DIAMOND_BLOCK).build();
		spawnItem = new ItemBuilder().setName("§rSpawn").setMaterial(Material.BEACON).build();
		bedItem = new ItemBuilder().setName("§rBed").setMaterial(Material.WHITE_BED).build();
		createBaseItem = new ItemBuilder().setName("§rCreate Base").setMaterial(Material.WHITE_WOOL).build();
	}
	
	private void giveSetupItems() {
		setuper.getInventory().setItem(0, spawnerItem);
		setuper.getInventory().setItem(1, itemShopItem);
		setuper.getInventory().setItem(2, teamShopItem);
		setuper.getInventory().setItem(3, spawnItem);
		setuper.getInventory().setItem(4, bedItem);
		setuper.getInventory().setItem(5, createBaseItem);
	}
	
}
