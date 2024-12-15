package filip.bedwars.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import filip.bedwars.BedwarsPlugin;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class PlayerNPC {

	private ServerPlayer entity;

	public PlayerNPC(Location location, String customName, Player... viewers) {
		spawn(location, customName, viewers);
	}

	private void spawn(Location location, String customName, Player[] viewers) {
		try {
			ServerLevel nmsWorld = BedwarsPlugin.getInstance().reflectionUtils.worldToNMSWorld(location.getWorld());
			//CraftWorld craftWorld = (CraftWorld)location.getWorld();
			//Object craftWorld = craftWorldClass.cast(location.getWorld());
			//ServerLevel nmsWorld = craftWorld.getHandle();
			//Object nmsWorld = getHandleCraftWorldMethod.invoke(craftWorld);
			DedicatedServer nmsServer = BedwarsPlugin.getInstance().reflectionUtils.serverToNMSServer(Bukkit.getServer());
			//CraftServer craftServer = (CraftServer)Bukkit.getServer();
			//Object craftServer = craftServerClass.cast(Bukkit.getServer());
			//DedicatedServer nmsServer = craftServer.getServer();
			//Object nmsServer = getServerCraftServerMethod.invoke(craftServer);
			GameProfile gameprofile = new GameProfile(UUID.randomUUID(), customName);
			gameprofile.getProperties().put("textures", new Property("textures", "eyJ0aW1lc3RhbXAiOjE1NjE3NjI0MTIxMDksInByb2ZpbGVJZCI6IjA5NzJiZGQxNGI4NjQ5ZmI5ZWNjYTM1M2Y4NDkxYTUxIiwicHJvZmlsZU5hbWUiOiJNSEZfTGF2YVNsaW1lIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kOTBkNjFlOGNlOTUxMWEwYTJiNWVhMjc0MmNiMWVmMzYxMzEzODBlZDQxMjllMWIxNjNjZThmZjAwMGRlOGVhIn19fQ==", "ltQQFsgURcn3q235uAc0NsZBuziCQtDrlKDwrAYf7n2isEyNHATncmvCxQf14K8PJJ+vw/vIecQsiqdj7xSw3sWGsWflSppuVqmA2K2S0mBUFdEByHVVVs8NyqIoZZZGgUDe2L/PjNm2hewdxZDUx3EvU7KoeqyoILEna75XWPrY/QR+T30wOLBxvqeJ1j6N4LcJlIFhPq8DUvB6Z5QKPpldMOrNlBxjVwbsalUfcPpsqGZf6PyCBp/HZIy1q0XWbY4li68Vux1txDQZXpDRrbfg6VLzzZuwcVdtny3EaXb0pI+NGFW8BbaaTaZBl8nxxhfT0aoX7KaGffa+ugF7pmKWTQV4zDNTaupa3+ZMXDF8scszw+qUnbJmxQf274Ulk36K/srU9pBPyVmsN28Te/x/N9XZggulzgSjUM4IkrwESVdl1xl90ATlh4GsCD/KojBc8HO5Tmjr7Dt6+FiZwMzsyKW+cv7tVq7SAjn0r86KwgICea8oTdk7rQGn2hdUNkzdcMet/Dv6UzPYGbrNkvEQEfpoikK74ZZONw1XCoAMPRN81DL3PnVa7xJ/zyFHqluA50vBUvsaj/LJwXAaO5dyBnx7hy8Fmd9EYqFyHZxpTIeoiyIx0sbBSH3LH9OxbFn2uPOe6hxoO5vfNwEq9ryLy4hNq/vr/sYWzomvPGQ="));
	        entity = new ServerPlayer(nmsServer, nmsWorld, gameprofile, ClientInformation.createDefault());
			//entity = entityPlayerConstructor.newInstance(nmsServer, nmsWorld, gameprofile, playerInteractManagerConstructor.newInstance(nmsWorld));
			entity.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
			//setLocationMethod.invoke(entity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
        
        for(Player p : viewers){
        	try {
				ServerPlayer entityPlayer = BedwarsPlugin.getInstance().reflectionUtils.playerToNMSPlayer(p);
				//CraftPlayer craftPlayer = (CraftPlayer)p;
        		//Object craftPlayer = craftPlayerClass.cast(p);
				//ServerPlayer entityPlayer = craftPlayer.getHandle();
				//Object entityPlayer = getHandleCraftPlayerMethod.invoke(craftPlayer);
				ServerGamePacketListenerImpl connection = entityPlayer.connection;
				//Object connection = playerConnectionField.get(entityPlayer);
				connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, entity));
				//Object[] entityPlayerArray = (Object[]) java.lang.reflect.Array.newInstance(entityPlayerClass, 1);
				//entityPlayerArray[0] = entity;
				//sendPacketMethod.invoke(connection, packetPlayOutPlayerInfoConstructor.newInstance(Enum.valueOf((Class<Enum>)enumPlayerInfoActionClass, "ADD_PLAYER"), entityPlayerArray));

				String bukkitVersion = Bukkit.getBukkitVersion();

				if (bukkitVersion.compareTo("1.20.2-R0.1-SNAPSHOT") >= 0) {
					connection.send(new ClientboundAddEntityPacket(entity));
				} else {
					try {
						Class<?> clientboundAddPlayerPacketClass = Class.forName("net.minecraft.network.protocol.game.ClientboundAddPlayerPacket");
						Constructor<?> clientboundAddPlayerPacketConstructor = clientboundAddPlayerPacketClass.getConstructor(ServerPlayer.class);
						Packet<?> addPlayerPacket = (Packet<?>) clientboundAddPlayerPacketConstructor.newInstance(entity);
						connection.send(addPlayerPacket);
					} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}

				//sendPacketMethod.invoke(connection, packetPlayOutNamedEntitySpawnConstructor.newInstance(entity));
				float var0 = (location.getYaw() * 256.0F / 360.0F);
		        int var1 = (int)var0;
		        byte headYaw = (byte)((var0 < var1) ? (var1 - 1) : var1);
				connection.send(new ClientboundRotateHeadPacket(entity, headYaw));
				//sendPacketMethod.invoke(connection, packetPlayOutEntityHeadRotationConstructor.newInstance(entity, headYaw));
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(BedwarsPlugin.getInstance(), () -> {
					connection.send(new ClientboundPlayerInfoRemovePacket(List.of(entity.getUUID())));
					/*try {
						sendPacketMethod.invoke(connection, packetPlayOutPlayerInfoConstructor.newInstance(Enum.valueOf((Class<Enum>)enumPlayerInfoActionClass, "REMOVE_PLAYER"), entityPlayerArray));
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
						e.printStackTrace();
					}*/
				}, 5L);
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
        }
	}
	
	public void teleport(double x, double y, double z, float yaw, float pitch, Player... viewers) {
		for (Player p : viewers) {
			try {
				entity.moveTo(x, y, z, yaw, pitch);
				//setLocationMethod.invoke(entity, x, y, z, yaw, pitch);
				ServerPlayer entityPlayer = BedwarsPlugin.getInstance().reflectionUtils.playerToNMSPlayer(p);
				//CraftPlayer craftPlayer = (CraftPlayer)p;
				//Object craftPlayer = craftPlayerClass.cast(p);
				//ServerPlayer entityPlayer = craftPlayer.getHandle();
				//Object entityPlayer = getHandleCraftPlayerMethod.invoke(craftPlayer);
				ServerGamePacketListenerImpl playerConnection = entityPlayer.connection;
				//Object playerConnection = playerConnectionField.get(entityPlayer);
				playerConnection.send(new ClientboundTeleportEntityPacket(entity));
				//sendPacketMethod.invoke(playerConnection, packetPlayOutEntityTeleportConstructor.newInstance(entity));
				float var0 = (yaw * 256.0F / 360.0F);
		        int var1 = (int)var0;
		        byte headYaw = (byte)((var0 < var1) ? (var1 - 1) : var1);
				playerConnection.send(new ClientboundRotateHeadPacket(entity, headYaw));
				//sendPacketMethod.invoke(playerConnection, packetPlayOutEntityHeadRotationConstructor.newInstance(entity, headYaw));
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void despawn(Player... viewers) {
		for (Player p : viewers) {
			try {
				ServerPlayer entityPlayer = BedwarsPlugin.getInstance().reflectionUtils.playerToNMSPlayer(p);
				//CraftPlayer craftPlayer = (CraftPlayer)p;
				//Object craftPlayer = craftPlayerClass.cast(p);
				//ServerPlayer entityPlayer = craftPlayer.getHandle();
				//Object entityPlayer = getHandleCraftPlayerMethod.invoke(craftPlayer);
				ServerGamePacketListenerImpl playerConnection = entityPlayer.connection;
				//Object playerConnection = playerConnectionField.get(entityPlayer);
				playerConnection.send(new ClientboundRemoveEntitiesPacket(entity.getId()));
				//sendPacketMethod.invoke(playerConnection, packetPlayOutEntityDestroyConstructor.newInstance(new int[] {(int) getIdMethod.invoke(entity)}));
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
}
