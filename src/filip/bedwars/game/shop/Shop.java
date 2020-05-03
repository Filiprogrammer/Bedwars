package filip.bedwars.game.shop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.game.GamePlayer;
import filip.bedwars.inventory.ItemBuilder;

public class Shop {
	
	private final ShopCategory[] categories;
	
	/**
	 * Inventory containing only the category items
	 */
	private final Inventory categoryListInventory;
	
	/**
	 * Inventories for every category
	 */
    private final Inventory[] inventories;
    
    public Shop(String title, ShopCategory[] categories) {
    	this.categories = categories;
    	this.inventories = new Inventory[categories.length];
        this.categoryListInventory = Bukkit.createInventory(null, 9 * 2, title);
    	
        addCategoryListToInv(this.categoryListInventory);
        
        for (int i = 0; i < categories.length; i++)
            createInventory(i, categories[i], title);
    }
    
    public Inventory getCategoryListInventory() {
    	return categoryListInventory;
    }
    
    public Inventory getCategoryInventory(int categoryIndex) {
    	if (categoryIndex >= inventories.length || categoryIndex < 0)
    		return null;
    	
    	return inventories[categoryIndex];
    }
    
	public int handleClick(int shopCategoryIndex, InventoryClickEvent event, GamePlayer gamePlayer) {
		if (!(event.getWhoClicked() instanceof Player))
			return shopCategoryIndex;
		
		Player player = (Player) event.getWhoClicked();
		Inventory inv = getCategoryInventory(event.getSlot());
		
		if (inv != null) {
			player.openInventory(inv);
			return event.getSlot();
		}
		
		inv = getCategoryInventory(shopCategoryIndex);
		
		if (inv == null)
			return shopCategoryIndex;
		
		ShopCategory category = categories[shopCategoryIndex];
		ShopEntry entry;
		
		if (event.getSlot() >= 27)
			entry = category.getShopEntry(event.getSlot() - 27);
		else
			entry = category.getShopEntry(event.getSlot() - 18);
		
		if (entry != null)
			entry.buy(gamePlayer, event.isShiftClick());
		
		return shopCategoryIndex;
	}
    
    /**
     * Create an Inventory for the given ShopCategory including the category list at the top and puts it in the inventories field at the specified index.
     * @param index the index of the category
     * @param category the ShopCategory
     */
    private void createInventory(int index, ShopCategory category, String title) {
    	// Create a new inventory and put it into the inventories field
    	Inventory thisCategoryInv = Bukkit.createInventory(null, 9 * 4, title);
    	inventories[index] = thisCategoryInv;
    	
    	// Add the category items at the top of the inventory
    	addCategoryListToInv(thisCategoryInv);
    	
    	// Add the items with the prices to the inventory
    	for (int i = 0; i < category.getShopEntriesCount(); ++i) {
    		ShopEntry shopEntry = category.getShopEntry(i);
    		
    		ItemStack displayItem = shopEntry.getDisplayItem();
    		thisCategoryInv.setItem(9 * 2 + i, displayItem);
    		
    		ItemStack priceItem = new ItemBuilder()
    				.setMaterial(shopEntry.getPriceMaterial())
    				.setAmount(shopEntry.getPriceCount())
    				.setName(ChatColor.RESET + "" + ChatColor.BOLD + "Preis: " + shopEntry.getPriceCount())
    				.build();
    		thisCategoryInv.setItem(9 * 3 + i, priceItem);
    	}
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
