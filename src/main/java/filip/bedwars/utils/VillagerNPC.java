package filip.bedwars.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import filip.bedwars.BedwarsPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

public class VillagerNPC {

	private final ReflectionUtils reflectionUtils;
	private VillagerType villagerType;
	private VillagerProfession villagerProfession;
	private net.minecraft.world.entity.npc.Villager entity;
	private ServerLevel nmsWorld;
	
	public VillagerNPC(Location location, String villagerType, String villagerProfession, String customName, Player... viewers) {
		reflectionUtils = BedwarsPlugin.getInstance().reflectionUtils;

		this.villagerType = reflectionUtils.parseVillagerType(villagerType);
		if (this.villagerType == null)
			this.villagerType = VillagerType.DESERT;

		this.villagerProfession = reflectionUtils.parseVillagerProfession(villagerProfession);
		if (this.villagerProfession == null)
			this.villagerProfession = VillagerProfession.ARMORER;

		spawn(location, customName, viewers);
	}

	private void spawn(Location location, String customName, Player[] viewers) {
		try {
			nmsWorld = reflectionUtils.worldToNMSWorld(location.getWorld());
			net.minecraft.world.entity.EntityType entityType = (EntityType)reflectionUtils.entityTypesVillagerField.get(null);
			entity = new net.minecraft.world.entity.npc.Villager(entityType, nmsWorld);
			//entity = reflectionUtils.entityVillagerConstructor.newInstance(reflectionUtils.entityTypesVillagerField.get(null), reflectionUtils.craftWorldGetHandleMethod.invoke(craftWorld));
			reflectionUtils.entitySetLocationMethod.invoke(entity, location.getX(), location.getY(), location.getZ(), 0f, 0f);
			//entity.absMoveTo(location.getX(), location.getY(), location.getZ(), 0f, 0f);
			//reflectionUtils.entitySetLocationMethod.invoke(entity, location.getX(), location.getY(), location.getZ(), 0f, 0f);
			//entity.setCustomName(Component.literal(customName));
			Component component = (Component)reflectionUtils.componentNullToEmptyMethod.invoke(null, customName);
			reflectionUtils.entitySetCustomNameMethod.invoke(entity, component);
			//reflectionUtils.entitySetCustomNameMethod.invoke(entity, reflectionUtils.chatComponentConstructor.newInstance(customName));
			//entity.setCustomNameVisible(true);
			reflectionUtils.entitySetCustomNameVisibleMethod.invoke(entity, true);
			//reflectionUtils.entitySetCustomNameVisibleMethod.invoke(entity, true);
			VillagerData villagerData = new VillagerData(villagerType, villagerProfession, 5);
			//Object villagerData = reflectionUtils.villagerDataConstructor.newInstance(desertVillagerTypeField.get(null), armorerVillagerProfessionField.get(null), 5);
			//entity.setVillagerData(villagerData);
			reflectionUtils.entityVillagerSetVillagerDataMethod.invoke(entity, villagerData);
			//reflectionUtils.entityVillagerSetVillagerDataMethod.invoke(entity, villagerData);
			
			respawn(viewers);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public void teleport(double x, double y, double z, Player... viewers) {
		String bukkitVersion = Bukkit.getBukkitVersion();

		try {
			reflectionUtils.entitySetLocationMethod.invoke(entity, x, y, z, 0f, 0f);

			final Packet<?> teleportEntityPacket;
			if (bukkitVersion.compareTo("1.21.1-R0.1-SNAPSHOT") <= 0) {
				teleportEntityPacket = (Packet<?>)reflectionUtils.clientboundTeleportEntityPacketConstructor.newInstance(entity);
			} else {
				teleportEntityPacket = (Packet<?>)reflectionUtils.clientboundTeleportEntityPacketConstructor.newInstance(
					getEntityId(),
					reflectionUtils.positionMoveRotationOfMethod.invoke(null, entity),
					Set.of(),
					false
				);
			}

			for (Player p : viewers)
				reflectionUtils.playerSendPacket(p, teleportEntityPacket);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			e.printStackTrace();
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
	
	public void respawn(Player... viewers) {
		String bukkitVersion = Bukkit.getBukkitVersion();

		for (Player p : viewers) {
			try {
				final Object addEntityPacket;
				if (bukkitVersion.compareTo("1.21-R0.1-SNAPSHOT") >= 0) {
					addEntityPacket = reflectionUtils.clientboundAddEntityPacketConstructor.newInstance(
						entity,
						new ServerEntity(nmsWorld, entity, 0, false, packet -> {}, Set.of())
					);
				} else {
					addEntityPacket = reflectionUtils.clientboundAddEntityPacketConstructor.newInstance(entity);
				}

				ServerGamePacketListenerImpl playerConnection = reflectionUtils.playerGetConnection(p);
				//playerConnection.send(packet);
				reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, addEntityPacket);
				//playerConnection.send(new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData().getNonDefaultValues()));
				SynchedEntityData synchedEntityData = (SynchedEntityData)reflectionUtils.entityGetEntityDataMethod.invoke(entity);
				ClientboundSetEntityDataPacket setEntityDataPacket;

				if (bukkitVersion.compareTo("1.19.3-R0.1-SNAPSHOT") >= 0) {
					List<SynchedEntityData.DataValue<?>> packedItems = (List<SynchedEntityData.DataValue<?>>)reflectionUtils.synchedEntityDataPackAllMethod.invoke(synchedEntityData);
					setEntityDataPacket = new ClientboundSetEntityDataPacket(getEntityId(), packedItems);
				} else {
					setEntityDataPacket = ClientboundSetEntityDataPacket.class.getConstructor(int.class, SynchedEntityData.class, boolean.class).newInstance(getEntityId(), synchedEntityData, true);
				}
				reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, setEntityDataPacket);
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException e) {
				e.printStackTrace();
			}
		}
	}

	public int getEntityId() {
		// .hashCode() does the same thing as .getId()
		// We do not use .getId() because the method name is obfuscated on some nms version.
		return entity.hashCode();
	}
	
}
