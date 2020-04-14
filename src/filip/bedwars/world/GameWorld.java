package filip.bedwars.world;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.config.MainConfig;

public class GameWorld {

	private static int gameWorldCounter = 0;
	
	private World world;
	
	public GameWorld(World loadFrom) {
		loadWorld(loadFrom);
	}
	
	public World getWorld() {
		return world;
	}
	
	private void loadWorld(World loadFrom) {
		String gameWorldName = MainConfig.getInstance().getGameWorldPrefix() + String.format("%05d", gameWorldCounter);
		String worldContainerPath = BedwarsPlugin.getInstance().getServer().getWorldContainer().getAbsolutePath();
		Path sourceRegionDirectory = Paths.get(worldContainerPath + File.separator + loadFrom.getName() + File.separator + "region");
        Path targetRegionDirectory = Paths.get(worldContainerPath + File.separator + gameWorldName + File.separator + "region");
		Path sourceLevelDat = Paths.get(worldContainerPath + File.separator + loadFrom.getName() + File.separator + "level.dat");
        Path targetLevelDat = Paths.get(worldContainerPath + File.separator + gameWorldName + File.separator + "level.dat");
        
        // Create the directory of the game world
        new File(worldContainerPath + File.separator + gameWorldName).mkdirs();
        
        // Copy the region files to the game world
        try {
        	copyDirectory(sourceRegionDirectory, targetRegionDirectory);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        // Copy the "level.dat" file to the game world
        try {
			Files.copy(sourceLevelDat, targetLevelDat, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        // Load the game world into memory
        WorldCreator worldCreator = new WorldCreator(gameWorldName).copy(Bukkit.getWorld(loadFrom.getName())).generateStructures(false);
		world = worldCreator.createWorld();
		
		// Change some attributes of the game world
		world.setAutoSave(false);
		world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		world.setGameRule(GameRule.DO_FIRE_TICK, false);
		world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		world.setGameRule(GameRule.KEEP_INVENTORY, false);
		world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
		world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		world.setTime(6000);
		world.setDifficulty(Difficulty.EASY);
		world.setPVP(true);
		world.setStorm(false);
		world.setThundering(false);
	}
	
	public void unloadWorld() {
		String gameWorldName = world.getName();
		Bukkit.unloadWorld(world, false);
		String worldContainerPath = BedwarsPlugin.getInstance().getServer().getWorldContainer().getAbsolutePath();
		Path gameWorldDirectory = Paths.get(worldContainerPath + File.separator + gameWorldName);
		
		try {
			deleteDirectory(gameWorldDirectory);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void copyDirectory(Path src, Path dest) throws IOException {
		List<Path> sources = Files.walk(src).collect(Collectors.toList());
		
		for (Path source : sources)
			Files.copy(source, dest.resolve(src.relativize(source)), StandardCopyOption.REPLACE_EXISTING);
	}
	
	private void deleteDirectory(Path path) throws IOException {
		Files.walk(path)
			.sorted(Comparator.reverseOrder())
			.map(Path::toFile)
			.forEach(File::delete);
	}
	
}
