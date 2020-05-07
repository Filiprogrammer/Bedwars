package filip.bedwars.game.shop;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.game.GamePlayer;
import filip.bedwars.inventory.ItemBuilder;
import filip.bedwars.utils.TeamColorConverter;

public class ColoredGlassItemShopReward extends ItemShopReward {

	private int glassAmount;
	
	public ColoredGlassItemShopReward(int glassAmount) {
		super(new ItemStack(Material.GLASS, glassAmount));
		this.glassAmount = glassAmount;
	}
	
	@Override
	public void reward(GamePlayer gamePlayer, int amount) {
		Player player = gamePlayer.getPlayer();
		ItemBuilder itemBuilder = new ItemBuilder().setMaterial(TeamColorConverter.convertTeamColorToStainedGlassMaterial(gamePlayer.getTeam().getBase().getTeamColor()));
		itemBuilder.setAmount(glassAmount * amount);
		HashMap<Integer, ItemStack> didNotFit = player.getInventory().addItem(itemBuilder.build());
		
		for (ItemStack is : didNotFit.values())
    		player.getWorld().dropItemNaturally(player.getLocation(), is).setVelocity(player.getLocation().getDirection().multiply(0.5));
	}

}
