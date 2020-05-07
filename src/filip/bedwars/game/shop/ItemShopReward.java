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
	
	public void reward(GamePlayer gamePlayer, int amount) {
		Player player = gamePlayer.getPlayer();
		HashMap<Integer, ItemStack> didNotFit = player.getInventory().addItem(item.asQuantity(item.getAmount() * amount));
		
		for (ItemStack is : didNotFit.values())
    		player.getWorld().dropItemNaturally(player.getLocation(), is).setVelocity(player.getLocation().getDirection().multiply(0.5));
	}
	
	public int getMaxAmountAtOnce() {
		return item.getMaxStackSize() / item.getAmount();
	}
	
	public ItemStack getDisplayItem() {
		return item;
	}
	
}
