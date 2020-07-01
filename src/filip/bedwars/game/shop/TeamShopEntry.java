package filip.bedwars.game.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.GamePlayer;
import filip.bedwars.game.Team;
import filip.bedwars.utils.InventoryUtils;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class TeamShopEntry extends ShopEntry {

	private final TeamShopReward reward;
	
	public TeamShopEntry(TeamShopReward reward) {
		this.reward = reward;
	}

	public int getPriceCount(Team team) {
		return reward.getPriceCount(team);
	}
	
	public Material getPriceMaterial(Team team) {
		return reward.getPriceMaterial(team);
	}
	
	public ItemStack getDisplayItem(Team team) {
		return reward.getDisplayItem(team);
	}

	public boolean buy(GamePlayer gamePlayer, boolean fullStack) {
		Player player = gamePlayer.getPlayer();
		Team team = gamePlayer.getTeam();
		
		if (reward.canBuy(gamePlayer)) {
			if (reward.reward(team)) {
				InventoryUtils.removeItems(player.getInventory(), reward.getPriceMaterial(team), reward.getPriceCount(team));
				SoundPlayer.playSound("buy-item", player);
				
				for (GamePlayer gp : team.getMembers()) {
					Player p = gp.getPlayer();
					String msg = MessagesConfig.getInstance().getStringValue(player.getLocale(), "upgrade-" + reward.type.toString().toLowerCase().replace('_', '-'))
							.replace("%player%", player.getName())
							.replace("%level%", "" + team.upgrades.get(reward.type));
					
					MessageSender.sendMessage(p, msg);
				}
				
				return true;
			} else {
				MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "upgrade-cannot-be-bought"));
				SoundPlayer.playSound("error", player);
			}
		} else {
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "cant-afford-item"));
			SoundPlayer.playSound("cant-afford-item", player);
		}
		
		return false;
	}

}
