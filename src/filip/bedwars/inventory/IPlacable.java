package filip.bedwars.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.BedwarsPlugin;

public interface IPlacable {
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
