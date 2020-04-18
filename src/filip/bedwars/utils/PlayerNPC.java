package filip.bedwars.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import filip.bedwars.BedwarsPlugin;

public class PlayerNPC {

	private Object entity;
	
	Class<?> entityClass;
	Class<?> entityLivingClass;
	Class<?> entityPlayerClass;
	Class<?> iChatBaseComponentClass;
	Class<?> chatComponentTextClass;
	Class<?> villagerDataClass;
	Class<?> entityTypesClass;
	Class<?> packetClass;
	Class<?> playerConnectionClass;
	Class<?> packetPlayOutPlayerInfoClass;
	Class<?> packetPlayOutEntityTeleportClass;
	Class<?> packetPlayOutEntityDestroyClass;
	Class<?> PacketPlayOutNamedEntitySpawnClass;
	Class<?> craftPlayerClass;
	Class<?> craftWorldClass;
	Class<?> craftServerClass;
	Class<?> playerInteractManagerClass;
	Class<?> minecraftServerClass;
	Class<?> worldServerClass;
	Class<?> enumPlayerInfoActionClass;
	Class<?> entityHumanClass;
	Constructor<?> packetPlayOutPlayerInfoConstructor;
	Constructor<?> packetPlayOutEntityTeleportConstructor;
	Constructor<?> packetPlayOutEntityDestroyConstructor;
	Constructor<?> packetPlayOutNamedEntitySpawnConstructor;
	Constructor<?> chatComponentConstructor;
	Constructor<?> entityPlayerConstructor;
	Constructor<?> playerInteractManagerConstructor;
	Method getHandleCraftPlayerMethod;
	Field playerConnectionField;
	Field villagerEntityTypesField;
	Field desertVillagerTypeField;
	Field armorerVillagerProfessionField;
	Method sendPacketMethod;
	Method getHandleCraftWorldMethod;
	Method getServerCraftServerMethod;
	Method setLocationMethod;
	Method getIdMethod;
	
	public PlayerNPC(Location location, String customName, Player... viewers) {
		try {
			String versionStr = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
			entityClass = Class.forName("net.minecraft.server." + versionStr + ".Entity");
			entityLivingClass = Class.forName("net.minecraft.server." + versionStr + ".EntityLiving");
			entityPlayerClass = Class.forName("net.minecraft.server." + versionStr + ".EntityPlayer");
			iChatBaseComponentClass = Class.forName("net.minecraft.server." + versionStr + ".IChatBaseComponent");
			chatComponentTextClass = Class.forName("net.minecraft.server." + versionStr + ".ChatComponentText");
			villagerDataClass = Class.forName("net.minecraft.server." + versionStr + ".VillagerData");
			entityTypesClass = Class.forName("net.minecraft.server." + versionStr + ".EntityTypes");
			packetClass = Class.forName("net.minecraft.server." + versionStr + ".Packet");
			playerConnectionClass = Class.forName("net.minecraft.server." + versionStr + ".PlayerConnection");
			packetPlayOutPlayerInfoClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutPlayerInfo");
			packetPlayOutEntityTeleportClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutEntityTeleport");
			packetPlayOutEntityDestroyClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutEntityDestroy");
			PacketPlayOutNamedEntitySpawnClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutNamedEntitySpawn");
			craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + versionStr + ".entity.CraftPlayer");
			craftWorldClass = Class.forName("org.bukkit.craftbukkit." + versionStr + ".CraftWorld");
			craftServerClass = Class.forName("org.bukkit.craftbukkit." + versionStr + ".CraftServer");
			playerInteractManagerClass = Class.forName("net.minecraft.server." + versionStr + ".PlayerInteractManager");
			minecraftServerClass = Class.forName("net.minecraft.server." + versionStr + ".MinecraftServer");
			worldServerClass = Class.forName("net.minecraft.server." + versionStr + ".WorldServer");
			enumPlayerInfoActionClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
			entityHumanClass = Class.forName("net.minecraft.server." + versionStr + ".EntityHuman");
			packetPlayOutPlayerInfoConstructor = packetPlayOutPlayerInfoClass.getConstructor(enumPlayerInfoActionClass, java.lang.reflect.Array.newInstance(entityPlayerClass, 0).getClass());
			packetPlayOutEntityTeleportConstructor = packetPlayOutEntityTeleportClass.getConstructor(entityClass);
			packetPlayOutEntityDestroyConstructor = packetPlayOutEntityDestroyClass.getConstructor(int[].class);
			packetPlayOutNamedEntitySpawnConstructor = PacketPlayOutNamedEntitySpawnClass.getConstructor(entityHumanClass);
			entityPlayerConstructor = entityPlayerClass.getConstructor(minecraftServerClass, worldServerClass, GameProfile.class, playerInteractManagerClass);
			chatComponentConstructor = chatComponentTextClass.getConstructor(String.class);
			playerInteractManagerConstructor = playerInteractManagerClass.getConstructor(worldServerClass);
			getHandleCraftPlayerMethod = craftPlayerClass.getMethod("getHandle");
			playerConnectionField = entityPlayerClass.getField("playerConnection");
			sendPacketMethod = playerConnectionClass.getMethod("sendPacket", packetClass);
			getHandleCraftWorldMethod = craftWorldClass.getMethod("getHandle");
			getServerCraftServerMethod = craftServerClass.getMethod("getServer");
			setLocationMethod = entityPlayerClass.getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
			getIdMethod = entityPlayerClass.getMethod("getId");
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		
		spawn(location, customName, viewers);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void spawn(Location location, String customName, Player[] viewers) {
		try {
			Object craftWorld = craftWorldClass.cast(location.getWorld());
			Object nmsWorld = getHandleCraftWorldMethod.invoke(craftWorld);
			Object craftServer = craftServerClass.cast(Bukkit.getServer());
			Object nmsServer = getServerCraftServerMethod.invoke(craftServer);
			GameProfile gameprofile = new GameProfile(UUID.randomUUID(), customName);
			gameprofile.getProperties().put("textures", new Property("textures", "eyJ0aW1lc3RhbXAiOjE1MTQwNjU1NjM4NzcsInByb2ZpbGVJZCI6ImJkZTc2ODgwMzQ5NzQ0MTRiNDI1NDRhZDBmZGExNTQ1IiwicHJvZmlsZU5hbWUiOiJLcnVzdHlOdXRzIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8xMThlMjExZWNlMjEyMjEwODI0NWY3ZGQ2NjMyYWE5N2JhMzY2MmRmYTNiZmYzODIzNmJhYTNkOGM4OWMzIn19fQ==", "YBePlXOWjcer6fnU2S40hqkEyP2J6M+E/APMxgBBbCg37mtcvsUSUuznzI2ZDhmsFahfYGmD55MsHrjUSmEeES2soAg28Qo33hUGynWIQjb7y8mVuiVfueVKAkXil/mJqrLUM+5nnz5JatZX6kymuOdtyda/o/5UMX8u08metxAgsn5GPFNnzLHwLHYMuJOtcyAI//ClubrMbXX6SkCMzC5Vg/G6eniQGdaJBokIQQTQbadcGPL1+SkH+aNHvrqvuSkdHwccYTdcTjlr5W2DV5DfVz8dZ91B2/LTLi/Q29bYSXhjDekcrBi0yhuIdbZAIgQTD8EeKwG7S1A4MTBrmRP5mwA8Xc1qMcz3JhEskbteO8cqbu/0+xpZp8uPjAb3+7090OGWAbQfa9+Nyl9EG+SaWoDpieI7rx+pqcmk0JuEi2FKGatcIWDUiOb/Rqz357TEIEcZuyfsxcsnwKKtXQQHhrGjdhUpEyltJq0Qjwviqf704mD11yiapUksAQxq8aSRpCDpqunXKITAiPiIuTpxN4nrCklPH+HBBKmVGcSl+9BCS3LHfSqyJ3wbgLSITgLQJZLwRjfe/k4mJKyxW+OWWoqPtmbWrKto+gMI/wQPGpb6aQlpM7gvjMIPucuA5Wl1ErBE9spaEsUcpDlyGhph6k2O04a5Taz8sL8yeSc="));
	        entity = entityPlayerConstructor.newInstance(nmsServer, nmsWorld, gameprofile, playerInteractManagerConstructor.newInstance(nmsWorld));
			setLocationMethod.invoke(entity, location.getX(), location.getY(), location.getZ(), 0, 0);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			e.printStackTrace();
		}
        
        for(Player p : viewers){
        	try {
        		Object craftPlayer = craftPlayerClass.cast(p);
				Object entityPlayer = getHandleCraftPlayerMethod.invoke(craftPlayer);
				Object connection = playerConnectionField.get(entityPlayer);
				Object[] entityPlayerArray = (Object[]) java.lang.reflect.Array.newInstance(entityPlayerClass, 1);
				entityPlayerArray[0] = entity;
				sendPacketMethod.invoke(connection, packetPlayOutPlayerInfoConstructor.newInstance(Enum.valueOf((Class<Enum>)enumPlayerInfoActionClass, "ADD_PLAYER"), entityPlayerArray));
				sendPacketMethod.invoke(connection, packetPlayOutNamedEntitySpawnConstructor.newInstance(entity));
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(BedwarsPlugin.getInstance(), () -> {
					try {
						sendPacketMethod.invoke(connection, packetPlayOutPlayerInfoConstructor.newInstance(Enum.valueOf((Class<Enum>)enumPlayerInfoActionClass, "REMOVE_PLAYER"), entityPlayerArray));
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
						e.printStackTrace();
					}
				}, 5L);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
				e.printStackTrace();
			}
        }
	}
	
	public void teleport(double x, double y, double z, Player... viewers) {
		for (Player p : viewers) {
			try {
				setLocationMethod.invoke(entity, x, y, z, 0, 0);
				Object craftPlayer = craftPlayerClass.cast(p);
				Object entityPlayer = getHandleCraftPlayerMethod.invoke(craftPlayer);
				Object playerConnection = playerConnectionField.get(entityPlayer);
				sendPacketMethod.invoke(playerConnection, packetPlayOutEntityTeleportConstructor.newInstance(entity));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void despawn(Player... viewers) {
		for (Player p : viewers) {
			try {
				Object craftPlayer = craftPlayerClass.cast(p);
				Object entityPlayer = getHandleCraftPlayerMethod.invoke(craftPlayer);
				Object playerConnection = playerConnectionField.get(entityPlayer);
				sendPacketMethod.invoke(playerConnection, packetPlayOutEntityDestroyConstructor.newInstance(new int[] {(int) getIdMethod.invoke(entity)}));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
				e.printStackTrace();
			}
		}
	}
	
}
