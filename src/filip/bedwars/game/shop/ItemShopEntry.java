package filip.bedwars.game.shop;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.GamePlayer;
import filip.bedwars.game.Team;
import filip.bedwars.utils.InventoryUtils;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class ItemShopEntry extends ShopEntry {

	protected final Material priceMaterial;
	protected final int priceCount;
	private final List<ItemShopReward> rewards;
	private final ItemStack displayItem;
	
	public ItemShopEntry(Material priceMaterial, int priceCount, List<ItemShopReward> rewards, ItemStack displayItem) {
		this.priceMaterial = priceMaterial;
		this.priceCount = priceCount;
		this.rewards = rewards;
		this.displayItem = displayItem;
	}
	
	public Material getPriceMaterial(Team team) {
		return priceMaterial;
	}
	
	public int getPriceCount(Team team) {
		return priceCount;
	}
	
	public int getPriceCountFullStack() {
		if (rewards.size() == 1) {
			ItemShopReward reward = rewards.get(0);
			return reward.getMaxAmountAtOnce() * priceCount;
		}
		
		return priceCount;
	}
	
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
	
	/**
	 * Check if the player can afford a full stack of the item.
	 * @param player player that should be checked
	 * @return true if they can afford it, false if not
	 */
	public boolean canBuyFullStack(Player player) {
		if (getPriceItemCount(player.getInventory()) >= getPriceCountFullStack())
			return true;
		
		return false;
	}

	@Override
	public ItemStack getDisplayItem(Team team) {
		if (displayItem != null)
			return displayItem;
		
		if (rewards.size() > 0)
			return rewards.get(0).getDisplayItem();
		
		return null;
	}

	@Override
	public boolean buy(GamePlayer gamePlayer, boolean fullStack) {
		Player player = gamePlayer.getPlayer();
		
		if (canBuy(player)) {
			int buyAmount = 1;
			
			if (fullStack && rewards.size() == 1) {
				ItemShopReward reward = rewards.get(0);
				buyAmount = Math.min(getMaxBuyAmount(player), reward.getMaxAmountAtOnce());
			}
			
			InventoryUtils.removeItems(player.getInventory(), priceMaterial, buyAmount * priceCount);
			
			for (ItemShopReward reward : rewards)
				reward.reward(gamePlayer, buyAmount);
			
			SoundPlayer.playSound("buy-item", player);
		} else {
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "cant-afford-item"));
			SoundPlayer.playSound("cant-afford-item", player);
		}
		
		return false;
	}
	
}
