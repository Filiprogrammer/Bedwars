package filip.bedwars.game.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.Game;
import filip.bedwars.game.GameLogic;
import filip.bedwars.game.Team;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.TeamColorConverter;

public class ScoreboardManager {

	private Game game;
	private GameLogic gameLogic;
	
	public ScoreboardManager(@NotNull Game game, @NotNull GameLogic gameLogic) {
		this.game = game;
		this.gameLogic = gameLogic;
	}
	
	public void update() {
		for (Player p : gameLogic.getGameWorld().getWorld().getPlayers())
			update(p);
	}
	
	public void update(Player p) {
		String scoreboardTitle = MessagesConfig.getInstance().getStringValue(p.getLocale(), "scoreboard-title");
		
		if(scoreboardTitle == null) {
			scoreboardTitle = "BEDWARS";
			MessageSender.sendWarning("scoreboard-title in messages.yml for language " + p.getLocale() + " was null! Setting a default value for it...");
		}
		
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = scoreboard.registerNewObjective("bw_scoreboard", "bbb", scoreboardTitle);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		int lineCount = 4 + game.getTeams().size();
		
		String scoreboardGamestate = MessagesConfig.getInstance().getStringValue(p.getLocale(), "scoreboard-state");
		
		if(scoreboardGamestate == null) {
			scoreboardGamestate = "State: %gamestate%";
			MessageSender.sendWarning("scoreboard-cur-state in messages.yml for language " + p.getLocale() + " was null! Setting a default value for it...");
		}
		
		objective.getScore(" ").setScore(lineCount--);
		objective.getScore(scoreboardGamestate.replace("%gamestate%", gameLogic.getGameState().getName())).setScore(lineCount--);
		objective.getScore("§a" + gameLogic.getGameState().getCountdown().getSecondsLeft()).setScore(lineCount--);
		objective.getScore("  ").setScore(lineCount--);
		
		for (Team team : game.getTeams()) {
			int teamMemberCount = team.getMembers().size();
			String msgKey = null;
			
			if (team.hasBed())
				msgKey = "scoreboard-team-has-bed";
			else
				msgKey = "scoreboard-team-has-no-bed";
			
			objective.getScore(
					MessagesConfig.getInstance().getStringValue(p.getLocale(), msgKey)
							.replace("%team%", TeamColorConverter.convertTeamColorToStringForMessages(team.getBase().getTeamColor(), p.getLocale()))
							.replace("%membercount%", "" + teamMemberCount))
			.setScore(lineCount--);
		}
		
		p.setScoreboard(scoreboard);
	}
	
	public void reset(Player p) {
		p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
	}
	
}
