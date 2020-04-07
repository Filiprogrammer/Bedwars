package filip.bedwars.listener.player;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.inventory.IPlacable;

public class BlockPlaceListener implements Listener {

	public BlockPlaceListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Block blockPlaced = event.getBlockPlaced();
		
		if (blockPlaced != null) {
			ItemStack item = event.getItemInHand();
			
			if (item != null) {
				if (item.hasItemMeta()) {
					ItemMeta itemMeta = item.getItemMeta();
					
					if (itemMeta.hasDisplayName()) {
						IPlacable placable = BedwarsPlugin.getInstance().getPlacable(item);
						
						if (placable != null) {
							placable.place(event);
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
}
