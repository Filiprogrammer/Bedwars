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

	private final ReflectionUtils reflectionUtils;
	private ServerPlayer entity;

	public PlayerNPC(Location location, String customName, Player... viewers) {
		reflectionUtils = BedwarsPlugin.getInstance().reflectionUtils;

		spawn(location, customName, viewers);
	}

	private void spawn(Location location, String customName, Player[] viewers) {
		String bukkitVersion = Bukkit.getBukkitVersion();

		try {
			ServerLevel nmsWorld = reflectionUtils.worldToNMSWorld(location.getWorld());
			DedicatedServer nmsServer = reflectionUtils.serverToNMSServer(Bukkit.getServer());
			GameProfile gameprofile = new GameProfile(UUID.randomUUID(), customName);
			gameprofile.getProperties().put("textures", new Property("textures", "eyJ0aW1lc3RhbXAiOjE1NjE3NjI0MTIxMDksInByb2ZpbGVJZCI6IjA5NzJiZGQxNGI4NjQ5ZmI5ZWNjYTM1M2Y4NDkxYTUxIiwicHJvZmlsZU5hbWUiOiJNSEZfTGF2YVNsaW1lIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kOTBkNjFlOGNlOTUxMWEwYTJiNWVhMjc0MmNiMWVmMzYxMzEzODBlZDQxMjllMWIxNjNjZThmZjAwMGRlOGVhIn19fQ==", "ltQQFsgURcn3q235uAc0NsZBuziCQtDrlKDwrAYf7n2isEyNHATncmvCxQf14K8PJJ+vw/vIecQsiqdj7xSw3sWGsWflSppuVqmA2K2S0mBUFdEByHVVVs8NyqIoZZZGgUDe2L/PjNm2hewdxZDUx3EvU7KoeqyoILEna75XWPrY/QR+T30wOLBxvqeJ1j6N4LcJlIFhPq8DUvB6Z5QKPpldMOrNlBxjVwbsalUfcPpsqGZf6PyCBp/HZIy1q0XWbY4li68Vux1txDQZXpDRrbfg6VLzzZuwcVdtny3EaXb0pI+NGFW8BbaaTaZBl8nxxhfT0aoX7KaGffa+ugF7pmKWTQV4zDNTaupa3+ZMXDF8scszw+qUnbJmxQf274Ulk36K/srU9pBPyVmsN28Te/x/N9XZggulzgSjUM4IkrwESVdl1xl90ATlh4GsCD/KojBc8HO5Tmjr7Dt6+FiZwMzsyKW+cv7tVq7SAjn0r86KwgICea8oTdk7rQGn2hdUNkzdcMet/Dv6UzPYGbrNkvEQEfpoikK74ZZONw1XCoAMPRN81DL3PnVa7xJ/zyFHqluA50vBUvsaj/LJwXAaO5dyBnx7hy8Fmd9EYqFyHZxpTIeoiyIx0sbBSH3LH9OxbFn2uPOe6hxoO5vfNwEq9ryLy4hNq/vr/sYWzomvPGQ="));

			if (bukkitVersion.compareTo("1.20.2-R0.1-SNAPSHOT") >= 0) {
				entity = new ServerPlayer(nmsServer, nmsWorld, gameprofile, ClientInformation.createDefault());
			} else {
				entity = (ServerPlayer)reflectionUtils.serverPlayerConstructor.newInstance(nmsServer, nmsWorld, gameprofile);
			}

			reflectionUtils.entitySetLocationMethod.invoke(entity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			e.printStackTrace();
		}
        
        for (Player p : viewers) {
        	try {
				ServerGamePacketListenerImpl connection = reflectionUtils.playerGetConnection(p);
				if (bukkitVersion.compareTo("1.19.3-R0.1-SNAPSHOT") >= 0) {
					connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, entity));
				} else {
					Object playerInfoPacket = reflectionUtils.clientboundPlayerInfoPacketConstructor.newInstance(Enum.valueOf((Class<Enum>)reflectionUtils.enumPlayerInfoActionClass, "ADD_PLAYER"), new ServerPlayer[]{entity});
					reflectionUtils.playerConnectionSendPacketMethod.invoke(connection, playerInfoPacket);
				}
				//Object[] entityPlayerArray = (Object[]) java.lang.reflect.Array.newInstance(entityPlayerClass, 1);
				//entityPlayerArray[0] = entity;
				//sendPacketMethod.invoke(connection, clientboundPlayerInfoPacketConstructor.newInstance(Enum.valueOf((Class<Enum>)enumPlayerInfoActionClass, "ADD_PLAYER"), entityPlayerArray));

				if (bukkitVersion.compareTo("1.20.2-R0.1-SNAPSHOT") >= 0) {
					connection.send(new ClientboundAddEntityPacket(entity));
				} else {
					try {
						Class<?> clientboundAddPlayerPacketClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn");
						Constructor<?> clientboundAddPlayerPacketConstructor = clientboundAddPlayerPacketClass.getConstructor(net.minecraft.world.entity.player.Player.class);
						Packet<?> addPlayerPacket = (Packet<?>) clientboundAddPlayerPacketConstructor.newInstance(entity);
						reflectionUtils.playerConnectionSendPacketMethod.invoke(connection, addPlayerPacket);
					} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}

				float var0 = (location.getYaw() * 256.0F / 360.0F);
		        int var1 = (int)var0;
		        byte headYaw = (byte)((var0 < var1) ? (var1 - 1) : var1);
				reflectionUtils.playerConnectionSendPacketMethod.invoke(connection, new ClientboundRotateHeadPacket(entity, headYaw));

				Bukkit.getScheduler().scheduleSyncDelayedTask(BedwarsPlugin.getInstance(), () -> {
					if (bukkitVersion.compareTo("1.19.3-R0.1-SNAPSHOT") >= 0) {
						connection.send(new ClientboundPlayerInfoRemovePacket(List.of(entity.getUUID())));
					} else {
						try {
							Object playerInfoPacket = reflectionUtils.clientboundPlayerInfoPacketConstructor.newInstance(Enum.valueOf((Class<Enum>)reflectionUtils.enumPlayerInfoActionClass, "REMOVE_PLAYER"), new ServerPlayer[]{entity});
							reflectionUtils.playerConnectionSendPacketMethod.invoke(connection, playerInfoPacket);
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
					
					/*try {
						sendPacketMethod.invoke(connection, clientboundPlayerInfoPacketConstructor.newInstance(Enum.valueOf((Class<Enum>)enumPlayerInfoActionClass, "REMOVE_PLAYER"), entityPlayerArray));
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
						e.printStackTrace();
					}*/
				}, 5L);
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
				e.printStackTrace();
			}
        }
	}
	
	public void teleport(double x, double y, double z, float yaw, float pitch, Player... viewers) {
		try {
			reflectionUtils.entitySetLocationMethod.invoke(entity, x, y, z, yaw, pitch);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return;
		}

		for (Player p : viewers) {
			try {
				ServerGamePacketListenerImpl playerConnection = reflectionUtils.playerGetConnection(p);
				reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, new ClientboundTeleportEntityPacket(entity));
				float var0 = (yaw * 256.0F / 360.0F);
		        int var1 = (int)var0;
		        byte headYaw = (byte)((var0 < var1) ? (var1 - 1) : var1);
				reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, new ClientboundRotateHeadPacket(entity, headYaw));
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void despawn(Player... viewers) {
		for (Player p : viewers) {
			try {
				reflectionUtils.playerSendPacket(p, new ClientboundRemoveEntitiesPacket(getEntityId()));
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public int getEntityId() {
		// .hashCode() does the same thing as .getId()
		// We do not use .getId() because the method name is obfuscated on some nms version.
		return entity.hashCode();
	}
	
}
