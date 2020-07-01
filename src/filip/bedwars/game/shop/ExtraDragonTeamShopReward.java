package filip.bedwars.game.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.game.Team;
import filip.bedwars.game.Team.TeamUpgradeType;
import filip.bedwars.inventory.ItemBuilder;

public class ExtraDragonTeamShopReward extends TeamShopReward {

	public ExtraDragonTeamShopReward(int maxLevel, int[] priceCounts, Material[] priceMaterials) {
		super(TeamUpgradeType.EXTRA_DRAGONS, maxLevel, priceCounts, priceMaterials);
	}

	@Override
	public ItemStack getDisplayItem(Team team) {
		int level = team.upgrades.get(type);
		
		// TODO: Read the messages from a config file
		if (level >= maxLevel) {
			return new ItemBuilder()
					.setMaterial(Material.DRAGON_HEAD)
					.setName("§rExtra dragons are maxed out")
					.build();
		} else {
			return new ItemBuilder()
					.setMaterial(Material.DRAGON_HEAD)
					.setName("§rUpgrade to " + (team.upgrades.get(type) + 1) + " Extra Dragons")
					.build();
		}
	}
	
}
