package filip.bedwars.game.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.game.Team;
import filip.bedwars.game.Team.TeamUpgradeType;
import filip.bedwars.inventory.ItemBuilder;

public class AttackBoostTeamShopReward extends TeamShopReward {

	public AttackBoostTeamShopReward(int maxLevel, int[] priceCounts, Material[] priceMaterials) {
		super(TeamUpgradeType.ATTACK_BOOST, maxLevel, priceCounts, priceMaterials);
	}

	@Override
	public ItemStack getDisplayItem(Team team) {
		int level = team.upgrades.get(type);
		
		// TODO: Read the messages from a config file
		if (level >= maxLevel) {
			return new ItemBuilder()
					.setMaterial(Material.DIAMOND_SWORD)
					.setName("§rAttack Boost is maxed out")
					.build();
		} else {
			return new ItemBuilder()
					.setMaterial(Material.GOLDEN_SWORD)
					.setName("§rBuy Attack Boost Level " + (team.upgrades.get(type) + 1))
					.build();
		}
	}

}
