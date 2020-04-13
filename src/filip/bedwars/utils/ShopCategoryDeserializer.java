package filip.bedwars.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.game.shop.ShopCategory;
import filip.bedwars.game.shop.ShopEntry;

public class ShopCategoryDeserializer {
	
	public static ShopCategory deserializeCategory(Object serializedCategory) {
		Map<String, Object> mapOfElements = (Map<String, Object>) serializedCategory;
		
		String categoryName = (String) mapOfElements.get("name");
		
		if (categoryName == null) {
			MessageSender.sendWarning("A Shop Category does not have a name");
			return null;
		}
		
		Material categoryMaterial = Material.valueOf((String) mapOfElements.get("material"));
		
		if (categoryMaterial == null) {
			MessageSender.sendWarning("A Shop Category does not have a valid material");
			return null;
		}
		
		List<Map<String, Object>> shopEntriesList = (List<Map<String, Object>>) mapOfElements.get("shopentries");
		List<ShopEntry> shopEntries = new ArrayList<ShopEntry>();
		
		for (Map<String, Object> serializedShopEntry : shopEntriesList) {
			Material priceMaterial = Material.valueOf((String) serializedShopEntry.get("priceMaterial"));
			int priceCount = (int) serializedShopEntry.get("priceCount");
			ItemStack item = (ItemStack) serializedShopEntry.get("item");
			shopEntries.add(new ShopEntry(priceMaterial, priceCount, item));
		}
		
		return new ShopCategory(categoryName, categoryMaterial, shopEntries);
	}

}
