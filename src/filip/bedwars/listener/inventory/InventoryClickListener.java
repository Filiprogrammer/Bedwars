package filip.bedwars.listener.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.inventory.IClickable;

public class InventoryClickListener implements Listener {
	
	public InventoryClickListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
    private void onCall(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() != null) {
            if (event.getCurrentItem() != null
                    && event.getCurrentItem().hasItemMeta()
                    && event.getCurrentItem().getItemMeta().hasDisplayName()) {

                IClickable clickable = BedwarsPlugin.getInstance().getClickable(event.getClickedInventory(), player);
                if (clickable != null) {
                    clickable.click(event);
                    event.setCancelled(true);
                }
            }

        }
	}
	
}
