package filip.bedwars.game.shop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.game.GamePlayer;
import filip.bedwars.game.Team;
import filip.bedwars.inventory.ItemBuilder;

public class Shop {
	
	private final String title;
	private final ShopCategory[] categories;
	
	/**
	 * Inventory containing only the category items
	 */
	private final Inventory categoryListInventory;
    
    public Shop(String title, ShopCategory[] categories) {
    	this.title = title;
    	this.categories = categories;
        this.categoryListInventory = Bukkit.createInventory(null, 9 * 2, title);
    	
        addCategoryListToInv(this.categoryListInventory);
    }
    
    public Inventory getCategoryListInventory() {
    	return categoryListInventory;
    }
    
    public Inventory getCategoryInventory(int categoryIndex, Team team) {
    	if (categoryIndex >= categories.length || categoryIndex < 0)
    		return null;
    	
    	return createInventory(categories[categoryIndex], title, team);
    }
    
	public int handleClick(int shopCategoryIndex, InventoryClickEvent event, GamePlayer gamePlayer) {
		if (!(event.getWhoClicked() instanceof Player))
			return shopCategoryIndex;
		
		Player player = (Player) event.getWhoClicked();
		Team team = gamePlayer.getTeam();
		Inventory inv = getCategoryInventory(event.getSlot(), team);
		
		if (inv != null) {
			player.openInventory(inv);
			return event.getSlot();
		}
		
		inv = getCategoryInventory(shopCategoryIndex, team);
		
		if (inv == null)
			return shopCategoryIndex;
		
		ShopCategory category = categories[shopCategoryIndex];
		ShopEntry entry;
		
		if (event.getSlot() >= 27)
			entry = category.getShopEntry(event.getSlot() - 27);
		else
			entry = category.getShopEntry(event.getSlot() - 18);
		
		if (entry != null && entry.buy(gamePlayer, event.isShiftClick()))
			player.openInventory(getCategoryInventory(shopCategoryIndex, team));
		
		return shopCategoryIndex;
	}
    
    /**
     * Create an Inventory for the given ShopCategory including the category list at the top and puts it in the inventories field at the specified index.
     * @param category the ShopCategory
     * @param title the title of the inventory
     * @param team the team that opened the shop
     */
    private Inventory createInventory(ShopCategory category, String title, Team team) {
    	// Create a new inventory and put it into the inventories field
    	Inventory thisCategoryInv = Bukkit.createInventory(null, 9 * 4, title);
    	
    	// Add the category items at the top of the inventory
    	addCategoryListToInv(thisCategoryInv);
    	
    	// Add the items with the prices to the inventory
    	for (int i = 0; i < category.getShopEntriesCount(); ++i) {
    		ShopEntry shopEntry = category.getShopEntry(i);
    		
    		ItemStack displayItem = shopEntry.getDisplayItem(team);
    		thisCategoryInv.setItem(9 * 2 + i, displayItem);
    		
    		ItemStack priceItem = new ItemBuilder()
    				.setMaterial(shopEntry.getPriceMaterial(team))
    				.setAmount(shopEntry.getPriceCount(team))
    				.setName(ChatColor.RESET + "" + ChatColor.BOLD + "Price: " + shopEntry.getPriceCount(team))
    				.build();
    		thisCategoryInv.setItem(9 * 3 + i, priceItem);
    	}
    	
    	return thisCategoryInv;
    }
    
    /**
     * Add the category items at the top of the given Inventory.
     * @param inv the inventory to add the category items to
     */
    private void addCategoryListToInv(Inventory inv) {
    	for (int i = 0; i < 9; i++) {
            if (categories.length > i) {
            	ItemStack categoryItem = new ItemBuilder()
                        .setMaterial(categories[i].getMaterial())
                        .setName("§r§l".concat(categories[i].getName()))
                        .build();
            	
            	inv.setItem(i, categoryItem);
            }
            
            inv.setItem(i + 9, ItemBuilder.NULL);
    	}
    }

}
