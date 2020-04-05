package filip.bedwars.inventory;

import org.bukkit.inventory.ItemStack;

public abstract class UsableItem implements IUsable {

	protected final ItemStack itemStack;
	
	public UsableItem(ItemStack itemStack) {
		this.itemStack = itemStack;
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
