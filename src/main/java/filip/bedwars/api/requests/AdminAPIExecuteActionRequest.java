package filip.bedwars.api.requests;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.game.Game;
import filip.bedwars.game.GameManager;
import filip.bedwars.game.action.Action;
import filip.bedwars.utils.ActionDeserializer;

public class AdminAPIExecuteActionRequest implements IAdminAPIRequest {

	private Game game;
	private Action action;

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

		int len = in.readInt();
		byte[] serializedActionBytes = new byte[len];
		in.readFully(serializedActionBytes);

		ByteArrayInputStream byteStream = new ByteArrayInputStream(serializedActionBytes);
        try (ObjectInputStream objectStream = new ObjectInputStream(byteStream)) {
            @SuppressWarnings("unchecked")
			Map<String, Object> serializedAction = (Map<String, Object>) objectStream.readObject();
			action = ActionDeserializer.deserializeAction(serializedAction);
        } catch (ClassCastException | ClassNotFoundException e) {}

		// To generate a serialized action:
		/*
		Map<String, Object> map = new HashMap<>();
	    map.put("action", "SET_MAX_HEALTH");
	    map.put("max-health", 40);
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectStream = new ObjectOutputStream(byteStream)) {
            objectStream.writeObject(map);
        }
        byte[] bytes = byteStream.toByteArray();
		*/
	}

	@Override
	public void process(DataOutput out) throws IOException {
		if (game == null) {
			out.writeByte(1);
			return;
		}

		if (action == null) {
			out.writeByte(3);
			return;
		}

		if (!game.isRunning()) {
			out.writeByte(2);
			return;
		}

		Bukkit.getScheduler().runTask(BedwarsPlugin.getInstance(), () -> action.execute(game, game.getGameLogic()));
		out.writeByte(0);
	}

}
