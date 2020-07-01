package filip.bedwars.game.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.game.GamePlayer;
import filip.bedwars.game.Team;

public abstract class ShopEntry {
	
	public abstract Material getPriceMaterial(Team team);
	
	public abstract int getPriceCount(Team team);
	
	public abstract ItemStack getDisplayItem(Team team);
	
	/**
	 * Attempt to buy.
     * @param gamePlayer the gamePlayer who attempts to buy
     * @param fullStack whether a full stack should be bought or not
     * @return whether the shop inventory should be refreshed or not
	 */
	public abstract boolean buy(GamePlayer gamePlayer, boolean fullStack);

}
