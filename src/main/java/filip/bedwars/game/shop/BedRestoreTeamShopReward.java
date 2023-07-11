package filip.bedwars.game.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.GamePlayer;
import filip.bedwars.game.Team;
import filip.bedwars.game.Team.TeamUpgradeType;
import filip.bedwars.inventory.ItemBuilder;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;
import filip.bedwars.utils.TeamColorConverter;

public class BedRestoreTeamShopReward extends TeamShopReward {

	public BedRestoreTeamShopReward(int maxLevel, int[] priceCounts, Material[] priceMaterials) {
		super(TeamUpgradeType.BED_RESTORE, maxLevel, priceCounts, priceMaterials);
	}

	@Override
	public ItemStack getDisplayItem(Team team) {
		// TODO: Read the messages from a config file
		if (team.hasBed()) {
			return new ItemBuilder()
					.setMaterial(Material.LIGHT_GRAY_BED)
					.setName("§rYou still have a bed")
					.build();
		} else {
			return new ItemBuilder()
					.setMaterial(TeamColorConverter.convertTeamColorToBedMaterial(team.getBase().getTeamColor()))
					.setName("§rRestore bed")
					.build();
		}
	}
	
	@Override
	public boolean reward(Team team) {
		if (team.getMembers().size() == 0)
			return false;
		
		if (team.getMembers().get(0).game.getGameLogic().allBedsPermDestroyed)
			return false;
		
		if (team.hasBed())
			return false;
		
		team.restoreBed(team.getMembers().get(0).getPlayer().getWorld());
		
		for (GamePlayer gp : team.getMembers()) {
			Player p = gp.getPlayer();
			p.sendTitle(MessagesConfig.getInstance().getStringValue(p.getLocale(), "your-bed-restored"), "", 10, 70, 20);
		}
		
		broadcastBedRestored(team);
		
		return true;
	}
	
	private void broadcastBedRestored(Team team) {
		if (team.getMembers().size() == 0)
			return;
		
		for (Player p : team.getMembers().get(0).getPlayer().getWorld().getPlayers()) {
			String colorStr = TeamColorConverter.convertTeamColorToStringForMessages(team.getBase().getTeamColor(), p.getLocale());
			
			MessageSender.sendMessage(p,
					MessagesConfig.getInstance().getStringValue(p.getLocale(), "bed-restored")
					.replace("%teamcolor%", colorStr));
			
			SoundPlayer.playSound("bed-restored", p);
		}
	}

}
