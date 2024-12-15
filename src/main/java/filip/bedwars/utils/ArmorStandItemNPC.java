package filip.bedwars.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import filip.bedwars.BedwarsPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorStandItemNPC {

	private ArmorStand entity;

	public ArmorStandItemNPC(Location location, String customName, Material material, Player... viewers) {
		spawn(location, customName, material, viewers);
	}

	private void spawn(Location location, String customName, Material material, Player[] viewers) {
		try {
			ServerLevel nmsWorld = BedwarsPlugin.getInstance().reflectionUtils.worldToNMSWorld(location.getWorld());
			//CraftWorld craftWorld = (CraftWorld)location.getWorld();
			//Object craftWorld = craftWorldClass.cast(location.getWorld());
			entity = new ArmorStand(nmsWorld, location.getX(), location.getY(), location.getZ());
			//entity = entityArmorStandConstructor.newInstance(getHandleCraftWorldMethod.invoke(craftWorld), location.getX(), location.getY(), location.getZ());
			entity.setSmall(true);
			//entityArmorStandClass.getMethod("setSmall", boolean.class).invoke(entity, true);
			entity.setCustomName(Component.literal(customName));
			//entityArmorStandClass.getMethod("setCustomName", iChatBaseComponentClass).invoke(entity, chatComponentConstructor.newInstance(customName));
			entity.setCustomNameVisible(true);
			//entityArmorStandClass.getMethod("setCustomNameVisible", boolean.class).invoke(entity, true);
			entity.setInvisible(true);
			//entityArmorStandClass.getMethod("setInvisible", boolean.class).invoke(entity, true);
			
			for (Player p : viewers) {
				ServerPlayer entityPlayer = BedwarsPlugin.getInstance().reflectionUtils.playerToNMSPlayer(p);
				//CraftPlayer craftPlayer = (CraftPlayer)p;
				//Object craftPlayer = craftPlayerClass.cast(p);
				//ServerPlayer entityPlayer = craftPlayer.getHandle();
				//Object entityPlayer = getHandleCraftPlayerMethod.invoke(craftPlayer);
				ServerGamePacketListenerImpl playerConnection = entityPlayer.connection;
				//Object playerConnection = playerConnectionField.get(entityPlayer);
				playerConnection.send(new ClientboundAddEntityPacket(entity));
				//sendPacketMethod.invoke(playerConnection, packetPlayOutSpawnEntityConstructor.newInstance(entity));
				playerConnection.send(new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData().getNonDefaultValues()));
				//sendPacketMethod.invoke(playerConnection, packetPlayOutEntityMetadataConstructor.newInstance(getIdMethod.invoke(entity), getDataWatcherMethod.invoke(entity), true));
				List<Pair<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack>> list = Lists.newArrayList();
				list.add(Pair.of(net.minecraft.world.entity.EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(material))));
				playerConnection.send(new ClientboundSetEquipmentPacket(entity.getId(), list));
				//sendPacketMethod.invoke(playerConnection, packetPlayOutEntityEquipmentConstructor.newInstance(getIdMethod.invoke(entity), Enum.valueOf((Class<Enum>)enumItemSlotClass, "HEAD"), asNMSCopyMethod.invoke(craftItemStackClass, new ItemStack(material))));
			}
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public void teleport(double x, double y, double z, Player... viewers) {
		for (Player p : viewers) {
			try {
				entity.moveTo(x, y, z);
				//setPositionMethod.invoke(entity, x, y, z);
				ClientboundTeleportEntityPacket packet = new ClientboundTeleportEntityPacket(entity);
				//Object packet = packetPlayOutEntityTeleportConstructor.newInstance(entity);
				ServerPlayer entityPlayer = BedwarsPlugin.getInstance().reflectionUtils.playerToNMSPlayer(p);
				//CraftPlayer craftPlayer = (CraftPlayer)p;
				//Object craftPlayer = craftPlayerClass.cast(p);
				//ServerPlayer entityPlayer = craftPlayer.getHandle();
				//Object entityPlayer = getHandleCraftPlayerMethod.invoke(craftPlayer);
				ServerGamePacketListenerImpl playerConnection =  entityPlayer.connection;
				//Object playerConnection = playerConnectionField.get(entityPlayer);
				playerConnection.send(packet);
				//sendPacketMethod.invoke(playerConnection, packet);
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void despawn(Player... viewers) {
		for (Player p : viewers) {
			try {
				ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(entity.getId());
				//Object packet = packetPlayOutEntityDestroyConstructor.newInstance(new int[] {entity.getId()});
				ServerPlayer entityPlayer = BedwarsPlugin.getInstance().reflectionUtils.playerToNMSPlayer(p);
				//CraftPlayer craftPlayer = (CraftPlayer)p;
				//Object craftPlayer = craftPlayerClass.cast(p);
				//ServerPlayer entityPlayer = craftPlayer.getHandle();
				//Object entityPlayer = getHandleCraftPlayerMethod.invoke(craftPlayer);
				ServerGamePacketListenerImpl playerConnection = entityPlayer.connection;
				//Object playerConnection = playerConnectionField.get(entityPlayer);
				playerConnection.send(packet);
				//sendPacketMethod.invoke(playerConnection, packet);
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getEntityId() {
		return entity.getId();
		/*try {
			return (int) getIdMethod.invoke(entity);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return 0;*/
	}
	
}
