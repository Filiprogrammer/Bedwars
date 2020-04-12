package filip.bedwars.utils;

import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundSetting {
	
	private final Sound sound;
	private final float pitch;
	private final float volume;
	
	public SoundSetting(Sound sound, float pitch, float volume) {
		this.sound = sound;
		this.pitch = pitch;
		this.volume = volume;
	}
	
	public Sound getSound() {
		return sound;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public float getVolume() {
		return volume;
	}
	
	/**
	 * Plays the SoundSetting object for a player
	 * @param player the player that receives the sound
	 */
	public void play(Player player) {
		player.playSound(player.getLocation(), sound, volume, pitch);
	}
	
	/**
	 * Plays the SoundSetting object for a list of players
	 * @param players players that receive the sound
	 */
	public void play(List<Player> players) {
		for(Player player : players)
			play(player);
	}

}
