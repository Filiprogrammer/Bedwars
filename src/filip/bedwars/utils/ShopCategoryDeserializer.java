package filip.bedwars.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import filip.bedwars.game.shop.ArmorItemShopEntry;
import filip.bedwars.game.shop.ColoredGlassItemShopEntry;
import filip.bedwars.game.shop.ColoredWoolItemShopEntry;
import filip.bedwars.game.shop.ItemShopEntry;
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
			Material priceMaterial = Material.valueOf((String) serializedShopEntry.get("priceMaterial"));
			int priceCount = (int) serializedShopEntry.get("priceCount");
			Object item = serializedShopEntry.get("item");
			
			if (item instanceof ItemStack) {
				shopEntries.add(new ItemShopEntry(priceMaterial, priceCount, (ItemStack) item));
			} else if (item instanceof Map) {
				Map<String, Object> customItemMap = (Map<String, Object>) item;
				Object customItemNameObject = customItemMap.get("custom-item");
				
				if (customItemNameObject != null && customItemNameObject instanceof String) {
					String customItemName = (String) customItemNameObject;
					int customItemAmount = 1;
					Material customItemType = Material.STONE;
					ItemMeta customItemMeta = null;
					
					Object customItemAmountObject = customItemMap.get("amount");
					
					if (customItemAmountObject != null && customItemAmountObject instanceof Integer)
						customItemAmount = (int) customItemAmountObject;
					
					Object customItemTypeObject = customItemMap.get("type");
					
					if (customItemTypeObject != null && customItemTypeObject instanceof String)
						customItemType = Material.valueOf((String) customItemTypeObject);
					
					Object customItemMetaObject = customItemMap.get("meta");
					
					if (customItemMetaObject != null && customItemMetaObject instanceof ItemMeta)
						customItemMeta = (ItemMeta) customItemMetaObject;
					
					switch (customItemName) {
					case "COLORED_WOOL":
						shopEntries.add(new ColoredWoolItemShopEntry(priceMaterial, priceCount, customItemAmount));
						break;
					case "COLORED_GLASS":
						shopEntries.add(new ColoredGlassItemShopEntry(priceMaterial, priceCount, customItemAmount));
						break;
					case "ARMOR":
						ItemStack customItem = new ItemStack(customItemType, customItemAmount);
						customItem.setItemMeta(customItemMeta);
						shopEntries.add(new ArmorItemShopEntry(priceMaterial, priceCount, customItem));
						break;
					}
				}
			} else {
				MessageSender.sendWarning("An item in the shop category §6\"" + categoryName + "\" §eis not valid");
				return null;
			}
		}
		
		return new ShopCategory(categoryName, categoryMaterial, shopEntries);
	}

}
