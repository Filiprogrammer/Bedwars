package filip.bedwars.listener.player;

import org.bukkit.entity.Player;

public interface IPacketListener {

	void readPacket(Object packet, Player player);
	
}
