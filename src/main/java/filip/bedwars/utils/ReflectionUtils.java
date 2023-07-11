package filip.bedwars.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.inventory.ItemStack;

import filip.bedwars.BedwarsPlugin;

public class ReflectionUtils {

	public Class<?> craftWorldClass;
	public Class<?> craftPlayerClass;
	public Class<?> dragonControllerPhaseClass;
	public Class<?> dragonControllerStrafeClass;
	public Class<?> dragonControllerChargeClass;
	public Class<?> dragonControllerManagerClass;
	public Class<?> entityEnderDragonClass;
	public Class<?> entityLivingClass;
	public Class<?> entityPlayerClass;
	public Class<?> entityClass;
	public Class<?> entityTypesClass;
	public Class<?> packetClass;
	public Class<?> packetPlayOutEntityDestroyClass;
	public Class<?> packetPlayOutEntityTeleportClass;
	public Class<?> packetPlayOutSpawnEntityLivingClass;
	public Class<?> playerConnectionClass;
	public Class<?> vec3DClass;
	public Class<?> worldClass;
	public Class<?> itemStackClass;
	public Class<?> nbtTagCompoundClass;
	public Class<?> craftItemStackClass;
	public Class<?> nbtTagIntClass;
	public Class<?> nbtBaseClass;
	public Class<?> entityVillagerClass;
	public Class<?> iChatBaseComponentClass;
	public Class<?> chatComponentTextClass;
	public Class<?> villagerDataClass;
	public Class<?> villagerTypeClass;
	public Class<?> villagerProfessionClass;
	public Class<?> entityHumanClass;
	public Class<?> packetPlayOutPlayerInfoClass;
	public Class<?> enumPlayerInfoActionClass;
	public Class<?> packetPlayOutNamedEntitySpawnClass;
	public Class<?> damageSourceClass;
	public Class<?> combatTrackerClass;
	public Method entityEnderDragonTickMethod;
	public Method entitySetLocationMethod;
	public Method entityGetIdMethod;
	public Method entityGetWorldMethod;
	public Method craftWorldGetHandleMethod;
	public Method craftWorldGetNameMethod;
	public Method craftPlayerGetHandleMethod;
	public Method worldGetWorldMethod;
	public Method playerConnectionSendPacketMethod;
	public Method entityEnderDragonGetDragonControllerManagerMethod;
	public Method dragonControllerManagerSetControllerPhaseMethod;
	public Method dragonControllerManagerBMethod;
	public Method dragonControllerStrafeAMethod;
	public Method dragonControllerChargeAMethod;
	public Method craftItemStackAsNMSCopyMethod;
	public Method craftItemStackAsBukkitCopyMethod;
	public Method itemStackGetOrCreateTagMethod;
	public Method nbtTagCompoundSetMethod;
	public Method nbtTagCompoundHasKeyMethod;
	public Method itemStackSetTagMethod;
	public Method itemStackHasTagMethod;
	public Method itemStackGetTagMethod;
	public Method entitySetCustomNameMethod;
	public Method entitySetCustomNameVisibleMethod;
	public Method entityVillagerSetVillagerDataMethod;
	public Method damageSourceDamageEntityMethod;
	public Method iChatBaseComponentAddSiblingMethod;
	public Method entityPlayerGetCombatTrackerMethod;
	public Method entityPlayerSendMessageMethod;
	public Method combatTrackerGetDeathMessageMethod;
	public Field entityPlayerPlayerConnectionField;
	public Field entityTypesEnderDragonField;
	public Field entityLocXField;
	public Field entityLocYField;
	public Field entityLocZField;
	public Field dragonControllerPhaseStrafePlayerField;
	public Field dragonControllerPhaseHoldingPatternField;
	public Field dragonControllerPhaseChargingPlayerField;
	public Field dragonControllerPhaseLandingField;
	public Field dragonControllerPhaseLandingApproachField;
	public Field entityTypesVillagerField;
	public Constructor<?> packetPlayOutSpawnEntityLivingConstructor;
	public Constructor<?> packetPlayOutEntityDestroyConstructor;
	public Constructor<?> entityEnderDragonConstructor;
	public Constructor<?> packetPlayOutEntityTeleportConstructor;
	public Constructor<?> vec3DConstructor;
	public Constructor<?> nbtTagIntConstructor;
	public Constructor<?> entityVillagerConstructor;
	public Constructor<?> chatComponentConstructor;
	public Constructor<?> villagerDataConstructor;
	public Constructor<?> packetPlayOutPlayerInfoConstructor;
	public Constructor<?> packetPlayOutNamedEntitySpawnConstructor;
	
	public ReflectionUtils() {
		String serverVersion = BedwarsPlugin.getInstance().getServerVersion();
		try {
			craftWorldClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".CraftWorld");
			craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".entity.CraftPlayer");
			dragonControllerPhaseClass = Class.forName("net.minecraft.server." + serverVersion + ".DragonControllerPhase");
			dragonControllerStrafeClass = Class.forName("net.minecraft.server." + serverVersion + ".DragonControllerStrafe");
			dragonControllerChargeClass = Class.forName("net.minecraft.server." + serverVersion + ".DragonControllerCharge");
			dragonControllerManagerClass = Class.forName("net.minecraft.server." + serverVersion + ".DragonControllerManager");
			entityEnderDragonClass = Class.forName("net.minecraft.server." + serverVersion + ".EntityEnderDragon");
			entityLivingClass = Class.forName("net.minecraft.server." + serverVersion + ".EntityLiving");
			entityPlayerClass = Class.forName("net.minecraft.server." + serverVersion + ".EntityPlayer");
			entityClass = Class.forName("net.minecraft.server." + serverVersion + ".Entity");
			entityTypesClass = Class.forName("net.minecraft.server." + serverVersion + ".EntityTypes");
			packetClass = Class.forName("net.minecraft.server." + serverVersion + ".Packet");
			packetPlayOutEntityDestroyClass = Class.forName("net.minecraft.server." + serverVersion + ".PacketPlayOutEntityDestroy");
			packetPlayOutEntityTeleportClass = Class.forName("net.minecraft.server." + serverVersion + ".PacketPlayOutEntityTeleport");
			packetPlayOutSpawnEntityLivingClass = Class.forName("net.minecraft.server." + serverVersion + ".PacketPlayOutSpawnEntityLiving");
			playerConnectionClass = Class.forName("net.minecraft.server." + serverVersion + ".PlayerConnection");
			vec3DClass = Class.forName("net.minecraft.server." + serverVersion + ".Vec3D");
			worldClass = Class.forName("net.minecraft.server." + serverVersion + ".World");
			entityVillagerClass = Class.forName("net.minecraft.server." + serverVersion + ".EntityVillager");
			iChatBaseComponentClass = Class.forName("net.minecraft.server." + serverVersion + ".IChatBaseComponent");
			chatComponentTextClass = Class.forName("net.minecraft.server." + serverVersion + ".ChatComponentText");
			villagerDataClass = Class.forName("net.minecraft.server." + serverVersion + ".VillagerData");
			villagerTypeClass = Class.forName("net.minecraft.server." + serverVersion + ".VillagerType");
			villagerProfessionClass = Class.forName("net.minecraft.server." + serverVersion + ".VillagerProfession");
			entityHumanClass = Class.forName("net.minecraft.server." + serverVersion + ".EntityHuman");
			packetPlayOutPlayerInfoClass = Class.forName("net.minecraft.server." + serverVersion + ".PacketPlayOutPlayerInfo");
			enumPlayerInfoActionClass = Class.forName("net.minecraft.server." + serverVersion + ".PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
			packetPlayOutNamedEntitySpawnClass = Class.forName("net.minecraft.server." + serverVersion + ".PacketPlayOutNamedEntitySpawn");
			damageSourceClass = Class.forName("net.minecraft.server." + serverVersion + ".DamageSource");
			combatTrackerClass = Class.forName("net.minecraft.server." + serverVersion + ".CombatTracker");
			entityEnderDragonTickMethod = entityEnderDragonClass.getMethod("tick");
			entitySetLocationMethod = entityClass.getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
			entityGetIdMethod = entityClass.getMethod("getId");
			entityGetWorldMethod = entityClass.getMethod("getWorld");
			craftWorldGetHandleMethod = craftWorldClass.getMethod("getHandle");
			craftWorldGetNameMethod = craftWorldClass.getMethod("getName");
			craftPlayerGetHandleMethod = craftPlayerClass.getMethod("getHandle");
			worldGetWorldMethod = worldClass.getMethod("getWorld");
			playerConnectionSendPacketMethod = playerConnectionClass.getMethod("sendPacket", packetClass);
			entityEnderDragonGetDragonControllerManagerMethod = entityEnderDragonClass.getMethod("getDragonControllerManager");
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
			entitySetCustomNameMethod = entityClass.getMethod("setCustomName", iChatBaseComponentClass);
			entitySetCustomNameVisibleMethod = entityClass.getMethod("setCustomNameVisible", boolean.class);
			entityVillagerSetVillagerDataMethod = entityVillagerClass.getMethod("setVillagerData", villagerDataClass);
			damageSourceDamageEntityMethod = entityPlayerClass.getMethod("damageEntity", damageSourceClass, float.class);
			iChatBaseComponentAddSiblingMethod = iChatBaseComponentClass.getMethod("addSibling", iChatBaseComponentClass);
			entityPlayerGetCombatTrackerMethod = entityPlayerClass.getMethod("getCombatTracker");
			entityPlayerSendMessageMethod = entityPlayerClass.getMethod("sendMessage", iChatBaseComponentClass);
			combatTrackerGetDeathMessageMethod = combatTrackerClass.getMethod("getDeathMessage");
			entityPlayerPlayerConnectionField = entityPlayerClass.getField("playerConnection");
			entityTypesEnderDragonField = entityTypesClass.getField("ENDER_DRAGON");
			entityLocXField = entityClass.getDeclaredField("locX");
			entityLocXField.setAccessible(true);
			entityLocYField = entityClass.getDeclaredField("locY");
			entityLocYField.setAccessible(true);
			entityLocZField = entityClass.getDeclaredField("locZ");
			entityLocZField.setAccessible(true);
			dragonControllerPhaseStrafePlayerField = dragonControllerPhaseClass.getField("STRAFE_PLAYER");
			dragonControllerPhaseHoldingPatternField = dragonControllerPhaseClass.getField("HOLDING_PATTERN");
			dragonControllerPhaseChargingPlayerField = dragonControllerPhaseClass.getField("CHARGING_PLAYER");
			dragonControllerPhaseLandingField = dragonControllerPhaseClass.getField("LANDING");
			dragonControllerPhaseLandingApproachField = dragonControllerPhaseClass.getField("LANDING_APPROACH");
			entityTypesVillagerField = entityTypesClass.getField("VILLAGER");
			packetPlayOutSpawnEntityLivingConstructor = packetPlayOutSpawnEntityLivingClass.getConstructor(entityLivingClass);
			packetPlayOutEntityDestroyConstructor = packetPlayOutEntityDestroyClass.getConstructor(new int[0].getClass());
			for (Constructor<?> constructor : entityEnderDragonClass.getConstructors()) {
				if (constructor.getParameterCount() == 2) {
					entityEnderDragonConstructor = constructor;
					break;
				}
			}
			packetPlayOutEntityTeleportConstructor = packetPlayOutEntityTeleportClass.getConstructor(entityClass);
			vec3DConstructor = vec3DClass.getConstructor(double.class, double.class, double.class);
			itemStackClass = Class.forName("net.minecraft.server." + serverVersion + ".ItemStack");
			nbtTagCompoundClass = Class.forName("net.minecraft.server." + serverVersion + ".NBTTagCompound");
			craftItemStackClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".inventory.CraftItemStack");
			craftItemStackAsNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
			craftItemStackAsBukkitCopyMethod = craftItemStackClass.getMethod("asBukkitCopy", itemStackClass);
			itemStackGetOrCreateTagMethod = itemStackClass.getMethod("getOrCreateTag");
			nbtTagIntClass = Class.forName("net.minecraft.server." + serverVersion + ".NBTTagInt");
			nbtBaseClass = Class.forName("net.minecraft.server." + serverVersion + ".NBTBase");
			nbtTagIntConstructor = nbtTagIntClass.getDeclaredConstructor(int.class);
			nbtTagIntConstructor.setAccessible(true);
			nbtTagCompoundSetMethod = nbtTagCompoundClass.getMethod("set", String.class, nbtBaseClass);
			nbtTagCompoundHasKeyMethod = nbtTagCompoundClass.getMethod("hasKey", String.class);
			itemStackSetTagMethod = itemStackClass.getMethod("setTag", nbtTagCompoundClass);
			itemStackHasTagMethod = itemStackClass.getMethod("hasTag");
			itemStackGetTagMethod = itemStackClass.getMethod("getTag");
			entityVillagerConstructor = null;
			for (Constructor<?> constructor : entityVillagerClass.getConstructors()) {
				if (constructor.getParameterCount() == 2) {
					entityVillagerConstructor = constructor;
					break;
				}
			}
			chatComponentConstructor = chatComponentTextClass.getConstructor(String.class);
			villagerDataConstructor = villagerDataClass.getConstructor(villagerTypeClass, villagerProfessionClass, int.class);
			packetPlayOutPlayerInfoConstructor = packetPlayOutPlayerInfoClass.getConstructor(enumPlayerInfoActionClass, java.lang.reflect.Array.newInstance(entityPlayerClass, 0).getClass());
			packetPlayOutNamedEntitySpawnConstructor = packetPlayOutNamedEntitySpawnClass.getConstructor(entityHumanClass);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
}
