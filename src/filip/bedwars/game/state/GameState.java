package filip.bedwars.game.state;

import java.util.List;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.Countdown;
import filip.bedwars.game.Game;
import filip.bedwars.game.GameLogic;
import filip.bedwars.game.action.Action;
import filip.bedwars.utils.MessageSender;

public class GameState {
	
	private String name;
	private Countdown countdown;
	
	public GameState(@NotNull GameStateSetting gameStateSetting, @NotNull Game game, @NotNull GameLogic gameLogic) {
		this.name = gameStateSetting.name;
		int durationSeconds = gameStateSetting.durationSeconds;
		String countdownMsgKey = gameStateSetting.countdownMsgKey;
		String countdownOneMinMsgKey = gameStateSetting.countdownOneMinMsgKey;
		List<Action> actionsStart = gameStateSetting.actionsStart;
		List<Action> actionsEnd = gameStateSetting.actionsEnd;
		
		countdown = new Countdown(durationSeconds) {
			@Override
			public void onTick() {
				if (getSecondsLeft() == 60) {
					if (countdownOneMinMsgKey != null) {
						for (Player p : gameLogic.getGameWorld().getWorld().getPlayers())
						MessageSender.sendMessage(p, MessagesConfig.getInstance().getStringValue(p.getLocale(), countdownOneMinMsgKey));
					}
				} else if (getSecondsLeft() != 0 && (getSecondsLeft() % 60) == 0) {
					if (countdownMsgKey != null) {
						for (Player p : gameLogic.getGameWorld().getWorld().getPlayers())
							MessageSender.sendMessage(p, MessagesConfig.getInstance().getStringValue(p.getLocale(), countdownMsgKey).replace("%minutes%", "" + (getSecondsLeft() / 60)));
					}
				}
			}
			
			@Override
			public void onStart() {
				for (Action action : actionsStart)
					action.execute(game, gameLogic);
			}
			
			@Override
			public boolean onFinish() {
				for (Action action : actionsEnd)
					action.execute(game, gameLogic);
				
				gameLogic.initiateNextGameState();
				return false;
			}
			
			@Override
			public void onCancel() {}
		};
	}
	
	public String getName() {
		return name;
	}
	
	public Countdown getCountdown() {
		return countdown;
	}
	
	public void initiate() {
		countdown.start();
	}
	
}
