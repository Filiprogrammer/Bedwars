package filip.bedwars.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.BedwarsPlugin;

public interface IUsable {
	/**
	 * Get the item.
	 * @return
	 */
	ItemStack getItemStack();
	
	Player getPlayer();
	
	/**
	 * Get whether the item matches the param.
	 * @param itemStack
	 * @param player
	 * @return matches
	 */
	boolean matches(ItemStack itemStack, Player player);
	
	/**
     * Called when the item is interacted on
     *
     * @param event
     * @param playerWrapper
     */
	void use(PlayerInteractEvent event);
	
	/**
	 * Register the usable.
	 */
	default void registerUsable() {
		BedwarsPlugin.getInstance().addUsable(this);
	}
}
