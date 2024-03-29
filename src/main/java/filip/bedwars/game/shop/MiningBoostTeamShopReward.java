package filip.bedwars.game.shop;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.game.Team;
import filip.bedwars.game.Team.TeamUpgradeType;
import filip.bedwars.inventory.ItemBuilder;

public class MiningBoostTeamShopReward extends TeamShopReward {

	public MiningBoostTeamShopReward(int maxLevel, int[] priceCounts, Material[] priceMaterials) {
		super(TeamUpgradeType.MINING_BOOST, maxLevel, priceCounts, priceMaterials);
	}

	@Override
	public ItemStack getDisplayItem(Team team) {
		int level = team.upgrades.get(type);
		StringBuilder lore = new StringBuilder();
		
		int i;
		for (i = 0; i < level; ++i)
			lore.append("§l§a[]");
		
		for (; i < maxLevel; ++i)
			lore.append("§l§7[]");
		
		@SuppressWarnings("serial")
		ItemBuilder itemBuilder = new ItemBuilder().setLore(new ArrayList<String>() {{ add(lore.toString()); }});
		
		// TODO: Read the messages from a config file
		if (level >= maxLevel) {
			return itemBuilder
					.setMaterial(Material.DIAMOND_PICKAXE)
					.setName("§rMining Boost is maxed out")
					.build();
		} else {
			return itemBuilder
					.setMaterial(Material.GOLDEN_PICKAXE)
					.setName("§rBuy Mining Boost Level " + (team.upgrades.get(type) + 1))
					.build();
		}
	}

}
