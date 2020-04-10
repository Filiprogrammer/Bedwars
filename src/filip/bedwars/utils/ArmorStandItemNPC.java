package filip.bedwars.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorStandItemNPC {

	private Object entity;
	
	Class<?> entityClass;
	Class<?> entityArmorStandClass;
	Class<?> entityLivingClass;
	Class<?> entityPlayerClass;
	Class<?> iChatBaseComponentClass;
	Class<?> chatComponentTextClass;
	Class<?> packetClass;
	Class<?> playerConnectionClass;
	Class<?> packetPlayOutSpawnEntityLivingClass;
	Class<?> packetPlayOutEntityTeleportClass;
	Class<?> packetPlayOutEntityDestroyClass;
	Class<?> packetPlayOutSpawnEntityClass;
	Class<?> packetPlayOutEntityMetadataClass;
	Class<?> packetPlayOutEntityEquipmentClass;
	Class<?> craftPlayerClass;
	Class<?> craftWorldClass;
	Class<?> worldClass;
	Class<?> enumItemSlotClass;
	Class<?> itemStackClass;
	Class<?> craftItemStackClass;
	Class<?> dataWatcherClass;
	Method setPositionMethod;
	Constructor<?> packetPlayOutSpawnEntityLivingConstructor;
	Constructor<?> packetPlayOutEntityTeleportConstructor;
	Constructor<?> packetPlayOutEntityDestroyConstructor;
	Constructor<?> packetPlayOutSpawnEntityConstructor;
	Constructor<?> packetPlayOutEntityMetadataConstructor;
	Constructor<?> packetPlayOutEntityEquipmentConstructor;
	Constructor<?> entityArmorStandConstructor;
	Constructor<?> chatComponentConstructor;
	Method getHandleCraftPlayerMethod;
	Field playerConnectionField;
	Method sendPacketMethod;
	Method getHandleCraftWorldMethod;
	Method getIdMethod;
	Method getDataWatcherMethod;
	Method asNMSCopyMethod;
	
	public ArmorStandItemNPC(Location location, String customName, Material material, Player... viewers) {
		try {
			String versionStr = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
			entityClass = Class.forName("net.minecraft.server." + versionStr + ".Entity");
			entityArmorStandClass = Class.forName("net.minecraft.server." + versionStr + ".EntityArmorStand");
			entityLivingClass = Class.forName("net.minecraft.server." + versionStr + ".EntityLiving");
			entityPlayerClass = Class.forName("net.minecraft.server." + versionStr + ".EntityPlayer");
			iChatBaseComponentClass = Class.forName("net.minecraft.server." + versionStr + ".IChatBaseComponent");
			chatComponentTextClass = Class.forName("net.minecraft.server." + versionStr + ".ChatComponentText");
			worldClass = Class.forName("net.minecraft.server." + versionStr + ".World");
			packetClass = Class.forName("net.minecraft.server." + versionStr + ".Packet");
			playerConnectionClass = Class.forName("net.minecraft.server." + versionStr + ".PlayerConnection");
			packetPlayOutSpawnEntityLivingClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutSpawnEntityLiving");
			packetPlayOutEntityTeleportClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutEntityTeleport");
			packetPlayOutEntityDestroyClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutEntityDestroy");
			packetPlayOutSpawnEntityClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutSpawnEntity");
			packetPlayOutEntityMetadataClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutEntityMetadata");
			packetPlayOutEntityEquipmentClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutEntityEquipment");
			craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + versionStr + ".entity.CraftPlayer");
			craftWorldClass = Class.forName("org.bukkit.craftbukkit." + versionStr + ".CraftWorld");
			craftItemStackClass = Class.forName("org.bukkit.craftbukkit." + versionStr + ".inventory.CraftItemStack");
			enumItemSlotClass = Class.forName("net.minecraft.server." + versionStr + ".EnumItemSlot");
			itemStackClass = Class.forName("net.minecraft.server." + versionStr + ".ItemStack");
			dataWatcherClass = Class.forName("net.minecraft.server." + versionStr + ".DataWatcher");
			entityArmorStandConstructor = entityArmorStandClass.getConstructor(worldClass, double.class, double.class, double.class);
			packetPlayOutSpawnEntityLivingConstructor = packetPlayOutSpawnEntityLivingClass.getConstructor(entityLivingClass);
			packetPlayOutEntityTeleportConstructor = packetPlayOutEntityTeleportClass.getConstructor(entityClass);
			packetPlayOutEntityDestroyConstructor = packetPlayOutEntityDestroyClass.getConstructor(int[].class);
			packetPlayOutSpawnEntityConstructor = packetPlayOutSpawnEntityClass.getConstructor(entityClass);
			packetPlayOutEntityMetadataConstructor = packetPlayOutEntityMetadataClass.getConstructor(int.class, dataWatcherClass, boolean.class);
			packetPlayOutEntityEquipmentConstructor = packetPlayOutEntityEquipmentClass.getConstructor(int.class, enumItemSlotClass, itemStackClass);
			chatComponentConstructor = chatComponentTextClass.getConstructor(String.class);
			getHandleCraftPlayerMethod = craftPlayerClass.getMethod("getHandle");
			playerConnectionField = entityPlayerClass.getField("playerConnection");
			sendPacketMethod = playerConnectionClass.getMethod("sendPacket", packetClass);
			getHandleCraftWorldMethod = craftWorldClass.getMethod("getHandle");
			getIdMethod = entityArmorStandClass.getMethod("getId");
			getDataWatcherMethod = entityArmorStandClass.getMethod("getDataWatcher");
			setPositionMethod = entityArmorStandClass.getMethod("setPosition", double.class, double.class, double.class);
			asNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
			
			spawn(location, customName, material, viewers);
		} catch (NoSuchMethodException | SecurityException | NoSuchFieldException | ClassNotFoundException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void spawn(Location location, String customName, Material material, Player[] viewers) {
		try {
			Object craftWorld = craftWorldClass.cast(location.getWorld());
			entity = entityArmorStandConstructor.newInstance(getHandleCraftWorldMethod.invoke(craftWorld), location.getX(), location.getY(), location.getZ());
			entityArmorStandClass.getMethod("setSmall", boolean.class).invoke(entity, true);
			entityArmorStandClass.getMethod("setCustomName", iChatBaseComponentClass).invoke(entity, chatComponentConstructor.newInstance(customName));
			entityArmorStandClass.getMethod("setCustomNameVisible", boolean.class).invoke(entity, true);
			entityArmorStandClass.getMethod("setInvisible", boolean.class).invoke(entity, true);
			
			for (Player p : viewers) {
				Object craftPlayer = craftPlayerClass.cast(p);
				Object entityPlayer = getHandleCraftPlayerMethod.invoke(craftPlayer);
				Object playerConnection = playerConnectionField.get(entityPlayer);
				sendPacketMethod.invoke(playerConnection, packetPlayOutSpawnEntityConstructor.newInstance(entity));
				sendPacketMethod.invoke(playerConnection, packetPlayOutEntityMetadataConstructor.newInstance(getIdMethod.invoke(entity), getDataWatcherMethod.invoke(entity), true));
				sendPacketMethod.invoke(playerConnection, packetPlayOutEntityEquipmentConstructor.newInstance(getIdMethod.invoke(entity), Enum.valueOf((Class<Enum>)enumItemSlotClass, "HEAD"), asNMSCopyMethod.invoke(craftItemStackClass, new ItemStack(material))));
			}
		} catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public void teleport(double x, double y, double z, Player... viewers) {
		for (Player p : viewers) {
			try {
				setPositionMethod.invoke(entity, x, y, z);
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
