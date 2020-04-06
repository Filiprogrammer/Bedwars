package filip.bedwars.inventory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class ClickableInventory implements IClickable {
	protected final Inventory inventory;
	
	public ClickableInventory(Inventory inventory) {
        this.inventory = inventory;
    }
	
	@Override
	public Inventory getInventory() {
		return inventory;
	}
	
	@Override
    public boolean matches(Inventory inventory, Player player) {
        return inventoryGetTitle(this.inventory).equals(inventoryGetTitle(inventory));
    }
	
	/**
	 * An ugly way to get the title of the inventory because inventory.getTitle() has been removed in 1.14
	 * @param inv
	 * @return
	 */
	private String inventoryGetTitle(Inventory inv) {
		try {
			String versionStr = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
			
			Class<?> minecraftInventoryClass = Class.forName("org.bukkit.craftbukkit." + versionStr + ".inventory.CraftInventoryCustom.MinecraftInventory");
			Class<?> craftInventoryClass = Class.forName("org.bukkit.craftbukkit." + versionStr + ".inventory.CraftInventory");
			Method getInventoryMethod = craftInventoryClass.getMethod("getInventory");
			
			// CraftInventory cinv = (CraftInventory) inv;
			Object cinv = craftInventoryClass.cast(inv);
			
			// IInventory iinv = cinv.getInventory();
			Object iinv = getInventoryMethod.invoke(cinv);
			
			Object minv = minecraftInventoryClass.cast(iinv);
			Method getTitleMethod = minecraftInventoryClass.getMethod("getTitle");
			String title = (String) getTitleMethod.invoke(minv);
			return title;
		} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		return null;
    }
}
