package filip.bedwars.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorStandItemNPC {

	private ArmorStand entity;
	
	//Class<?> entityClass;
	//Class<?> entityArmorStandClass;
	//Class<?> entityLivingClass;
	//Class<?> entityPlayerClass;
	//Class<?> iChatBaseComponentClass;
	//Class<?> chatComponentTextClass;
	//Class<?> packetClass;
	//Class<?> playerConnectionClass;
	//Class<?> packetPlayOutSpawnEntityLivingClass;
	//Class<?> packetPlayOutEntityTeleportClass;
	//Class<?> packetPlayOutEntityDestroyClass;
	//Class<?> packetPlayOutSpawnEntityClass;
	//Class<?> packetPlayOutEntityMetadataClass;
	//Class<?> packetPlayOutEntityEquipmentClass;
	//Class<?> craftPlayerClass;
	//Class<?> craftWorldClass;
	//Class<?> worldClass;
	//Class<?> enumItemSlotClass;
	//Class<?> itemStackClass;
	//Class<?> craftItemStackClass;
	//Class<?> dataWatcherClass;
	//Method setPositionMethod;
	//Constructor<?> packetPlayOutSpawnEntityLivingConstructor;
	//Constructor<?> packetPlayOutEntityTeleportConstructor;
	//Constructor<?> packetPlayOutEntityDestroyConstructor;
	//Constructor<?> packetPlayOutSpawnEntityConstructor;
	//Constructor<?> packetPlayOutEntityMetadataConstructor;
	//Constructor<?> packetPlayOutEntityEquipmentConstructor;
	//Constructor<?> entityArmorStandConstructor;
	//Constructor<?> chatComponentConstructor;
	//Method getHandleCraftPlayerMethod;
	//Field playerConnectionField;
	//Method sendPacketMethod;
	//Method getHandleCraftWorldMethod;
	//Method getIdMethod;
	//Method getDataWatcherMethod;
	//Method asNMSCopyMethod;
	
	public ArmorStandItemNPC(Location location, String customName, Material material, Player... viewers) {
		try {
			String versionStr = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
			//entityClass = Class.forName("net.minecraft.server." + versionStr + ".Entity");
			//entityArmorStandClass = Class.forName("net.minecraft.server." + versionStr + ".EntityArmorStand");
			//entityLivingClass = Class.forName("net.minecraft.server." + versionStr + ".EntityLiving");
			//entityPlayerClass = Class.forName("net.minecraft.server." + versionStr + ".EntityPlayer");
			//iChatBaseComponentClass = Class.forName("net.minecraft.server." + versionStr + ".IChatBaseComponent");
			//chatComponentTextClass = Class.forName("net.minecraft.server." + versionStr + ".ChatComponentText");
			//worldClass = Class.forName("net.minecraft.server." + versionStr + ".World");
			//packetClass = Class.forName("net.minecraft.server." + versionStr + ".Packet");
			//playerConnectionClass = Class.forName("net.minecraft.server." + versionStr + ".PlayerConnection");
			//packetPlayOutSpawnEntityLivingClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutSpawnEntityLiving");
			//packetPlayOutEntityTeleportClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutEntityTeleport");
			//packetPlayOutEntityDestroyClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutEntityDestroy");
			//packetPlayOutSpawnEntityClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutSpawnEntity");
			//packetPlayOutEntityMetadataClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutEntityMetadata");
			//packetPlayOutEntityEquipmentClass = Class.forName("net.minecraft.server." + versionStr + ".PacketPlayOutEntityEquipment");
			//craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + versionStr + ".entity.CraftPlayer");
			//craftWorldClass = Class.forName("org.bukkit.craftbukkit." + versionStr + ".CraftWorld");
			//craftItemStackClass = Class.forName("org.bukkit.craftbukkit." + versionStr + ".inventory.CraftItemStack");
			//enumItemSlotClass = Class.forName("net.minecraft.server." + versionStr + ".EnumItemSlot");
			//itemStackClass = Class.forName("net.minecraft.server." + versionStr + ".ItemStack");
			//dataWatcherClass = Class.forName("net.minecraft.server." + versionStr + ".DataWatcher");
			//entityArmorStandConstructor = entityArmorStandClass.getConstructor(worldClass, double.class, double.class, double.class);
			//packetPlayOutSpawnEntityLivingConstructor = packetPlayOutSpawnEntityLivingClass.getConstructor(entityLivingClass);
			//packetPlayOutEntityTeleportConstructor = packetPlayOutEntityTeleportClass.getConstructor(entityClass);
			//packetPlayOutEntityDestroyConstructor = packetPlayOutEntityDestroyClass.getConstructor(int[].class);
			//packetPlayOutSpawnEntityConstructor = packetPlayOutSpawnEntityClass.getConstructor(entityClass);
			//packetPlayOutEntityMetadataConstructor = packetPlayOutEntityMetadataClass.getConstructor(int.class, dataWatcherClass, boolean.class);
			//packetPlayOutEntityEquipmentConstructor = packetPlayOutEntityEquipmentClass.getConstructor(int.class, enumItemSlotClass, itemStackClass);
			//chatComponentConstructor = chatComponentTextClass.getConstructor(String.class);
			//getHandleCraftPlayerMethod = craftPlayerClass.getMethod("getHandle");
			//playerConnectionField = entityPlayerClass.getField("playerConnection");
			//sendPacketMethod = playerConnectionClass.getMethod("sendPacket", packetClass);
			//getHandleCraftWorldMethod = craftWorldClass.getMethod("getHandle");
			//getIdMethod = entityArmorStandClass.getMethod("getId");
			//getDataWatcherMethod = entityArmorStandClass.getMethod("getDataWatcher");
			//setPositionMethod = entityArmorStandClass.getMethod("setPosition", double.class, double.class, double.class);
			//asNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
			
			spawn(location, customName, material, viewers);
		} catch (SecurityException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void spawn(Location location, String customName, Material material, Player[] viewers) {
		try {
			CraftWorld craftWorld = (CraftWorld)location.getWorld();
			//Object craftWorld = craftWorldClass.cast(location.getWorld());
			entity = new ArmorStand(craftWorld.getHandle(), location.getX(), location.getY(), location.getZ());
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
				CraftPlayer craftPlayer = (CraftPlayer)p;
				//Object craftPlayer = craftPlayerClass.cast(p);
				ServerPlayer entityPlayer = craftPlayer.getHandle();
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
		} catch (SecurityException | IllegalArgumentException e) {
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
				CraftPlayer craftPlayer = (CraftPlayer)p;
				//Object craftPlayer = craftPlayerClass.cast(p);
				ServerPlayer entityPlayer = craftPlayer.getHandle();
				//Object entityPlayer = getHandleCraftPlayerMethod.invoke(craftPlayer);
				ServerGamePacketListenerImpl playerConnection =  entityPlayer.connection;
				//Object playerConnection = playerConnectionField.get(entityPlayer);
				playerConnection.send(packet);
				//sendPacketMethod.invoke(playerConnection, packet);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void despawn(Player... viewers) {
		for (Player p : viewers) {
			try {
				ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(entity.getId());
				//Object packet = packetPlayOutEntityDestroyConstructor.newInstance(new int[] {entity.getId()});
				CraftPlayer craftPlayer = (CraftPlayer)p;
				//Object craftPlayer = craftPlayerClass.cast(p);
				ServerPlayer entityPlayer = craftPlayer.getHandle();
				//Object entityPlayer = getHandleCraftPlayerMethod.invoke(craftPlayer);
				ServerGamePacketListenerImpl playerConnection = entityPlayer.connection;
				//Object playerConnection = playerConnectionField.get(entityPlayer);
				playerConnection.send(packet);
				//sendPacketMethod.invoke(playerConnection, packet);
			} catch (IllegalArgumentException e) {
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
