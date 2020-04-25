package filip.bedwars.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class UsableItem implements IUsable {

	protected final ItemStack itemStack;
	protected final Player player;
	
	public UsableItem(ItemStack itemStack, Player player) {
		this.itemStack = itemStack;
		this.player = player;
		registerUsable();
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
		return this.itemStack.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName()) && (this.player == player);
	}

}
