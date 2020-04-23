package filip.bedwars.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import filip.bedwars.game.action.Action;
import filip.bedwars.game.action.ActionDestroyBeds;
import filip.bedwars.game.action.ActionKillDragons;
import filip.bedwars.game.action.ActionPlaceBeds;
import filip.bedwars.game.action.ActionPlaySound;
import filip.bedwars.game.action.ActionSendMessage;
import filip.bedwars.game.action.ActionSendTitle;
import filip.bedwars.game.action.ActionSetMaxHealth;
import filip.bedwars.game.action.ActionSpawnerChange;
import filip.bedwars.game.action.ActionSummonDragons;

public class ActionDeserializer {

	private static final Map<String, Class<?>> actionMap = new HashMap<String, Class<?>>() {{
		put("DESTROY_BEDS", ActionDestroyBeds.class);
		put("PLACE_BEDS", ActionPlaceBeds.class);
		put("SUMMON_DRAGONS", ActionSummonDragons.class);
		put("KILL_DRAGONS", ActionKillDragons.class);
		put("PLAY_SOUND", ActionPlaySound.class);
		put("SEND_MESSAGE", ActionSendMessage.class);
		put("SEND_TITLE", ActionSendTitle.class);
		put("SET_MAX_HEALTH", ActionSetMaxHealth.class);
		put("SPAWNER_CHANGE", ActionSpawnerChange.class);
	}};
	
	public static Action deserializeAction(Map<String, Object> serializedAction) {
		String actionStr = (String) serializedAction.get("action");
		
		if (actionStr == null) {
			MessageSender.sendWarning("An action needs to have a specified type");
			return null;
		}
		
		Class<?> actionClass = actionMap.get(actionStr);
		
		if (actionClass == null) {
			MessageSender.sendWarning("Action " + actionStr + " is unknown. Skipping it...");
			return null;
		}
		
		Constructor<?> actionConstructor = actionClass.getConstructors()[0];
		String[] parameterNames = null;
		try {
			parameterNames = (String[]) actionClass.getMethod("getArgumentNames").invoke(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Parameter[] actionConstructorParameters = actionConstructor.getParameters();
		Object[] initArgs = new Object[actionConstructorParameters.length];
		
		for (int i = 0; i < initArgs.length; ++i) {
			Parameter parameter = actionConstructorParameters[i];
			
			Object arg = serializedAction.get(parameterNames[i]);
			
			if (arg == null) {
				MessageSender.sendWarning("Missing parameter " + parameterNames[i] + " for action " + actionStr + ". Skipping it...");
				return null;
			}
			
			if (!arg.getClass().isAssignableFrom(parameter.getType())) {
				MessageSender.sendWarning("Invalid parameter " + parameterNames[i] + " for action " + actionStr + ". Skipping it...");
				return null;
			}
			
			initArgs[i] = arg;
		}
		
		Action action = null;
		try {
			action = (Action) actionConstructor.newInstance(initArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return action;
	}
	
}
