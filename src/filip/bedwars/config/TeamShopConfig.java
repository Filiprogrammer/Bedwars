package filip.bedwars.config;

import java.util.ArrayList;
import java.util.List;

import filip.bedwars.game.shop.Shop;
import filip.bedwars.game.shop.ShopCategory;
import filip.bedwars.utils.ShopCategoryDeserializer;

public class TeamShopConfig extends SingleConfig{

	private static TeamShopConfig instance = null;
	
	private Shop shop;
	
	public Shop getShop() {
		return shop;
	}
	
	private TeamShopConfig() {
		super("teamshop.yml");
		reloadConfig();
	}

	@Override
	public boolean saveConfig() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reloadConfig() {
		createAndLoadConfigFileIfNotExistent(false);
		
		if (config.isList("categories")) {
			List<Object> serializedCategories = (List<Object>) config.getList("categories");
			
			List<ShopCategory> shopCategories = new ArrayList<ShopCategory>();
			
			for (Object serializedCategory : serializedCategories)
				shopCategories.add(ShopCategoryDeserializer.deserializeCategory(serializedCategory));
			
			shop = new Shop("§2Team Shop", shopCategories.toArray(new ShopCategory[0]));
		}
		
	}
	
	public static TeamShopConfig getInstance() {
		if (instance == null)
			instance = new TeamShopConfig();
		
		return instance;
	}

}
