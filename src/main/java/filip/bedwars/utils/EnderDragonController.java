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
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.BedwarsPlugin;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
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
	private ServerLevel nmsWorld;

	public EnderDragonController(@NotNull final Location loc, @NotNull List<Entity> targetEntities, @NotNull Set<Player> viewers) {
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
					dragonPhase = random.nextInt(3);
				}

				if (currentTargetEntity != null && random.nextInt(50) == 0) {
					// Fire a Dragon Fireball
					try {
						Location dragonLoc = getLocation();
						DragonFireball dragonFireball = (DragonFireball)spawnLoc.getWorld().spawnEntity(dragonLoc, EntityType.DRAGON_FIREBALL);
						Vector dragonFireballVelocity = currentTargetEntity.getLocation().clone().subtract(dragonLoc).toVector().normalize();
						dragonFireball.setDirection(dragonFireballVelocity);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}

				if (currentTargetEntity == null) {
					dragonHoldingPattern(spawnLoc);
				} else {
					switch (dragonPhase) {
					case 0:
						dragonChargingPlayer(currentTargetEntity.getLocation().clone().add(0, -2, 0));
						break;
					case 1:
						dragonLanding(currentTargetEntity.getLocation().clone().add(0, -2, 0));
						break;
					case 2:
						dragonHoldingPattern(currentTargetEntity.getLocation().clone().add(0, -2, 0));
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
		final String bukkitVersion = Bukkit.getBukkitVersion();

		for (Player p : viewers) {
			try {
				final Packet<?> addEntityPacket;
				if (bukkitVersion.compareTo("1.21-R0.1-SNAPSHOT") >= 0) {
					addEntityPacket = (Packet<?>)reflectionUtils.clientboundAddEntityPacketConstructor.newInstance(
						dragon,
						new ServerEntity(nmsWorld, dragon, 0, false, packet -> {}, Set.of())
					);
				} else {
					addEntityPacket = (Packet<?>)reflectionUtils.clientboundAddEntityPacketConstructor.newInstance(dragon);
				}
				reflectionUtils.playerSendPacket(p, addEntityPacket);
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
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

	public Location getLocation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object craftEntity = reflectionUtils.entityGetBukkitEntityMethod.invoke(dragon);
		return (Location) reflectionUtils.craftEntityGetLocationMethod.invoke(craftEntity);
	}

	private boolean isTaskRunning() {
		if (task == null)
			return false;

		return !task.isCancelled();
	}

	private void spawn(final Location loc) {
		try {
			nmsWorld = reflectionUtils.worldToNMSWorld(loc.getWorld());
			dragon = new EnderDragon(net.minecraft.world.entity.EntityType.ENDER_DRAGON, nmsWorld);
			BedwarsPlugin.getInstance().reflectionUtils.entitySetLocationMethod.invoke(dragon, loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		respawn(viewers.keySet().toArray(new Player[0]));
	}

	private void updateLocation() {
		final String bukkitVersion = Bukkit.getBukkitVersion();
		Iterator<Player> iter = viewers.keySet().iterator();

		try {
			final Packet<?> teleportEntityPacket;
			if (bukkitVersion.compareTo("1.21.1-R0.1-SNAPSHOT") <= 0) {
				teleportEntityPacket = (Packet<?>)reflectionUtils.clientboundTeleportEntityPacketConstructor.newInstance(dragon);
			} else {
				teleportEntityPacket = (Packet<?>)reflectionUtils.clientboundTeleportEntityPacketConstructor.newInstance(
					getEntityId(),
					reflectionUtils.positionMoveRotationOfMethod.invoke(null, dragon),
					Set.of(),
					false
				);
			}

			while (iter.hasNext()) {
				Player p = iter.next();

				Object craftWorld = reflectionUtils.levelGetWorldMethod.invoke(reflectionUtils.entityLevelMethod.invoke(dragon));

				if (!p.getWorld().getName().equals(reflectionUtils.craftWorldGetNameMethod.invoke(craftWorld))) {
					iter.remove();
					continue;
				}

				double dist = p.getLocation().distance(getLocation());
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

				reflectionUtils.playerSendPacket(p, teleportEntityPacket);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
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
