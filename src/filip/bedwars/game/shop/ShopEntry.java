package filip.bedwars.game.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopEntry {
	
	private Material priceMaterial;
	private int priceCount;
	private ItemStack item;
	
	public ShopEntry(Material priceMaterial, int priceCount, ItemStack item) {
		this.priceMaterial = priceMaterial;
		this.priceCount = priceCount;
		this.item = item;
	}
	
	public Material getPriceMaterial() {
		return priceMaterial;
	}
	
	public int getPriceCount() {
		return priceCount;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	/**
	 * Get the amount of priceMaterial in the players inventory. (for example how much gold they have)
	 * @param inv the inventory that should be checked
	 * @return amount of items
	 */
	private int getPriceItemCount(Inventory inv) {
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
	
	/**
	 * Check if the player can afford a full stack of the item.
	 * @param player player that should be checked
	 * @return true if they can afford it, false if not
	 */
	public boolean canBuyFullStack(Player player) {
		int amount = getPriceItemCount(player.getInventory());
		int value = 64 / item.getAmount();
		
		if(amount >= (priceCount * value))
			return true;
		
		return false;
	}

}
