package filip.bedwars.game.arena.setup;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
	
	public ArenaSetup() {
		spawnerBuilder = new SpawnerBuilder();
		baseBuilder = new BaseBuilder();
		arenaBuilder = new ArenaBuilder();
		
		// TODO: Read stuff like spawner ticks and item names from config files
		// TODO: Add localizations
		
		IClickable spawnerSelector = new ClickableInventory(Bukkit.createInventory(null, 9 * 3, "Select an Item for the Spawner")) {
			
			{
				inventory.setItem(9 + 2, new ItemBuilder().setName("§r§aBronze Spawner setzen").setMaterial(Material.BRICK).build());
                inventory.setItem(9 + 4, new ItemBuilder().setName("§r§7Eisen Spawner setzen").setMaterial(Material.IRON_INGOT).build());
                inventory.setItem(9 + 6, new ItemBuilder().setName("§r§bGold Spawner setzen").setMaterial(Material.GOLD_INGOT).build());
			}
			
			@Override
			public void click(InventoryClickEvent event) {
				int slot = event.getSlot();
				Player player = (Player) event.getWhoClicked();
				
				switch (slot) {
				case (9 + 2): // Bronze
					{
						Spawner spawner = new SpawnerBuilder()
								.setItem(Material.BRICK)
								.setItemName("Bronze")
								.setTicksPerSpawn(10)
								.build();
						
						arenaBuilder.addSpawner(spawner);
					}
					break;
				case (9 + 4): // Eisen
					{
						Spawner spawner = new SpawnerBuilder()
								.setItem(Material.IRON_INGOT)
								.setItemName("Eisen")
								.setTicksPerSpawn(40)
								.build();
				
						arenaBuilder.addSpawner(spawner);
					}
					break;
				case (9 + 6): // Gold
					{
						Spawner spawner = new SpawnerBuilder()
								.setItem(Material.GOLD_INGOT)
								.setItemName("Gold")
								.setTicksPerSpawn(100)
								.build();
				
						arenaBuilder.addSpawner(spawner);
					}
					break;
				}
				
			}
		};
		
		new UsableItem(new ItemBuilder().setName("§rSpawner").setMaterial(Material.SPAWNER).build()) {
			
			@Override
			public void use(PlayerInteractEvent event) {
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
	}
	
	public Arena finish() {
		
	}
	
}
