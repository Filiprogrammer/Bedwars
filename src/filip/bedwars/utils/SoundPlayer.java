package filip.bedwars.utils;

import java.util.List;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.config.SoundsConfig;

public class SoundPlayer {
	
	/**
	 * Play the sound that is in the sounds.yml to the given player.
	 * @param sound Sound as string
	 * @param player Player that should receive the sound
	 * @return true if the sound was played successfully; false if the sound was not found
	 */
	public static boolean playSound(@NotNull String sound, @NotNull Player player) {
		SoundSetting soundSetting = SoundsConfig.getInstance().getSoundValue(sound);
		
		if(soundSetting == null)
			return false;
		
		soundSetting.play(player);
		return true;
	}
	
	/**
	 * Play the sound that is in the sounds.yml to the given group of players.
	 * @param sound Sound as string
	 * @param players Players that should receive the sound
	 * @return true if the sound was played successfully; false if the sound was not found
	 */
	public static boolean playSound(@NotNull String sound, @NotNull List<Player> players) {
		return playSound(sound, (Player[]) players.toArray());
	}
	
	/**
	 * Play the sound that is in the sounds.yml to the given group of players.
	 * @param sound Sound as string
	 * @param players Players that should receive the sound
	 * @return true if the sound was played successfully; false if the sound was not found
	 */
	public static boolean playSound(@NotNull String sound, @NotNull Player... players) {
		SoundSetting soundSetting = SoundsConfig.getInstance().getSoundValue(sound);

		if(soundSetting == null)
			return false;
		
		for(Player player : players)
			playSound(sound, player);
		
		return true;
	}

}
