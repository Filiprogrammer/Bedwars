package filip.bedwars.inventory;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.BedwarsPlugin;

public interface IUsable {
	/**
	 * Get the item.
	 * @return
	 */
	ItemStack getItemStack();
	
	/**
	 * Get whether the item matches the param.
	 * @param itemStack
	 * @return matches
	 */
	boolean matches(ItemStack itemStack);
	
	/**
     * Called when the item is interacted on
     *
     * @param event
     * @param playerWrapper
     */
	void use(PlayerInteractEvent event);
	
	/**
     * Called when the block is placed
     *
     * @param event
     * @param playerWrapper
     */
	void place(BlockPlaceEvent event);
	
	/**
	 * Register the usable.
	 */
	default void registerUsable() {
		BedwarsPlugin.getInstance().addUsable(this);
	}
}
