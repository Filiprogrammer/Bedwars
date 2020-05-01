package filip.bedwars.game.action;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.Game;
import filip.bedwars.game.GameLogic;
import filip.bedwars.game.GamePlayer;

public class ActionSetMaxHealth extends Action {

	private final int maxHealth;
	
	public ActionSetMaxHealth(@NotNull Integer maxHealth) {
		this.maxHealth = maxHealth;
	}

	@Override
	public void execute(@NotNull Game game, @NotNull GameLogic gameLogic) {
		for (GamePlayer gamePlayer : game.getPlayers()) {
			Player p = gamePlayer.getPlayer();
			
			if (p != null)
				p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
		}
	}

	public static String[] getArgumentNames() {
		return new String[] { "max-health" };
	}

}
