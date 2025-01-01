package filip.bedwars.game.shop;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.game.GamePlayer;

public class ItemShopReward {

	protected final ItemStack item;
	
	public ItemShopReward(ItemStack item) {
		this.item = item;
	}
	
	public void reward(GamePlayer gamePlayer, final int amount) {
		Player player = gamePlayer.getPlayer();
		final HashMap<Integer, ItemStack> didNotFit = player.getInventory().addItem(item.asQuantity(item.getAmount() * amount));
		
		for (final ItemStack is : didNotFit.values())
    		player.getWorld().dropItemNaturally(player.getLocation(), is).setVelocity(player.getLocation().getDirection().multiply(0.5));
	}
	
	public int getMaxAmountAtOnce() {
		return item.getMaxStackSize() / item.getAmount();
	}
	
	public ItemStack getDisplayItem() {
		return item;
	}
	
}
