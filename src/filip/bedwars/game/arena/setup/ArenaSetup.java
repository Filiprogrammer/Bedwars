package filip.bedwars.game.arena.setup;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;

import filip.bedwars.game.arena.Arena;
import filip.bedwars.inventory.ClickableInventory;
import filip.bedwars.inventory.IClickable;
import filip.bedwars.inventory.ItemBuilder;
import filip.bedwars.inventory.UsableItem;
import filip.bedwars.utils.TeamColorConverter;

public class ArenaSetup {

	SpawnerBuilder spawnerBuilder;
	BaseBuilder baseBuilder;
	ArenaBuilder arenaBuilder;
	
	public ArenaSetup(@NotNull String mapName) {
		spawnerBuilder = new SpawnerBuilder();
		baseBuilder = new BaseBuilder();
		arenaBuilder = new ArenaBuilder();
		arenaBuilder.setMapName(mapName);
		
		// TODO: Read stuff like spawner ticks and item names from config files
		// TODO: Add localizations
		
		IClickable spawnerSelector = new ClickableInventory(Bukkit.createInventory(null, 9 * 3, "Select an Item for the Spawner")) {
			
			{
				inventory.setItem(9 + 2, new ItemBuilder().setName("§r§aBronze Spawner setzen").setMaterial(Material.BRICK).build());
                inventory.setItem(9 + 4, new ItemBuilder().setName("§r§7Eisen Spawner setzen").setMaterial(Material.IRON_INGOT).build());
                inventory.setItem(9 + 6, new ItemBuilder().setName("§r§bGold Spawner setzen").setMaterial(Material.GOLD_INGOT).build());
			}
			
			@Override
			public void click(@NotNull InventoryClickEvent event) {
				int slot = event.getSlot();
				
				switch (slot) {
				case (9 + 2): // Bronze
					{
						new SpawnerBuilder()
								.setItem(Material.BRICK)
								.setItemName("Bronze")
								.setTicksPerSpawn(10);
						
						addSpawner();
					}
					break;
				case (9 + 4): // Eisen
					{
						new SpawnerBuilder()
								.setItem(Material.IRON_INGOT)
								.setItemName("Eisen")
								.setTicksPerSpawn(40);
				
						addSpawner();
					}
					break;
				case (9 + 6): // Gold
					{
						new SpawnerBuilder()
								.setItem(Material.GOLD_INGOT)
								.setItemName("Gold")
								.setTicksPerSpawn(100);
				
						addSpawner();
					}
					break;
				}
				
			}
		};
		
		// Do the following when an item with the custom name is right clicked on a block:
		// Set the location of the spawner to the blocks' position through the spawnerBuilder.
		// Open an inventory menu to select the spawner type.
		new UsableItem(new ItemBuilder().setName("§rSpawner").setMaterial(Material.SPAWNER).build()) {
			
			@Override
			public void use(@NotNull PlayerInteractEvent event) {}

			@Override
			public void place(@NotNull BlockPlaceEvent event) {
				Block block = event.getBlockPlaced();
				
				if (block != null) {
					spawnerBuilder.setLocation(block.getLocation());
					Player player = event.getPlayer();
					player.openInventory(spawnerSelector.getInventory());
				}
			}
		};
		
		// Do the following when an item with the custom name is right clicked on a block:
		// Set the item shop of the base at the blocks' position through the baseBuilder.
		new UsableItem(new ItemBuilder().setName("§rItem Shop").setMaterial(Material.EMERALD_BLOCK).build()) {
			
			@Override
			public void use(@NotNull PlayerInteractEvent event) {}

			@Override
			public void place(@NotNull BlockPlaceEvent event) {
				Block block = event.getBlockPlaced();
				
				if (block != null)
					baseBuilder.setItemShop(block.getLocation());
			}
		};
		
		// Do the following when an item with the custom name is right clicked on a block:
		// Set the team shop of the base at the blocks' position through the baseBuilder.
		new UsableItem(new ItemBuilder().setName("§rTeam Shop").setMaterial(Material.DIAMOND_BLOCK).build()) {
			
			@Override
			public void use(@NotNull PlayerInteractEvent event) {}

			@Override
			public void place(@NotNull BlockPlaceEvent event) {
				Block block = event.getBlockPlaced();
				
				if (block != null)
					baseBuilder.setTeamShop(block.getLocation());
			}
		};
		
		// Do the following when an item with the custom name is right clicked on a block:
		// Set the spawn of the base at the blocks' position through the baseBuilder.
		new UsableItem(new ItemBuilder().setName("§rSpawn").setMaterial(Material.BEACON).build()) {
			
			@Override
			public void use(@NotNull PlayerInteractEvent event) {}

			@Override
			public void place(@NotNull BlockPlaceEvent event) {
				Block block = event.getBlockPlaced();
				
				if (block != null)
					baseBuilder.setSpawn(block.getLocation());
			}
		};
		
		// Do the following when an item with the custom name is right clicked:
		// Set the team color of the base by checking the wool color the player is holding.
		// Build a base with base builder.
		// Add it to arenaBuilder.
		// Create a new BaseBuilder.
		new UsableItem(new ItemBuilder().setName("§rCreate Base").setMaterial(Material.WHITE_WOOL).build()) {
			
			@Override
			public void use(@NotNull PlayerInteractEvent event) {
				if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					baseBuilder.setTeamColor(TeamColorConverter.convertMaterialToTeamColor(event.getItem().getType()));
					addBase();
				}
			}

			@Override
			public void place(@NotNull BlockPlaceEvent event) {
			}
		};
	}
	
	public void addBase() {
		arenaBuilder.addBase(baseBuilder.build());
		baseBuilder = new BaseBuilder();
	}
	
	public void addSpawner() {
		arenaBuilder.addSpawner(spawnerBuilder.build());
		spawnerBuilder = new SpawnerBuilder();
	}
	
	public Arena finish() {
		return arenaBuilder.build();
	}
	
}
