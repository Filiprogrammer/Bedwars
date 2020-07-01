package filip.bedwars.game.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.Game;
import filip.bedwars.game.GameLogic;
import filip.bedwars.game.GamePlayer;
import filip.bedwars.game.Team;
import filip.bedwars.game.Team.TeamUpgradeType;
import filip.bedwars.utils.EnderDragonController;

public class ActionSummonDragons extends Action {

	@Override
	public void execute(@NotNull Game game, @NotNull GameLogic gameLogic) {
		for (Team team : game.getTeams()) {
			if (team.getMembers().size() == 0)
				continue;
			
			List<Entity> targetEntities = new ArrayList<>();
			
			for (Team t : game.getTeams()) {
				if (t.getId() != team.getId()) {
					for (GamePlayer gamePlayer : t.getMembers())
						targetEntities.add(gamePlayer.getPlayer());
				}
			}
			
			int dragonCount = 1 + team.upgrades.get(TeamUpgradeType.EXTRA_DRAGONS);
			
			for (int i = 0; i < dragonCount; ++i) {
				// TODO: Clean this shit up
				gameLogic.enderDragonControllers.add(
						new EnderDragonController(team.getBase().getSpawn(gameLogic.getGameWorld().getWorld()).clone().add(0, 50, 0),
								targetEntities,
								new HashSet<Player>(gameLogic.getGameWorld().getWorld().getPlayers()))
				);
			}
		}
	}

	public static String[] getArgumentNames() {
		return new String[0];
	}

}
