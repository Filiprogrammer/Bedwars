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

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorStandItemNPC extends NPC {
	private Material material;

	public ArmorStandItemNPC(Location location, String customName, Material material, Player... viewers) {
		this.material = material;
		spawn(location, customName, viewers);
	}

	private void spawn(Location location, String customName, Player[] viewers) {
		try {
			nmsWorld = reflectionUtils.worldToNMSWorld(location.getWorld());
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

			respawn(viewers);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void respawn(Player... viewers) {
		String bukkitVersion = Bukkit.getBukkitVersion();

		for (Player p : viewers) {
			try {
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

				//playerConnection.send(new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData().getNonDefaultValues()));
				reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, setEntityDataPacket);
				List<Pair<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack>> list = Lists.newArrayList();
				net.minecraft.world.item.ItemStack nmsItemStack = (net.minecraft.world.item.ItemStack)reflectionUtils.craftItemStackAsNMSCopyMethod.invoke(null, new ItemStack(material));
				list.add(Pair.of(net.minecraft.world.entity.EquipmentSlot.HEAD, nmsItemStack));
				//playerConnection.send(new ClientboundSetEquipmentPacket(getEntityId(), list));
				reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, new ClientboundSetEquipmentPacket(getEntityId(), list));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}
}
