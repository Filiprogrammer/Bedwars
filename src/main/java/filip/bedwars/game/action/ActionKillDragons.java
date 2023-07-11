package filip.bedwars.game.action;

import java.util.Iterator;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.Game;
import filip.bedwars.game.GameLogic;
import filip.bedwars.utils.EnderDragonController;

public class ActionKillDragons extends Action {

	@Override
	public void execute(@NotNull Game game, @NotNull GameLogic gameLogic) {
		Iterator<EnderDragonController> iter = gameLogic.enderDragonControllers.iterator();
		
		while (iter.hasNext()) {
			EnderDragonController enderDragonController = iter.next();
			iter.remove();
			enderDragonController.stopTask();
			enderDragonController.despawn(gameLogic.getGameWorld().getWorld().getPlayers().toArray(new Player[0]));
		}
	}

	public static String[] getArgumentNames() {
		return new String[0];
	}

}
