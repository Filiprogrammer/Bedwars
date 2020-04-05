package filip.bedwars.listener.player;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.inventory.IUsable;

public class PlayerInteractItemListener implements Listener {

	public PlayerInteractItemListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		
		if (item != null) {
			if (item.hasItemMeta()) {
				ItemMeta itemMeta = item.getItemMeta();
				
				if (itemMeta.hasDisplayName()) {
					IUsable usable = BedwarsPlugin.getInstance().getUsable(item);
					
					if (usable != null) {
						usable.use(event);
						event.setCancelled(true);
					}
				}
			}
		}
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
						IUsable usable = BedwarsPlugin.getInstance().getUsable(item);
						
						if (usable != null) {
							usable.place(event);
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
}
