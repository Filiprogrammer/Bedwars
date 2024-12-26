package filip.bedwars.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import filip.bedwars.BedwarsPlugin;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ServerPlayer;

public class PlayerUtils {

	public static void hidePlayerEntity(Player toHide, Player viewer) {
		try {
			int toHideEntityId = toHide.getEntityId();
			ClientboundRemoveEntitiesPacket packetPlayOutEntityDestroy = new ClientboundRemoveEntitiesPacket(toHideEntityId);
			BedwarsPlugin.getInstance().reflectionUtils.playerSendPacket(viewer, packetPlayOutEntityDestroy);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void showPlayerEntity(Player toShow, Player viewer) {
		try {
			ServerPlayer toShowEntityPlayer = BedwarsPlugin.getInstance().reflectionUtils.playerToNMSPlayer(toShow);
			ClientboundAddEntityPacket packetPlayOutNamedEntitySpawn = new ClientboundAddEntityPacket(toShowEntityPlayer);
			BedwarsPlugin.getInstance().reflectionUtils.playerSendPacket(viewer, packetPlayOutNamedEntitySpawn);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void hidePlayer(Player toHide, Player viewer) {
		String bukkitVersion = Bukkit.getBukkitVersion();

		try {
			/*Object entityPlayerArray = java.lang.reflect.Array.newInstance(BedwarsPlugin.getInstance().reflectionUtils.entityPlayerClass, 1);
			java.lang.reflect.Array.set(entityPlayerArray, 0, BedwarsPlugin.getInstance().reflectionUtils.craftPlayerGetHandleMethod.invoke(BedwarsPlugin.getInstance().reflectionUtils.craftPlayerClass.cast(toHide)));
			Object packetPlayOutPlayerInfo = BedwarsPlugin.getInstance().reflectionUtils.packetPlayOutPlayerInfoConstructor.newInstance(
					Enum.valueOf((Class<Enum>)BedwarsPlugin.getInstance().reflectionUtils.enumPlayerInfoActionClass, "REMOVE_PLAYER"),
					entityPlayerArray);*/
			//ClientboundPlayerInfoRemovePacket packetPlayOutPlayerInfo = new ClientboundPlayerInfoRemovePacket(List.of(toHide.getUniqueId()));
			Packet<?> packet;
			if (bukkitVersion.compareTo("1.19.3-R0.1-SNAPSHOT") >= 0) {
				packet = new ClientboundPlayerInfoRemovePacket(List.of(toHide.getUniqueId()));
			} else {
				ServerPlayer nmsToHide = BedwarsPlugin.getInstance().reflectionUtils.playerToNMSPlayer(toHide);
				packet = (Packet<?>)BedwarsPlugin.getInstance().reflectionUtils.packetPlayOutPlayerInfoConstructor.newInstance(Enum.valueOf((Class<Enum>)BedwarsPlugin.getInstance().reflectionUtils.enumPlayerInfoActionClass, "REMOVE_PLAYER"), new ServerPlayer[]{nmsToHide});
			}

			BedwarsPlugin.getInstance().reflectionUtils.playerSendPacket(viewer, packet);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			e.printStackTrace();
		}
	}

	public static void damagePlayerVoid(Player player, float amount) {
		String bukkitVersion = Bukkit.getBukkitVersion();

		if (bukkitVersion.compareTo("1.20.4-R0.1-SNAPSHOT") >= 0) {
			player.damage(amount, DamageSource.builder(DamageType.OUT_OF_WORLD).build());
			return;
		}

		ServerPlayer entityPlayer;
		try {
			entityPlayer = BedwarsPlugin.getInstance().reflectionUtils.playerToNMSPlayer(player);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return;
		}

		switch (bukkitVersion) {
			case "1.17.1-R0.1-SNAPSHOT":
				try {
					// DamageSource damageSource = DamageSource.OUT_OF_WORLD;
					Field damageSourceField = net.minecraft.world.damagesource.DamageSource.class.getField("m");
					net.minecraft.world.damagesource.DamageSource damageSource = (net.minecraft.world.damagesource.DamageSource)damageSourceField.get(null);

					//entityPlayer.hurt(damageSource, amount);
					Method hurtMethod = ServerPlayer.class.getMethod("damageEntity", net.minecraft.world.damagesource.DamageSource.class, float.class);
					hurtMethod.invoke(entityPlayer, damageSource, amount);
				} catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
				break;
			case "1.18-R0.1-SNAPSHOT":
			case "1.18.1-R0.1-SNAPSHOT":
			case "1.18.2-R0.1-SNAPSHOT":
			case "1.19-R0.1-SNAPSHOT":
			case "1.19.1-R0.1-SNAPSHOT":
			case "1.19.2-R0.1-SNAPSHOT":
			case "1.19.3-R0.1-SNAPSHOT":
				try {
					// DamageSource damageSource = DamageSource.OUT_OF_WORLD;
					Field damageSourceField = net.minecraft.world.damagesource.DamageSource.class.getField("m");
					net.minecraft.world.damagesource.DamageSource damageSource = (net.minecraft.world.damagesource.DamageSource)damageSourceField.get(null);

					//entityPlayer.hurt(damageSource, amount);
					Method hurtMethod = ServerPlayer.class.getMethod("a", net.minecraft.world.damagesource.DamageSource.class, float.class);
					hurtMethod.invoke(entityPlayer, damageSource, amount);
				} catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
				break;
			case "1.19.4-R0.1-SNAPSHOT":
			case "1.20-R0.1-SNAPSHOT":
			case "1.20.1-R0.1-SNAPSHOT":
			case "1.20.2-R0.1-SNAPSHOT":
			case "1.20.3-R0.1-SNAPSHOT":
				entityPlayer.hurt(new net.minecraft.world.damagesource.DamageSources(net.minecraft.core.RegistryAccess.EMPTY).fellOutOfWorld(), amount);
				break;
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

}
