package filip.bedwars.game.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.game.Team;
import filip.bedwars.game.Team.TeamUpgradeType;
import filip.bedwars.inventory.ItemBuilder;

public class HealPoolTeamShopReward extends TeamShopReward {

	public HealPoolTeamShopReward(int maxLevel, int[] priceCounts, Material[] priceMaterials) {
		super(TeamUpgradeType.HEAL_POOL, maxLevel, priceCounts, priceMaterials);
	}

	@Override
	public ItemStack getDisplayItem(Team team) {
		int level = team.upgrades.get(type);
		
		// TODO: Read the messages from a config file
		if (level >= maxLevel) {
			return new ItemBuilder()
					.setMaterial(Material.ENCHANTED_GOLDEN_APPLE)
					.setName("§rHeal Pool is maxed out")
					.build();
		} else {
			return new ItemBuilder()
					.setMaterial(Material.GOLDEN_APPLE)
					.setName("§rBuy Heal Pool Level " + (team.upgrades.get(type) + 1))
					.build();
		}
	}
	
}
