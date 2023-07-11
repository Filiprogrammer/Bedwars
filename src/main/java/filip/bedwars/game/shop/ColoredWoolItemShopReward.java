package filip.bedwars.game.shop;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.game.GamePlayer;
import filip.bedwars.utils.TeamColorConverter;

public class ColoredWoolItemShopReward extends ItemShopReward {

	private int woolAmount;
	
	public ColoredWoolItemShopReward(ItemStack item) {
		super(createWoolItem(item));
		this.woolAmount = item.getAmount();
	}
	
	private static ItemStack createWoolItem(ItemStack item) {
		ItemStack itemStack = item.clone();
		itemStack.setType(Material.WHITE_WOOL);
		return itemStack;
	}
	
	@Override
	public void reward(GamePlayer gamePlayer, int amount) {
		Player player = gamePlayer.getPlayer();
		ItemStack itemStack = item.clone();
		itemStack.setType(TeamColorConverter.convertTeamColorToWoolMaterial(gamePlayer.getTeam().getBase().getTeamColor()));
		itemStack.setAmount(woolAmount * amount);
		HashMap<Integer, ItemStack> didNotFit = player.getInventory().addItem(itemStack);
		
		for (ItemStack is : didNotFit.values())
    		player.getWorld().dropItemNaturally(player.getLocation(), is).setVelocity(player.getLocation().getDirection().multiply(0.5));
	}

}
