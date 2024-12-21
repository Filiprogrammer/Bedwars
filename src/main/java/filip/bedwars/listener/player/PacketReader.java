package filip.bedwars.listener.player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import filip.bedwars.BedwarsPlugin;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.Connection;
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
			ServerGamePacketListenerImpl playerConnection = BedwarsPlugin.getInstance().reflectionUtils.playerGetConnection(player);
			Connection networkManager = (Connection)BedwarsPlugin.getInstance().reflectionUtils.playerConnectionConnectionField.get(playerConnection);
			Channel channel = (Channel)BedwarsPlugin.getInstance().reflectionUtils.connectionChannelField.get(networkManager);
			channelPipeline = channel.pipeline();
			//Object networkManager = networkManagerField.get(playerConnection);
			//channelPipeline = networkManager.channel.pipeline();
			//channelPipeline = ((Channel) channelField.get(networkManager)).pipeline();
			uninject(); // Avoid duplicate handler
			channelPipeline.addBefore("packet_handler", "bedwars_handler_" + player.getName(), channelDuplexHandler);
		} catch (IllegalArgumentException | SecurityException | IllegalAccessException | InvocationTargetException e) {
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
