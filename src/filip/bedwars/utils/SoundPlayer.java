package filip.bedwars.utils;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
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
	
	/**
	 * Play the sound that is in the sounds.yml to a uuid
	 * @param sound Sound as string
	 * @param uuid uuid that should receive the sound
	 * @return true if the sound was played successfully; false if the sound was not found
	 */
	public static boolean playSoundUUID(@NotNull String sound, @NotNull UUID uuid) {
		SoundSetting soundSetting = SoundsConfig.getInstance().getSoundValue(sound);

		Player player = Bukkit.getPlayer(uuid);
		
		if(soundSetting == null)
			return false;
		
		if(player == null)
			return false;
		
		playSound(sound, player);
		
		return true;
	}
	
	/**
	 * Play the sound that is in the sounds.yml to a uuid list
	 * @param sound Sound as string
	 * @param uuid uuids that should receive the sound
	 * @return true if the sound was played successfully; false if the sound was not found
	 */
	public static boolean playSoundUUID(@NotNull String sound, @NotNull List<UUID> uuids) {
		SoundSetting soundSetting = SoundsConfig.getInstance().getSoundValue(sound);

		if(soundSetting == null)
			return false;

		for(UUID uuid : uuids) {
			Player player = Bukkit.getPlayer(uuid);
			
			if(player == null)
				continue;
			
			playSound(sound, player);
		}
		
		return true;
	}

}
