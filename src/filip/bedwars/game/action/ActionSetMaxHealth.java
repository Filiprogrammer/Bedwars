package filip.bedwars.game.action;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.Game;
import filip.bedwars.game.GameLogic;

public class ActionSetMaxHealth extends Action {

	private final int maxHealth;
	
	public ActionSetMaxHealth(@NotNull Integer maxHealth) {
		this.maxHealth = maxHealth;
	}

	@Override
	public void execute(@NotNull Game game, @NotNull GameLogic gameLogic) {
		for (UUID uuid : game.getPlayers()) {
			Player p = Bukkit.getPlayer(uuid);
			
			if (p != null)
				p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
		}
	}

	public static String[] getArgumentNames() {
		return new String[] { "max-health" };
	}

}
