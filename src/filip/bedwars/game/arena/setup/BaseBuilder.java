package filip.bedwars.game.arena.setup;

import org.bukkit.Location;

import filip.bedwars.game.TeamColor;
import filip.bedwars.game.arena.Base;

public class BaseBuilder {
	private Location spawn, itemShop, teamShop, bedTop, bedBottom;
	private TeamColor teamColor;

	public BaseBuilder setSpawn(Location spawn) {
		this.spawn = spawn;
		return this;
	}

	public BaseBuilder setItemShop(Location itemShop) {
		this.itemShop = itemShop;
		return this;
	}

	public BaseBuilder setTeamShop(Location teamShop) {
		this.teamShop = teamShop;
		return this;
	}

	public BaseBuilder setBedTop(Location bedTop) {
		this.bedTop = bedTop;
		return this;
	}

	public BaseBuilder setBedBottom(Location bedBottom) {
		this.bedTop = bedBottom;
		return this;
	}

	public BaseBuilder setTeamColor(TeamColor teamColor) {
		this.teamColor = teamColor;
		return this;
	}

	public Base build() {
		return new Base(spawn, itemShop, teamShop, bedTop, bedBottom, teamColor);
	}

}
