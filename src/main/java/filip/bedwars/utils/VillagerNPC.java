package filip.bedwars.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import filip.bedwars.BedwarsPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

public class VillagerNPC {

	private final ReflectionUtils reflectionUtils;
	//private Field desertVillagerTypeField;
	//private Field armorerVillagerProfessionField;
	private net.minecraft.world.entity.npc.Villager entity;
	
	public VillagerNPC(Location location, String villagerType, String villagerProfession, String customName, Player... viewers) {
		reflectionUtils = BedwarsPlugin.getInstance().reflectionUtils;

		/*try {
			desertVillagerTypeField = reflectionUtils.villagerTypeClass.getField(villagerType);
			armorerVillagerProfessionField = reflectionUtils.villagerProfessionClass.getField(villagerProfession);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}*/
		
		spawn(location, customName, viewers);
	}
	
	private void spawn(Location location, String customName, Player[] viewers) {
		try {
			ServerLevel nmsWorld = reflectionUtils.worldToNMSWorld(location.getWorld());
			//CraftWorld craftWorld = (CraftWorld)location.getWorld();
			//Object craftWorld = reflectionUtils.craftWorldClass.cast(location.getWorld());
			net.minecraft.world.entity.EntityType villagerType = (EntityType)BedwarsPlugin.getInstance().reflectionUtils.entityTypesVillagerField.get(null);
			entity = new net.minecraft.world.entity.npc.Villager(villagerType, nmsWorld);
			//entity = reflectionUtils.entityVillagerConstructor.newInstance(reflectionUtils.entityTypesVillagerField.get(null), reflectionUtils.craftWorldGetHandleMethod.invoke(craftWorld));
			BedwarsPlugin.getInstance().reflectionUtils.entitySetLocationMethod.invoke(entity, location.getX(), location.getY(), location.getZ(), 0f, 0f);
			//entity.absMoveTo(location.getX(), location.getY(), location.getZ(), 0f, 0f);
			//reflectionUtils.entitySetLocationMethod.invoke(entity, location.getX(), location.getY(), location.getZ(), 0f, 0f);
			//entity.setCustomName(Component.literal(customName));
			Component component = (Component)BedwarsPlugin.getInstance().reflectionUtils.componentNullToEmptyMethod.invoke(null, customName);
			BedwarsPlugin.getInstance().reflectionUtils.entitySetCustomNameMethod.invoke(entity, component);
			//reflectionUtils.entitySetCustomNameMethod.invoke(entity, reflectionUtils.chatComponentConstructor.newInstance(customName));
			//entity.setCustomNameVisible(true);
			BedwarsPlugin.getInstance().reflectionUtils.entitySetCustomNameVisibleMethod.invoke(entity, true);
			//reflectionUtils.entitySetCustomNameVisibleMethod.invoke(entity, true);
			VillagerData villagerData = new VillagerData(VillagerType.DESERT, VillagerProfession.ARMORER, 5);
			//Object villagerData = reflectionUtils.villagerDataConstructor.newInstance(desertVillagerTypeField.get(null), armorerVillagerProfessionField.get(null), 5);
			//entity.setVillagerData(villagerData);
			BedwarsPlugin.getInstance().reflectionUtils.entityVillagerSetVillagerDataMethod.invoke(entity, villagerData);
			//reflectionUtils.entityVillagerSetVillagerDataMethod.invoke(entity, villagerData);
			
			respawn(viewers);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public void teleport(double x, double y, double z, Player... viewers) {
		for (Player p : viewers) {
			try {
				//entity.moveTo(x, y, z, 0f, 0f);
				BedwarsPlugin.getInstance().reflectionUtils.entitySetLocationMethod.invoke(entity, x, y, z, 0f, 0f);
				//reflectionUtils.entitySetLocationMethod.invoke(entity, x, y, z, 0f, 0f);
				ClientboundTeleportEntityPacket packet = new ClientboundTeleportEntityPacket(entity);
				//Object packet = reflectionUtils.packetPlayOutEntityTeleportConstructor.newInstance(entity);
				ServerPlayer entityPlayer = reflectionUtils.playerToNMSPlayer(p);
				//CraftPlayer craftPlayer = (CraftPlayer)p;
				//Object craftPlayer = reflectionUtils.craftPlayerClass.cast(p);
				//ServerPlayer entityPlayer = craftPlayer.getHandle();
				//Object entityPlayer = reflectionUtils.craftPlayerGetHandleMethod.invoke(craftPlayer);
				//ServerGamePacketListenerImpl playerConnection =  entityPlayer.connection;
				ServerGamePacketListenerImpl playerConnection = (ServerGamePacketListenerImpl)BedwarsPlugin.getInstance().reflectionUtils.entityPlayerPlayerConnectionField.get(entityPlayer);
				//Object playerConnection = reflectionUtils.entityPlayerPlayerConnectionField.get(entityPlayer);
				//playerConnection.send(packet);
				BedwarsPlugin.getInstance().reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
				//reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void despawn(Player... viewers) {
		for (Player p : viewers) {
			try {
				ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(getEntityId());
				//Object packet = reflectionUtils.packetPlayOutEntityDestroyConstructor.newInstance(new int[] {entity.getId()});
				ServerPlayer entityPlayer = reflectionUtils.playerToNMSPlayer(p);
				//CraftPlayer craftPlayer = (CraftPlayer)p;
				//Object craftPlayer = reflectionUtils.craftPlayerClass.cast(p);
				//ServerPlayer entityPlayer = craftPlayer.getHandle();
				//Object entityPlayer = reflectionUtils.craftPlayerGetHandleMethod.invoke(craftPlayer);
				//ServerGamePacketListenerImpl playerConnection = entityPlayer.connection;
				ServerGamePacketListenerImpl playerConnection = (ServerGamePacketListenerImpl)BedwarsPlugin.getInstance().reflectionUtils.entityPlayerPlayerConnectionField.get(entityPlayer);
				//Object playerConnection = reflectionUtils.entityPlayerPlayerConnectionField.get(entityPlayer);
				//playerConnection.send(packet);
				BedwarsPlugin.getInstance().reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
				//reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void respawn(Player... viewers) {
		String bukkitVersion = Bukkit.getBukkitVersion();

		for (Player p : viewers) {
			try {
				//Object packet = reflectionUtils.packetPlayOutSpawnEntityLivingConstructor.newInstance(entity);
				ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket((net.minecraft.world.entity.Entity)entity);
				ServerPlayer entityPlayer = reflectionUtils.playerToNMSPlayer(p);
				//CraftPlayer craftPlayer = (CraftPlayer)p;
				//Object craftPlayer = reflectionUtils.craftPlayerClass.cast(p);
				//ServerPlayer entityPlayer = craftPlayer.getHandle();
				//Object entityPlayer = reflectionUtils.craftPlayerGetHandleMethod.invoke(craftPlayer);
				//ServerGamePacketListenerImpl playerConnection = entityPlayer.connection;
				ServerGamePacketListenerImpl playerConnection = (ServerGamePacketListenerImpl)BedwarsPlugin.getInstance().reflectionUtils.entityPlayerPlayerConnectionField.get(entityPlayer);
				//Object playerConnection = reflectionUtils.entityPlayerPlayerConnectionField.get(entityPlayer);
				//playerConnection.send(packet);
				BedwarsPlugin.getInstance().reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
				//reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
				//playerConnection.send(new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData().getNonDefaultValues()));
				SynchedEntityData synchedEntityData = (SynchedEntityData)BedwarsPlugin.getInstance().reflectionUtils.entityGetEntityDataMethod.invoke(entity);
				ClientboundSetEntityDataPacket setEntityDataPacket;

				if (bukkitVersion.compareTo("1.19-R0.1-SNAPSHOT") >= 0) {
					List<SynchedEntityData.DataValue<?>> packedItems = (List<SynchedEntityData.DataValue<?>>)BedwarsPlugin.getInstance().reflectionUtils.synchedEntityDataPackMethod.invoke(synchedEntityData);
					setEntityDataPacket = new ClientboundSetEntityDataPacket(getEntityId(), packedItems);
				} else {
					setEntityDataPacket = ClientboundSetEntityDataPacket.class.getConstructor(int.class, SynchedEntityData.class, boolean.class).newInstance(getEntityId(), synchedEntityData, true);
				}
				BedwarsPlugin.getInstance().reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, setEntityDataPacket);
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException e) {
				e.printStackTrace();
			}
		}
	}

	public int getEntityId() {
		// .hashCode() does the same thing as .getId()
		// We do not use .getId() because the method name is obfuscated on some nms version.
		return entity.hashCode();
		/*try {
			return (int) reflectionUtils.entityGetIdMethod.invoke(entity);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return 0;*/
	}
	
}
