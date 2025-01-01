package filip.bedwars.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import filip.bedwars.BedwarsPlugin;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.GameType;

public class PlayerNPC extends NPC {
	private UUID entityUUID;
	private GameProfile gameprofile;
	private ServerLevel nmsWorld;
	private Location location;

	public PlayerNPC(Location location, String customName, Player... viewers) {
		this.location = location;
		spawn(customName, viewers);
	}

	private void spawn(String customName, Player[] viewers) {
		final String bukkitVersion = Bukkit.getBukkitVersion();
		entityUUID = UUID.randomUUID();
		gameprofile = new GameProfile(entityUUID, "Spawn-Point");

		try {
			nmsWorld = reflectionUtils.worldToNMSWorld(location.getWorld());
			DedicatedServer nmsServer = reflectionUtils.serverToNMSServer(Bukkit.getServer());
			gameprofile.getProperties().put("textures", new Property("textures", "eyJ0aW1lc3RhbXAiOjE1NjE3NjI0MTIxMDksInByb2ZpbGVJZCI6IjA5NzJiZGQxNGI4NjQ5ZmI5ZWNjYTM1M2Y4NDkxYTUxIiwicHJvZmlsZU5hbWUiOiJNSEZfTGF2YVNsaW1lIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kOTBkNjFlOGNlOTUxMWEwYTJiNWVhMjc0MmNiMWVmMzYxMzEzODBlZDQxMjllMWIxNjNjZThmZjAwMGRlOGVhIn19fQ==", "ltQQFsgURcn3q235uAc0NsZBuziCQtDrlKDwrAYf7n2isEyNHATncmvCxQf14K8PJJ+vw/vIecQsiqdj7xSw3sWGsWflSppuVqmA2K2S0mBUFdEByHVVVs8NyqIoZZZGgUDe2L/PjNm2hewdxZDUx3EvU7KoeqyoILEna75XWPrY/QR+T30wOLBxvqeJ1j6N4LcJlIFhPq8DUvB6Z5QKPpldMOrNlBxjVwbsalUfcPpsqGZf6PyCBp/HZIy1q0XWbY4li68Vux1txDQZXpDRrbfg6VLzzZuwcVdtny3EaXb0pI+NGFW8BbaaTaZBl8nxxhfT0aoX7KaGffa+ugF7pmKWTQV4zDNTaupa3+ZMXDF8scszw+qUnbJmxQf274Ulk36K/srU9pBPyVmsN28Te/x/N9XZggulzgSjUM4IkrwESVdl1xl90ATlh4GsCD/KojBc8HO5Tmjr7Dt6+FiZwMzsyKW+cv7tVq7SAjn0r86KwgICea8oTdk7rQGn2hdUNkzdcMet/Dv6UzPYGbrNkvEQEfpoikK74ZZONw1XCoAMPRN81DL3PnVa7xJ/zyFHqluA50vBUvsaj/LJwXAaO5dyBnx7hy8Fmd9EYqFyHZxpTIeoiyIx0sbBSH3LH9OxbFn2uPOe6hxoO5vfNwEq9ryLy4hNq/vr/sYWzomvPGQ="));

			if (bukkitVersion.compareTo("1.20.2-R0.1-SNAPSHOT") >= 0) {
				entity = new ServerPlayer(nmsServer, nmsWorld, gameprofile, ClientInformation.createDefault());
			} else if (bukkitVersion.compareTo("1.19.3-R0.1-SNAPSHOT") >= 0) {
				entity = (ServerPlayer)reflectionUtils.serverPlayerConstructor.newInstance(nmsServer, nmsWorld, gameprofile);
			} else if (bukkitVersion.compareTo("1.19-R0.1-SNAPSHOT") >= 0) {
				entity = (ServerPlayer)reflectionUtils.serverPlayerConstructor.newInstance(nmsServer, nmsWorld, gameprofile, null);
			} else {
				entity = (ServerPlayer)reflectionUtils.serverPlayerConstructor.newInstance(nmsServer, nmsWorld, gameprofile);
			}

			// Avoid Kyori Adventure warning on Paper due to use of legacy formatting codes
			reflectionUtils.gameProfileNameField.set(gameprofile, customName);

			reflectionUtils.entitySetLocationMethod.invoke(entity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

			respawn(viewers);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException | SecurityException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void respawn(Player... viewers) {
		final String bukkitVersion = Bukkit.getBukkitVersion();

		for (Player p : viewers) {
			try {
				ServerGamePacketListenerImpl connection = reflectionUtils.playerGetConnection(p);

				Object playerInfoUpdatePacket;
				if (bukkitVersion.compareTo("1.19.3-R0.1-SNAPSHOT") >= 0) {
					Object actions = EnumSet.of(Enum.valueOf((Class<Enum>) reflectionUtils.clientboundPlayerInfoUpdatePacketActionEnum, "ADD_PLAYER"));
					playerInfoUpdatePacket = reflectionUtils.clientboundPlayerInfoUpdatePacketConstructor.newInstance(actions, new ArrayList<>());
					final Object entry;
					if (bukkitVersion.compareTo("1.21.1-R0.1-SNAPSHOT") <= 0) {
						entry = reflectionUtils.clientboundPlayerInfoUpdatePacketEntryConstructor.newInstance(
							entityUUID,
							gameprofile,
							false,
							0,
							GameType.SURVIVAL,
							null,
							null
						);
					} else if (bukkitVersion.compareTo("1.21.3-R0.1-SNAPSHOT") <= 0) {
						entry = reflectionUtils.clientboundPlayerInfoUpdatePacketEntryConstructor.newInstance(
							entityUUID,
							gameprofile,
							false,
							0,
							GameType.SURVIVAL,
							null,
							0,
							null
						);
					} else {
						entry = reflectionUtils.clientboundPlayerInfoUpdatePacketEntryConstructor.newInstance(
							entityUUID,
							gameprofile,
							false,
							0,
							GameType.SURVIVAL,
							null,
							false,
							0,
							null
						);
					}
					reflectionUtils.clientboundPlayerInfoUpdatePacketEntriesField.set(playerInfoUpdatePacket, Collections.singletonList(entry));
				} else {
					playerInfoUpdatePacket = reflectionUtils.clientboundPlayerInfoPacketConstructor.newInstance(Enum.valueOf((Class<Enum>)reflectionUtils.enumPlayerInfoActionClass, "ADD_PLAYER"), new ServerPlayer[]{(ServerPlayer)entity});
				}

				reflectionUtils.playerConnectionSendPacketMethod.invoke(connection, playerInfoUpdatePacket);

				final Object addPlayerPacket;
				if (bukkitVersion.compareTo("1.21-R0.1-SNAPSHOT") >= 0) {
					addPlayerPacket = reflectionUtils.clientboundAddEntityPacketConstructor.newInstance(
						entity,
						new ServerEntity(nmsWorld, entity, 0, false, packet -> {}, Set.of())
					);
				} else if (bukkitVersion.compareTo("1.20.2-R0.1-SNAPSHOT") >= 0) {
					addPlayerPacket = reflectionUtils.clientboundAddEntityPacketConstructor.newInstance(entity);
				} else {
					addPlayerPacket = reflectionUtils.clientboundAddPlayerPacketConstructor.newInstance(entity);
				}

				reflectionUtils.playerConnectionSendPacketMethod.invoke(connection, addPlayerPacket);

				float var0 = (location.getYaw() * 256.0F / 360.0F);
		        int var1 = (int)var0;
		        byte headYaw = (byte)((var0 < var1) ? (var1 - 1) : var1);
				reflectionUtils.playerConnectionSendPacketMethod.invoke(connection, new ClientboundRotateHeadPacket(entity, headYaw));

				Bukkit.getScheduler().scheduleSyncDelayedTask(BedwarsPlugin.getInstance(), () -> {
					try {
						Object playerInfoRemovePacket;

						if (bukkitVersion.compareTo("1.19.3-R0.1-SNAPSHOT") >= 0) {
							playerInfoRemovePacket = new ClientboundPlayerInfoRemovePacket(List.of(entityUUID));
						} else {
							playerInfoRemovePacket = reflectionUtils.clientboundPlayerInfoPacketConstructor.newInstance(Enum.valueOf((Class<Enum>)reflectionUtils.enumPlayerInfoActionClass, "REMOVE_PLAYER"), new ServerPlayer[]{(ServerPlayer)entity});
						}

						reflectionUtils.playerConnectionSendPacketMethod.invoke(connection, playerInfoRemovePacket);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}, 5L);
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
				e.printStackTrace();
			}
        }
	}
}
