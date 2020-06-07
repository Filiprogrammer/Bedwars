package filip.bedwars.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import filip.bedwars.BedwarsPlugin;

public class VillagerNPC {

	private final ReflectionUtils reflectionUtils;
	private Field desertVillagerTypeField;
	private Field armorerVillagerProfessionField;
	private Object entity;
	
	public VillagerNPC(Location location, String villagerType, String villagerProfession, String customName, Player... viewers) {
		reflectionUtils = BedwarsPlugin.getInstance().reflectionUtils;
		
		try {
			desertVillagerTypeField = reflectionUtils.villagerTypeClass.getField(villagerType);
			armorerVillagerProfessionField = reflectionUtils.villagerProfessionClass.getField(villagerProfession);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		
		spawn(location, customName, viewers);
	}
	
	private void spawn(Location location, String customName, Player[] viewers) {
		try {
			Object craftWorld = reflectionUtils.craftWorldClass.cast(location.getWorld());
			entity = reflectionUtils.entityVillagerConstructor.newInstance(reflectionUtils.entityTypesVillagerField.get(null), reflectionUtils.craftWorldGetHandleMethod.invoke(craftWorld));
			reflectionUtils.entitySetLocationMethod.invoke(entity, location.getX(), location.getY(), location.getZ(), 0f, 0f);
			reflectionUtils.entitySetCustomNameMethod.invoke(entity, reflectionUtils.chatComponentConstructor.newInstance(customName));
			reflectionUtils.entitySetCustomNameVisibleMethod.invoke(entity, true);
			Object villagerData = reflectionUtils.villagerDataConstructor.newInstance(desertVillagerTypeField.get(null), armorerVillagerProfessionField.get(null), 5);
			reflectionUtils.entityVillagerSetVillagerDataMethod.invoke(entity, villagerData);
			
			respawn(viewers);
		} catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public void teleport(double x, double y, double z, Player... viewers) {
		for (Player p : viewers) {
			try {
				reflectionUtils.entitySetLocationMethod.invoke(entity, x, y, z, 0f, 0f);
				Object packet = reflectionUtils.packetPlayOutEntityTeleportConstructor.newInstance(entity);
				Object craftPlayer = reflectionUtils.craftPlayerClass.cast(p);
				Object entityPlayer = reflectionUtils.craftPlayerGetHandleMethod.invoke(craftPlayer);
				Object playerConnection = reflectionUtils.entityPlayerPlayerConnectionField.get(entityPlayer);
				reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void despawn(Player... viewers) {
		for (Player p : viewers) {
			try {
				Object packet = reflectionUtils.packetPlayOutEntityDestroyConstructor.newInstance(new int[] {(int) reflectionUtils.entityGetIdMethod.invoke(entity)});
				Object craftPlayer = reflectionUtils.craftPlayerClass.cast(p);
				Object entityPlayer = reflectionUtils.craftPlayerGetHandleMethod.invoke(craftPlayer);
				Object playerConnection = reflectionUtils.entityPlayerPlayerConnectionField.get(entityPlayer);
				reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void respawn(Player... viewers) {
		for (Player p : viewers) {
			try {
				Object packet = reflectionUtils.packetPlayOutSpawnEntityLivingConstructor.newInstance(entity);
				Object craftPlayer = reflectionUtils.craftPlayerClass.cast(p);
				Object entityPlayer = reflectionUtils.craftPlayerGetHandleMethod.invoke(craftPlayer);
				Object playerConnection = reflectionUtils.entityPlayerPlayerConnectionField.get(entityPlayer);
				reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getEntityId() {
		try {
			return (int) reflectionUtils.entityGetIdMethod.invoke(entity);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
}
