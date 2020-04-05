package filip.bedwars.utils;

import org.bukkit.Material;

import filip.bedwars.game.TeamColor;

public class TeamColorConverter {
	
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
	
}
