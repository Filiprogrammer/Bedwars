package filip.bedwars.utils;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

public class VillagerNPC {

	//private final ReflectionUtils reflectionUtils;
	//private Field desertVillagerTypeField;
	//private Field armorerVillagerProfessionField;
	private net.minecraft.world.entity.npc.Villager entity;
	
	public VillagerNPC(Location location, String villagerType, String villagerProfession, String customName, Player... viewers) {
		//reflectionUtils = BedwarsPlugin.getInstance().reflectionUtils;
		
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
			CraftWorld craftWorld = (CraftWorld)location.getWorld();
			//Object craftWorld = reflectionUtils.craftWorldClass.cast(location.getWorld());
			entity = new net.minecraft.world.entity.npc.Villager(net.minecraft.world.entity.EntityType.VILLAGER, craftWorld.getHandle());
			//entity = reflectionUtils.entityVillagerConstructor.newInstance(reflectionUtils.entityTypesVillagerField.get(null), reflectionUtils.craftWorldGetHandleMethod.invoke(craftWorld));
			entity.absMoveTo(location.getX(), location.getY(), location.getZ(), 0f, 0f);
			//reflectionUtils.entitySetLocationMethod.invoke(entity, location.getX(), location.getY(), location.getZ(), 0f, 0f);
			entity.setCustomName(Component.literal(customName));
			//reflectionUtils.entitySetCustomNameMethod.invoke(entity, reflectionUtils.chatComponentConstructor.newInstance(customName));
			entity.setCustomNameVisible(true);
			//reflectionUtils.entitySetCustomNameVisibleMethod.invoke(entity, true);
			VillagerData villagerData = new VillagerData(VillagerType.DESERT, VillagerProfession.ARMORER, 5);
			//Object villagerData = reflectionUtils.villagerDataConstructor.newInstance(desertVillagerTypeField.get(null), armorerVillagerProfessionField.get(null), 5);
			entity.setVillagerData(villagerData);
			//reflectionUtils.entityVillagerSetVillagerDataMethod.invoke(entity, villagerData);
			
			respawn(viewers);
		} catch (SecurityException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	public void teleport(double x, double y, double z, Player... viewers) {
		for (Player p : viewers) {
			try {
				entity.moveTo(x, y, z, 0f, 0f);
				//reflectionUtils.entitySetLocationMethod.invoke(entity, x, y, z, 0f, 0f);
				ClientboundTeleportEntityPacket packet = new ClientboundTeleportEntityPacket(entity);
				//Object packet = reflectionUtils.packetPlayOutEntityTeleportConstructor.newInstance(entity);
				CraftPlayer craftPlayer = (CraftPlayer)p;
				//Object craftPlayer = reflectionUtils.craftPlayerClass.cast(p);
				ServerPlayer entityPlayer = craftPlayer.getHandle();
				//Object entityPlayer = reflectionUtils.craftPlayerGetHandleMethod.invoke(craftPlayer);
				ServerGamePacketListenerImpl playerConnection =  entityPlayer.connection;
				//Object playerConnection = reflectionUtils.entityPlayerPlayerConnectionField.get(entityPlayer);
				playerConnection.send(packet);
				//reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void despawn(Player... viewers) {
		for (Player p : viewers) {
			try {
				ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(entity.getId());
				//Object packet = reflectionUtils.packetPlayOutEntityDestroyConstructor.newInstance(new int[] {entity.getId()});
				CraftPlayer craftPlayer = (CraftPlayer)p;
				//Object craftPlayer = reflectionUtils.craftPlayerClass.cast(p);
				ServerPlayer entityPlayer = craftPlayer.getHandle();
				//Object entityPlayer = reflectionUtils.craftPlayerGetHandleMethod.invoke(craftPlayer);
				ServerGamePacketListenerImpl playerConnection = entityPlayer.connection;
				//Object playerConnection = reflectionUtils.entityPlayerPlayerConnectionField.get(entityPlayer);
				playerConnection.send(packet);
				//reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void respawn(Player... viewers) {
		for (Player p : viewers) {
			try {
				//Object packet = reflectionUtils.packetPlayOutSpawnEntityLivingConstructor.newInstance(entity);
				ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket((net.minecraft.world.entity.Entity)entity);
				CraftPlayer craftPlayer = (CraftPlayer)p;
				//Object craftPlayer = reflectionUtils.craftPlayerClass.cast(p);
				ServerPlayer entityPlayer = craftPlayer.getHandle();
				//Object entityPlayer = reflectionUtils.craftPlayerGetHandleMethod.invoke(craftPlayer);
				ServerGamePacketListenerImpl playerConnection = entityPlayer.connection;
				//Object playerConnection = reflectionUtils.entityPlayerPlayerConnectionField.get(entityPlayer);
				playerConnection.send(packet);
				//reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
				playerConnection.send(new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData().packDirty()));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getEntityId() {
		return entity.getId();
		/*try {
			return (int) reflectionUtils.entityGetIdMethod.invoke(entity);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return 0;*/
	}
	
}
