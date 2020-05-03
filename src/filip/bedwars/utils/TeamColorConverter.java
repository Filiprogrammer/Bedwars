package filip.bedwars.utils;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.TeamColor;

public class TeamColorConverter {
	
	/**
	 * If you input a colored Wool Block you will get the TeamColor-Enum returned
	 * @param material The wool block that is going to be converted
	 * @return TeamColor enum; default return value is TeamColor.RED
	 */
	public static TeamColor convertMaterialToTeamColor(Material material) {
		String materialString = material.toString();
		
		// Default team color
		TeamColor ret = TeamColor.RED;
		
		if (materialString.endsWith("_WOOL")) {
			try {
				ret = TeamColor.valueOf(materialString.substring(0, materialString.length() - 5));
			} catch (IllegalArgumentException e) {}
		}
		
		return ret;
	}
	
	public static Material convertTeamColorToWoolMaterial(@NotNull TeamColor teamColor) {
		return Material.valueOf(teamColor.toString() + "_WOOL");
	}
	
	public static Material convertTeamColorToStainedGlassMaterial(@NotNull TeamColor teamColor) {
		return Material.valueOf(teamColor.toString() + "_STAINED_GLASS");
	}
	
	public static String convertTeamColorToStringForMessages(TeamColor teamColor, String locale) {
		String colorConfigKey = "color-" + teamColor.toString().toLowerCase().replace("_", "-");
		return MessagesConfig.getInstance().getStringValue(locale, colorConfigKey);
	}
	
}
