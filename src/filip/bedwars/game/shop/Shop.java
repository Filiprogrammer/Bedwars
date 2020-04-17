package filip.bedwars.game.shop;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import filip.bedwars.config.MessagesConfig;
import filip.bedwars.inventory.ItemBuilder;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

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
    
	public int handleClick(int shopCategoryIndex, InventoryClickEvent event) {
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
		ShopEntry entry = category.getShopEntry(event.getSlot() - 18);
		
		if (entry == null)
			return shopCategoryIndex;
		
		HashMap<Integer, ItemStack> didNotFit = null;
		int itemAmount = 0;
		
		if (event.isShiftClick()) {
			if (entry.canBuyFullStack(player)) {
				removeItems(player.getInventory(), entry.getPriceMaterial(), entry.getPriceCountFullStack());
				ItemStack itemStack = entry.getItem().clone();
				itemAmount = entry.getItem().getAmount() * (entry.getPriceCountFullStack() / entry.getPriceCount());
				itemStack.setAmount(itemAmount);
				didNotFit = player.getInventory().addItem(itemStack);
			}
		} else {
			if (entry.canBuy(player)) {
				removeItems(player.getInventory(), entry.getPriceMaterial(), entry.getPriceCount());
				itemAmount = entry.getItem().getAmount();
		    	didNotFit = player.getInventory().addItem(entry.getItem());
			}
		}
		
		if (didNotFit == null) {
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "cant-afford-item"));
			SoundPlayer.playSound("cant-afford-item", player);
		} else {
			for (ItemStack is : didNotFit.values())
	    		player.getWorld().dropItemNaturally(player.getLocation(), is).setVelocity(player.getLocation().getDirection().multiply(0.5));
			
			String itemName = entry.getItem().getType().toString();
			
			if (entry.getItem().hasItemMeta()) {
				ItemMeta itemMeta = entry.getItem().getItemMeta();
				if (itemMeta.hasDisplayName())
					itemName = itemMeta.getDisplayName();
			}
			
			MessageSender.sendMessage(player, 
				MessagesConfig.getInstance().getStringValue(player.getLocale(), "bought-item")
					.replace("%amount%", "" + itemAmount)
					.replace("%item%", itemName));
			SoundPlayer.playSound("buy-item", player);
		}
		
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
    		
    		ItemStack buyItem = shopEntry.getItem();
    		thisCategoryInv.setItem(9 * 2 + i, buyItem);
    		
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
    
    private void removeItems(Inventory inventory, Material type, int amount) {
        if (amount <= 0)
        	return;
        
        int size = inventory.getSize();
        
        for (int slot = 0; slot < size; ++slot) {
            ItemStack is = inventory.getItem(slot);
            
            if (is == null)
            	continue;
            
            if (type == is.getType()) {
                int newAmount = is.getAmount() - amount;
                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;
                    
                    if (amount == 0)
                    	break;
                }
            }
        }
    }

}
