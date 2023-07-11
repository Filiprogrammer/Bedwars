package filip.bedwars.listener.player;

import org.bukkit.entity.Player;

import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket.ActionType;

public abstract class UseEntityPacketListener implements IPacketListener {

	private int entityId;
	
	public UseEntityPacketListener(int entityId) {
		this.entityId = entityId;
	}
	
	@Override
	public void readPacket(Object packet, Player player) {
		if (!packet.getClass().getSimpleName().equals("PacketPlayInUseEntity"))
			return;
		
		//String versionStr = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			int entityId = ((ServerboundInteractPacket)packet).getEntityId();
			//Class<?> packetPlayInUseEntityClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayInUseEntity");
			//Method getEntityIdMethod = packetPlayInUseEntityClass.getMethod("getEntityId");
			//Method bMethod = packetPlayInUseEntityClass.getMethod("b");
			//int entityId = (int) getEntityIdMethod.invoke(packet);
			ActionType action = ((ServerboundInteractPacket)packet).getActionType();
			//Object action = bMethod.invoke(packet);
			
			if (entityId == this.entityId)
				onUse(((Enum<?>)action).toString(), player);
		} catch (SecurityException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	public abstract void onUse(String action, Player player);

}
