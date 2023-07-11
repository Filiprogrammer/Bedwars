package filip.bedwars.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class PlacableItem implements IPlacable {

	protected final ItemStack itemStack;
	protected final Player player;
	
	public PlacableItem(ItemStack itemStack, Player player) {
		this.itemStack = itemStack;
		this.player = player;
		registerPlacable();
	}
	
	@Override
	public ItemStack getItemStack() {
		return itemStack;
	}
	
	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public boolean matches(ItemStack itemStack, Player player) {
		return (this.player == player) && this.itemStack.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName());
	}

}
