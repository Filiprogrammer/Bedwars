package filip.bedwars.game.action;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.Game;
import filip.bedwars.game.GameLogic;
import filip.bedwars.utils.MessageSender;

public class ActionSpawnerChange extends Action {

	enum Alert {
		CHAT, ACTION_BAR, BOSS_BAR, TITLE, SUBTITLE
	}
	
	private final String name;
	private final double ticksPerSpawnMultiplier;
	private final Alert alert;
	
	public ActionSpawnerChange(String name, Double ticksPerSpawnMultiplier, String alert) {
		this.name = name;
		this.ticksPerSpawnMultiplier = ticksPerSpawnMultiplier;
		this.alert = Alert.valueOf(alert);
	}
	
	@Override
	public void execute(@NotNull Game game, @NotNull GameLogic gameLogic) {
		// TODO: change the spawner rates
		
		for (Player p : gameLogic.getGameWorld().getWorld().getPlayers()) {
			switch (alert) {
			case CHAT:
				MessageSender.sendMessage(p, name.replace('&', '§') + "§7-Spawner delays have been multiplied by " + ticksPerSpawnMultiplier);
				break;
			case ACTION_BAR:
				p.sendActionBar(name.replace('&', '§') + "§r-Spawner delays have been multiplied by " + ticksPerSpawnMultiplier);
				break;
			case BOSS_BAR:
				// TODO: boss bar
				break;
			case TITLE:
				p.sendTitle(name.replace('&', '§') + "§r-Spawner delays have been multiplied by " + ticksPerSpawnMultiplier, null, 10, 70, 20);
				break;
			case SUBTITLE:
				p.sendTitle(null, name.replace('&', '§') + "§r-Spawner delays have been multiplied by " + ticksPerSpawnMultiplier, 10, 70, 20);
				break;
			}
		}
	}
	
	public static String[] getArgumentNames() {
		return new String[] { "name", "ticksPerSpawn-multiplier", "alert" };
	}

}
