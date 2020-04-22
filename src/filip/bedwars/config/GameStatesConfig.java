package filip.bedwars.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import filip.bedwars.game.state.GameStateSetting;
import filip.bedwars.utils.GameStateSettingDeserializer;

public class GameStatesConfig extends SingleConfig{

	private static GameStatesConfig instance = null;
	
	private List<GameStateSetting> gameStateSettings;
	
	private GameStatesConfig() {
		super("gamestates.yml");
		reloadConfig();
	}
	
	public List<GameStateSetting> getGameStateSettings() {
		return gameStateSettings;
	}

	@Override
	public boolean saveConfig() {
		return false;
	}

	@Override
	public void reloadConfig() {
		createAndLoadConfigFileIfNotExistent(false);
		
		gameStateSettings = new ArrayList<>();
		
		if (config.isList("gamestates")) {
			List<Map<String, Object>> serializedGameStates = (List<Map<String, Object>>) config.getList("gamestates");
			
			for (Map<String, Object> serializedGameState : serializedGameStates) {
				GameStateSetting gameStateSetting = GameStateSettingDeserializer.deserializeGameStateSetting(serializedGameState);
				
				if (gameStateSetting != null)
					gameStateSettings.add(gameStateSetting);
			}
		}
		
	}
	
	public static GameStatesConfig getInstance() {
		if (instance == null)
			instance = new GameStatesConfig();
		
		return instance;
	}

}
