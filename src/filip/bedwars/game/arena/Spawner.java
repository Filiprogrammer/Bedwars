package filip.bedwars.game.arena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.inventory.ItemBuilder;

public class Spawner {
	private final Location location;
    private final int ticksPerSpawn;
    private final ItemStack itemStack;
    
    private int currentTick;
    
    public Spawner(Location location, int ticksPerSpawn, Material material, String name) {
    	this.location = location;
    	this.ticksPerSpawn = ticksPerSpawn;
    	this.itemStack = new ItemBuilder()
    			.setMaterial(material)
    			.setName(name)
    			.build();
    }
    
    public Location getLocation() {
    	return location;
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
    public void update() {
        currentTick++;
        if (currentTick > ticksPerSpawn) {
            spawnItem();
            currentTick = 0;
        }
    }
    
    /**
     * Spawn the item at the location.
     */
    private void spawnItem() {
    	location.getWorld().dropItemNaturally(location, itemStack);
    }
}
