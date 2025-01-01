package filip.bedwars.game.arena;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class SpawnerType {

	private final int defaultTicksPerSpawn;
	private final Material material;
	private final String name;

	public SpawnerType(@NotNull final Material material, @NotNull final String name, final int defaultTicksPerSpawn) {
		this.name = name;
		this.material = material;
		this.defaultTicksPerSpawn = defaultTicksPerSpawn;
	}
	
	public int getDefaultTicksPerSpawn() {
		return defaultTicksPerSpawn;
	}
	
	public Material getMaterial() {
		return material;
	}

	@NotNull
	public String getName() {
		return name;
	}

}
