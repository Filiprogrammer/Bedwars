package filip.bedwars;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import filip.bedwars.inventory.IClickable;
import filip.bedwars.inventory.IUsable;
import filip.bedwars.listener.inventory.InventoryClickListener;

public class BedwarsPlugin extends JavaPlugin {

	private static BedwarsPlugin plugin;
	
	private List<IClickable> clickables;
	private List<IUsable> usables;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		clickables = new ArrayList<IClickable>();
		usables = new ArrayList<IUsable>();
		
		new InventoryClickListener(this);
		new BedwarsCommand(this);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public static BedwarsPlugin getInstance() {
		return plugin;
	}
	
	/**
     * Get a clickable.
     *
     * @param inventory
     * @return clickable
     */
    public IClickable getClickable(Inventory inventory, Player player) {
        for (int i = 0; i < clickables.size(); i++) {
            if (clickables.get(i).matches(inventory, player)) {
                return clickables.get(i);
            }
        }
        
        return null;
    }
    
    public void addClickable(IClickable clickable) {
    	clickables.add(clickable);
    }
    
    /**
     * Get a usable.
     *
     * @param inventory
     * @return usable
     */
    public IUsable getUsable(ItemStack itemStack) {
        for (int i = 0; i < usables.size(); i++) {
            if (usables.get(i).matches(itemStack)) {
                return usables.get(i);
            }
        }
        
        return null;
    }
    
    public void addUsable(IUsable usable) {
    	usables.add(usable);
    }
	
}
