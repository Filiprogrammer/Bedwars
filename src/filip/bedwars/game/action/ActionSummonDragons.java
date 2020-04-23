package filip.bedwars.game.action;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.Game;
import filip.bedwars.game.GameLogic;
import filip.bedwars.game.Team;
import filip.bedwars.utils.EnderDragonController;

public class ActionSummonDragons extends Action {

	@Override
	public void execute(@NotNull Game game, @NotNull GameLogic gameLogic) {
		for (Team team : game.getTeams()) {
			if (team.getMembers().size() == 0)
				continue;
			
			Set<Entity> targetEntities = new HashSet<>();
			targetEntities.addAll(gameLogic.getGameWorld().getWorld().getPlayers());
			
			// TODO: Clean this shit up
			gameLogic.enderDragonControllers.add(
					new EnderDragonController(team.getBase().getSpawn(gameLogic.getGameWorld().getWorld()),
							targetEntities,
							new HashSet<Player>(gameLogic.getGameWorld().getWorld().getPlayers()))
			);
		}
	}

	public static String[] getArgumentNames() {
		return new String[0];
	}

}
