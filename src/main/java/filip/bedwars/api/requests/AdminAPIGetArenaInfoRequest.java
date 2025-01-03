package filip.bedwars.api.requests;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import filip.bedwars.config.ArenaConfig;
import filip.bedwars.game.arena.Arena;

public class AdminAPIGetArenaInfoRequest implements IAdminAPIRequest {

	private Arena arena;

	@Override
	public void parse(DataInput in) throws IOException {
		String name = in.readUTF();
		arena = ArenaConfig.getInstance().getArena(name);
	}

	@Override
	public void process(DataOutput out) throws IOException {
		if (arena == null) {
			out.writeByte(1);
			return;
		}

		out.writeByte(0);
		out.writeInt(arena.getMinPlayersToStart());
		out.writeInt(arena.getPlayersPerTeam());
		out.writeUTF(arena.getWorld().getName());
	}

}
