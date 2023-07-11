package filip.bedwars.game.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.game.GamePlayer;

public class ArmorItemShopReward extends ItemShopReward {

	public ArmorItemShopReward(ItemStack item) {
		super(item);
	}
	
	@Override
	public void reward(GamePlayer gamePlayer, int amount) {
		Player player = gamePlayer.getPlayer();
		
		String typeString = item.getType().toString();
		ItemStack previousItem;
		
		if (typeString.endsWith("_CHESTPLATE") || typeString.equals("ELYTRA")) {
			previousItem = player.getInventory().getChestplate();
			player.getInventory().setChestplate(item.clone());
		} else if (typeString.endsWith("_LEGGINGS")) {
			previousItem = player.getInventory().getLeggings();
			player.getInventory().setLeggings(item.clone());
		} else if (typeString.endsWith("_BOOTS")) {
			previousItem = player.getInventory().getBoots();
			player.getInventory().setBoots(item.clone());
		} else {
			previousItem = player.getInventory().getHelmet();
			player.getInventory().setHelmet(item.clone());
		}
		
		if (previousItem != null)
			player.getWorld().dropItemNaturally(player.getLocation(), previousItem).setVelocity(player.getLocation().getDirection().multiply(0.5));
	}

}
