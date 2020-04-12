package filip.bedwars.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class VillagerNPC {

	private Object entity;
	
	Class<?> entityClass;
	Class<?> entityVillagerClass;
	Class<?> entityLivingClass;
	Class<?> entityPlayerClass;
	Class<?> iChatBaseComponentClass;
	Class<?> chatComponentTextClass;
	Class<?> villagerDataClass;
	Class<?> entityTypesClass;
	Class<?> packetClass;
	Class<?> playerConnectionClass;
	Class<?> packetPlayOutSpawnEntityLivingClass;
	Class<?> packetPlayOutEntityTeleportClass;
	Class<?> packetPlayOutEntityDestroyClass;
	Class<?> craftPlayerClass;
	Class<?> craftWorldClass;
	Class<?> villagerTypeClass;
	Class<?> villagerProfessionClass;
	Method setLocationMethod;
	Method setCustomNameMethod;
	Method setCustomNameVisibleMethod;
	Method setVillagerDataMethod;
	Constructor<?> packetPlayOutSpawnEntityLivingConstructor;
	Constructor<?> packetPlayOutEntityTeleportConstructor;
	Constructor<?> packetPlayOutEntityDestroyConstructor;
	Constructor<?> entityVillagerConstructor;
	Constructor<?> chatComponentConstructor;
	Constructor<?> villagerDataConstructor;
	Method getHandleCraftPlayerMethod;
	Field playerConnectionField;
	Field villagerEntityTypesField;
	Field desertVillagerTypeField;
	Field armorerVillagerProfessionField;
	Method sendPacketMethod;
	Method getHandleCraftWorldMethod;
	Method getIdMethod;
	
	public VillagerNPC(Location location, String villagerType, String villagerProfession, String customName, Player... viewers) {
		try {
			String versionStr = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
			entityClass = Class.forName("net.minecraft.server." + versionStr + ".Entity");
			entityVillagerClass = Class.forName("net.minecraft.server." + versionStr + ".EntityVillager");
			entityLivingClass = Class.forName("net.minecraft.server." + versionStr + ".EntityLiving");
			entityPlayerClass = Class.forName("net.minecraft.server." + versionStr + ".EntityPlayer");
			iChatBaseComponentClass = Class.forName("net.minecraft.server." + versionStr + ".IChatBaseComponent");
			chatComponentTextClass = Class.forName("net.minecraft.server." + versionStr + ".ChatComponentText");
			villagerDataClass = Class.forName("net.minecraft.server." + versionStr + ".VillagerData");
			entityTypesClass = Class.forName("net.minecraft.server." + versionStr + ".EntityTypes");
			packetClass = Class.forName("net.minecraft.server." + versionStr + ".Packet");
			playerConnectionClass = Class.forName("net.minecraft.server." + versionStr + ".PlayerConnection");
			packetPlayOutSpawnEntityLivingClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutSpawnEntityLiving");
			packetPlayOutEntityTeleportClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutEntityTeleport");
			packetPlayOutEntityDestroyClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutEntityDestroy");
			craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + versionStr + ".entity.CraftPlayer");
			craftWorldClass = Class.forName("org.bukkit.craftbukkit." + versionStr + ".CraftWorld");
			villagerTypeClass = Class.forName("net.minecraft.server." + versionStr + ".VillagerType");
			villagerProfessionClass = Class.forName("net.minecraft.server." + versionStr + ".VillagerProfession");
			setLocationMethod = entityVillagerClass.getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
			setCustomNameMethod = entityVillagerClass.getMethod("setCustomName", iChatBaseComponentClass);
			setCustomNameVisibleMethod = entityVillagerClass.getMethod("setCustomNameVisible", boolean.class);
			setVillagerDataMethod = entityVillagerClass.getMethod("setVillagerData", villagerDataClass);
			getIdMethod = entityVillagerClass.getMethod("getId");
			packetPlayOutSpawnEntityLivingConstructor = packetPlayOutSpawnEntityLivingClass.getConstructor(entityLivingClass);
			packetPlayOutEntityTeleportConstructor = packetPlayOutEntityTeleportClass.getConstructor(entityClass);
			packetPlayOutEntityDestroyConstructor = packetPlayOutEntityDestroyClass.getConstructor(int[].class);
			entityVillagerConstructor = null;
			for (Constructor<?> constructor : entityVillagerClass.getConstructors()) {
				if (constructor.getParameterCount() == 2) {
					entityVillagerConstructor = constructor;
					break;
				}
			}
			chatComponentConstructor = chatComponentTextClass.getConstructor(String.class);
			villagerDataConstructor = villagerDataClass.getConstructor(villagerTypeClass, villagerProfessionClass, int.class);
			getHandleCraftPlayerMethod = craftPlayerClass.getMethod("getHandle");
			playerConnectionField = entityPlayerClass.getField("playerConnection");
			villagerEntityTypesField = entityTypesClass.getField("VILLAGER");
			desertVillagerTypeField = villagerTypeClass.getField(villagerType);
			armorerVillagerProfessionField = villagerProfessionClass.getField(villagerProfession);
			sendPacketMethod = playerConnectionClass.getMethod("sendPacket", packetClass);
			getHandleCraftWorldMethod = craftWorldClass.getMethod("getHandle");
			
			spawn(location, customName, viewers);
		} catch (NoSuchMethodException | SecurityException | NoSuchFieldException | ClassNotFoundException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	private void spawn(Location location, String customName, Player[] viewers) {
		try {
			Object craftWorld = craftWorldClass.cast(location.getWorld());
			entity = entityVillagerConstructor.newInstance(villagerEntityTypesField.get(null), getHandleCraftWorldMethod.invoke(craftWorld));
			setLocationMethod.invoke(entity, location.getX(), location.getY(), location.getZ(), 0f, 0f);
			setCustomNameMethod.invoke(entity, chatComponentConstructor.newInstance(customName));
			setCustomNameVisibleMethod.invoke(entity, true);
			Object villagerData = villagerDataConstructor.newInstance(desertVillagerTypeField.get(null), armorerVillagerProfessionField.get(null), 5);
			setVillagerDataMethod.invoke(entity, villagerData);
			
			for (Player p : viewers) {
				Object packet = packetPlayOutSpawnEntityLivingConstructor.newInstance(entity);
				Object craftPlayer = craftPlayerClass.cast(p);
				Object entityPlayer = getHandleCraftPlayerMethod.invoke(craftPlayer);
				Object playerConnection = playerConnectionField.get(entityPlayer);
				sendPacketMethod.invoke(playerConnection, packet);
			}
		} catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public void teleport(double x, double y, double z, Player... viewers) {
		for (Player p : viewers) {
			try {
				setLocationMethod.invoke(entity, x, y, z, 0f, 0f);
				Object packet = packetPlayOutEntityTeleportConstructor.newInstance(entity);
				Object craftPlayer = craftPlayerClass.cast(p);
				Object entityPlayer = getHandleCraftPlayerMethod.invoke(craftPlayer);
				Object playerConnection = playerConnectionField.get(entityPlayer);
				sendPacketMethod.invoke(playerConnection, packet);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void despawn(Player... viewers) {
		for (Player p : viewers) {
			try {
				Object packet = packetPlayOutEntityDestroyConstructor.newInstance(new int[] {(int) getIdMethod.invoke(entity)});
				Object craftPlayer = craftPlayerClass.cast(p);
				Object entityPlayer = getHandleCraftPlayerMethod.invoke(craftPlayer);
				Object playerConnection = playerConnectionField.get(entityPlayer);
				sendPacketMethod.invoke(playerConnection, packet);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getEntityId() {
		try {
			return (int) getIdMethod.invoke(entity);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
}
