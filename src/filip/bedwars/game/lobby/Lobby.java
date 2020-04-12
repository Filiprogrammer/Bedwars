package filip.bedwars.game.lobby;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import filip.bedwars.config.MessagesConfig;
import filip.bedwars.config.SoundsConfig;
import filip.bedwars.game.Countdown;
import filip.bedwars.game.Game;
import filip.bedwars.utils.MessageSender;

public class Lobby {
	
	private Location spawnPoint;
	private Countdown countdown;
	private Game game;
	
	public Lobby(Location spawnPoint) {
		this.spawnPoint = spawnPoint;
		// TODO: read from config
		this.countdown = new Countdown(60) {
			
			@Override
			public void onTick() {
				if (game.getPlayers().size() < game.getArena().getMinPlayersToStart()) {
					// Not enough players, countdown should be cancelled
					for (UUID uuid : game.getPlayers()) {
						Player player = Bukkit.getPlayer(uuid);
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "countdown-not-enough-player"));
						player.playSound(player.getLocation(), SoundsConfig.getInstance().getSoundValue("error"), 1, 1);
					}
					
					countdown.cancel();
				}
				
				int secondsLeft = countdown.getSecondsLeft();
				
				if (secondsLeft == 1) {
					for (UUID uuid : game.getPlayers()) {
						Player player = Bukkit.getPlayer(uuid);
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "game-starts-in-one-second"));
						player.playSound(player.getLocation(), SoundsConfig.getInstance().getSoundValue("countdown-tick"), 1, 1);
					}
				} else if ((secondsLeft % 10) == 0 || secondsLeft <= 5) {
					for (UUID uuid : game.getPlayers()) {
						Player player = Bukkit.getPlayer(uuid);
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "game-starts-in"));
						player.playSound(player.getLocation(), SoundsConfig.getInstance().getSoundValue("countdown-tick"), 1, 1);
					}
				}
			}
			
			@Override
			public void onStart() {
				// TODO: Get message from config
				MessageSender.sendMessageUUID(game.getPlayers(), "Countdown was started");
			}
			
			@Override
			public boolean onFinish() {
				if (game.getPlayers().size() < game.getArena().getMinPlayersToStart()) {
					// Not enough players, countdown should be cancelled
					for (UUID uuid : game.getPlayers()) {
						Player player = Bukkit.getPlayer(uuid);
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "countdown-not-enough-player"));
						player.playSound(player.getLocation(), SoundsConfig.getInstance().getSoundValue("error"), 1, 1);
					}
					
					countdown.cancel();
					return true;
				}
				
				// TODO: Start game
				return false;
			}
			
			@Override
			public void onCancel() {
				// TODO: Get message from config
				MessageSender.sendMessageUUID(game.getPlayers(), "Countdown was cancelled");
			}
		};
		
	}
	
	public Location getSpawnPoint() {
		return spawnPoint;
	}
	
	/**
	 * Teleport player into the lobby.
	 * @param uuid player UUID
	 */
	public void joinPlayer(Player player) {
		player.teleport(spawnPoint);
		
		if (!countdown.isRunning() && (game.getPlayers().size() >= game.getArena().getMinPlayersToStart()))
			countdown.start();
		// TODO: Hide other players
	}
	
	/**
	 * Remove player from lobby.
	 * @param uuid player UUID
	 */
	public void leavePlayer(Player player) {
		// TODO: Unhide the player
		// TODO: Teleport to main lobby
	}
	
	private void updatePlayerVisibilities() {
		
	}
	
}
