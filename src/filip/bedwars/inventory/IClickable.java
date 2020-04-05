package filip.bedwars.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import filip.bedwars.BedwarsPlugin;

public interface IClickable {
	/**
     * Gets whether the inventory matches the parameter
     *
     * @param inventory
     * @param playerWrapper
     * @return match
     */
    boolean matches(Inventory inventory, Player player);
	
	/**
     * Called when the inventory is clicked
     *
     * @param event
     * @param playerWrapper
     */
    void click(InventoryClickEvent event);
    
    /**
     * Registers the clickable
     */
    default void registerClickable() {
        BedwarsPlugin.getInstance().addClickable(this);
    }
}
