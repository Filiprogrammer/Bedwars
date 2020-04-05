package filip.bedwars.game.arena.setup;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.bukkit.event.block.Action;

import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.arena.Spawner;
import filip.bedwars.inventory.ClickableInventory;
import filip.bedwars.inventory.IClickable;
import filip.bedwars.inventory.ItemBuilder;
import filip.bedwars.inventory.UsableItem;

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
						Spawner spawner = new SpawnerBuilder()
								.setItem(Material.BRICK)
								.setItemName("Bronze")
								.setTicksPerSpawn(10)
								.build();
						
						addSpawner();
					}
					break;
				case (9 + 4): // Eisen
					{
						Spawner spawner = new SpawnerBuilder()
								.setItem(Material.IRON_INGOT)
								.setItemName("Eisen")
								.setTicksPerSpawn(40)
								.build();
				
						addSpawner();
					}
					break;
				case (9 + 6): // Gold
					{
						Spawner spawner = new SpawnerBuilder()
								.setItem(Material.GOLD_INGOT)
								.setItemName("Gold")
								.setTicksPerSpawn(100)
								.build();
				
						addSpawner();
					}
					break;
				}
				
			}
		};
		
		new UsableItem(new ItemBuilder().setName("§rSpawner").setMaterial(Material.SPAWNER).build()) {
			
			@Override
			public void use(@NotNull PlayerInteractEvent event) {
				if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					Block clickedBlock = event.getClickedBlock();
					
					if (clickedBlock != null) {
						spawnerBuilder.setLocation(clickedBlock.getLocation());
						Player player = event.getPlayer();
						player.openInventory(spawnerSelector.getInventory());
					}
				}
			}
		};
		
		
		
		
		new UsableItem(new ItemBuilder().setName("§rShop").setMaterial(Material.EMERALD_BLOCK).build()) {
			
			@Override
			public void use(@NotNull PlayerInteractEvent event) {
				if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					Block clickedBlock = event.getClickedBlock();
					
					if (clickedBlock != null)
						baseBuilder.setItemShop(clickedBlock.getLocation());
				}
			}
		};
		
		
		new UsableItem(new ItemBuilder().setName("§rSpawn").setMaterial(Material.BEACON).build()) {
			
			@Override
			public void use(@NotNull PlayerInteractEvent event) {
				if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					Block clickedBlock = event.getClickedBlock();
					
					if (clickedBlock != null)
						baseBuilder.setSpawn(clickedBlock.getLocation());
				}
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
