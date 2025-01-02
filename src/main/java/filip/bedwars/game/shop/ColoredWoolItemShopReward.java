package filip.bedwars.game.shop;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.GamePlayer;
import filip.bedwars.utils.TeamColorConverter;

public class ColoredWoolItemShopReward extends ItemShopReward {

	private final int woolAmount;
	
	public ColoredWoolItemShopReward(@NotNull final ItemStack item) {
		super(createWoolItem(item));
		this.woolAmount = item.getAmount();
	}
	
	private static ItemStack createWoolItem(final ItemStack item) {
		ItemStack itemStack = item.clone();
		itemStack.setType(Material.WHITE_WOOL);
		return itemStack;
	}
	
	@Override
	public void reward(@NotNull final GamePlayer gamePlayer, final int amount) {
		Player player = gamePlayer.getPlayer();
		ItemStack itemStack = item.clone();
		itemStack.setType(TeamColorConverter.convertTeamColorToWoolMaterial(gamePlayer.getTeam().getBase().getTeamColor()));
		itemStack.setAmount(woolAmount * amount);
		final HashMap<Integer, ItemStack> didNotFit = player.getInventory().addItem(itemStack);
		
		for (final ItemStack is : didNotFit.values())
			player.getWorld().dropItem(player.getLocation(), is).setVelocity(player.getLocation().getDirection().multiply(0.5));
	}

}
