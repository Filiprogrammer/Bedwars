package filip.bedwars.game.arena;

import org.bukkit.Location;

public class Base {
	private Location spawn, itemShop, teamShop, bedTop, bedBottom;
	
	public Base(Location spawn, Location itemShop, Location teamShop, Location bedTop, Location bedBottom) {
		this.spawn = spawn;
		this.itemShop = itemShop;
		this.teamShop = teamShop;
		this.bedTop = bedTop;
		this.bedBottom = bedBottom;
	}
	
	public Location getSpawn() {
    	return spawn;
    }
    
    public Location getItemShop() {
    	return itemShop;
    }
    
    public Location getTeamShop() {
    	return teamShop;
    }
    
    public Location getBedTop() {
    	return bedTop;
    }
    
    public Location getBedBottom() {
    	return bedBottom;
    }
}
