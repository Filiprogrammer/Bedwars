package filip.bedwars.game.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.GamePlayer;
import filip.bedwars.utils.InventoryUtils;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class ArmorItemShopEntry extends ItemShopEntry {

	public ArmorItemShopEntry(Material priceMaterial, int priceCount, ItemStack item) {
		super(priceMaterial, priceCount, item);
	}
	
	@Override
	public void buy(GamePlayer gamePlayer, boolean fullStack) {
		Player player = gamePlayer.getPlayer();
		
		if (canBuy(player)) {
			InventoryUtils.removeItems(player.getInventory(), priceMaterial, priceCount);
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
			
			String itemName = item.getType().toString();
			
			if (item.hasItemMeta()) {
				ItemMeta itemMeta = item.getItemMeta();
				if (itemMeta.hasDisplayName())
					itemName = itemMeta.getDisplayName();
			}
			
			MessageSender.sendMessage(player, 
				MessagesConfig.getInstance().getStringValue(player.getLocale(), "bought-item")
					.replace("%amount%", "" + item.getAmount())
					.replace("%item%", itemName));
			SoundPlayer.playSound("buy-item", player);
		} else {
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "cant-afford-item"));
			SoundPlayer.playSound("cant-afford-item", player);
		}
	}

}
