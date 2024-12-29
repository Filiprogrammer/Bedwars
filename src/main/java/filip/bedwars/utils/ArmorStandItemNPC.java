package filip.bedwars.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import filip.bedwars.BedwarsPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorStandItemNPC {

	private final ReflectionUtils reflectionUtils;
	private ArmorStand entity;

	public ArmorStandItemNPC(Location location, String customName, Material material, Player... viewers) {
		reflectionUtils = BedwarsPlugin.getInstance().reflectionUtils;

		spawn(location, customName, material, viewers);
	}

	private void spawn(Location location, String customName, Material material, Player[] viewers) {
		String bukkitVersion = Bukkit.getBukkitVersion();

		try {
			ServerLevel nmsWorld = reflectionUtils.worldToNMSWorld(location.getWorld());
			entity = new ArmorStand(nmsWorld, location.getX(), location.getY(), location.getZ());
			// entity.setSmall(true);
			reflectionUtils.entityArmorStandSetSmallMethod.invoke(entity, true);
			// entity.setCustomName(Component.literal(customName));
			Component component = (Component)reflectionUtils.componentNullToEmptyMethod.invoke(null, customName);
			reflectionUtils.entitySetCustomNameMethod.invoke(entity, component);
			// entity.setCustomNameVisible(true);
			reflectionUtils.entitySetCustomNameVisibleMethod.invoke(entity, true);
			// entity.setInvisible(true);
			reflectionUtils.entitySetInvisibleMethod.invoke(entity, true);

			for (Player p : viewers) {
				ServerGamePacketListenerImpl playerConnection = reflectionUtils.playerGetConnection(p);
				// playerConnection.send(new ClientboundAddEntityPacket(entity));
				final Object addEntityPacket;
				if (bukkitVersion.compareTo("1.21-R0.1-SNAPSHOT") >= 0) {
					addEntityPacket = reflectionUtils.clientboundAddEntityPacketConstructor.newInstance(
						entity,
						new ServerEntity(nmsWorld, entity, 0, false, packet -> {}, Set.of())
					);
				} else {
					addEntityPacket = reflectionUtils.clientboundAddEntityPacketConstructor.newInstance(entity);
				}
				reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, addEntityPacket);

				SynchedEntityData synchedEntityData = (SynchedEntityData)reflectionUtils.entityGetEntityDataMethod.invoke(entity);
				ClientboundSetEntityDataPacket setEntityDataPacket;

				if (bukkitVersion.compareTo("1.19.3-R0.1-SNAPSHOT") >= 0) {
					List<SynchedEntityData.DataValue<?>> packedItems = (List<SynchedEntityData.DataValue<?>>)reflectionUtils.synchedEntityDataPackAllMethod.invoke(synchedEntityData);
					setEntityDataPacket = new ClientboundSetEntityDataPacket(getEntityId(), packedItems);
				} else {
					setEntityDataPacket = ClientboundSetEntityDataPacket.class.getConstructor(int.class, SynchedEntityData.class, boolean.class).newInstance(getEntityId(), synchedEntityData, true);
				}

				reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, setEntityDataPacket);
				//playerConnection.send(new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData().getNonDefaultValues()));
				//sendPacketMethod.invoke(playerConnection, packetPlayOutEntityMetadataConstructor.newInstance(getIdMethod.invoke(entity), getDataWatcherMethod.invoke(entity), true));
				List<Pair<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack>> list = Lists.newArrayList();
				net.minecraft.world.item.ItemStack nmsItemStack = (net.minecraft.world.item.ItemStack)reflectionUtils.craftItemStackAsNMSCopyMethod.invoke(null, new ItemStack(material));
				list.add(Pair.of(net.minecraft.world.entity.EquipmentSlot.HEAD, nmsItemStack));
				//playerConnection.send(new ClientboundSetEquipmentPacket(getEntityId(), list));
				reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, new ClientboundSetEquipmentPacket(getEntityId(), list));
				//sendPacketMethod.invoke(playerConnection, packetPlayOutEntityEquipmentConstructor.newInstance(getIdMethod.invoke(entity), Enum.valueOf((Class<Enum>)enumItemSlotClass, "HEAD"), asNMSCopyMethod.invoke(craftItemStackClass, new ItemStack(material))));
			}
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public void teleport(double x, double y, double z, Player... viewers) {
		String bukkitVersion = Bukkit.getBukkitVersion();

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
