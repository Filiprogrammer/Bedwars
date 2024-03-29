package filip.bedwars.listener.player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class PacketReader {
	
	private Player player;
	private ChannelPipeline channelPipeline;
	private List<IPacketListener> listeners = Collections.synchronizedList(new ArrayList<IPacketListener>());
	
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
			//String versionStr = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
			//Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + versionStr + ".entity.CraftPlayer");
			//Method getHandleMethod = craftPlayerClass.getMethod("getHandle");
			//Class<?> entityPlayerClass = Class.forName("net.minecraft.server." + versionStr + ".EntityPlayer");
			//Field playerConnectionField = entityPlayerClass.getField("playerConnection");
			//Class<?> playerConnectionClass = Class.forName("net.minecraft.server." + versionStr + ".PlayerConnection");
			//Field networkManagerField = playerConnectionClass.getField("networkManager");
			//Class<?> networkManagerClass = Class.forName("net.minecraft.server." + versionStr + ".NetworkManager");
			//Field channelField = networkManagerClass.getField("channel");
			
			CraftPlayer cPlayer = (CraftPlayer)player;
			//Object cPlayer = craftPlayerClass.cast(player);
			ServerPlayer entityPlayer = cPlayer.getHandle();
			//Object entityPlayer = getHandleMethod.invoke(cPlayer);
			ServerGamePacketListenerImpl playerConnection = entityPlayer.connection;
			//Object playerConnection = playerConnectionField.get(entityPlayer);
			Connection networkManager = playerConnection.connection;
			//Object networkManager = networkManagerField.get(playerConnection);
			channelPipeline = networkManager.channel.pipeline();
			//channelPipeline = ((Channel) channelField.get(networkManager)).pipeline();
			uninject(); // Avoid duplicate handler
			channelPipeline.addBefore("packet_handler", "bedwars_handler_" + player.getName(), channelDuplexHandler);
		} catch (IllegalArgumentException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void uninject() {
		if(channelPipeline.get("bedwars_handler_" + player.getName()) != null)
			channelPipeline.remove("bedwars_handler_" + player.getName());
	}
	
	public Player getPlayer() {
		return player;
	}
	
	private void readPacket(Object packet) {
		synchronized (listeners) {
			for (IPacketListener listener : listeners)
				listener.readPacket(packet, player);
		}
	}
	
	private boolean writePacket(Object packet) {
		boolean ret = true;
		
		synchronized (listeners) {
			for (IPacketListener listener : listeners) {
				if (!listener.writePacket(packet, player))
					ret = false;
			}
		}
		
		return ret;
	}
	
	public void addListener(IPacketListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}
	
	public boolean removeListener(IPacketListener listener) {
		synchronized (listeners) {
			return listeners.remove(listener);
		}
	}
	
	public int getListenersCount() {
		synchronized (listeners) {
			return listeners.size();
		}
	}
	
	public boolean hasListeners() {
		synchronized (listeners) {
			return !listeners.isEmpty();
		}
	}
	
}
