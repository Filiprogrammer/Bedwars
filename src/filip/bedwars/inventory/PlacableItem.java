package filip.bedwars.inventory;

import org.bukkit.inventory.ItemStack;

public abstract class PlacableItem implements IPlacable {

	protected final ItemStack itemStack;
	
	public PlacableItem(ItemStack itemStack) {
		this.itemStack = itemStack;
		registerPlacable();
	}
	
	@Override
	public ItemStack getItemStack() {
		return itemStack;
	}

	@Override
	public boolean matches(ItemStack itemStack) {
		return this.itemStack.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName());
	}

}
