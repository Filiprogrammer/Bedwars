package filip.bedwars.listener.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.plugin.java.JavaPlugin;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.inventory.IClickable;

public class InventoryClickListener implements Listener {
	
	public InventoryClickListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
    private void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() != null) {
            IClickable clickable = BedwarsPlugin.getInstance().getClickable(event.getClickedInventory(), player);
            if (clickable != null) {
                clickable.click(event);
                event.setCancelled(true);
            }
        }
	}
	
	@EventHandler
	private void onDrag (InventoryDragEvent event) {
		Player player = (Player) event.getWhoClicked();

		if (event.getInventory() != null) {
            IClickable clickable = BedwarsPlugin.getInstance().getClickable(event.getInventory(), player);
            if (clickable != null) {
                clickable.drag(event);
                event.setCancelled(true);
            }
        }
	}
	
}
