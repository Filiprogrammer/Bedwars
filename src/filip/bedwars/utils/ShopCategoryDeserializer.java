package filip.bedwars.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.game.shop.ArmorItemShopReward;
import filip.bedwars.game.shop.ColoredGlassItemShopReward;
import filip.bedwars.game.shop.ColoredWoolItemShopReward;
import filip.bedwars.game.shop.ItemShopEntry;
import filip.bedwars.game.shop.ItemShopReward;
import filip.bedwars.game.shop.ShopCategory;
import filip.bedwars.game.shop.ShopEntry;

public class ShopCategoryDeserializer {
	
	public static ShopCategory deserializeCategory(Object serializedCategory) {
		Map<String, Object> mapOfElements = (Map<String, Object>) serializedCategory;
		
		String categoryName = ((String) mapOfElements.get("name")).replace('&', '§');
		
		if (categoryName == null) {
			MessageSender.sendWarning("A Shop Category does not have a name");
			return null;
		}
		
		Material categoryMaterial = Material.valueOf((String) mapOfElements.get("material"));
		
		if (categoryMaterial == null) {
			MessageSender.sendWarning("§eThe Shop Category §6\"" + categoryName + "\" §edoes not have a valid material");
			return null;
		}
		
		List<Map<String, Object>> shopEntriesList = (List<Map<String, Object>>) mapOfElements.get("shopentries");
		List<ShopEntry> shopEntries = new ArrayList<ShopEntry>();
		
		for (Map<String, Object> serializedShopEntry : shopEntriesList) {
			Object priceMaterialObject = serializedShopEntry.get("priceMaterial");
			Material priceMaterial = Material.STONE;
			
			if (priceMaterialObject == null) {
				MessageSender.sendWarning("priceMaterial of an item in the shop category §6\"" + categoryName + "\" §e was not specified");
			} else if (!(priceMaterialObject instanceof String)) {
				MessageSender.sendWarning("priceMaterial of an item in the shop category §6\"" + categoryName + "\" §e has an invalid value");
			} else {
				try {
					priceMaterial = Material.valueOf((String) priceMaterialObject);
				} catch (IllegalArgumentException e) {
					MessageSender.sendWarning("priceMaterial of an item in the shop category §6\"" + categoryName + "\" §e has an invalid value");
				}
			}
			
			Object priceCountObject = serializedShopEntry.get("priceCount");
			int priceCount = 1;
			
			if (priceCountObject == null)
				MessageSender.sendWarning("priceCount of an item in the shop category §6\"" + categoryName + "\" §e was not specified");
			else if (!(priceCountObject instanceof Integer))
				MessageSender.sendWarning("priceCount of an item in the shop category §6\"" + categoryName + "\" §e has an invalid value");
			else
				priceCount = (int) priceCountObject;
			
			Object serializedRewards = serializedShopEntry.get("rewards");
			List<ItemShopReward> rewards = new ArrayList<>();
			
			if (serializedRewards instanceof List) {
				for (Object serializedReward : (List<Object>) serializedRewards) {
					if (serializedReward instanceof Map) {
						Map<String, Object> itemMap = (Map<String, Object>) serializedReward;
						int itemAmount = 1;
						Material itemType = Material.STONE;
						ItemMeta itemMeta = null;
						
						Object itemAmountObject = itemMap.get("amount");
						
						if (itemAmountObject != null && itemAmountObject instanceof Integer)
							itemAmount = (int) itemAmountObject;
						
						Object itemTypeObject = itemMap.get("type");
						
						if (itemTypeObject != null && itemTypeObject instanceof String)
							itemType = Material.valueOf((String) itemTypeObject);
						
						Object itemMetaObject = itemMap.get("meta");
						
						if (itemMetaObject != null && itemMetaObject instanceof ItemMeta)
							itemMeta = (ItemMeta) itemMetaObject;
						
						Object customItemNameObject = itemMap.get("custom-item");
						ItemStack itemStack = new ItemStack(itemType, itemAmount);
						itemStack.setItemMeta(itemMeta);
						Object blastProofObject = itemMap.get("blast-proof");
						
						if (blastProofObject != null && blastProofObject instanceof Boolean && (Boolean)blastProofObject) {
							try {
								Object nmsItemStack = BedwarsPlugin.getInstance().reflectionUtils.craftItemStackAsNMSCopyMethod.invoke(null, itemStack);
								Object nbtTagCompound = BedwarsPlugin.getInstance().reflectionUtils.itemStackGetOrCreateTagMethod.invoke(nmsItemStack);
								BedwarsPlugin.getInstance().reflectionUtils.nbtTagCompoundSetMethod.invoke(nbtTagCompound, "bedwars-blast-proof", BedwarsPlugin.getInstance().reflectionUtils.nbtTagIntConstructor.newInstance(0));
								BedwarsPlugin.getInstance().reflectionUtils.itemStackSetTagMethod.invoke(nmsItemStack, nbtTagCompound);
								itemStack = (ItemStack) BedwarsPlugin.getInstance().reflectionUtils.craftItemStackAsBukkitCopyMethod.invoke(null, nmsItemStack);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
								e.printStackTrace();
							}
						}
						
						if (customItemNameObject != null && customItemNameObject instanceof String) {
							String customItemName = (String) customItemNameObject;
							
							switch (customItemName) {
							case "COLORED_WOOL":
								rewards.add(new ColoredWoolItemShopReward(itemStack));
								break;
							case "COLORED_GLASS":
								rewards.add(new ColoredGlassItemShopReward(itemStack));
								break;
							case "ARMOR":
								rewards.add(new ArmorItemShopReward(itemStack));
								break;
							case "FIREBALL":
								try {
									Object nmsItemStack = BedwarsPlugin.getInstance().reflectionUtils.craftItemStackAsNMSCopyMethod.invoke(null, itemStack);
									Object nbtTagCompound = BedwarsPlugin.getInstance().reflectionUtils.itemStackGetOrCreateTagMethod.invoke(nmsItemStack);
									BedwarsPlugin.getInstance().reflectionUtils.nbtTagCompoundSetMethod.invoke(nbtTagCompound, "bedwars-fireball", BedwarsPlugin.getInstance().reflectionUtils.nbtTagIntConstructor.newInstance(0));
									BedwarsPlugin.getInstance().reflectionUtils.itemStackSetTagMethod.invoke(nmsItemStack, nbtTagCompound);
									rewards.add(new ItemShopReward((ItemStack) BedwarsPlugin.getInstance().reflectionUtils.craftItemStackAsBukkitCopyMethod.invoke(null, nmsItemStack)));
								} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
									e.printStackTrace();
								}
								break;
							}
						} else {
							rewards.add(new ItemShopReward(itemStack));
						}
					} else {
						MessageSender.sendWarning("An item in the shop category §6\"" + categoryName + "\" §eis not valid");
						return null;
					}
				}
			} else {
				MessageSender.sendWarning("An item in the shop category §6\"" + categoryName + "\" §eis not valid");
				return null;
			}
			
			Object serializedDisplayItem = serializedShopEntry.get("display-item");
			ItemStack displayItem = null;
			
			if (serializedDisplayItem != null && serializedDisplayItem instanceof Map) {
				Map<String, Object> displayItemMap = (Map<String, Object>) serializedDisplayItem;
				int itemAmount = 1;
				Material itemType = Material.STONE;
				ItemMeta itemMeta = null;
				
				Object itemAmountObject = displayItemMap.get("amount");
				
				if (itemAmountObject != null && itemAmountObject instanceof Integer)
					itemAmount = (int) itemAmountObject;
				
				Object itemTypeObject = displayItemMap.get("type");
				
				if (itemTypeObject != null && itemTypeObject instanceof String)
					itemType = Material.valueOf((String) itemTypeObject);
				
				Object itemMetaObject = displayItemMap.get("meta");
				
				if (itemMetaObject != null && itemMetaObject instanceof ItemMeta)
					itemMeta = (ItemMeta) itemMetaObject;
				
				displayItem = new ItemStack(itemType, itemAmount);
				displayItem.setItemMeta(itemMeta);
			}
			
			shopEntries.add(new ItemShopEntry(priceMaterial, priceCount, rewards, displayItem));
		}
		
		return new ShopCategory(categoryName, categoryMaterial, shopEntries);
	}

}
