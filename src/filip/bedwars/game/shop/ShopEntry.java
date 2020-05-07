package filip.bedwars.game.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.game.GamePlayer;

public abstract class ShopEntry {
	
	protected final Material priceMaterial;
	protected final int priceCount;
	
	public ShopEntry(Material priceMaterial, int priceCount) {
		this.priceMaterial = priceMaterial;
		this.priceCount = priceCount;
	}
	
	public Material getPriceMaterial() {
		return priceMaterial;
	}
	
	public int getPriceCount() {
		return priceCount;
	}
	
	public abstract ItemStack getDisplayItem();
	
	public abstract void buy(GamePlayer gamePlayer, boolean fullStack);
	
	/**
	 * Get the amount of priceMaterial in the players inventory. (for example how much gold they have)
	 * @param inv the inventory that should be checked
	 * @return amount of items
	 */
	protected int getPriceItemCount(Inventory inv) {
		int amount = 0;
		
		for (int i = 0; i < 36; ++i) {
			ItemStack itemStack = inv.getItem(i);
			
			if (itemStack == null)
				continue;
			
			if (itemStack.getType().equals(priceMaterial)) {
				amount += itemStack.getAmount();
			}
		}
		
		return amount;
	}
	
	/**
	 * Check if the player can afford one item.
	 * @param player player that should be checked
	 * @return true if they can afford it, false if not
	 */
	public boolean canBuy(Player player) {
		Inventory inv = player.getInventory();
		int amount = getPriceItemCount(inv);
		
		if(amount >= priceCount)
			return true;
		
		return false;
	}
	
	public int getMaxBuyAmount(Player player) {
		return getPriceItemCount(player.getInventory()) / priceCount;
	}

}
