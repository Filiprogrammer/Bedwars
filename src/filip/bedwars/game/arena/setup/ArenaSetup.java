package filip.bedwars.game.arena.setup;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import filip.bedwars.game.arena.Arena;
import filip.bedwars.inventory.ClickableInventory;
import filip.bedwars.inventory.ItemBuilder;

public class ArenaSetup {

	SpawnerBuilder spawnerBuilder;
	BaseBuilder baseBuilder;
	ArenaBuilder arenaBuilder;
	
	public ArenaSetup() {
		spawnerBuilder = new SpawnerBuilder();
		baseBuilder = new BaseBuilder();
		arenaBuilder = new ArenaBuilder();
		
		new ClickableInventory(Bukkit.createInventory(null, 9 * 3, "Select an Item")) {
			
			{
				inventory.setItem(9 + 2, new ItemBuilder().setName("§r§aBronze Spawner setzen").setMaterial(Material.BRICK).build());
                inventory.setItem(9 + 4, new ItemBuilder().setName("§r§7Eisen Spawner setzen").setMaterial(Material.IRON_INGOT).build());
                inventory.setItem(9 + 6, new ItemBuilder().setName("§r§bGold Spawner setzen").setMaterial(Material.GOLD_INGOT).build());
			}
			
			@Override
			public void click(InventoryClickEvent event) {
				int slot = event.getSlot();
				
				switch (slot) {
				case 9 + 2: // Bronze
					
					break;
				case 9 + 4: // Eisen
					break;
				case 9 + 6: // Gold
					break;
				}
			}
		};
	}
	
	
	
	public Arena finish() {
		
	}
	
}
