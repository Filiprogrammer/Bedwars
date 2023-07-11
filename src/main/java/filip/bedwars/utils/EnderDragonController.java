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
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import filip.bedwars.BedwarsPlugin;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonHoldingPatternPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonLandingApproachPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonLandingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.phys.Vec3;

public class EnderDragonController {

	private final ReflectionUtils reflectionUtils;
	private final List<Entity> targetEntities;
	private final Map<Player, Boolean> viewers = new HashMap<>();
	private BukkitTask task;
	private BukkitRunnable bukkitRunnable;
	private net.minecraft.world.entity.boss.enderdragon.EnderDragon dragon;
	
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
							dragonStrafePlayer((LivingEntity)currentTargetEntity);
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
				/*try {
					reflectionUtils.entityEnderDragonTickMethod.invoke(dragon);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}*/
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
				ServerPlayer entityPlayer = ((CraftPlayer) p).getHandle();
				//Object entityPlayer = reflectionUtils.craftPlayerGetHandleMethod.invoke(reflectionUtils.craftPlayerClass.cast(p));
				// PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(dragon);
				ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket((net.minecraft.world.entity.Entity)dragon);
				//Object packet = reflectionUtils.packetPlayOutSpawnEntityLivingConstructor.newInstance(dragon);
				ServerGamePacketListenerImpl playerConnection = entityPlayer.connection;
				//Object playerConnection = reflectionUtils.entityPlayerPlayerConnectionField.get(entityPlayer);
				playerConnection.send(packet);
				//reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	public void despawn(Player... viewers) {
		try {
			for (Player p : viewers) {
				ServerPlayer entityPlayer = ((CraftPlayer) p).getHandle();
				//Object entityPlayer = reflectionUtils.craftPlayerGetHandleMethod.invoke(reflectionUtils.craftPlayerClass.cast(p));
				// PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(dragon.getId());
				ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(dragon.getId());
				//Object packet = reflectionUtils.packetPlayOutEntityDestroyConstructor.newInstance(new int[] {dragon.getId()});
				ServerGamePacketListenerImpl playerConnection = entityPlayer.connection;
				//Object playerConnection = reflectionUtils.entityPlayerPlayerConnectionField.get(entityPlayer);
				playerConnection.send(packet);
				//reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
			}
		} catch (IllegalArgumentException e) {
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
			//Object worldServer = reflectionUtils.craftWorldGetHandleMethod.invoke(reflectionUtils.craftWorldClass.cast(loc.getWorld()));
			ServerLevel worldServer = ((CraftWorld)loc.getWorld()).getHandle();
			// dragon = new EntityEnderDragon(EntityTypes.ENDER_DRAGON, worldServer);
			dragon = new EnderDragon(net.minecraft.world.entity.EntityType.ENDER_DRAGON, worldServer);
			//dragon = reflectionUtils.entityEnderDragonConstructor.newInstance(reflectionUtils.entityTypesEnderDragonField.get(null), worldServer);
			// dragon.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
			dragon.moveTo(loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
			//reflectionUtils.entitySetLocationMethod.invoke(dragon, loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		respawn(viewers.keySet().toArray(new Player[0]));
	}
	
	private void updateLocation() {
		Iterator<Player> iter = viewers.keySet().iterator();
		
		try {
			while (iter.hasNext()) {
				Player p = iter.next();
				
				CraftWorld craftWorld = dragon.level().getWorld();
				//Object craftWorld = reflectionUtils.worldGetWorldMethod.invoke(reflectionUtils.entityGetWorldMethod.invoke(dragon));
				
				if (!p.getWorld().getName().equals(reflectionUtils.craftWorldGetNameMethod.invoke(craftWorld))) {
					iter.remove();
					continue;
				}

				double dist = p.getLocation().distance(new Location((World) craftWorld, dragon.xo, dragon.yo, dragon.zo));
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
				
				ServerPlayer entityPlayer = ((CraftPlayer) p).getHandle();
				//Object entityPlayer = reflectionUtils.craftPlayerGetHandleMethod.invoke(reflectionUtils.craftPlayerClass.cast(p));
				// PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(dragon);
				ClientboundTeleportEntityPacket packet = new ClientboundTeleportEntityPacket(dragon);
				//Object packet = reflectionUtils.packetPlayOutEntityTeleportConstructor.newInstance(dragon);
				ServerGamePacketListenerImpl playerConnection = entityPlayer.connection;
				//Object playerConnection = reflectionUtils.entityPlayerPlayerConnectionField.get(entityPlayer);
				playerConnection.send(packet);
				//reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	private void dragonHoldingPattern(Location loc) {
		try {
			// dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.HOLDING_PATTERN);
			dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
			//Object dragonControllerManager = reflectionUtils.entityEnderDragonGetDragonControllerManagerMethod.invoke(dragon);
			//reflectionUtils.dragonControllerManagerSetControllerPhaseMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseHoldingPatternField.get(null));
			// DragonControllerHold dragonControllerHold = dragon.getDragonControllerManager().b(DragonControllerPhase.HOLDING_PATTERN);
			DragonHoldingPatternPhase dragonControllerHold = dragon.getPhaseManager().getPhase(EnderDragonPhase.HOLDING_PATTERN);
			//Object dragonControllerHold = reflectionUtils.dragonControllerManagerBMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseHoldingPatternField.get(null));
			// dragonControllerHold.targetLocation = new net.minecraft.world.phys.Vec3(loc.getX(), loc.getY(), loc.getZ());
			Field targetLocationField = Class.forName("net.minecraft.world.entity.boss.enderdragon.phases.DragonHoldingPatternPhase").getDeclaredField("targetLocation");
			targetLocationField.setAccessible(true);
			targetLocationField.set(dragonControllerHold, new net.minecraft.world.phys.Vec3(loc.getX(), loc.getY(), loc.getZ()));
			//Field dField = Class.forName("net.minecraft.server." + BedwarsPlugin.getInstance().getServerVersion() + ".DragonControllerHold").getDeclaredField("d");
			//dField.setAccessible(true);
			//dField.set(dragonControllerHold, reflectionUtils.vec3DConstructor.newInstance(loc.getX(), loc.getY(), loc.getZ()));
		} catch (NoSuchFieldException | SecurityException | ClassNotFoundException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private void dragonStrafePlayer(LivingEntity entityLiving) {
		try {
			EnderDragonPhaseManager dragonControllerManager = dragon.getPhaseManager();
			//Object dragonControllerManager = reflectionUtils.entityEnderDragonGetDragonControllerManagerMethod.invoke(dragon);
			// dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.STRAFE_PLAYER);
			dragonControllerManager.setPhase(EnderDragonPhase.STRAFE_PLAYER);
			//reflectionUtils.dragonControllerManagerSetControllerPhaseMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseStrafePlayerField.get(null));
			// dragon.getDragonControllerManager().b(DragonControllerPhase.STRAFE_PLAYER).a(entityLiving);
			dragonControllerManager.getPhase(EnderDragonPhase.STRAFE_PLAYER).setTarget(entityLiving);
			//reflectionUtils.dragonControllerStrafeAMethod.invoke(reflectionUtils.dragonControllerManagerBMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseStrafePlayerField.get(null)), entityLiving);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	private void dragonChargingPlayer(Location loc) {
		try {
			EnderDragonPhaseManager dragonControllerManager = dragon.getPhaseManager();
			//Object dragonControllerManager = reflectionUtils.entityEnderDragonGetDragonControllerManagerMethod.invoke(dragon);
			// dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.CHARGING_PLAYER);
			dragonControllerManager.setPhase(EnderDragonPhase.CHARGING_PLAYER);
			//reflectionUtils.dragonControllerManagerSetControllerPhaseMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseChargingPlayerField.get(null));
			// dragon.getDragonControllerManager().b(DragonControllerPhase.CHARGING_PLAYER).a(new Vec3D(loc.getX(), loc.getY(), loc.getZ()));
			dragonControllerManager.getPhase(EnderDragonPhase.CHARGING_PLAYER).setTarget(new Vec3(loc.getX(), loc.getY(), loc.getZ()));
			/*reflectionUtils.dragonControllerChargeAMethod.invoke(reflectionUtils.dragonControllerManagerBMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseChargingPlayerField.get(null)),
					reflectionUtils.vec3DConstructor.newInstance(loc.getX(), loc.getY(), loc.getZ()));*/
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	private void dragonLandingApproach(Location loc) {
		try {
			EnderDragonPhaseManager dragonControllerManager = dragon.getPhaseManager();
			//Object dragonControllerManager = reflectionUtils.entityEnderDragonGetDragonControllerManagerMethod.invoke(dragon);
			// dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.LANDING_APPROACH);
			dragonControllerManager.setPhase(EnderDragonPhase.LANDING_APPROACH);
			//reflectionUtils.dragonControllerManagerSetControllerPhaseMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseLandingApproachField.get(null));
			// DragonControllerLandingFly dragonControllerLandingFly = dragon.getDragonControllerManager().b(DragonControllerPhase.LANDING_APPROACH);
			DragonLandingApproachPhase dragonControllerLandingFly =  dragonControllerManager.getPhase(EnderDragonPhase.LANDING_APPROACH);
			//Object dragonControllerLandingFly = reflectionUtils.dragonControllerManagerBMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseLandingApproachField.get(null));
			Field dField = Class.forName("net.minecraft.world.entity.boss.enderdragon.phases.DragonLandingApproachPhase").getDeclaredField("targetLocation");
			dField.setAccessible(true);
			dField.set(dragonControllerLandingFly, new Vec3(loc.getX(), loc.getY(), loc.getZ()));
		} catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void dragonLanding(Location loc) {
		try {
			EnderDragonPhaseManager dragonControllerManager = dragon.getPhaseManager();
			//Object dragonControllerManager = reflectionUtils.entityEnderDragonGetDragonControllerManagerMethod.invoke(dragon);
			// dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.LANDING);
			dragonControllerManager.setPhase(EnderDragonPhase.LANDING);
			//reflectionUtils.dragonControllerManagerSetControllerPhaseMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseLandingField.get(null));
			// DragonControllerLanding dragonControllerLanding = dragon.getDragonControllerManager().b(DragonControllerPhase.LANDING);
			DragonLandingPhase dragonControllerLanding = dragonControllerManager.getPhase(EnderDragonPhase.LANDING);
			//Object dragonControllerLanding = reflectionUtils.dragonControllerManagerBMethod.invoke(dragonControllerManager, reflectionUtils.dragonControllerPhaseLandingField.get(null));
			Field dField = Class.forName("net.minecraft.world.entity.boss.enderdragon.phases.DragonLandingPhase").getDeclaredField("targetLocation");
			dField.setAccessible(true);
			dField.set(dragonControllerLanding, new Vec3(loc.getX(), loc.getY(), loc.getZ()));
		} catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
