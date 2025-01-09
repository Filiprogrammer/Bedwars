package filip.bedwars.api.requests;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import filip.bedwars.config.ArenaConfig;
import filip.bedwars.game.arena.Arena;

public class AdminAPIListArenasRequest implements IAdminAPIRequest {
	@Override
	public void parse(DataInput in) throws IOException {}

	@Override
	public void process(DataOutput out) throws IOException {
		// TODO: Fix race condition
		int arenaCount = ArenaConfig.getInstance().getArenaCount();
		out.writeInt(arenaCount);

		for (int i = 0; i < arenaCount; ++i) {
			Arena arena = ArenaConfig.getInstance().getArena(i);
			out.writeUTF(arena.getMapName());
		}
	}
}
