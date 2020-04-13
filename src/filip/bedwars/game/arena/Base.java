package filip.bedwars.game.arena;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.TeamColor;

public class Base {
	private final Location spawn, itemShop, teamShop, bedTop, bedBottom;
	private final TeamColor teamColor;
	
	public Base(@NotNull Location spawn, @NotNull Location itemShop, Location teamShop, @NotNull Location bedTop, @NotNull Location bedBottom, @NotNull TeamColor teamColor) {
		this.spawn = spawn;
		this.itemShop = itemShop;
		this.teamShop = teamShop;
		this.bedTop = bedTop;
		this.bedBottom = bedBottom;
		this.teamColor = teamColor;
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
    
    public TeamColor getTeamColor() {
    	return teamColor;
    }
}
