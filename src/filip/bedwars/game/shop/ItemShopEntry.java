package filip.bedwars.game.shop;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.GamePlayer;
import filip.bedwars.utils.InventoryUtils;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class ItemShopEntry extends ShopEntry {

	protected final ItemStack item;
	
	public ItemShopEntry(Material priceMaterial, int priceCount, ItemStack item) {
		super(priceMaterial, priceCount);
		this.item = item;
	}
	
	public int getPriceCountFullStack() {
		return (item.getMaxStackSize() / item.getAmount()) * priceCount;
	}
	
	public int getPriceCount(int buyItemAmount) {
		return (buyItemAmount / item.getAmount()) * priceCount;
	}
	
	/**
	 * Check if the player can afford a full stack of the item.
	 * @param player player that should be checked
	 * @return true if they can afford it, false if not
	 */
	public boolean canBuyFullStack(Player player) {
		int amount = getPriceItemCount(player.getInventory());
		int value = item.getMaxStackSize() / item.getAmount();
		
		if(amount >= (priceCount * value))
			return true;
		
		return false;
	}
	
	public int getMaxBuyAmount(Player player) {
		return (getPriceItemCount(player.getInventory()) / priceCount) * item.getAmount();
	}

	@Override
	public ItemStack getDisplayItem() {
		return item;
	}

	@Override
	public void buy(GamePlayer gamePlayer, boolean fullStack) {
		Player player = gamePlayer.getPlayer();
		HashMap<Integer, ItemStack> didNotFit = null;
		int itemAmount = 0;
		
		if (fullStack) {
			int maxBuyAmount = getMaxBuyAmount(player);
			
			if (maxBuyAmount > 0) {
				ItemStack itemStack = item.clone();
				
				if (maxBuyAmount < item.getMaxStackSize()) {
					InventoryUtils.removeItems(player.getInventory(), priceMaterial, getPriceCount(maxBuyAmount));
					itemAmount = maxBuyAmount;
				} else {
					InventoryUtils.removeItems(player.getInventory(), priceMaterial, getPriceCountFullStack());
					itemAmount = item.getAmount() * (getPriceCountFullStack() / priceCount);
				}
				
				itemStack.setAmount(itemAmount);
				didNotFit = player.getInventory().addItem(itemStack);
			}
		} else {
			if (canBuy(player)) {
				InventoryUtils.removeItems(player.getInventory(), priceMaterial, priceCount);
				itemAmount = item.getAmount();
		    	didNotFit = player.getInventory().addItem(item);
			}
		}
		
		if (didNotFit == null) {
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "cant-afford-item"));
			SoundPlayer.playSound("cant-afford-item", player);
		} else {
			for (ItemStack is : didNotFit.values())
	    		player.getWorld().dropItemNaturally(player.getLocation(), is).setVelocity(player.getLocation().getDirection().multiply(0.5));
			
			String itemName = item.getType().toString();
			
			if (item.hasItemMeta()) {
				ItemMeta itemMeta = item.getItemMeta();
				if (itemMeta.hasDisplayName())
					itemName = itemMeta.getDisplayName();
			}
			
			MessageSender.sendMessage(player, 
				MessagesConfig.getInstance().getStringValue(player.getLocale(), "bought-item")
					.replace("%amount%", "" + itemAmount)
					.replace("%item%", itemName));
			SoundPlayer.playSound("buy-item", player);
		}
	}
	
}
