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
import filip.bedwars.game.GamePlayer;
import filip.bedwars.game.Team;
import filip.bedwars.game.state.GameState;
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
		
		objective.getScore(" ").setScore(lineCount--);
		
		String nextGameStateName;
		GameState nextGameState;
		
		if (gameLogic.getLastGameEndGameState() == gameLogic.getGameState())
			nextGameState = gameLogic.getGameState();
		else
			nextGameState = gameLogic.getNextGameState();
		
		if (nextGameState == null)
			nextGameStateName = "";
		else
			nextGameStateName = nextGameState.getName();
		
		objective.getScore(nextGameStateName).setScore(lineCount--);
		objective.getScore("§a" +
				(gameLogic.getGameState().getCountdown().getSecondsLeft() / 60) +
				":" +
				String.format("%02d", gameLogic.getGameState().getCountdown().getSecondsLeft() % 60))
		.setScore(lineCount--);
		objective.getScore("  ").setScore(lineCount--);
		
		for (Team team : game.getTeams()) {
			org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.registerNewTeam("" + team.getId());
			scoreboardTeam.setPrefix(TeamColorConverter.convertTeamColorToStringForMessages(team.getBase().getTeamColor(), p.getLocale()) + "§7|§r ");
			
			for (GamePlayer gp : team.getMembers())
				scoreboardTeam.addEntry(gp.getPlayer().getName());
			
			int teamMemberCount = team.getMembers().size();
			String msgKey = null;
			
			if (team.hasBed())
				msgKey = "scoreboard-team-has-bed";
			else
				msgKey = "scoreboard-team-has-no-bed";
			
			String youStr = "";
			
			if (team.containsMember(p.getUniqueId()))
				youStr = " " + MessagesConfig.getInstance().getStringValue(p.getLocale(), "scoreboard-you");
			
			objective.getScore(
					MessagesConfig.getInstance().getStringValue(p.getLocale(), msgKey)
							.replace("%team%", TeamColorConverter.convertTeamColorToStringForMessages(team.getBase().getTeamColor(), p.getLocale()))
							.replace("%membercount%", "" + teamMemberCount) + youStr)
			.setScore(lineCount--);
		}
		
		p.setScoreboard(scoreboard);
	}
	
	public void reset(Player p) {
		p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
	}
	
}
