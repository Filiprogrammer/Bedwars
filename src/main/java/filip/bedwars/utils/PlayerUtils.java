package filip.bedwars.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import filip.bedwars.BedwarsPlugin;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.damagesource.DamageSource;

public class PlayerUtils {

	public static void hidePlayerEntity(Player toHide, Player viewer) {
		try {
			int toHideEntityId = ((CraftPlayer) toHide).getHandle().getId();
			//Object toHideCraftPlayer = BedwarsPlugin.getInstance().reflectionUtils.craftPlayerClass.cast(toHide);
			//Object toHideEntityPlayer = BedwarsPlugin.getInstance().reflectionUtils.craftPlayerGetHandleMethod.invoke(toHideCraftPlayer);
			//int toHideEntityId = (int) BedwarsPlugin.getInstance().reflectionUtils.entityGetIdMethod.invoke(toHideEntityPlayer);
			
			// viewerConnection.sendPacket(new PacketPlayOutEntityDestroy(toHideEntityId));
			ClientboundRemoveEntitiesPacket packetPlayOutEntityDestroy = new ClientboundRemoveEntitiesPacket(toHideEntityId);
			//Object packetPlayOutEntityDestroy = BedwarsPlugin.getInstance().reflectionUtils.packetPlayOutEntityDestroyConstructor.newInstance(new int[] {toHideEntityId});
			sendPacket(viewer, packetPlayOutEntityDestroy);
		} catch (SecurityException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	public static void showPlayerEntity(Player toHide, Player viewer) {
		try {
			// EntityPlayer toHideEntityPlayer = ((CraftPlayer) toHide).getHandle();
			//Object toHideCraftPlayer = BedwarsPlugin.getInstance().reflectionUtils.craftPlayerClass.cast(toHide);
			//Object toHideEntityPlayer = BedwarsPlugin.getInstance().reflectionUtils.craftPlayerGetHandleMethod.invoke(toHideCraftPlayer);
			ServerPlayer toHideEntityPlayer = ((CraftPlayer) toHide).getHandle();
			
			// viewerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(toHideEntityPlayer));
			ClientboundAddEntityPacket packetPlayOutNamedEntitySpawn = new ClientboundAddEntityPacket(toHideEntityPlayer);
			//Object packetPlayOutNamedEntitySpawn = BedwarsPlugin.getInstance().reflectionUtils.packetPlayOutNamedEntitySpawnConstructor.newInstance(toHideEntityPlayer);
			sendPacket(viewer, packetPlayOutNamedEntitySpawn);
		} catch (SecurityException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	public static void hidePlayer(Player toHide, Player viewer) {
		try {
			/*Object entityPlayerArray = java.lang.reflect.Array.newInstance(BedwarsPlugin.getInstance().reflectionUtils.entityPlayerClass, 1);
			java.lang.reflect.Array.set(entityPlayerArray, 0, BedwarsPlugin.getInstance().reflectionUtils.craftPlayerGetHandleMethod.invoke(BedwarsPlugin.getInstance().reflectionUtils.craftPlayerClass.cast(toHide)));
			Object packetPlayOutPlayerInfo = BedwarsPlugin.getInstance().reflectionUtils.packetPlayOutPlayerInfoConstructor.newInstance(
					Enum.valueOf((Class<Enum>)BedwarsPlugin.getInstance().reflectionUtils.enumPlayerInfoActionClass, "REMOVE_PLAYER"),
					entityPlayerArray);*/
			ClientboundPlayerInfoRemovePacket packetPlayOutPlayerInfo = new ClientboundPlayerInfoRemovePacket(List.of(toHide.getUniqueId()));
			
			sendPacket(viewer, packetPlayOutPlayerInfo);
		} catch (SecurityException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	public static void damagePlayer(Player player, DamageSource cause, float amount) {
		try {
			//Field damageSourceField = BedwarsPlugin.getInstance().reflectionUtils.damageSourceClass.getField(cause);

			ServerPlayer entityPlayer = ((CraftPlayer)player).getHandle();
			//Object craftPlayer = BedwarsPlugin.getInstance().reflectionUtils.craftPlayerClass.cast(player);
			//Object entityPlayer = BedwarsPlugin.getInstance().reflectionUtils.craftPlayerGetHandleMethod.invoke(craftPlayer);
			entityPlayer.hurt(cause, amount);
			//BedwarsPlugin.getInstance().reflectionUtils.damageSourceDamageEntityMethod.invoke(entityPlayer, damageSourceField.get(null), amount);
		} catch (SecurityException | IllegalArgumentException e) {
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
	
	private static void sendPacket(Player player, net.minecraft.network.protocol.Packet<?> packet) {
	    try {
			ServerPlayer handle = ((CraftPlayer)player).getHandle();
	    	//Object handle = BedwarsPlugin.getInstance().reflectionUtils.craftPlayerGetHandleMethod.invoke(player);
			ServerGamePacketListenerImpl playerConnection = handle.connection;
	    	//Object playerConnection = BedwarsPlugin.getInstance().reflectionUtils.entityPlayerPlayerConnectionField.get(handle);
			
			playerConnection.send(packet);
	    	//BedwarsPlugin.getInstance().reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
	    } catch (IllegalArgumentException e) {
	        e.printStackTrace();
	    }
	}
	
}
