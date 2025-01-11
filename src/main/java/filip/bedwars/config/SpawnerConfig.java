package filip.bedwars.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import filip.bedwars.game.arena.SpawnerType;
import filip.bedwars.utils.MessageSender;

public class SpawnerConfig extends SingleConfig{

	private static SpawnerConfig instance = null;

	private List<SpawnerType> spawnerTypes = new ArrayList<SpawnerType>();

	@Nullable
	public SpawnerType getSpawnerType(final int index) {
		if (index >= spawnerTypes.size())
			return null;

		return spawnerTypes.get(index);
	}

	@NotNull
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

		spawnerTypes.clear();

		if (config.isList("spawner")) {
			List<Object> serializedSpawners = (List<Object>) config.getList("spawner");

			for (Object serializedSpawner : serializedSpawners) {
				final Map<String, Object> mapOfElements = (Map<String, Object>) serializedSpawner;

				final String spawnerName;
				try {
					spawnerName = ((String) mapOfElements.get("name")).replace('&', '§');
				} catch (ClassCastException | NullPointerException e) {
					MessageSender.sendWarning("One Spawner could not be loaded! The Spawner-name is invalid! Please check your spawner.yml!");
					continue;
				}

				final Material spawnerMaterial;
				try {
					spawnerMaterial = Material.valueOf((String) mapOfElements.get("material"));
				} catch (ClassCastException | IllegalArgumentException | NullPointerException e) {
					MessageSender.sendWarning("§6" + spawnerName + "-Spawner §ecould not be loaded! Spawner-Material is invalid! Please check your spawner.yml!");
					continue;
				}

				final int spawnerDefaultTicksPerSpawn;
				try {
					spawnerDefaultTicksPerSpawn = (int) mapOfElements.get("defaultTicksPerSpawn");
				} catch (ClassCastException | NullPointerException e) {
					MessageSender.sendWarning("§6" + spawnerName + "-Spawner §ecould not be loaded! defaultTicksPerSpawn is invalid! Please check your spawner.yml!");
					continue;
				}

				spawnerTypes.add(new SpawnerType(spawnerMaterial, spawnerName, spawnerDefaultTicksPerSpawn));
			}
		}
	}

	@NotNull
	public static SpawnerConfig getInstance() {
		if (instance == null)
			instance = new SpawnerConfig();

		return instance;
	}

}
