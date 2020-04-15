package filip.bedwars.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerEntityHider {

	public static void hidePlayerEntity(Player toHide, Player viewer) {
		try {
			Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".entity.CraftPlayer");
			Method getHandleMethod = craftPlayerClass.getMethod("getHandle");
			Class<?> entityPlayerClass = getNMSClass("EntityPlayer");
			Method getIdMethod = entityPlayerClass.getMethod("getId");
			Class<?> packetPlayOutEntityDestroyClass = getNMSClass("PacketPlayOutEntityDestroy");
			Object aintArray = java.lang.reflect.Array.newInstance(int.class, 1);
			Constructor<?> packetPlayOutEntityDestroyConstructor = packetPlayOutEntityDestroyClass.getConstructor(aintArray.getClass());
			
			// int toHideEntityId = ((CraftPlayer) toHide).getHandle().getId();
			Object toHideCraftPlayer = craftPlayerClass.cast(toHide);
			Object toHideEntityPlayer = getHandleMethod.invoke(toHideCraftPlayer);
			int toHideEntityId = (int) getIdMethod.invoke(toHideEntityPlayer);
			
			// viewerConnection.sendPacket(new PacketPlayOutEntityDestroy(toHideEntityId));
			java.lang.reflect.Array.set(aintArray, 0, toHideEntityId);
			Object packetPlayOutEntityDestroy = packetPlayOutEntityDestroyConstructor.newInstance(aintArray);
			sendPacket(viewer, packetPlayOutEntityDestroy);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public static void showPlayerEntity(Player toHide, Player viewer) {
		try {
			Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".entity.CraftPlayer");
			Method getHandleMethod = craftPlayerClass.getMethod("getHandle");
			Class<?> packetPlayOutNamedEntitySpawnClass = getNMSClass("PacketPlayOutNamedEntitySpawn");
			Class<?> entityHumanClass = getNMSClass("EntityHuman");
			Constructor<?> packetPlayOutNamedEntitySpawnConstructor = packetPlayOutNamedEntitySpawnClass.getConstructor(entityHumanClass);
			
			// EntityPlayer toHideEntityPlayer = ((CraftPlayer) toHide).getHandle();
			Object toHideCraftPlayer = craftPlayerClass.cast(toHide);
			Object toHideEntityPlayer = getHandleMethod.invoke(toHideCraftPlayer);
			
			// viewerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(toHideEntityPlayer));
			Object packetPlayOutNamedEntitySpawn = packetPlayOutNamedEntitySpawnConstructor.newInstance(toHideEntityPlayer);
			sendPacket(viewer, packetPlayOutNamedEntitySpawn);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	private static void sendPacket(Player player, Object packet) {
	    try {
	        Object handle = player.getClass().getMethod("getHandle").invoke(player);
	        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
	        playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	private static Class<?> getNMSClass(String name) {
	    try {
	        return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
}
