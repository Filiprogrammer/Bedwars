package filip.bedwars.game.shop;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.GamePlayer;
import filip.bedwars.inventory.ItemBuilder;
import filip.bedwars.utils.InventoryUtils;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;
import filip.bedwars.utils.TeamColorConverter;

public class ColoredGlassItemShopEntry extends ItemShopEntry {

	private final int GLASS_MAX_STACK_SIZE = 64;
	
	private final int glassAmount;
	
	public ColoredGlassItemShopEntry(Material priceMaterial, int priceCount, int glassAmount) {
		super(priceMaterial, priceCount, new ItemStack(Material.GLASS, glassAmount));
		this.glassAmount = glassAmount;
	}
	
	@Override
	public void buy(GamePlayer gamePlayer, boolean fullStack) {
		Player player = gamePlayer.getPlayer();
		HashMap<Integer, ItemStack> didNotFit = null;
		ItemBuilder itemBuilder = new ItemBuilder().setMaterial(TeamColorConverter.convertTeamColorToStainedGlassMaterial(gamePlayer.getTeam().getBase().getTeamColor()));
		int itemAmount = 0;
		
		if (fullStack) {
			int maxBuyAmount = getMaxBuyAmount(player);
			
			if (maxBuyAmount > 0) {
				if (maxBuyAmount < GLASS_MAX_STACK_SIZE) {
					InventoryUtils.removeItems(player.getInventory(), priceMaterial, getPriceCount(maxBuyAmount));
					itemAmount = maxBuyAmount;
				} else {
					InventoryUtils.removeItems(player.getInventory(), priceMaterial, getPriceCountFullStack());
					itemAmount = glassAmount * (getPriceCountFullStack() / priceCount);
				}
				
				itemBuilder.setAmount(itemAmount);
				didNotFit = player.getInventory().addItem(itemBuilder.build());
			}
		} else {
			if (canBuy(player)) {
				InventoryUtils.removeItems(player.getInventory(), priceMaterial, priceCount);
				itemAmount = glassAmount;
				itemBuilder.setAmount(itemAmount);
				didNotFit = player.getInventory().addItem(itemBuilder.build());
			}
		}
		
		if (didNotFit == null) {
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "cant-afford-item"));
			SoundPlayer.playSound("cant-afford-item", player);
		} else {
			for (ItemStack is : didNotFit.values())
	    		player.getWorld().dropItemNaturally(player.getLocation(), is).setVelocity(player.getLocation().getDirection().multiply(0.5));
			
			MessageSender.sendMessage(player, 
				MessagesConfig.getInstance().getStringValue(player.getLocale(), "bought-item")
					.replace("%amount%", "" + itemAmount)
					.replace("%item%", itemBuilder.getMaterial().toString()));
			SoundPlayer.playSound("buy-item", player);
		}
	}

}
