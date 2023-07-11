package filip.bedwars.game.state;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import filip.bedwars.game.action.Action;

public class GameStateSetting {

	public final String name;
	public final int durationSeconds;
	public final String countdownMsgKey;
	public final String countdownOneMinMsgKey;
	public final List<Action> actionsStart;
	public final List<Action> actionsEnd;
	
	public GameStateSetting(String name, int durationSeconds, @Nullable String countdownMsgKey, @Nullable String countdownOneMinMsgKey, @Nullable List<Action> actionsStart, @Nullable List<Action> actionsEnd) {
		this.name = name;
		this.durationSeconds = durationSeconds;
		this.countdownMsgKey = countdownMsgKey;
		this.countdownOneMinMsgKey = countdownOneMinMsgKey;
		this.actionsStart = (actionsStart == null) ? (new ArrayList<>()) : (actionsStart);
		this.actionsEnd = (actionsEnd == null) ? (new ArrayList<>()) : (actionsEnd);
	}
	
}
