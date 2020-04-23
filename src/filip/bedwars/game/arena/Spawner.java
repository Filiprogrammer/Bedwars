package filip.bedwars.game.arena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.inventory.ItemBuilder;

public class Spawner implements Cloneable {
	private final Location location;
    private final int ticksPerSpawn;
    private final ItemStack itemStack;
    
    private int currentTick;
    
    public Spawner(@NotNull Location location, int ticksPerSpawn, @NotNull Material material, @NotNull String name) {
    	this.location = location;
    	this.ticksPerSpawn = ticksPerSpawn;
    	this.itemStack = new ItemBuilder()
    			.setMaterial(material)
    			.setName(name)
    			.build();
    }
    
    public Location getLocation(World world) {
    	return new Location(world, location.getX(), location.getY(), location.getZ());
    }
    
    public int getTicksPerSpawn() {
    	return ticksPerSpawn;
    }
    
    public ItemStack getItemStack() {
    	return itemStack;
    }
    
    /**
     * Execute one tick and if ticksPerSpawn is reached spawn the item.
     */
    public void update(World world) {
        currentTick++;
        if (currentTick > ticksPerSpawn) {
            spawnItem(world);
            currentTick = 0;
        }
    }
    
    /**
     * Spawn the item at the location.
     */
    private void spawnItem(World world) {
    	world.dropItemNaturally(location, itemStack);
    }
    
    public Spawner clone() {
    	String name = "";
		
		if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName())
			name = itemStack.getItemMeta().getDisplayName();
		
		return new Spawner(getLocation(null), ticksPerSpawn, itemStack.getType(), name);
    }
}
