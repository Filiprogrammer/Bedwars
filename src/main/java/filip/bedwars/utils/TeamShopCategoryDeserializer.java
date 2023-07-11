package filip.bedwars.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

import filip.bedwars.game.Team.TeamUpgradeType;
import filip.bedwars.game.Trap;
import filip.bedwars.game.shop.AttackBoostTeamShopReward;
import filip.bedwars.game.shop.BedRestoreTeamShopReward;
import filip.bedwars.game.shop.ExtraDragonTeamShopReward;
import filip.bedwars.game.shop.HealPoolTeamShopReward;
import filip.bedwars.game.shop.MiningBoostTeamShopReward;
import filip.bedwars.game.shop.ProtectionBoostTeamShopReward;
import filip.bedwars.game.shop.ShopCategory;
import filip.bedwars.game.shop.ShopEntry;
import filip.bedwars.game.shop.TeamShopEntry;
import filip.bedwars.game.shop.TeamShopReward;
import filip.bedwars.game.shop.TrapTeamShopReward;

public class TeamShopCategoryDeserializer {

	public static ShopCategory deserializeCategory(Object serializedCategory) {
		Map<String, Object> mapOfElements = (Map<String, Object>) serializedCategory;
		
		String categoryName = ((String) mapOfElements.get("name")).replace('&', '§');
		
		if (categoryName == null) {
			MessageSender.sendWarning("A Shop Category does not have a name");
			return null;
		}
		
		Material categoryMaterial = Material.valueOf((String) mapOfElements.get("material"));
		
		if (categoryMaterial == null) {
			MessageSender.sendWarning("§eThe Shop Category §6\"" + categoryName + "\" §edoes not have a valid material");
			return null;
		}
		
		List<Map<String, Object>> shopEntriesList = (List<Map<String, Object>>) mapOfElements.get("shopentries");
		List<ShopEntry> shopEntries = new ArrayList<ShopEntry>();
		
		for (Map<String, Object> serializedShopEntry : shopEntriesList) {
			Object typeObject = serializedShopEntry.get("type");
			TeamUpgradeType type = null;
			
			if (typeObject == null) {
				MessageSender.sendWarning("team upgrade type of an entry in the team shop category §6\"" + categoryName + "\" §e was not specified");
			} else if (!(typeObject instanceof String)) {
				MessageSender.sendWarning("team upgrade type of an entry in the team shop category §6\"" + categoryName + "\" §e has an invalid value");
			} else {
				try {
					type = TeamUpgradeType.valueOf((String) typeObject);
				} catch (IllegalArgumentException e) {
					MessageSender.sendWarning("team upgrade type of an item in the team shop category §6\"" + categoryName + "\" §e has an invalid value");
				}
			}
			
			if (type == null)
				continue;
			
			Object maxlevelObject = serializedShopEntry.get("maxlevel");
			int maxlevel = 1;
			
			if (maxlevelObject == null)
				MessageSender.sendWarning("maxlevel of an entry in the team shop category §6\"" + categoryName + "\" §e was not specified");
			else if (!(maxlevelObject instanceof Integer))
				MessageSender.sendWarning("maxlevel of an entry in the team shop category §6\"" + categoryName + "\" §e has an invalid value");
			else
				maxlevel = (int) maxlevelObject;
			
			Object priceCountsObject = serializedShopEntry.get("priceCounts");
			int[] priceCounts = null;
			
			if (priceCountsObject == null)
				MessageSender.sendWarning("priceCounts of an entry in the team shop category §6\"" + categoryName + "\" §e was not specified");
			else if (!(priceCountsObject instanceof List<?>))
				MessageSender.sendWarning("priceCounts of an entry in the team shop category §6\"" + categoryName + "\" §e has an invalid value");
			else
				priceCounts = ((List<Integer>) priceCountsObject).stream().mapToInt(i->i).toArray();
			
			Object priceMaterialsObject = serializedShopEntry.get("priceMaterials");
			Material[] priceMaterials = null;
			
			if (priceMaterialsObject == null)
				MessageSender.sendWarning("priceMaterials of an entry in the team shop category §6\"" + categoryName + "\" §e was not specified");
			else if (!(priceMaterialsObject instanceof List<?>))
				MessageSender.sendWarning("priceMaterials of an entry in the team shop category §6\"" + categoryName + "\" §e has an invalid value");
			else {
				List<String> priceMaterialsStrings = (List<String>) priceMaterialsObject;
				priceMaterials = new Material[priceMaterialsStrings.size()];
				
				for (int i = 0; i < priceMaterialsStrings.size(); ++i)
					priceMaterials[i] = Material.valueOf(priceMaterialsStrings.get(i));
			}
			
			TeamShopReward reward = null;
			
			switch (type) {
			case HEAL_POOL:
				reward = new HealPoolTeamShopReward(maxlevel, priceCounts, priceMaterials);
				break;
			case MINING_BOOST:
				reward = new MiningBoostTeamShopReward(maxlevel, priceCounts, priceMaterials);
				break;
			case ATTACK_BOOST:
				reward = new AttackBoostTeamShopReward(maxlevel, priceCounts, priceMaterials);
				break;
			case PROTECTION_BOOST:
				reward = new ProtectionBoostTeamShopReward(maxlevel, priceCounts, priceMaterials);
				break;
			case EXTRA_DRAGONS:
				reward = new ExtraDragonTeamShopReward(maxlevel, priceCounts, priceMaterials);
				break;
			case BED_RESTORE:
				reward = new BedRestoreTeamShopReward(maxlevel, priceCounts, priceMaterials);
				break;
			case TRAP:
				Object displayNameObject = serializedShopEntry.get("displayName");
				String displayName = null;
				
				if (displayNameObject instanceof String)
					displayName = ((String) displayNameObject).replace('&', '§');
				
				Object materialObject = serializedShopEntry.get("material");
				Material material = null;
				
				if (materialObject instanceof String) {
					try {
						material = Material.valueOf((String) materialObject);
					} catch (IllegalArgumentException e) {
						MessageSender.sendWarning("material of a team shop trap has an invalid value \"" + ((String) materialObject) + "\"");
					}
				}
				
				Object rangeObject = serializedShopEntry.get("range");
				int range = 20;
				
				if (rangeObject instanceof Integer)
					range = (int) rangeObject;
				
				Object effectsIntruderObject = serializedShopEntry.get("effectsIntruder");
				List<PotionEffect> effectsIntruder = new ArrayList<>();
				
				if (effectsIntruderObject instanceof List) {
					List<?> effectsIntruderList = (List<?>) effectsIntruderObject;
					
					for (Object effectObject : effectsIntruderList) {
						if (effectObject instanceof PotionEffect)
							effectsIntruder.add((PotionEffect) effectObject);
					}
				}
				
				Object effectsTeamObject = serializedShopEntry.get("effectsTeam");
				List<PotionEffect> effectsTeam = new ArrayList<>();
				
				if (effectsTeamObject instanceof List) {
					List<?> effectsTeamList = (List<?>) effectsTeamObject;
					
					for (Object effectObject : effectsTeamList) {
						if (effectObject instanceof PotionEffect)
							effectsTeam.add((PotionEffect) effectObject);
					}
				}
				
				reward = new TrapTeamShopReward(maxlevel, priceCounts, priceMaterials, new Trap(displayName, range, effectsIntruder, effectsTeam), material);
				break;
			}
			
			shopEntries.add(new TeamShopEntry(reward));
		}
		
		return new ShopCategory(categoryName, categoryMaterial, shopEntries);
	}

}
