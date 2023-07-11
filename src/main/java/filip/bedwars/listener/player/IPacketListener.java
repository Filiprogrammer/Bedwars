package filip.bedwars.listener.player;

import org.bukkit.entity.Player;

public interface IPacketListener {

	default void readPacket(Object packet, Player player) {}
	
	default boolean writePacket(Object packet, Player player) { return true; }
	
}
