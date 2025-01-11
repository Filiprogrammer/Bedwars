package filip.bedwars.api.requests;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import filip.bedwars.game.Game;
import filip.bedwars.game.GameLogic;
import filip.bedwars.game.GameManager;
import filip.bedwars.game.GamePlayer;
import filip.bedwars.game.Team;
import filip.bedwars.game.arena.Base;
import filip.bedwars.game.state.GameState;

public class AdminAPIGetGameInfoRequest implements IAdminAPIRequest {

	private Game game;

	@Override
	public void parse(DataInput in) throws IOException {
		int gameHashCode = in.readInt();

		List<Game> games = GameManager.getInstance().getGames();

		for (Game game : games) {
			if (game.hashCode() == gameHashCode) {
				this.game = game;
				break;
			}
		}
	}

	@Override
	public void process(DataOutput out) throws IOException {
		if (game == null) {
			out.writeByte(1);
			return;
		}

		out.writeByte(0);
		out.writeUTF(game.getArena().getMapName());

		boolean isRunning = game.isRunning();
		out.writeBoolean(isRunning);

		if (isRunning) {
			GameLogic gameLogic = game.getGameLogic();
			out.writeUTF(gameLogic.getGameWorld().getWorld().getName());
			GameState gameState = gameLogic.getGameState();
			out.writeUTF(gameState.getName());
			out.writeInt(gameState.getCountdown().getSecondsLeft());
		}

		final List<Team> teams = game.getTeams();
		out.writeInt(teams.size());
		for (final Team team : teams)
			writeTeam(out, team);
	}

	private void writeTeam(DataOutput out, final Team team) throws IOException {
		out.writeInt(team.getId());
		out.writeBoolean(team.hasBed());

		final Base base = team.getBase();
		writeBase(out, base);

		// Take a snapshot of the members list to avoid a race condition.
		List<GamePlayer> members = List.copyOf(team.getMembers());
		out.writeInt(members.size());
		for (final GamePlayer member : members)
			writeGamePlayer(out, member);
	}

	private void writeBase(DataOutput out, final Base base) throws IOException {
		out.writeByte(base.getTeamColor().ordinal());
		final Location bedBottom = base.getBedBottom(null);
		out.writeInt(bedBottom.getBlockX());
		out.writeInt(bedBottom.getBlockY());
		out.writeInt(bedBottom.getBlockZ());
		final Location bedTop = base.getBedTop(null);
		out.writeInt(bedTop.getBlockX());
		out.writeInt(bedTop.getBlockY());
		out.writeInt(bedTop.getBlockZ());
		final Location spawn = base.getSpawn(null);
		out.writeInt(spawn.getBlockX());
		out.writeInt(spawn.getBlockY());
		out.writeInt(spawn.getBlockZ());
		out.writeFloat(spawn.getYaw());
		out.writeFloat(spawn.getPitch());
		final Location itemShop = base.getItemShop(null);
		out.writeInt(itemShop.getBlockX());
		out.writeInt(itemShop.getBlockY());
		out.writeInt(itemShop.getBlockZ());
		final Location teamShop = base.getTeamShop(null);
		if (teamShop == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeInt(teamShop.getBlockX());
			out.writeInt(teamShop.getBlockY());
			out.writeInt(teamShop.getBlockZ());
		}
	}

	private void writeGamePlayer(DataOutput out, final GamePlayer gamePlayer) throws IOException {
		final UUID memberUUID = gamePlayer.uuid;
		out.writeLong(memberUUID.getMostSignificantBits());
		out.writeLong(memberUUID.getLeastSignificantBits());
		Player player = gamePlayer.getPlayer();
		if (player == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeUTF(player.getName());
		}
	}

}
