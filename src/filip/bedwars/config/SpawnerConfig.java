package filip.bedwars.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;

import filip.bedwars.game.arena.SpawnerType;
import filip.bedwars.utils.MessageSender;

public class SpawnerConfig extends SingleConfig{

	private static SpawnerConfig instance = null;
	
	private List<SpawnerType> spawnerTypes = new ArrayList<SpawnerType>();
	
	public SpawnerType getSpawnerType(int index) {
		if (index >= spawnerTypes.size())
			return null;
		
		return spawnerTypes.get(index);
	}
	
	public List<SpawnerType> getSpawnerTypes(){
		return spawnerTypes;
	}
	
	private SpawnerConfig() {
		super("spawner.yml");
		reloadConfig();
	}

	@Override
	public boolean saveConfig() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reloadConfig() {
		createAndLoadConfigFileIfNotExistent(false);
		
		if (config.isList("spawner")) {
			List<Object> serializedSpawners = (List<Object>) config.getList("spawner");
			
			for (Object serializedSpawner : serializedSpawners) {
				Map<String, Object> mapOfElements = (Map<String, Object>) serializedSpawner;
				
				String spawnerName = ((String) mapOfElements.get("name")).replace('&', '§');
				
				if(spawnerName == null) {
					MessageSender.sendWarning("One Spawner could not be loaded! The Spawner-name is invalid! Please check your spawner.yml!");
					continue;
				}
				
				Material spawnerMaterial = Material.valueOf((String) mapOfElements.get("material"));
				
				if(spawnerMaterial == null) {
					MessageSender.sendWarning("§6" + spawnerName + "-Spawner §ecould not be loaded! Spawner-Material is invalid! Please check your spawner.yml!");
					continue;
				}
				
				int spawnerDefaultTicksPerSpawn = 0;
				
				try {
					spawnerDefaultTicksPerSpawn = (int) mapOfElements.get("defaultTicksPerSpawn");
				} catch(Exception e) {
					MessageSender.sendWarning("§6" + spawnerName + "-Spawner §ecould not be loaded! defaultTicksPerSpawn is invalid! Please check your spawner.yml!");
					continue;
				}
				
				spawnerTypes.add(new SpawnerType(spawnerMaterial, spawnerName, spawnerDefaultTicksPerSpawn));
			}
		}
	}
	
	public static SpawnerConfig getInstance() {
		if (instance == null)
			instance = new SpawnerConfig();
		
		return instance;
	}

}
