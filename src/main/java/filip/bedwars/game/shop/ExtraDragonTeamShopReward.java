package filip.bedwars.game.shop;

import java.util.ArrayList;

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
					.setMaterial(Material.DRAGON_HEAD)
					.setName("§rExtra dragons are maxed out")
					.build();
		} else {
			return itemBuilder
					.setMaterial(Material.DRAGON_HEAD)
					.setName("§rUpgrade to " + (team.upgrades.get(type) + 1) + " Extra Dragons")
					.build();
		}
	}
	
}
