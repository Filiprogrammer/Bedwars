package filip.bedwars.game.lobby;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.Countdown;
import filip.bedwars.game.Game;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class Lobby {
	
	private Location spawnPoint;
	private Countdown countdown;
	private Game game;
	
	public Lobby(Location spawnPoint, Game game) {
		this.spawnPoint = spawnPoint;
		this.game = game;
		// TODO: read from config
		this.countdown = new Countdown(60) {
			
			@Override
			public void onTick() {
				if (game.getPlayers().size() < game.getArena().getMinPlayersToStart()) {
					// Not enough players, countdown should be cancelled
					for (UUID uuid : game.getPlayers()) {
						Player player = Bukkit.getPlayer(uuid);
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "countdown-not-enough-player"));
						SoundPlayer.playSound("error", player);
					}
					
					cancel();
				}
				
				int secondsLeft = getSecondsLeft();
				
				if (secondsLeft == 1) {
					for (UUID uuid : game.getPlayers()) {
						Player player = Bukkit.getPlayer(uuid);
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "game-starts-in-one-second"));
						SoundPlayer.playSound("countdown-tick", player);
					}
				} else if ((secondsLeft % 10) == 0 || secondsLeft <= 5) {
					for (UUID uuid : game.getPlayers()) {
						Player player = Bukkit.getPlayer(uuid);
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "game-starts-in").replace("%seconds%", "" + secondsLeft));
						SoundPlayer.playSound("countdown-tick", player);
					}
				}
			}
			
			@Override
			public void onStart() {
				for(UUID uuid : game.getPlayers()) {
					MessageSender.sendMessageUUID(uuid, MessagesConfig.getInstance().getStringValue(Bukkit.getPlayer(uuid).getLocale(), "countdown-started"));
				}
			}
			
			@Override
			public boolean onFinish() {
				if (game.getPlayers().size() < game.getArena().getMinPlayersToStart()) {
					// Not enough players, countdown should be cancelled
					for (UUID uuid : game.getPlayers()) {
						Player player = Bukkit.getPlayer(uuid);
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "countdown-not-enough-player"));
						SoundPlayer.playSound("cancel", player);
					}
					
					cancel();
					return true;
				}
				
				game.startGame();
				return false;
			}
			
			@Override
			public void onCancel() {
				for(UUID uuid : game.getPlayers())
					MessageSender.sendMessageUUID(uuid, MessagesConfig.getInstance().getStringValue(Bukkit.getPlayer(uuid).getLocale(), "countdown-not-enough-player"));
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
		player.teleport(MainConfig.getInstance().getMainLobby());
	}
	
	private void updatePlayerVisibilities() {
		
	}
	
}
