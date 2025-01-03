package filip.bedwars.api.requests;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import filip.bedwars.game.Game;
import filip.bedwars.game.GameManager;

public class AdminAPISkipLobbyCountdownRequest implements IAdminAPIRequest {

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

		if (game.isRunning()) {
			out.writeByte(2);
			return;
		}

		if (!game.getLobby().skipLobbyCountdown()) {
			out.writeByte(3);
			return;
		}

		out.writeByte(0);
	}

}
