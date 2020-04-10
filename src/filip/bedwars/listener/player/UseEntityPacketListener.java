package filip.bedwars.listener.player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

public abstract class UseEntityPacketListener implements IPacketListener {

	private int entityId;
	
	public UseEntityPacketListener(int entityId) {
		this.entityId = entityId;
	}
	
	@Override
	public void readPacket(Object packet) {
		if (!packet.getClass().getSimpleName().equals("PacketPlayInUseEntity"))
			return;
		
		String versionStr = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			Class<?> packetPlayInUseEntityClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayInUseEntity");
			Method getEntityIdMethod = packetPlayInUseEntityClass.getMethod("getEntityId");
			int entityId = (int) getEntityIdMethod.invoke(packet);
			
			if (entityId == this.entityId)
				onUse();
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public abstract void onUse();

}
