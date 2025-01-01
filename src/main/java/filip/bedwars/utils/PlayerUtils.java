package filip.bedwars.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import filip.bedwars.BedwarsPlugin;
import net.minecraft.server.level.ServerPlayer;

public class PlayerUtils {

	public static void damagePlayerVoid(Player player, float amount) {
		final String bukkitVersion = Bukkit.getBukkitVersion();

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
