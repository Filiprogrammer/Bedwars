package filip.bedwars.game.shop;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.game.GamePlayer;
import filip.bedwars.utils.TeamColorConverter;

public class ColoredGlassItemShopReward extends ItemShopReward {

	private int glassAmount;
	
	public ColoredGlassItemShopReward(ItemStack item) {
		super(createGlassItem(item));
		this.glassAmount = item.getAmount();
	}
	
	private static ItemStack createGlassItem(ItemStack item) {
		ItemStack itemStack = item.clone();
		itemStack.setType(Material.GLASS);
		return itemStack;
	}
	
	@Override
	public void reward(GamePlayer gamePlayer, int amount) {
		Player player = gamePlayer.getPlayer();
		ItemStack itemStack = item.clone();
		itemStack.setType(TeamColorConverter.convertTeamColorToStainedGlassMaterial(gamePlayer.getTeam().getBase().getTeamColor()));
		itemStack.setAmount(glassAmount * amount);
		HashMap<Integer, ItemStack> didNotFit = player.getInventory().addItem(itemStack);
		
		for (ItemStack is : didNotFit.values())
    		player.getWorld().dropItemNaturally(player.getLocation(), is).setVelocity(player.getLocation().getDirection().multiply(0.5));
	}

}
