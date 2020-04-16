package filip.bedwars.listener.player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class PacketReader {
	
	private Player player;
	private Channel channel;
	private List<IPacketListener> listeners = new ArrayList<IPacketListener>();
	
	public PacketReader(Player player) {
		this.player = player;
		inject();
	}
	
	private void inject() {
		ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
                super.channelRead(context, packet);
                readPacket(packet);
            }
            
            @Override
            public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception {
                if (writePacket(packet))
                	super.write(context, packet, channelPromise);
            }
        };
		
        try {
			String versionStr = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
			Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + versionStr + ".entity.CraftPlayer");
			Method getHandleMethod = craftPlayerClass.getMethod("getHandle");
			Class<?> entityPlayerClass = Class.forName("net.minecraft.server." + versionStr + ".EntityPlayer");
			Field playerConnectionField = entityPlayerClass.getField("playerConnection");
			Class<?> playerConnectionClass = Class.forName("net.minecraft.server." + versionStr + ".PlayerConnection");
			Field networkManagerField = playerConnectionClass.getField("networkManager");
			Class<?> networkManagerClass = Class.forName("net.minecraft.server." + versionStr + ".NetworkManager");
			Field channelField = networkManagerClass.getField("channel");
			
			Object cPlayer = craftPlayerClass.cast(player);
			Object entityPlayer = getHandleMethod.invoke(cPlayer);
			Object playerConnection = playerConnectionField.get(entityPlayer);
			Object networkManager = networkManagerField.get(playerConnection);
			channel = (Channel) channelField.get(networkManager);
			channel.pipeline().addBefore("packet_handler", "bedwars_handler_" + player.getName(), channelDuplexHandler);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchFieldException | SecurityException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public void uninject() {
		if(channel.pipeline().get("bedwars_handler_" + player.getName()) != null)
			channel.pipeline().remove("bedwars_handler_" + player.getName());
	}
	
	public Player getPlayer() {
		return player;
	}
	
	private void readPacket(Object packet) {
		for (IPacketListener listener : listeners)
			listener.readPacket(packet, player);
	}
	
	private boolean writePacket(Object packet) {
		boolean ret = true;
		
		for (IPacketListener listener : listeners) {
			if (!listener.writePacket(packet, player))
				ret = false;
		}
		
		return ret;
	}
	
	public void addListener(IPacketListener listener) {
		listeners.add(listener);
	}
	
	public boolean removeListener(IPacketListener listener) {
		return listeners.remove(listener);
	}
	
	public int getListenersCount() {
		return listeners.size();
	}
	
	public boolean hasListeners() {
		return !listeners.isEmpty();
	}
	
}
