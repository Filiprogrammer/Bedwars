package filip.bedwars.inventory;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.BedwarsPlugin;

public interface IPlacable {
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
     * Called when the block is placed
     *
     * @param event
     * @param playerWrapper
     */
	void place(BlockPlaceEvent event);
	
	/**
	 * Register the usable.
	 */
	default void registerPlacable() {
		BedwarsPlugin.getInstance().addPlacable(this);
	}
}
