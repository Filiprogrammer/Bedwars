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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import filip.bedwars.BedwarsPlugin;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonHoldingPatternPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonLandingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
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
		if (currentTargetEntity != null && entity.getEntityId() == currentTargetEntity.getEntityId()) {
			currentTargetEntity = null;
		}

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
				// TODO: This does not seem ideal. The dragon will alternate between targets, which might be problematic when the different targets are far apart.
				if (random.nextInt(200) == 0) {
					// Choose a random target
					if (targetEntities.size() == 0)
						currentTargetEntity = null;
					else
						currentTargetEntity = targetEntities.get(random.nextInt(targetEntities.size()));
				}

				if (random.nextInt(100) == 0) {
					dragonPhase = random.nextInt(4);
				}

				if (currentTargetEntity == null) {
					dragonHoldingPattern(spawnLoc);
				} else {
					switch (dragonPhase) {
					case 0:
						dragonChargingPlayer(currentTargetEntity.getLocation().clone().add(0, -1, 0));
						break;
					case 1:
						try {
							net.minecraft.world.entity.Entity nmsEntity = reflectionUtils.entityToNMSEntity(currentTargetEntity);
							if (reflectionUtils.entityLivingClass.isInstance(nmsEntity)) {
								dragonStrafePlayer((LivingEntity)nmsEntity);
							}
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
						}
						break;
					case 2:
						dragonLanding(currentTargetEntity.getLocation());
						break;
					case 3:
						dragonHoldingPattern(currentTargetEntity.getLocation());
						break;
					}
				}

				//dragon.tick();
				try {
					reflectionUtils.mobTickMethod.invoke(dragon);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
				updateLocation();
				// TODO: Check for Endstone or Obsidian blocks colliding with the dragons' hitbox and destroy these blocks.
				// We only want to do this for Endstone and Obsidian, since the other blocks are destroyed by the dragon anyway and play nice breaking sounds.
			}
		};
		
		try {
			task = bukkitRunnable.runTaskTimer(BedwarsPlugin.getInstance(), 1L, 1L);
		} catch (IllegalPluginAccessException e) {}
	}
	
	public void respawn(Player... viewers) {
		for (Player p : viewers) {
			try {
				reflectionUtils.playerSendPacket(p, new ClientboundAddEntityPacket((net.minecraft.world.entity.Entity)dragon));
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void despawn(Player... viewers) {
		for (Player p : viewers) {
			try {
				reflectionUtils.playerSendPacket(p, new ClientboundRemoveEntitiesPacket(getEntityId()));
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public int getEntityId() {
		// .hashCode() does the same thing as .getId()
		// We do not use .getId() because the method name is obfuscated on some nms version.
		return dragon.hashCode();
	}
	
	private boolean isTaskRunning() {
		if (task == null)
			return false;
		
		return !task.isCancelled();
	}
	
	private void spawn(Location loc) {
		try {
			ServerLevel worldServer = reflectionUtils.worldToNMSWorld(loc.getWorld());
			dragon = new EnderDragon(net.minecraft.world.entity.EntityType.ENDER_DRAGON, worldServer);
			BedwarsPlugin.getInstance().reflectionUtils.entitySetLocationMethod.invoke(dragon, loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		respawn(viewers.keySet().toArray(new Player[0]));
	}
	
	private void updateLocation() {
		Iterator<Player> iter = viewers.keySet().iterator();
		
		try {
			while (iter.hasNext()) {
				Player p = iter.next();

				Object craftWorld = reflectionUtils.levelGetWorldMethod.invoke(reflectionUtils.entityLevelMethod.invoke(dragon));
				
				if (!p.getWorld().getName().equals(reflectionUtils.craftWorldGetNameMethod.invoke(craftWorld))) {
					iter.remove();
					continue;
				}

				Object craftEntity = reflectionUtils.entityGetBukkitEntityMethod.invoke(dragon);
				Location dragonLoc = (Location) reflectionUtils.craftEntityGetLocationMethod.invoke(craftEntity);
				double dist = p.getLocation().distance(dragonLoc);
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

				reflectionUtils.playerSendPacket(p, new ClientboundTeleportEntityPacket(dragon));
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	private void dragonHoldingPattern(Location loc) {
		try {
			Object phaseManager = reflectionUtils.entityEnderDragonGetPhaseManagerMethod.invoke(dragon);
			reflectionUtils.dragonPhaseManagerSetPhaseMethod.invoke(phaseManager, EnderDragonPhase.HOLDING_PATTERN);
			DragonHoldingPatternPhase dragonControllerHold = (DragonHoldingPatternPhase) reflectionUtils.dragonPhaseManagerGetCurrentPhaseMethod.invoke(phaseManager);
			Field targetLocationField = DragonHoldingPatternPhase.class.getDeclaredField("d");
			targetLocationField.setAccessible(true);
			targetLocationField.set(dragonControllerHold, new net.minecraft.world.phys.Vec3(loc.getX(), loc.getY(), loc.getZ()));
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	private void dragonStrafePlayer(LivingEntity entityLiving) {
		try {
			Object phaseManager = reflectionUtils.entityEnderDragonGetPhaseManagerMethod.invoke(dragon);
			reflectionUtils.dragonPhaseManagerSetPhaseMethod.invoke(phaseManager, EnderDragonPhase.STRAFE_PLAYER);
			Object dragonStrafePlayerPhase = reflectionUtils.dragonPhaseManagerGetCurrentPhaseMethod.invoke(phaseManager);
			reflectionUtils.dragonStrafePlayerPhaseSetTargetMethod.invoke(dragonStrafePlayerPhase, entityLiving);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	private void dragonChargingPlayer(Location loc) {
		try {
			Object phaseManager = reflectionUtils.entityEnderDragonGetPhaseManagerMethod.invoke(dragon);
			reflectionUtils.dragonPhaseManagerSetPhaseMethod.invoke(phaseManager, EnderDragonPhase.CHARGING_PLAYER);
			Object dragonChargePlayerPhase = reflectionUtils.dragonPhaseManagerGetCurrentPhaseMethod.invoke(phaseManager);
			reflectionUtils.dragonChargePlayerPhaseSetTargetMethod.invoke(dragonChargePlayerPhase, new Vec3(loc.getX(), loc.getY(), loc.getZ()));
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void dragonLanding(Location loc) {
		try {
			Object phaseManager = reflectionUtils.entityEnderDragonGetPhaseManagerMethod.invoke(dragon);
			reflectionUtils.dragonPhaseManagerSetPhaseMethod.invoke(phaseManager, EnderDragonPhase.LANDING);
			Object dragonLandingPhase = reflectionUtils.dragonPhaseManagerGetCurrentPhaseMethod.invoke(phaseManager);
			Field dField = DragonLandingPhase.class.getDeclaredField("b");
			dField.setAccessible(true);
			dField.set(dragonLandingPhase, new Vec3(loc.getX(), loc.getY(), loc.getZ()));
		} catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
}
