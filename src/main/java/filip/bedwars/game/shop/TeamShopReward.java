package filip.bedwars.game.shop;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.GamePlayer;
import filip.bedwars.game.Team;
import filip.bedwars.game.Team.TeamUpgradeType;

public abstract class TeamShopReward {

	protected final TeamUpgradeType type;
	protected final int maxLevel;
	protected final int[] priceCounts;
	protected final Material[] priceMaterials;

	public TeamShopReward(final TeamUpgradeType type, final int maxLevel, @NotNull final int[] priceCounts, @NotNull final Material[] priceMaterials) {
		this.type = type;
		this.maxLevel = maxLevel;
		// TODO: Add argument checks
		this.priceCounts = priceCounts;
		this.priceMaterials = priceMaterials;
	}

	public int getPriceCount(@NotNull Team team) {
		int level = team.upgrades.get(type);

		if (level >= maxLevel)
			return 0;

		return priceCounts[level];
	}

	public Material getPriceMaterial(@NotNull Team team) {
		int level = team.upgrades.get(type);

		if (level >= maxLevel)
			return null;

		return priceMaterials[level];
	}

	protected int getPriceItemCount(Inventory inv, Material priceMaterial) {
		int amount = 0;

		for (int i = 0; i < 36; ++i) {
			final ItemStack itemStack = inv.getItem(i);

			if (itemStack == null)
				continue;

			if (itemStack.getType().equals(priceMaterial)) {
				amount += itemStack.getAmount();
			}
		}

		return amount;
	}

	public abstract ItemStack getDisplayItem(@NotNull Team team);

	public boolean canBuy(@NotNull GamePlayer gamePlayer) {
		Team team = gamePlayer.getTeam();
		Inventory inv = gamePlayer.getPlayer().getInventory();
		final int amount = getPriceItemCount(inv, getPriceMaterial(team));

		if(amount >= getPriceCount(team))
			return true;

		return false;
	}

	public boolean reward(@NotNull Team team) {
		int level = team.upgrades.get(type);

		if (level >= maxLevel)
			return false;

		++level;
		team.upgrades.put(type, level);
		return true;
	}

}
