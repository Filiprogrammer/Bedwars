package filip.bedwars.game;

import java.util.List;

import org.bukkit.potion.PotionEffect;

public class Trap {

	private final String name;
	private final int range;
	private final List<PotionEffect> effectsIntruder;
	private final List<PotionEffect> effectsTeam;
	
	public Trap(String name, int range, List<PotionEffect> effectsIntruder, List<PotionEffect> effectsTeam) {
		this.name = name;
		this.range = range;
		this.effectsIntruder = effectsIntruder;
		this.effectsTeam = effectsTeam;
	}
	
	public String getName() {
		return name;
	}
	
	public int getRange() {
		return range;
	}
	
	public List<PotionEffect> getEffectsIntruder() {
		return effectsIntruder;
	}
	
	public List<PotionEffect> getEffectsTeam() {
		return effectsTeam;
	}
	
}
