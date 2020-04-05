package filip.bedwars;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import filip.bedwars.inventory.IClickable;
import filip.bedwars.listener.inventory.InventoryClickListener;

public class BedwarsPlugin extends JavaPlugin {

	private static BedwarsPlugin plugin;
	
	private List<IClickable> clickables;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		clickables = new ArrayList<IClickable>();
		
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
     * Gets a clickable
     *
     * @param inventory
     * @param playerWrapper
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
	
}
