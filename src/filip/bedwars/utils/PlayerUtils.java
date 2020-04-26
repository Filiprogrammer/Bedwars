package filip.bedwars.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_14_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_14_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;

public class PlayerUtils {

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
	
	public static void hidePlayer(Player toHide, Player viewer) {
		PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) toHide).getHandle());
		sendPacket(viewer, packetPlayOutPlayerInfo);
	}
	
	public static void damagePlayer(Player player, String cause, float amount) {
		try {
			Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".entity.CraftPlayer");
			Method getHandleMethod = craftPlayerClass.getMethod("getHandle");
			Class<?> entityPlayerClass = getNMSClass("EntityPlayer");
			Class<?> damageSourceClass = getNMSClass("DamageSource");
			Field damageSourceField = damageSourceClass.getField(cause);
			Method damageEntityMethod = entityPlayerClass.getMethod("damageEntity", damageSourceClass, float.class);
			
			Object craftPlayer = craftPlayerClass.cast(player);
			Object entityPlayer = getHandleMethod.invoke(craftPlayer);
			damageEntityMethod.invoke(entityPlayer, damageSourceField.get(null), amount);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
	public static void playerReset(Player player) {
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
		player.setAbsorptionAmount(0);
		player.setBedSpawnLocation(null);
		player.setExhaustion(0);
		player.setSaturation(20);
		player.setFoodLevel(20);
		player.setExp(0);
		player.setFallDistance(0);
		player.setFireTicks(0);
		player.setFlying(false);
		player.setGameMode(GameMode.SURVIVAL);
		player.setGlowing(false);
		player.setHealth(20);
		player.setHealthScaled(false);
		player.setItemOnCursor(null);
		player.setLevel(0);
		player.setRemainingAir(player.getMaximumAir());
		player.setNoDamageTicks(0);
		player.setVelocity(new Vector(0, 0, 0));
		player.getInventory().clear();
		
		for (PotionEffect potionEffect : player.getActivePotionEffects())
			player.removePotionEffect(potionEffect.getType());
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
