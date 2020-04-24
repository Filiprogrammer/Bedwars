package filip.bedwars.utils;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import filip.bedwars.BedwarsPlugin;
import net.minecraft.server.v1_14_R1.DragonControllerHold;
import net.minecraft.server.v1_14_R1.DragonControllerLanding;
import net.minecraft.server.v1_14_R1.DragonControllerLandingFly;
import net.minecraft.server.v1_14_R1.DragonControllerPhase;
import net.minecraft.server.v1_14_R1.EntityEnderDragon;
import net.minecraft.server.v1_14_R1.EntityLiving;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_14_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_14_R1.PlayerConnection;
import net.minecraft.server.v1_14_R1.Vec3D;
import net.minecraft.server.v1_14_R1.WorldServer;

public class EnderDragonController {

	private final List<Entity> targetEntities;
	private final Set<Player> viewers;
	private BukkitTask task;
	private BukkitRunnable bukkitRunnable;
	private EntityEnderDragon dragon;
	
	private Entity currentTargetEntity;
	private int dragonPhase;
	private Random random = new Random();
	private final Location spawnLoc;
	
	public EnderDragonController(Location loc, List<Entity> targetEntities, Set<Player> viewers) {
		this.targetEntities = targetEntities;
		this.viewers = viewers;
		this.spawnLoc = loc;
		spawn(loc);
		runTask();
	}
	
	public void addTargetEntity(Entity entity) {
		targetEntities.add(entity);
	}
	
	public boolean removeTargetEntity(Entity entity) {
		return targetEntities.remove(entity);
	}
	
	public void addViewer(Player viewer) {
		viewers.add(viewer);
		respawn(viewer);
	}
	
	public boolean removeViewer(Player viewer) {
		return viewers.remove(viewer);
	}
	
	public void stopTask() {
		if (isTaskRunning()) {
			bukkitRunnable.cancel();
			task.cancel();
			task = null;
		}
	}
	
	public void runTask() {
		if (isTaskRunning())
			return;
		
		if (targetEntities.size() == 0)
			currentTargetEntity = null;
		else
			currentTargetEntity = targetEntities.get(random.nextInt(targetEntities.size()));
		
		bukkitRunnable = new BukkitRunnable() {
			@Override
			public void run() {
				if (random.nextInt(200) == 0) {
					// Choose a random target
					if (targetEntities.size() == 0)
						currentTargetEntity = null;
					else
						currentTargetEntity = targetEntities.get(random.nextInt(targetEntities.size()));
					
					dragonPhase = random.nextInt(5);
				}
				
				if (currentTargetEntity == null) {
					dragonHoldingPattern(spawnLoc);
				} else {
					switch (dragonPhase) {
					case 0:
						dragonHoldingPattern(currentTargetEntity.getLocation());
						break;
					case 1:
						if (currentTargetEntity instanceof EntityLiving)
							dragonStrafePlayer((EntityLiving) currentTargetEntity);
						break;
					case 2:
						dragonChargingPlayer(currentTargetEntity.getLocation());
						break;
					case 3:
						dragonLandingApproach(currentTargetEntity.getLocation());
						break;
					case 4:
						dragonLanding(currentTargetEntity.getLocation());
						break;
					}
				}
				
				dragon.tick();
				updateLocation();
			}
		};
		
		try {
			task = bukkitRunnable.runTaskTimer(BedwarsPlugin.getInstance(), 1L, 1L);
		} catch (IllegalPluginAccessException e) {}
	}
	
	public void respawn(Player... viewers) {
		for (Player p : viewers) {
			EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
			PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(dragon);
			PlayerConnection playerConnection = entityPlayer.playerConnection;
			playerConnection.sendPacket(packet);
		}
	}
	
	public void despawn(Player... viewers) {
		for (Player p : viewers) {
			EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
			PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(dragon.getId());
			PlayerConnection playerConnection = entityPlayer.playerConnection;
			playerConnection.sendPacket(packet);
		}
	}
	
	private boolean isTaskRunning() {
		if (task == null)
			return false;
		
		return !task.isCancelled();
	}
	
	private void spawn(Location loc) {
		WorldServer worldServer = ((CraftWorld)loc.getWorld()).getHandle();
		dragon = new EntityEnderDragon(EntityTypes.ENDER_DRAGON, worldServer);
		dragon.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
		
		respawn(viewers.toArray(new Player[0]));
	}
	
	private void updateLocation() {
		Iterator<Player> iter = viewers.iterator();
		
		while (iter.hasNext()) {
			Player p = iter.next();
			
			if (!p.getWorld().getName().equals(dragon.getWorld().getWorld().getName())) {
				iter.remove();
				continue;
			}
			
			EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
			PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(dragon);
			PlayerConnection playerConnection = entityPlayer.playerConnection;
			playerConnection.sendPacket(packet);
		}
	}
	
	private void dragonHoldingPattern(Location loc) {
		dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.HOLDING_PATTERN);
		DragonControllerHold dragonControllerHold = dragon.getDragonControllerManager().b(DragonControllerPhase.HOLDING_PATTERN);
		try {
			Field dField = Class.forName("net.minecraft.server.v1_14_R1.DragonControllerHold").getDeclaredField("d");
			dField.setAccessible(true);
			dField.set(dragonControllerHold, new Vec3D(loc.getX(), loc.getY(), loc.getZ()));
		} catch (NoSuchFieldException | SecurityException | ClassNotFoundException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private void dragonStrafePlayer(EntityLiving entityLiving) {
		dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.STRAFE_PLAYER);
		dragon.getDragonControllerManager().b(DragonControllerPhase.STRAFE_PLAYER).a(entityLiving);
	}
	
	private void dragonChargingPlayer(Location loc) {
		dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.CHARGING_PLAYER);
		dragon.getDragonControllerManager().b(DragonControllerPhase.CHARGING_PLAYER).a(new Vec3D(loc.getX(), loc.getY(), loc.getZ()));
	}
	
	private void dragonLandingApproach(Location loc) {
		dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.LANDING_APPROACH);
		DragonControllerLandingFly dragonControllerLandingFly = dragon.getDragonControllerManager().b(DragonControllerPhase.LANDING_APPROACH);
		try {
			Field dField = Class.forName("net.minecraft.server.v1_14_R1.DragonControllerLandingFly").getDeclaredField("d");
			dField.setAccessible(true);
			dField.set(dragonControllerLandingFly, new Vec3D(loc.getX(), loc.getY(), loc.getZ()));
		} catch (NoSuchFieldException | SecurityException | ClassNotFoundException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private void dragonLanding(Location loc) {
		dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.LANDING);
		DragonControllerLanding dragonControllerLanding = dragon.getDragonControllerManager().b(DragonControllerPhase.LANDING);
		try {
			Field dField = Class.forName("net.minecraft.server.v1_14_R1.DragonControllerLanding").getDeclaredField("b");
			dField.setAccessible(true);
			dField.set(dragonControllerLanding, new Vec3D(loc.getX(), loc.getY(), loc.getZ()));
		} catch (NoSuchFieldException | SecurityException | ClassNotFoundException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
}
