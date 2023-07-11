package filip.bedwars.game.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import filip.bedwars.game.Team;
import filip.bedwars.game.Team.TeamUpgradeType;
import filip.bedwars.game.Trap;
import filip.bedwars.inventory.ItemBuilder;

public class TrapTeamShopReward extends TeamShopReward {

	private final Trap trap;
	private final ItemStack displayItem;
	
	public TrapTeamShopReward(int maxLevel, int[] priceCounts, Material[] priceMaterials, Trap trap, Material material) {
		super(TeamUpgradeType.TRAP, maxLevel, priceCounts, priceMaterials);
		
		this.trap = trap;
		this.displayItem = new ItemBuilder()
				.setMaterial((material == null) ? Material.STRING : material)
				.build();
	}

	@Override
	public ItemStack getDisplayItem(Team team) {
		ItemMeta itemMeta = displayItem.getItemMeta();
		
		if (team.getTraps().stream().anyMatch(trap -> trap.getName().equals(this.trap.getName())))
			itemMeta.setDisplayName("§7Already bought " + trap.getName());
		else
			itemMeta.setDisplayName("§aBuy " + trap.getName());
		
		displayItem.setItemMeta(itemMeta);
		return displayItem;
	}
	
	public Trap getTrap() {
		return trap;
	}
	
	@Override
	public boolean reward(Team team) {
		if (team.getTraps().stream().anyMatch(trap -> trap.getName().equals(this.trap.getName())))
			return false;
		
		team.addTrap(trap);
		return true;
	}

}
