package filip.bedwars.inventory;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.utils.ReflectionUtils;

public abstract class ClickableInventory implements IClickable {

	protected final Inventory inventory;
	protected final Player player;

	public ClickableInventory(Inventory inventory, Player player) {
		this.inventory = inventory;
		this.player = player;
		registerClickable();
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public boolean matches(Inventory inventory, Player player) {
		String invTitle = inventoryGetTitle(inventory);

		if (invTitle == null)
			return false;

		return (this.player == player) && invTitle.equals(inventoryGetTitle(this.inventory));
	}

	/**
	 * An ugly way to get the title of the inventory because inventory.getTitle() has been removed in 1.14
	 * @param inv
	 * @return
	 */
	private String inventoryGetTitle(Inventory inv) {
		try {
			ReflectionUtils reflectionUtils = BedwarsPlugin.getInstance().reflectionUtils;
			// CraftInventory cinv = (CraftInventory) inv;
			Object cinv = reflectionUtils.craftInventoryClass.cast(inv);

			// IInventory iinv = cinv.getInventory();
			Object iinv = reflectionUtils.craftInventoryGetInventoryMethod.invoke(cinv);

			if (iinv.getClass() != reflectionUtils.minecraftInventoryClass)
				return null;

			Object minv = reflectionUtils.minecraftInventoryClass.cast(iinv);
			String title = (String) reflectionUtils.minecraftInventoryGetTitleMethod.invoke(minv);
			return title;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			e.printStackTrace();
		}

		return null;
	}
}
