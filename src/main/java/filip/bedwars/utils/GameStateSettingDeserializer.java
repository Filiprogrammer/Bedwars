package filip.bedwars.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import filip.bedwars.game.action.Action;
import filip.bedwars.game.state.GameStateSetting;

public class GameStateSettingDeserializer {

	public static GameStateSetting deserializeGameStateSetting(Map<String, Object> serializedGameState) {
		String name = (String) serializedGameState.get("name");
		
		if (name == null) {
			MessageSender.sendWarning("A game state does not have a name. Skipping it...");
			return null;
		}
		
		int durationSeconds = 90;
		
		if (!serializedGameState.containsKey("duration-seconds")) {
			MessageSender.sendWarning("A game state does not have a specified duration. Skipping it...");
			return null;
		}
		
		Object durationSecondsObj = serializedGameState.get("duration-seconds");
		
		if (!(durationSecondsObj instanceof Integer)) {
			MessageSender.sendWarning("A game state does not have a number as the duration. Skipping it...");
			return null;
		}
		
		durationSeconds = (int) durationSecondsObj;
		
		String countdownMsgKey = (String) serializedGameState.get("countdown-message");
		String countdownOneMinMsgKey = (String) serializedGameState.get("countdown-message-one-minute");
		List<Map<String, Object>> serializedActionsStart = (List<Map<String, Object>>) serializedGameState.get("actions-start");
		List<Map<String, Object>> serializedActionsEnd = (List<Map<String, Object>>) serializedGameState.get("actions-end");
		
		List<Action> actionsStart = new ArrayList<>();
		List<Action> actionsEnd = new ArrayList<>();
		
		for (Map<String, Object> serializedAction : serializedActionsStart) {
			Action action = ActionDeserializer.deserializeAction(serializedAction);
			
			if (action != null)
				actionsStart.add(action);
		}
		
		for (Map<String, Object> serializedAction : serializedActionsEnd) {
			Action action = ActionDeserializer.deserializeAction(serializedAction);
			
			if (action != null)
				actionsEnd.add(action);
		}
		
		return new GameStateSetting(name, durationSeconds, countdownMsgKey, countdownOneMinMsgKey, actionsStart, actionsEnd);
	}
	
}
