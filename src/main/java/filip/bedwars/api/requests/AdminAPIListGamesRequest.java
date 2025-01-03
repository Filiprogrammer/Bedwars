package filip.bedwars.api.requests;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import filip.bedwars.game.Game;
import filip.bedwars.game.GameManager;

public class AdminAPIListGamesRequest implements IAdminAPIRequest {
	@Override
	public void parse(DataInput in) throws IOException {}

	@Override
	public void process(DataOutput out) throws IOException {
		// Take a snapshot of the games list to avoid a race condition.
		List<Game> games = List.copyOf(GameManager.getInstance().getGames());

		out.writeInt(games.size());

		for (Game game : games) {
			out.writeInt(game.hashCode());
			out.writeUTF(game.getArena().getMapName());
			out.writeBoolean(game.isRunning());
		}
	}
}
