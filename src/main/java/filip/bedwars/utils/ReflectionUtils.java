package filip.bedwars.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mojang.authlib.GameProfile;

import filip.bedwars.BedwarsPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

public class ReflectionUtils {

	// org.bukkit.craftbukkit
	public final Class<?> craftEntityClass;
	public final Method craftEntityGetHandleMethod;
	public final Method craftEntityGetLocationMethod;
	public final Class<?> craftInventoryClass;
	public final Method craftInventoryGetInventoryMethod;
	public final Class<?> craftItemStackClass;
	public final Method craftItemStackAsNMSCopyMethod;
	public final Method craftItemStackAsBukkitCopyMethod;
	public final Class<?> craftPlayerClass;
	public final Method craftPlayerGetHandleMethod;
	public final Class<?> craftServerClass;
	public final Method craftServerGetServerMethod;
	public final Class<?> craftWorldClass;
	public final Method craftWorldGetHandleMethod;
	public final Method craftWorldGetNameMethod;
	public final Class<?> minecraftInventoryClass;
	public final Method minecraftInventoryGetTitleMethod;

	// net.minecraft.network
	public Field connectionChannelField;

	// net.minecraft.network.chat
	public Method componentNullToEmptyMethod;
	public Method mutableComponentAppendMethod;

	// net.minecraft.network.protocol
	public final Class<?> packetClass;
	public final Class<?> clientboundRemoveEntitiesPacketClass;
	public final Class<?> clientboundTeleportEntityPacketClass;
	public final Constructor<?> clientboundTeleportEntityPacketConstructor;
	//public Class<?> packetPlayOutSpawnEntityLivingClass;
	public final Class<?> clientboundPlayerInfoPacketClass;
	public final Constructor<?> clientboundPlayerInfoPacketConstructor;
	public Method clientboundPlayerInfoPacketEntriesMethod;
	//public Class<?> packetPlayOutNamedEntitySpawnClass;
	//public Constructor<?> packetPlayOutSpawnEntityLivingConstructor;
	//public Constructor<?> clientboundRemoveEntitiesPacketConstructor;
	//public Constructor<?> packetPlayOutNamedEntitySpawnConstructor;
	public Method clientboundUpdateAttributesPacketGetEntityIdMethod;
	public Method clientboundSetEquipmentPacketGetEntityIdMethod;
	public Method clientboundSetEntityDataPacketIdMethod;
	public Method clientboundAddEntityPacketGetIdMethod;
	public final Class<?> enumPlayerInfoActionClass;
	public final Class<?> playerInfoDataClass;
	public Method playerInfoDataGetGameProfileMethod;

	// net.minecraft.network.syncher
	public Method synchedEntityDataPackMethod;

	// net.minecraft.server
	public final Class<?> entityPlayerClass;
	public final Class<?> playerConnectionClass;
	public Field entityPlayerPlayerConnectionField;
	public final Constructor<?> serverPlayerConstructor;
	public final Method serverPlayerSendSystemMessageMethod;

	// net.minecraft.world
	public final Class<?> dragonControllerPhaseClass;
	public final Class<?> dragonStrafePlayerPhaseClass;
	public final Class<?> dragonChargePlayerPhaseClass;
	public final Class<?> enderDragonPhaseManagerClass;
	public final Class<?> entityEnderDragonClass;
	public Constructor<?> entityEnderDragonConstructor;
	public final Class<?> entityLivingClass;
	public Method entityLivingGetCombatTrackerMethod;
	public final Class<?> entityClass;
	public Method entitySetLocationMethod;
	public Method entityLevelMethod;
	public Method entityGetEntityDataMethod;
	public final Method entityGetBukkitEntityMethod;
	public Method entitySetCustomNameMethod;
	public final Method entitySetCustomNameVisibleMethod;
	public final Method entitySetInvisibleMethod;
	public final Class<?> entityHumanClass;
	public final Class<?> entityVillagerClass;
	public Method entityVillagerSetVillagerDataMethod;
	public Constructor<?> entityVillagerConstructor;
	public final Class<?> entityTypesClass;
	public Field entityTypesVillagerField;
	public final Class<?> vec3DClass;
	public final Constructor<?> vec3DConstructor;
	public final Class<?> villagerTypeClass;
	public final Class<?> villagerProfessionClass;
	public final Method mobTickMethod;
	public final Method levelGetWorldMethod;
	public Method playerConnectionSendPacketMethod;
	public Field playerConnectionConnectionField;
	public Method entityEnderDragonGetPhaseManagerMethod;
	public Method dragonPhaseManagerSetPhaseMethod;
	public Method dragonPhaseManagerGetCurrentPhaseMethod;
	public Method dragonStrafePlayerPhaseSetTargetMethod;
	public Method dragonChargePlayerPhaseSetTargetMethod;
	public Method itemStackGetOrCreateTagMethod;
	public Method itemStackSetTagMethod;
	public final Method itemStackHasTagMethod;
	//public Method itemStackGetTagMethod;
	public final Method entityArmorStandSetSmallMethod;
	public Method combatTrackerGetDeathMessageMethod;

	// net.minecraft.nbt
	public final Method compoundTagHasKeyMethod;
	public Method compoundTagPutIntMethod;

	//public Class<?> itemStackClass;
	//public Class<?> nbtTagCompoundClass;
	//public Class<?> nbtTagIntClass;
	//public Class<?> nbtBaseClass;
	//public final Class<?> iChatBaseComponentClass;
	//public Class<?> chatComponentTextClass;
	//public final Class<?> villagerDataClass;
	//public Class<?> damageSourceClass;
	//public Class<?> combatTrackerClass;

	//public Method damageSourceDamageEntityMethod;
	//public Method iChatBaseComponentAddSiblingMethod;
	//public Method entityPlayerGetCombatTrackerMethod;
	//public Method entityPlayerSendMessageMethod;
	//public Field entityTypesEnderDragonField;
	//public Field dragonControllerPhaseStrafePlayerField;
	//public Field dragonControllerPhaseHoldingPatternField;
	//public Field dragonControllerPhaseChargingPlayerField;
	//public Field dragonControllerPhaseLandingField;
	//public Field dragonControllerPhaseLandingApproachField;
	//public Constructor<?> nbtTagIntConstructor;
	//public Constructor<?> chatComponentConstructor;
	//public final Constructor<?> villagerDataConstructor;

	public ReflectionUtils() throws ClassNotFoundException, NoSuchMethodException, SecurityException, NoSuchFieldException {
		String bukkitVersion = Bukkit.getBukkitVersion();
		String serverVersion = BedwarsPlugin.getInstance().getServerVersion();

		// org.bukkit.craftbukkit - classes
		craftEntityClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".entity.CraftEntity");
		craftInventoryClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".inventory.CraftInventory");
		craftItemStackClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".inventory.CraftItemStack");
		craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".entity.CraftPlayer");
		craftServerClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".CraftServer");
		craftWorldClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".CraftWorld");
		minecraftInventoryClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".inventory.CraftInventoryCustom$MinecraftInventory");

		// net.minecraft.network.protocol - classes
		packetClass = Class.forName("net.minecraft.network.protocol.Packet");
		clientboundRemoveEntitiesPacketClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy");
		clientboundTeleportEntityPacketClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport");
		//packetPlayOutSpawnEntityLivingClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving");
		clientboundPlayerInfoPacketClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo");
		//packetPlayOutNamedEntitySpawnClass = Class.forName("net.minecraft.server." + serverVersion + ".PacketPlayOutNamedEntitySpawn");
		enumPlayerInfoActionClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
		playerInfoDataClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo$PlayerInfoData");

		// net.minecraft.server - classes
		entityPlayerClass = Class.forName("net.minecraft.server.level.EntityPlayer");
		playerConnectionClass = Class.forName("net.minecraft.server.network.PlayerConnection");

		// net.minecraft.world - classes
		dragonControllerPhaseClass = Class.forName("net.minecraft.world.entity.boss.enderdragon.phases.DragonControllerPhase");
		dragonStrafePlayerPhaseClass = Class.forName("net.minecraft.world.entity.boss.enderdragon.phases.DragonControllerStrafe");
		dragonChargePlayerPhaseClass = Class.forName("net.minecraft.world.entity.boss.enderdragon.phases.DragonControllerCharge");
		enderDragonPhaseManagerClass = Class.forName("net.minecraft.world.entity.boss.enderdragon.phases.DragonControllerManager");
		entityEnderDragonClass = Class.forName("net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon");
		entityLivingClass = Class.forName("net.minecraft.world.entity.EntityLiving");
		entityClass = Class.forName("net.minecraft.world.entity.Entity");
		entityHumanClass = Class.forName("net.minecraft.world.entity.player.EntityHuman");
		entityVillagerClass = Class.forName("net.minecraft.world.entity.npc.EntityVillager");
		entityTypesClass = Class.forName("net.minecraft.world.entity.EntityTypes");
		vec3DClass = Class.forName("net.minecraft.world.phys.Vec3D");
		villagerTypeClass = Class.forName("net.minecraft.world.entity.npc.VillagerType");
		villagerProfessionClass = Class.forName("net.minecraft.world.entity.npc.VillagerProfession");

		// org.bukkit.craftbukkit - methods
		craftEntityGetHandleMethod = craftEntityClass.getMethod("getHandle");
		craftEntityGetLocationMethod = craftEntityClass.getMethod("getLocation");
		craftInventoryGetInventoryMethod = craftInventoryClass.getMethod("getInventory");
		craftItemStackAsNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
		craftItemStackAsBukkitCopyMethod = craftItemStackClass.getMethod("asBukkitCopy", net.minecraft.world.item.ItemStack.class);
		craftPlayerGetHandleMethod = craftPlayerClass.getMethod("getHandle");
		craftServerGetServerMethod = craftServerClass.getMethod("getServer");
		craftWorldGetHandleMethod = craftWorldClass.getMethod("getHandle");
		craftWorldGetNameMethod = craftWorldClass.getMethod("getName");
		minecraftInventoryGetTitleMethod = minecraftInventoryClass.getMethod("getTitle");
		minecraftInventoryGetTitleMethod.setAccessible(true);

		// net.minecraft.network.chat - methods
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
		for (Method method : MutableComponent.class.getMethods()) {
			if (method.getReturnType() != MutableComponent.class)
				continue;

			if (method.getParameterCount() != 1)
				continue;

			if (method.getParameterTypes()[0] == Component.class) {
				mutableComponentAppendMethod = method;
				break;
			}
		}

		// net.minecraft.network.protocol - methods
		for (Method method : clientboundPlayerInfoPacketClass.getMethods()) {
			if (method.getParameterCount() != 0)
				continue;

			if (method.getReturnType() != List.class)
				continue;

			ParameterizedType parameterizedType = (ParameterizedType)method.getGenericReturnType();
			if (parameterizedType.getActualTypeArguments()[0] == playerInfoDataClass) {
				clientboundPlayerInfoPacketEntriesMethod = method;
				break;
			}
		}
		for (Method method : ClientboundUpdateAttributesPacket.class.getMethods()) {
			if (method.getReturnType() == int.class && method.getParameterCount() == 0) {
				clientboundUpdateAttributesPacketGetEntityIdMethod = method;
				break;
			}
		}
		for (Method method : ClientboundSetEquipmentPacket.class.getMethods()) {
			if (method.getReturnType() == int.class && method.getParameterCount() == 0) {
				clientboundSetEquipmentPacketGetEntityIdMethod = method;
				break;
			}
		}
		for (Method method : ClientboundSetEntityDataPacket.class.getMethods()) {
			if (method.getReturnType() == int.class && method.getParameterCount() == 0) {
				clientboundSetEntityDataPacketIdMethod = method;
				break;
			}
		}
		for (Method method : ClientboundAddEntityPacket.class.getMethods()) {
			if (method.getReturnType() == int.class && method.getParameterCount() == 0) {
				clientboundAddEntityPacketGetIdMethod = method;
				break;
			}
		}
		for (Method method : playerInfoDataClass.getMethods()) {
			if (method.getReturnType() == GameProfile.class) {
				playerInfoDataGetGameProfileMethod = method;
				break;
			}
		}

		// net.minecraft.network.syncher - methods
		for (Method method : SynchedEntityData.class.getMethods()) {
			if (method.getParameterCount() == 0 && method.getReturnType() == List.class) {
				synchedEntityDataPackMethod = method;
				break;
			}
		}

		// net.minecraft.server - methods
		if (bukkitVersion.compareTo("1.17.1-R0.1-SNAPSHOT") <= 0) {
			serverPlayerSendSystemMessageMethod = ServerPlayer.class.getMethod("sendMessage", Component.class, UUID.class);
		} else if (bukkitVersion.compareTo("1.18.2-R0.1-SNAPSHOT") <= 0) {
			serverPlayerSendSystemMessageMethod = ServerPlayer.class.getMethod("a", Component.class, UUID.class);
		} else {
			serverPlayerSendSystemMessageMethod = ServerPlayer.class.getMethod("a", Component.class);
		}

		// net.minecraft.world - methods
		for (Method method : entityLivingClass.getMethods()) {
			if (method.getReturnType() == CombatTracker.class && method.getParameterCount() == 0) {
				entityLivingGetCombatTrackerMethod = method;
				break;
			}
		}
		for (Method method : entityClass.getMethods()) {
			if (method.getParameterCount() != 5)
				continue;

			Class<?>[] paramTypes = method.getParameterTypes();
			if (paramTypes[0] == double.class && paramTypes[1] == double.class && paramTypes[2] == double.class && paramTypes[3] == float.class && paramTypes[4] == float.class) {
				entitySetLocationMethod = method;
				break;
			}
		}
		for (Method method : entityClass.getMethods()) {
			if (method.getParameterCount() == 0 && method.getReturnType() == net.minecraft.world.level.Level.class) {
				entityLevelMethod = method;
				break;
			}
		}
		for (Method method : entityClass.getMethods()) {
			if (method.getParameterCount() == 0 && method.getReturnType() == SynchedEntityData.class) {
				entityGetEntityDataMethod = method;
				break;
			}
		}
		entityGetBukkitEntityMethod = entityClass.getMethod("getBukkitEntity");
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
		if (bukkitVersion.compareTo("1.17.1-R0.1-SNAPSHOT") <= 0) {
			entitySetInvisibleMethod = entityClass.getMethod("setInvisible", boolean.class);
		} else {
			entitySetInvisibleMethod = entityClass.getMethod("j", boolean.class);
		}
		for (Method method : VillagerDataHolder.class.getMethods()) {
			if (method.getParameterCount() != 1)
				continue;

			if (method.getParameterTypes()[0] == net.minecraft.world.entity.npc.VillagerData.class) {
				entityVillagerSetVillagerDataMethod = method;
				break;
			}
		}
		if (bukkitVersion.compareTo("1.17.1-R0.1-SNAPSHOT") <= 0) {
			mobTickMethod = net.minecraft.world.entity.Mob.class.getMethod("tick");
		} else if (bukkitVersion.compareTo("1.19.2-R0.1-SNAPSHOT") <= 0) {
			mobTickMethod = net.minecraft.world.entity.Mob.class.getMethod("k");
		} else {
			mobTickMethod = net.minecraft.world.entity.Mob.class.getMethod("l");
		}
		levelGetWorldMethod = net.minecraft.world.level.Level.class.getMethod("getWorld");
		for (Method method : playerConnectionClass.getMethods()) {
			if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == packetClass) {
				playerConnectionSendPacketMethod = method;
				break;
			}
		}
		for (Method method : entityEnderDragonClass.getMethods()) {
			if (method.getParameterCount() == 0 && method.getReturnType() == net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager.class) {
				entityEnderDragonGetPhaseManagerMethod = method;
				break;
			}
		}
		for (Method method : enderDragonPhaseManagerClass.getMethods()) {
			if (method.getParameterCount() != 1)
				continue;

			if (method.getParameterTypes()[0] != net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase.class)
				continue;

			if (method.getReturnType().equals(Void.TYPE)) {
				dragonPhaseManagerSetPhaseMethod = method;
				break;
			}
		}
		for (Method method : enderDragonPhaseManagerClass.getMethods()) {
			if (method.getParameterCount() == 0 && method.getReturnType() == net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance.class) {
				dragonPhaseManagerGetCurrentPhaseMethod = method;
				break;
			}
		}
		for (Method method : dragonStrafePlayerPhaseClass.getMethods()) {
			if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == entityLivingClass) {
				dragonStrafePlayerPhaseSetTargetMethod = method;
				break;
			}
		}
		for (Method method : dragonChargePlayerPhaseClass.getMethods()) {
			if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == vec3DClass) {
				dragonChargePlayerPhaseSetTargetMethod = method;
				break;
			}
		}
		for (Method method : net.minecraft.world.item.ItemStack.class.getMethods()) {
			if (method.getReturnType() != net.minecraft.nbt.CompoundTag.class)
				continue;

			if (method.getParameterCount() != 0)
				continue;

			if (method.getAnnotationsByType(Nullable.class).length == 0) {
				itemStackGetOrCreateTagMethod = method;
				break;
			}
		}
		for (Method method : net.minecraft.world.item.ItemStack.class.getMethods()) {
			if (!method.getReturnType().equals(Void.TYPE))
				continue;

			if (method.getParameterCount() != 1)
				continue;

			if (method.getParameterTypes()[0] != net.minecraft.nbt.CompoundTag.class)
				continue;

			Annotation[][] paramAnnotations = method.getParameterAnnotations();
			if (paramAnnotations[0].length != 1)
				continue;

			if (paramAnnotations[0][0].annotationType() == Nullable.class) {
				itemStackSetTagMethod = method;
				break;
			}
		}
		if (bukkitVersion.compareTo("1.17.1-R0.1-SNAPSHOT") <= 0) {
			itemStackHasTagMethod = net.minecraft.world.item.ItemStack.class.getMethod("hasTag");
		} else if (bukkitVersion.compareTo("1.18.1-R0.1-SNAPSHOT") <= 0) {
			itemStackHasTagMethod = net.minecraft.world.item.ItemStack.class.getMethod("r");
		} else if (bukkitVersion.compareTo("1.18.2-R0.1-SNAPSHOT") <= 0) {
			itemStackHasTagMethod = net.minecraft.world.item.ItemStack.class.getMethod("s");
		} else if (bukkitVersion.compareTo("1.19.4-R0.1-SNAPSHOT") <= 0) {
			itemStackHasTagMethod = net.minecraft.world.item.ItemStack.class.getMethod("t");
		} else {
			itemStackHasTagMethod = net.minecraft.world.item.ItemStack.class.getMethod("u");
		}
		//itemStackGetTagMethod = itemStackClass.getMethod("getTag");
		if (bukkitVersion.compareTo("1.17.1-R0.1-SNAPSHOT") <= 0) {
			entityArmorStandSetSmallMethod = net.minecraft.world.entity.decoration.ArmorStand.class.getMethod("setSmall", boolean.class);
		} else if (bukkitVersion.compareTo("1.19.3-R0.1-SNAPSHOT") <= 0) {
			entityArmorStandSetSmallMethod = net.minecraft.world.entity.decoration.ArmorStand.class.getMethod("a", boolean.class);
		} else {
			entityArmorStandSetSmallMethod = net.minecraft.world.entity.decoration.ArmorStand.class.getMethod("t", boolean.class);
		}
		for (Method method : CombatTracker.class.getMethods()) {
			if (method.getParameterCount() == 0 && method.getReturnType() == Component.class) {
				combatTrackerGetDeathMessageMethod = method;
				break;
			}
		}

		// net.minecraft.nbt - methods
		if (bukkitVersion.compareTo("1.17.1-R0.1-SNAPSHOT") <= 0) {
			compoundTagHasKeyMethod = net.minecraft.nbt.CompoundTag.class.getMethod("hasKey", String.class);
		} else {
			compoundTagHasKeyMethod = net.minecraft.nbt.CompoundTag.class.getMethod("e", String.class);
		}
		for (Method method : net.minecraft.nbt.CompoundTag.class.getMethods()) {
			if (!method.getReturnType().equals(Void.TYPE))
				continue;

			if (method.getParameterCount() != 2)
				continue;

			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes[0] == String.class && parameterTypes[1] == int.class) {
				compoundTagPutIntMethod = method;
				break;
			}
		}

		// net.minecraft.network.protocol - constructors
		clientboundTeleportEntityPacketConstructor = clientboundTeleportEntityPacketClass.getConstructor(entityClass);
		clientboundPlayerInfoPacketConstructor = clientboundPlayerInfoPacketClass.getConstructor(enumPlayerInfoActionClass, java.lang.reflect.Array.newInstance(entityPlayerClass, 0).getClass());
		//packetPlayOutSpawnEntityLivingConstructor = packetPlayOutSpawnEntityLivingClass.getConstructor(entityLivingClass);
		//clientboundRemoveEntitiesPacketConstructor = clientboundRemoveEntitiesPacketClass.getConstructor(new int[0].getClass());
		//packetPlayOutNamedEntitySpawnConstructor = packetPlayOutNamedEntitySpawnClass.getConstructor(entityHumanClass);

		// net.minecraft.server - constructors
		serverPlayerConstructor = ServerPlayer.class.getConstructors()[0];

		// net.minecraft.world - constructors
		for (Constructor<?> constructor : entityEnderDragonClass.getConstructors()) {
			if (constructor.getParameterCount() == 2) {
				entityEnderDragonConstructor = constructor;
				break;
			}
		}
		for (Constructor<?> constructor : entityVillagerClass.getConstructors()) {
			if (constructor.getParameterCount() == 2) {
				entityVillagerConstructor = constructor;
				break;
			}
		}
		vec3DConstructor = vec3DClass.getConstructor(double.class, double.class, double.class);

		// net.minecraft.network - fields
		for (Field field : net.minecraft.network.Connection.class.getFields()) {
			if (field.getType() == io.netty.channel.Channel.class && Modifier.isPublic(field.getModifiers())) {
				connectionChannelField = field;
				break;
			}
		}
		for (Field field : entityPlayerClass.getFields()) {
			if (field.getType() == ServerGamePacketListenerImpl.class) {
				entityPlayerPlayerConnectionField = field;
				break;
			}
		}
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
		for (Field field : playerConnectionClass.getFields()) {
			int modifiers = field.getModifiers();

			if (!Modifier.isPublic(modifiers) || !Modifier.isFinal(modifiers))
				continue;

			if (field.getType() == net.minecraft.network.Connection.class) {
				playerConnectionConnectionField = field;
				break;
			}
		}

		//iChatBaseComponentClass = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
		//chatComponentTextClass = Class.forName("net.minecraft.server." + serverVersion + ".ChatComponentText");
		//villagerDataClass = Class.forName("net.minecraft.world.entity.npc.VillagerData");
		//damageSourceClass = Class.forName("net.minecraft.server." + serverVersion + ".DamageSource");
		//combatTrackerClass = Class.forName("net.minecraft.server." + serverVersion + ".CombatTracker");
		//damageSourceDamageEntityMethod = entityPlayerClass.getMethod("damageEntity", damageSourceClass, float.class);
		//iChatBaseComponentAddSiblingMethod = iChatBaseComponentClass.getMethod("addSibling", iChatBaseComponentClass);
		//entityPlayerGetCombatTrackerMethod = entityPlayerClass.getMethod("getCombatTracker");
		//entityPlayerSendMessageMethod = entityPlayerClass.getMethod("sendMessage", iChatBaseComponentClass);
		//entityTypesEnderDragonField = entityTypesClass.getField("ENDER_DRAGON");
		//dragonControllerPhaseStrafePlayerField = dragonControllerPhaseClass.getField("STRAFE_PLAYER");
		//dragonControllerPhaseHoldingPatternField = dragonControllerPhaseClass.getField("HOLDING_PATTERN");
		//dragonControllerPhaseChargingPlayerField = dragonControllerPhaseClass.getField("CHARGING_PLAYER");
		//dragonControllerPhaseLandingField = dragonControllerPhaseClass.getField("LANDING");
		//dragonControllerPhaseLandingApproachField = dragonControllerPhaseClass.getField("LANDING_APPROACH");
		//itemStackClass = Class.forName("net.minecraft.server." + serverVersion + ".ItemStack");
		//nbtTagCompoundClass = Class.forName("net.minecraft.server." + serverVersion + ".NBTTagCompound");
		//nbtTagIntClass = Class.forName("net.minecraft.server." + serverVersion + ".NBTTagInt");
		//nbtBaseClass = Class.forName("net.minecraft.server." + serverVersion + ".NBTBase");
		//nbtTagIntConstructor = nbtTagIntClass.getDeclaredConstructor(int.class);
		//nbtTagIntConstructor.setAccessible(true);
		//nbtTagCompoundSetMethod = nbtTagCompoundClass.getMethod("set", String.class, nbtBaseClass);
		//chatComponentConstructor = chatComponentTextClass.getConstructor(String.class);
		//villagerDataConstructor = villagerDataClass.getConstructor(villagerTypeClass, villagerProfessionClass, int.class);
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

	public net.minecraft.world.entity.Entity entityToNMSEntity(Entity entity) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// return ((CraftEntity)entity).getHandle();
		Object craftEntity = craftEntityClass.cast(entity);
		return (net.minecraft.world.entity.Entity)craftEntityGetHandleMethod.invoke(craftEntity);
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

	public void nmsPlayerSendSystemMessage(ServerPlayer nmsPlayer, Component message) {
		String bukkitVersion = Bukkit.getBukkitVersion();

		try {
			if (bukkitVersion.compareTo("1.18.2-R0.1-SNAPSHOT") <= 0)
				serverPlayerSendSystemMessageMethod.invoke(nmsPlayer, message, new UUID(0, 0));
			else
				serverPlayerSendSystemMessageMethod.invoke(nmsPlayer, message);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public ServerGamePacketListenerImpl playerGetConnection(Player player) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		ServerPlayer nmsPlayer = playerToNMSPlayer(player);
		return (ServerGamePacketListenerImpl)entityPlayerPlayerConnectionField.get(nmsPlayer);
	}

	public void playerSendPacket(Player player, Packet<?> packet) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ServerGamePacketListenerImpl connection = playerGetConnection(player);
		playerConnectionSendPacketMethod.invoke(connection, packet);
	}

}
