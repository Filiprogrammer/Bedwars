package filip.bedwars.game.arena;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import filip.bedwars.game.TeamColor;

public class Base implements Cloneable {
	private final Location spawn, itemShop, teamShop, bedTop, bedBottom;
	private final TeamColor teamColor;

	public Base(@NotNull final Location spawn, @NotNull final Location itemShop, @Nullable final Location teamShop, @NotNull final Location bedTop, @NotNull final Location bedBottom, @NotNull final TeamColor teamColor) {
		this.spawn = spawn;
		this.itemShop = itemShop;
		this.teamShop = teamShop;
		this.bedTop = bedTop;
		this.bedBottom = bedBottom;
		this.teamColor = teamColor;
	}

	@NotNull
	public Location getSpawn(@Nullable World world) {
		return new Location(world, spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch());
	}

	@NotNull
	public Location getItemShop(@Nullable World world) {
		return new Location(world, itemShop.getX(), itemShop.getY(), itemShop.getZ());
	}

	@Nullable
	public Location getTeamShop(@Nullable World world) {
		if (teamShop == null)
			return null;
		
		return new Location(world, teamShop.getX(), teamShop.getY(), teamShop.getZ());
	}

	@NotNull
	public Location getBedTop(@Nullable World world) {
		return new Location(world, bedTop.getX(), bedTop.getY(), bedTop.getZ());
	}

	@NotNull
	public Location getBedBottom(@Nullable World world) {
		return new Location(world, bedBottom.getX(), bedBottom.getY(), bedBottom.getZ());
	}

	public TeamColor getTeamColor() {
		return teamColor;
	}

	public Base clone() {
		return new Base(getSpawn(null), getItemShop(null), getTeamShop(null), getBedTop(null), getBedBottom(null), teamColor);
	}
}
