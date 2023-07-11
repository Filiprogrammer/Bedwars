package filip.bedwars.game.arena;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.TeamColor;

public class Base implements Cloneable {
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
	
	public Location getSpawn(World world) {
		return new Location(world, spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch());
	}
	
	public Location getItemShop(World world) {
		return new Location(world, itemShop.getX(), itemShop.getY(), itemShop.getZ());
	}
	
	public Location getTeamShop(World world) {
		if (teamShop == null)
			return null;
		
		return new Location(world, teamShop.getX(), teamShop.getY(), teamShop.getZ());
	}
	
	public Location getBedTop(World world) {
		return new Location(world, bedTop.getX(), bedTop.getY(), bedTop.getZ());
	}
	
	public Location getBedBottom(World world) {
		return new Location(world, bedBottom.getX(), bedBottom.getY(), bedBottom.getZ());
	}
	
	public TeamColor getTeamColor() {
		return teamColor;
	}
	
	public Base clone() {
		return new Base(getSpawn(null), getItemShop(null), getTeamShop(null), getBedTop(null), getBedBottom(null), teamColor);
	}
}
