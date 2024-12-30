package filip.bedwars.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

public class VillagerNPC extends NPC {
	private VillagerType villagerType;
	private VillagerProfession villagerProfession;
	private ServerLevel nmsWorld;
	
	public VillagerNPC(Location location, String villagerType, String villagerProfession, String customName, Player... viewers) {
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
			//entity.absMoveTo(location.getX(), location.getY(), location.getZ(), 0f, 0f);
			reflectionUtils.entitySetLocationMethod.invoke(entity, location.getX(), location.getY(), location.getZ(), 0f, 0f);
			//entity.setCustomName(Component.literal(customName));
			Component component = (Component)reflectionUtils.componentNullToEmptyMethod.invoke(null, customName);
			reflectionUtils.entitySetCustomNameMethod.invoke(entity, component);
			//entity.setCustomNameVisible(true);
			reflectionUtils.entitySetCustomNameVisibleMethod.invoke(entity, true);
			VillagerData villagerData = new VillagerData(villagerType, villagerProfession, 5);
			//entity.setVillagerData(villagerData);
			reflectionUtils.entityVillagerSetVillagerDataMethod.invoke(entity, villagerData);
			
			respawn(viewers);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
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
				//playerConnection.send(addEntityPacket);
				reflectionUtils.playerConnectionSendPacketMethod.invoke(playerConnection, addEntityPacket);
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
}
