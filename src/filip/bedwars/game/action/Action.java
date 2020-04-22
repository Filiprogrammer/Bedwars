package filip.bedwars.game.action;

import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.Game;
import filip.bedwars.game.GameLogic;

public abstract class Action {
	
	public abstract void execute(@NotNull Game game, @NotNull GameLogic gameLogic);
	
}
