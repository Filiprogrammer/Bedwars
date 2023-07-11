package filip.bedwars.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import filip.bedwars.BedwarsPlugin;

public class EnderDragonController {

	private final ReflectionUtils reflectionUtils;
	private final List<Entity> targetEntities;
	private final Map<Player, Boolean> viewers = new HashMap<>();
	private BukkitTask task;
	private BukkitRunnable bukkitRunnable;
	private Object dragon;
	
	private Entity currentTargetEntity;
	private int dragonPhase;
	private Random random = new Random();
	private final Location spawnLoc;
	
	public EnderDragonController(Location loc, List<Entity> targetEntities, Set<Player> viewers) {
		reflectionUtils = BedwarsPlugin.getInstance().reflectionUtils;
		
		this.targetEntities = targetEntities;
		
		for (Player viewer : viewers)
			this.viewers.put(viewer, true);
		
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
		viewers.put(viewer, true);
		respawn(viewer);
	}
	
	public boolean removeViewer(Player viewer) {
		if (spawnLoc.getWorld().getName().equals(viewer.getWorld().getName()))
			despawn(viewer);
		
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
						if (reflectionUtils.entityLivingClass.isInstance(currentTargetEntity))
							dragonStrafePlayer(currentTargetEntity);
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
				
				try {
					reflectionUtils.entityEnderDragonTickMethod.invoke(dragon);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
				updateLocation();
			}
		};
		
		try {
			task = bukkitRunnable.runTaskTimer(BedwarsPlugin.getInstance(), 1L, 1L);
		} catch (IllegalPluginAccessException e) {}
	}
	
	public void respawn(Player... viewers) {
		try {
			for (Player p : viewers) {
				// EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
				Object entityPlayer = reflectionUtils.craftPlayerGetHandleMethod.invoke(reflectionUtils.craftPlayerClass.cast(p));
				// PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(dragon);
				Object packet = reflectionUtils.packetPlayOutSpawnEntityLivingConstructor.newInstance(dragon);
				// PlayerConnection playerConnection = entityPlayer.playerConnection;
				Object playerConnection = reflectionUtils.entityPlayerPlayerConnectionField.get(entityPlayer);
				// playerConnection.sendPacket(packet);
				reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			e.printStackTrace();
		}
	}
	
	public void despawn(Player... viewers) {
		try {
			for (Player p : viewers) {
				// EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
				Object entityPlayer = reflectionUtils.craftPlayerGetHandleMethod.invoke(reflectionUtils.craftPlayerClass.cast(p));
				// PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(dragon.getId());
				Object packet = reflectionUtils.packetPlayOutEntityDestroyConstructor.newInstance(new int[] {(int) reflectionUtils.entityGetIdMethod.invoke(dragon)});
				// PlayerConnection playerConnection = entityPlayer.playerConnection;
				Object playerConnection = reflectionUtils.entityPlayerPlayerConnectionField.get(entityPlayer);
				// playerConnection.sendPacket(packet);
				reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isTaskRunning() {
		if (task == null)
			return false;
		
		return !task.isCancelled();
	}
	
	private void spawn(Location loc) {
		try {
			// WorldServer worldServer = ((CraftWorld)loc.getWorld()).getHandle();
			Object worldServer = reflectionUtils.craftWorldGetHandleMethod.invoke(reflectionUtils.craftWorldClass.cast(loc.getWorld()));
			// dragon = new EntityEnderDragon(EntityTypes.ENDER_DRAGON, worldServer);
			dragon = reflectionUtils.entityEnderDragonConstructor.newInstance(reflectionUtils.entityTypesEnderDragonField.get(null), worldServer);
			// dragon.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
			reflectionUtils.entitySetLocationMethod.invoke(dragon, loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			e.printStackTrace();
		}
		respawn(viewers.keySet().toArray(new Player[0]));
	}
	
	private void updateLocation() {
		Iterator<Player> iter = viewers.keySet().iterator();
		
		try {
			while (iter.hasNext()) {
				Player p = iter.next();
				
				Object craftWorld = reflectionUtils.worldGetWorldMethod.invoke(reflectionUtils.entityGetWorldMethod.invoke(dragon));
				
				if (!p.getWorld().getName().equals(reflectionUtils.craftWorldGetNameMethod.invoke(craftWorld))) {
					iter.remove();
					continue;
				}
				
				double dist = p.getLocation().distance(new Location((World) craftWorld, (double) reflectionUtils.entityLocXField.get(dragon), (double) reflectionUtils.entityLocYField.get(dragon), (double) reflectionUtils.entityLocZField.get(dragon)));
				int viewDistance = Math.min(Bukkit.getServer().getViewDistance(), p.getClientViewDistance());
				
				if (viewers.get(p)) {
					if (dist > viewDistance * 16) {
						despawn(p);
						viewers.put(p, false);
					}
				} else {
					if (dist < viewDistance * 16) {
						respawn(p);
						viewers.put(p, true);
					}
				}
				
				// EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
				Object entityPlayer = reflectionUtils.craftPlayerGetHandleMethod.invoke(reflectionUtils.craftPlayerClass.cast(p));
				// PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(dragon);
				Object packet = reflectionUtils.packetPlayOutEntityTeleportConstructor.newInstance(dragon);
				// PlayerConnection playerConnection = entityPlayer.playerConnection;
				Object playerConnection = reflectionUtils.entityPlayerPlayerConnectionField.get(entityPlayer);
				// playerConnection.sendPacket(packet);
				reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			e.printStackTrace();
		}
	}
	
	private void dragonHoldingPattern(Location loc) {
		try {
			// dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.HOLDING_PATTERN);
			Object dragonControllerManager = reflectionUtils.entityEnderDragonGetDragonControllerManagerMethod.invoke(dragon);
			reflectionUtils.dragonControllerManagerSetControllerPhaseMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseHoldingPatternField.get(null));
			// DragonControllerHold dragonControllerHold = dragon.getDragonControllerManager().b(DragonControllerPhase.HOLDING_PATTERN);
			Object dragonControllerHold = reflectionUtils.dragonControllerManagerBMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseHoldingPatternField.get(null));
			Field dField = Class.forName("net.minecraft.server." + BedwarsPlugin.getInstance().getServerVersion() + ".DragonControllerHold").getDeclaredField("d");
			dField.setAccessible(true);
			dField.set(dragonControllerHold, reflectionUtils.vec3DConstructor.newInstance(loc.getX(), loc.getY(), loc.getZ()));
		} catch (NoSuchFieldException | SecurityException | ClassNotFoundException | IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	private void dragonStrafePlayer(Object entityLiving) {
		try {
			Object dragonControllerManager = reflectionUtils.entityEnderDragonGetDragonControllerManagerMethod.invoke(dragon);
			// dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.STRAFE_PLAYER);
			reflectionUtils.dragonControllerManagerSetControllerPhaseMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseStrafePlayerField.get(null));
			// dragon.getDragonControllerManager().b(DragonControllerPhase.STRAFE_PLAYER).a(entityLiving);
			reflectionUtils.dragonControllerStrafeAMethod.invoke(reflectionUtils.dragonControllerManagerBMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseStrafePlayerField.get(null)), entityLiving);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	private void dragonChargingPlayer(Location loc) {
		try {
			Object dragonControllerManager = reflectionUtils.entityEnderDragonGetDragonControllerManagerMethod.invoke(dragon);
			// dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.CHARGING_PLAYER);
			reflectionUtils.dragonControllerManagerSetControllerPhaseMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseChargingPlayerField.get(null));
			// dragon.getDragonControllerManager().b(DragonControllerPhase.CHARGING_PLAYER).a(new Vec3D(loc.getX(), loc.getY(), loc.getZ()));
			reflectionUtils.dragonControllerChargeAMethod.invoke(reflectionUtils.dragonControllerManagerBMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseChargingPlayerField.get(null)),
					reflectionUtils.vec3DConstructor.newInstance(loc.getX(), loc.getY(), loc.getZ()));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			e.printStackTrace();
		}
	}
	
	private void dragonLandingApproach(Location loc) {
		try {
			Object dragonControllerManager = reflectionUtils.entityEnderDragonGetDragonControllerManagerMethod.invoke(dragon);
			// dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.LANDING_APPROACH);
			reflectionUtils.dragonControllerManagerSetControllerPhaseMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseLandingApproachField.get(null));
			// DragonControllerLandingFly dragonControllerLandingFly = dragon.getDragonControllerManager().b(DragonControllerPhase.LANDING_APPROACH);
			Object dragonControllerLandingFly = reflectionUtils.dragonControllerManagerBMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseLandingApproachField.get(null));
			Field dField = Class.forName("net.minecraft.server." + BedwarsPlugin.getInstance().getServerVersion() + ".DragonControllerLandingFly").getDeclaredField("d");
			dField.setAccessible(true);
			dField.set(dragonControllerLandingFly, reflectionUtils.vec3DConstructor.newInstance(loc.getX(), loc.getY(), loc.getZ()));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException | SecurityException | ClassNotFoundException | InstantiationException e) {
			e.printStackTrace();
		}
	}
	
	private void dragonLanding(Location loc) {
		try {
			Object dragonControllerManager = reflectionUtils.entityEnderDragonGetDragonControllerManagerMethod.invoke(dragon);
			// dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.LANDING);
			reflectionUtils.dragonControllerManagerSetControllerPhaseMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseLandingField.get(null));
			// DragonControllerLanding dragonControllerLanding = dragon.getDragonControllerManager().b(DragonControllerPhase.LANDING);
			Object dragonControllerLanding = reflectionUtils.dragonControllerManagerBMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseLandingField.get(null));
			Field dField = Class.forName("net.minecraft.server." + BedwarsPlugin.getInstance().getServerVersion() + ".DragonControllerLanding").getDeclaredField("b");
			dField.setAccessible(true);
			dField.set(dragonControllerLanding, reflectionUtils.vec3DConstructor.newInstance(loc.getX(), loc.getY(), loc.getZ()));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException | SecurityException | ClassNotFoundException | InstantiationException e) {
			e.printStackTrace();
		}
	}
	
}
