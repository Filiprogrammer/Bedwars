package filip.bedwars.game.shop;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.GamePlayer;
import filip.bedwars.utils.InventoryUtils;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class ItemShopEntry extends ShopEntry {

	private final List<ItemShopReward> rewards;
	private final ItemStack displayItem;
	
	public ItemShopEntry(Material priceMaterial, int priceCount, List<ItemShopReward> rewards, ItemStack displayItem) {
		super(priceMaterial, priceCount);
		this.rewards = rewards;
		this.displayItem = displayItem;
	}
	
	public int getPriceCountFullStack() {
		if (rewards.size() == 1) {
			ItemShopReward reward = rewards.get(0);
			return reward.getMaxAmountAtOnce() * priceCount;
		}
		
		return priceCount;
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
	public ItemStack getDisplayItem() {
		if (displayItem != null)
			return displayItem;
		
		if (rewards.size() > 0)
			return rewards.get(0).getDisplayItem();
		
		return null;
	}

	@Override
	public void buy(GamePlayer gamePlayer, boolean fullStack) {
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
	}
	
}
