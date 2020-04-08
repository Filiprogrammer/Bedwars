package filip.bedwars.game.arena.setup;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.TeamColor;
import filip.bedwars.game.arena.Base;

public class BaseBuilder {
	private Location spawn, itemShop, teamShop, bedTop, bedBottom;
	private TeamColor teamColor;

	public BaseBuilder setSpawn(@NotNull Location spawn) {
		this.spawn = spawn;
		return this;
	}

	public BaseBuilder setItemShop(@NotNull Location itemShop) {
		this.itemShop = itemShop;
		return this;
	}

	public BaseBuilder setTeamShop(@NotNull Location teamShop) {
		this.teamShop = teamShop;
		return this;
	}

	public BaseBuilder setBedTop(@NotNull Location bedTop) {
		this.bedTop = bedTop;
		return this;
	}

	public BaseBuilder setBedBottom(@NotNull Location bedBottom) {
		this.bedBottom = bedBottom;
		return this;
	}

	public BaseBuilder setTeamColor(@NotNull TeamColor teamColor) {
		this.teamColor = teamColor;
		return this;
	}
	
	public Location getBedBottom() {
		return bedBottom;
	}
	
	public TeamColor getTeamColor() {
		return teamColor;
	}

	public Base build() {
		return new Base(spawn, itemShop, teamShop, bedTop, bedBottom, teamColor);
	}

}
