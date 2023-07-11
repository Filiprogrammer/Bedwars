package filip.bedwars.utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {

	public static void removeItems(Inventory inventory, Material type, int amount) {
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
