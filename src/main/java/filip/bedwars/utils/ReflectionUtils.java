package filip.bedwars.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.BedwarsPlugin;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

public class ReflectionUtils {

	public final Class<?> craftWorldClass;
	public final Class<?> craftPlayerClass;
	public final Class<?> craftServerClass;
	public final Class<?> dragonControllerPhaseClass;
	public final Class<?> dragonControllerStrafeClass;
	public final Class<?> dragonControllerChargeClass;
	public final Class<?> dragonControllerManagerClass;
	public final Class<?> entityEnderDragonClass;
	public final Class<?> entityLivingClass;
	public final Class<?> entityPlayerClass;
	public final Class<?> entityClass;
	public final Class<?> entityTypesClass;
	public final Class<?> packetClass;
	public final Class<?> packetPlayOutEntityDestroyClass;
	public final Class<?> packetPlayOutEntityTeleportClass;
	//public Class<?> packetPlayOutSpawnEntityLivingClass;
	public final Class<?> playerConnectionClass;
	public final Class<?> vec3DClass;
	public final Class<?> worldClass;
	//public Class<?> itemStackClass;
	//public Class<?> nbtTagCompoundClass;
	public final Class<?> craftItemStackClass;
	//public Class<?> nbtTagIntClass;
	//public Class<?> nbtBaseClass;
	public final Class<?> entityVillagerClass;
	//public final Class<?> iChatBaseComponentClass;
	//public Class<?> chatComponentTextClass;
	//public final Class<?> villagerDataClass;
	public final Class<?> villagerTypeClass;
	public final Class<?> villagerProfessionClass;
	public final Class<?> entityHumanClass;
	public final Class<?> packetPlayOutPlayerInfoClass;
	public final Class<?> enumPlayerInfoActionClass;
	//public Class<?> packetPlayOutNamedEntitySpawnClass;
	//public Class<?> damageSourceClass;
	//public Class<?> combatTrackerClass;
	//public Method entityEnderDragonTickMethod;
	public Method entitySetLocationMethod;
	//public Method entityGetIdMethod;
	//public Method entityGetWorldMethod;
	public Method entityGetEntityDataMethod;
	public final Method craftWorldGetHandleMethod;
	public final Method craftWorldGetNameMethod;
	public final Method craftPlayerGetHandleMethod;
	public final Method craftServerGetServerMethod;
	public final Method worldGetWorldMethod;
	public Method playerConnectionSendPacketMethod;
	//public Method entityEnderDragonGetDragonControllerManagerMethod;
	public Method dragonControllerManagerSetControllerPhaseMethod;
	public Method dragonControllerManagerBMethod;
	public final Method dragonControllerStrafeAMethod;
	public final Method dragonControllerChargeAMethod;
	public final Method craftItemStackAsNMSCopyMethod;
	public final Method craftItemStackAsBukkitCopyMethod;
	//public Method itemStackGetOrCreateTagMethod;
	//public Method nbtTagCompoundSetMethod;
	//public Method nbtTagCompoundHasKeyMethod;
	//public Method itemStackSetTagMethod;
	//public Method itemStackHasTagMethod;
	//public Method itemStackGetTagMethod;
	public Method entitySetCustomNameMethod;
	public final Method entitySetCustomNameVisibleMethod;
	public Method entityVillagerSetVillagerDataMethod;
	//public Method damageSourceDamageEntityMethod;
	//public Method iChatBaseComponentAddSiblingMethod;
	//public Method entityPlayerGetCombatTrackerMethod;
	//public Method entityPlayerSendMessageMethod;
	//public Method combatTrackerGetDeathMessageMethod;
	public Field entityPlayerPlayerConnectionField;
	//public Field entityTypesEnderDragonField;
	//public Field entityLocXField;
	//public Field entityLocYField;
	//public Field entityLocZField;
	//public Field dragonControllerPhaseStrafePlayerField;
	//public Field dragonControllerPhaseHoldingPatternField;
	//public Field dragonControllerPhaseChargingPlayerField;
	//public Field dragonControllerPhaseLandingField;
	//public Field dragonControllerPhaseLandingApproachField;
	public Field entityTypesVillagerField;
	//public Constructor<?> packetPlayOutSpawnEntityLivingConstructor;
	//public Constructor<?> packetPlayOutEntityDestroyConstructor;
	public Constructor<?> entityEnderDragonConstructor;
	public final Constructor<?> packetPlayOutEntityTeleportConstructor;
	public final Constructor<?> vec3DConstructor;
	//public Constructor<?> nbtTagIntConstructor;
	public Constructor<?> entityVillagerConstructor;
	//public Constructor<?> chatComponentConstructor;
	//public final Constructor<?> villagerDataConstructor;
	public Constructor<?> packetPlayOutPlayerInfoConstructor;
	//public Constructor<?> packetPlayOutNamedEntitySpawnConstructor;

	public final Constructor<?> serverPlayerConstructor;
	public Method componentNullToEmptyMethod;
	public Method synchedEntityDataPackMethod;

	public ReflectionUtils() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		String bukkitVersion = Bukkit.getBukkitVersion();
		String serverVersion = BedwarsPlugin.getInstance().getServerVersion();
		craftWorldClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".CraftWorld");
		craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".entity.CraftPlayer");
		craftServerClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".CraftServer");
		dragonControllerPhaseClass = Class.forName("net.minecraft.world.entity.boss.enderdragon.phases.DragonControllerPhase");
		dragonControllerStrafeClass = Class.forName("net.minecraft.world.entity.boss.enderdragon.phases.DragonControllerStrafe");
		dragonControllerChargeClass = Class.forName("net.minecraft.world.entity.boss.enderdragon.phases.DragonControllerCharge");
		dragonControllerManagerClass = Class.forName("net.minecraft.world.entity.boss.enderdragon.phases.DragonControllerManager");
		entityEnderDragonClass = Class.forName("net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon");
		entityLivingClass = Class.forName("net.minecraft.world.entity.EntityLiving");
		entityPlayerClass = Class.forName("net.minecraft.server.level.EntityPlayer");
		entityClass = Class.forName("net.minecraft.world.entity.Entity");
		entityTypesClass = Class.forName("net.minecraft.world.entity.EntityTypes");
		packetClass = Class.forName("net.minecraft.network.protocol.Packet");
		packetPlayOutEntityDestroyClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy");
		packetPlayOutEntityTeleportClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport");
		//packetPlayOutSpawnEntityLivingClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving");
		playerConnectionClass = Class.forName("net.minecraft.server.network.PlayerConnection");
		vec3DClass = Class.forName("net.minecraft.world.phys.Vec3D");
		worldClass = Class.forName("net.minecraft.world.level.World");
		entityVillagerClass = Class.forName("net.minecraft.world.entity.npc.EntityVillager");
		//iChatBaseComponentClass = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
		//chatComponentTextClass = Class.forName("net.minecraft.server." + serverVersion + ".ChatComponentText");
		//villagerDataClass = Class.forName("net.minecraft.world.entity.npc.VillagerData");
		villagerTypeClass = Class.forName("net.minecraft.world.entity.npc.VillagerType");
		villagerProfessionClass = Class.forName("net.minecraft.world.entity.npc.VillagerProfession");
		entityHumanClass = Class.forName("net.minecraft.world.entity.player.EntityHuman");
		packetPlayOutPlayerInfoClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo");
		enumPlayerInfoActionClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
		//packetPlayOutNamedEntitySpawnClass = Class.forName("net.minecraft.server." + serverVersion + ".PacketPlayOutNamedEntitySpawn");
		//damageSourceClass = Class.forName("net.minecraft.server." + serverVersion + ".DamageSource");
		//combatTrackerClass = Class.forName("net.minecraft.server." + serverVersion + ".CombatTracker");
		//entityEnderDragonTickMethod = entityEnderDragonClass.getMethod("tick");

		for (Method method : entityClass.getMethods()) {
			if (method.getParameterCount() != 5)
				continue;

			Class<?>[] paramTypes = method.getParameterTypes();
			if (paramTypes[0] == double.class && paramTypes[1] == double.class && paramTypes[2] == double.class && paramTypes[3] == float.class && paramTypes[4] == float.class) {
				entitySetLocationMethod = method;
				break;
			}
		}
		//entitySetLocationMethod = entityClass.getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);

		//entityGetIdMethod = entityClass.getMethod("getId");
		// Alternative: .hashCode() (also just returns the id)
		// 1.17.1: .getId()
		// 1.19 & 1.19.1: .ae()

		//entityGetWorldMethod = entityClass.getMethod("getWorld");

		for (Method method : entityClass.getMethods()) {
			if (method.getParameterCount() == 0 && method.getReturnType() == SynchedEntityData.class) {
				entityGetEntityDataMethod = method;
				break;
			}
		}

		craftWorldGetHandleMethod = craftWorldClass.getMethod("getHandle");
		craftWorldGetNameMethod = craftWorldClass.getMethod("getName");
		craftPlayerGetHandleMethod = craftPlayerClass.getMethod("getHandle");
		craftServerGetServerMethod = craftServerClass.getMethod("getServer");
		worldGetWorldMethod = worldClass.getMethod("getWorld");
		for (Method method : playerConnectionClass.getMethods()) {
			if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == packetClass) {
				playerConnectionSendPacketMethod = method;
				break;
			}
		}
		//playerConnectionSendPacketMethod = playerConnectionClass.getMethod("sendPacket", packetClass);
		//entityEnderDragonGetDragonControllerManagerMethod = entityEnderDragonClass.getMethod("getDragonControllerManager");
		for (Method method : dragonControllerManagerClass.getMethods()) {
			if (method.getName().equals("setControllerPhase")) {
				dragonControllerManagerSetControllerPhaseMethod = method;
				break;
			}
		}
		for (Method method : dragonControllerManagerClass.getMethods()) {
			if (method.getName().equals("b")) {
				dragonControllerManagerBMethod = method;
				break;
			}
		}
		dragonControllerStrafeAMethod = dragonControllerStrafeClass.getMethod("a", entityLivingClass);
		dragonControllerChargeAMethod = dragonControllerChargeClass.getMethod("a", vec3DClass);
		//entitySetCustomNameMethod = entityClass.getMethod("setCustomName", iChatBaseComponentClass);
		for (Method method : entityClass.getMethods()) {
			if (method.getParameterCount() != 1)
				continue;

			if (method.getParameterTypes()[0] != net.minecraft.network.chat.Component.class)
				continue;

			if (method.getParameterAnnotations()[0][0].annotationType() == Nullable.class) {
				entitySetCustomNameMethod = method;
				break;
			}
		}
		if (bukkitVersion.compareTo("1.17.1-R0.1-SNAPSHOT") <= 0) {
			entitySetCustomNameVisibleMethod = entityClass.getMethod("setCustomNameVisible", boolean.class);
		} else {
			entitySetCustomNameVisibleMethod = entityClass.getMethod("n", boolean.class);
		}
		//entityVillagerSetVillagerDataMethod = entityVillagerClass.getMethod("setVillagerData", villagerDataClass);
		for (Method method : VillagerDataHolder.class.getMethods()) {
			if (method.getParameterCount() != 1)
				continue;

			if (method.getParameterTypes()[0] == net.minecraft.world.entity.npc.VillagerData.class) {
				entityVillagerSetVillagerDataMethod = method;
				break;
			}
		}
		//damageSourceDamageEntityMethod = entityPlayerClass.getMethod("damageEntity", damageSourceClass, float.class);
		//iChatBaseComponentAddSiblingMethod = iChatBaseComponentClass.getMethod("addSibling", iChatBaseComponentClass);
		//entityPlayerGetCombatTrackerMethod = entityPlayerClass.getMethod("getCombatTracker");
		//entityPlayerSendMessageMethod = entityPlayerClass.getMethod("sendMessage", iChatBaseComponentClass);
		//combatTrackerGetDeathMessageMethod = combatTrackerClass.getMethod("getDeathMessage");

		for (Field field : entityPlayerClass.getFields()) {
			if (field.getType() == ServerGamePacketListenerImpl.class) {
				entityPlayerPlayerConnectionField = field;
				break;
			}
		}
		//entityPlayerPlayerConnectionField = entityPlayerClass.getField("playerConnection");

		//entityTypesEnderDragonField = entityTypesClass.getField("ENDER_DRAGON");
		//entityLocXField = entityClass.getDeclaredField("xo");
		//entityLocXField.setAccessible(true);
		//entityLocYField = entityClass.getDeclaredField("yo");
		//entityLocYField.setAccessible(true);
		//entityLocZField = entityClass.getDeclaredField("zo");
		//entityLocZField.setAccessible(true);
		//dragonControllerPhaseStrafePlayerField = dragonControllerPhaseClass.getField("STRAFE_PLAYER");
		//dragonControllerPhaseHoldingPatternField = dragonControllerPhaseClass.getField("HOLDING_PATTERN");
		//dragonControllerPhaseChargingPlayerField = dragonControllerPhaseClass.getField("CHARGING_PLAYER");
		//dragonControllerPhaseLandingField = dragonControllerPhaseClass.getField("LANDING");
		//dragonControllerPhaseLandingApproachField = dragonControllerPhaseClass.getField("LANDING_APPROACH");

		for (Field field : entityTypesClass.getFields()) {
			int modifiers = field.getModifiers();

			if (!Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers))
				continue;

			if (field.getType() != net.minecraft.world.entity.EntityType.class)
				continue;

			ParameterizedType parameterizedType = (ParameterizedType)field.getGenericType();
			if (parameterizedType.getActualTypeArguments()[0] == net.minecraft.world.entity.npc.Villager.class) {
				entityTypesVillagerField = field;
				break;
			}
		}

		//packetPlayOutSpawnEntityLivingConstructor = packetPlayOutSpawnEntityLivingClass.getConstructor(entityLivingClass);
		//packetPlayOutEntityDestroyConstructor = packetPlayOutEntityDestroyClass.getConstructor(new int[0].getClass());
		for (Constructor<?> constructor : entityEnderDragonClass.getConstructors()) {
			if (constructor.getParameterCount() == 2) {
				entityEnderDragonConstructor = constructor;
				break;
			}
		}
		packetPlayOutEntityTeleportConstructor = packetPlayOutEntityTeleportClass.getConstructor(entityClass);
		vec3DConstructor = vec3DClass.getConstructor(double.class, double.class, double.class);
		//itemStackClass = Class.forName("net.minecraft.server." + serverVersion + ".ItemStack");
		//nbtTagCompoundClass = Class.forName("net.minecraft.server." + serverVersion + ".NBTTagCompound");
		craftItemStackClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".inventory.CraftItemStack");
		craftItemStackAsNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
		craftItemStackAsBukkitCopyMethod = craftItemStackClass.getMethod("asBukkitCopy", net.minecraft.world.item.ItemStack.class);
		//itemStackGetOrCreateTagMethod = itemStackClass.getMethod("getOrCreateTag");
		//nbtTagIntClass = Class.forName("net.minecraft.server." + serverVersion + ".NBTTagInt");
		//nbtBaseClass = Class.forName("net.minecraft.server." + serverVersion + ".NBTBase");
		//nbtTagIntConstructor = nbtTagIntClass.getDeclaredConstructor(int.class);
		//nbtTagIntConstructor.setAccessible(true);
		//nbtTagCompoundSetMethod = nbtTagCompoundClass.getMethod("set", String.class, nbtBaseClass);
		//nbtTagCompoundHasKeyMethod = nbtTagCompoundClass.getMethod("hasKey", String.class);
		//itemStackSetTagMethod = itemStackClass.getMethod("setTag", nbtTagCompoundClass);
		//itemStackHasTagMethod = itemStackClass.getMethod("hasTag");
		//itemStackGetTagMethod = itemStackClass.getMethod("getTag");
		entityVillagerConstructor = null;
		for (Constructor<?> constructor : entityVillagerClass.getConstructors()) {
			if (constructor.getParameterCount() == 2) {
				entityVillagerConstructor = constructor;
				break;
			}
		}
		//chatComponentConstructor = chatComponentTextClass.getConstructor(String.class);
		//villagerDataConstructor = villagerDataClass.getConstructor(villagerTypeClass, villagerProfessionClass, int.class);
		packetPlayOutPlayerInfoConstructor = packetPlayOutPlayerInfoClass.getConstructor(enumPlayerInfoActionClass, java.lang.reflect.Array.newInstance(entityPlayerClass, 0).getClass());
		//packetPlayOutNamedEntitySpawnConstructor = packetPlayOutNamedEntitySpawnClass.getConstructor(entityHumanClass);

		serverPlayerConstructor = ServerPlayer.class.getConstructors()[0];

		for (Method method : net.minecraft.network.chat.Component.class.getMethods()) {
			if (method.getParameterCount() != 1)
				continue;

			if (method.getReturnType() != net.minecraft.network.chat.Component.class)
				continue;

			if (!Modifier.isStatic(method.getModifiers()))
				continue;

			if (method.getParameterTypes()[0] != String.class)
				continue;

			if (method.getParameterAnnotations()[0][0].annotationType() == Nullable.class) {
				componentNullToEmptyMethod = method;
				break;
			}
		}

		for (Method method : SynchedEntityData.class.getMethods()) {
			if (method.getParameterCount() == 0 && method.getReturnType() == List.class) {
				synchedEntityDataPackMethod = method;
				break;
			}
		}
	}

	public ServerPlayer playerToNMSPlayer(Player player) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// return ((CraftPlayer)player).getHandle();
		Object craftPlayer = craftPlayerClass.cast(player);
		return (ServerPlayer)craftPlayerGetHandleMethod.invoke(craftPlayer);
	}

	public ServerLevel worldToNMSWorld(World world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// return ((CraftWorld)world).getHandle();
		Object craftWorld = craftWorldClass.cast(world);
		return (ServerLevel)craftWorldGetHandleMethod.invoke(craftWorld);
	}

	public DedicatedServer serverToNMSServer(Server server) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// return ((CraftServer)server).getServer();
		Object craftServer = craftServerClass.cast(server);
		return (DedicatedServer)craftServerGetServerMethod.invoke(craftServer);
	}

	public VillagerType parseVillagerType(String name) {
		for (Field field : VillagerType.class.getFields()) {
			if (field.getType() != VillagerType.class)
				continue;

			int modifiers = field.getModifiers();
			if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers))
				continue;

			try {
				VillagerType villagerType = (VillagerType)field.get(null);
				if (villagerType.toString().equalsIgnoreCase(name)) {
					return villagerType;
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public VillagerProfession parseVillagerProfession(String name) {
		for (Field field : VillagerProfession.class.getFields()) {
			if (field.getType() != VillagerProfession.class)
				continue;

			int modifiers = field.getModifiers();
			if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers))
				continue;

			try {
				VillagerProfession villagerProfession = (VillagerProfession)field.get(null);
				if (villagerProfession.toString().equalsIgnoreCase(name)) {
					return villagerProfession;
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

}
