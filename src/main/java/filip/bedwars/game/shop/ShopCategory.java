package filip.bedwars.game.shop;

import java.util.List;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ShopCategory {
	
	private final String name;
	private final Material material;
	private List<ShopEntry> shopEntries;
	
	public ShopCategory(final String name, final Material material, @NotNull List<ShopEntry> shopEntries) {
		this.name = name;
		this.material = material;
		this.shopEntries = shopEntries;
	}
	
	public String getName() {
		return name;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public List<ShopEntry> getShopEntries() {
		return shopEntries;
	}
	
	public int getShopEntriesCount() {
		return shopEntries.size();
	}
	
	public ShopEntry getShopEntry(int index) {
		if (index >= shopEntries.size() || index < 0)
			return null;
		
		return shopEntries.get(index);
	}
	
	public void addShopEntry(ShopEntry entry) {
		shopEntries.add(entry);
	}
	
	public void removeShopEntry(ShopEntry entry) {
		shopEntries.remove(entry);
	}
	
	public void removeShopEntry(int index) {
		shopEntries.remove(index);
	}
	
}
