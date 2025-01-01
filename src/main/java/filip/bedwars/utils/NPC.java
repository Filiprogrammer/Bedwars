package filip.bedwars.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import filip.bedwars.BedwarsPlugin;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.level.ServerLevel;

public abstract class NPC {
	protected final ReflectionUtils reflectionUtils;
	protected net.minecraft.world.entity.Entity entity;
	protected ServerLevel nmsWorld;

	protected NPC() {
		reflectionUtils = BedwarsPlugin.getInstance().reflectionUtils;
	}

	public abstract void respawn(Player... viewers);

	public void teleport(double x, double y, double z, float yaw, float pitch, Player... viewers) {
		teleport(x, y, z, viewers);

		for (Player p : viewers) {
			float var0 = (yaw * 256.0F / 360.0F);
			int var1 = (int)var0;
			byte headYaw = (byte)((var0 < var1) ? (var1 - 1) : var1);
			try {
				reflectionUtils.playerSendPacket(p, new ClientboundRotateHeadPacket(entity, headYaw));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public void teleport(final double x, final double y, final double z, final Player... viewers) {
		final String bukkitVersion = Bukkit.getBukkitVersion();

		try {
			reflectionUtils.entitySetLocationMethod.invoke(entity, x, y, z, 0f, 0f);

			final Packet<?> teleportEntityPacket;
			if (bukkitVersion.compareTo("1.21.1-R0.1-SNAPSHOT") <= 0) {
				teleportEntityPacket = (Packet<?>)reflectionUtils.clientboundTeleportEntityPacketConstructor.newInstance(entity);
			} else {
				teleportEntityPacket = (Packet<?>)reflectionUtils.clientboundTeleportEntityPacketConstructor.newInstance(
					getEntityId(),
					reflectionUtils.positionMoveRotationOfMethod.invoke(null, entity),
					Set.of(),
					false
				);
			}

			for (Player p : viewers)
				reflectionUtils.playerSendPacket(p, teleportEntityPacket);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			e.printStackTrace();
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
