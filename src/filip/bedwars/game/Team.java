package filip.bedwars.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.arena.Base;
import filip.bedwars.utils.TeamColorConverter;

public class Team {
    private final int id;
    private final Base base;
    private final List<GamePlayer> members = new ArrayList<GamePlayer>();
    private boolean hasBed = true;
    private Inventory teamChestInventory = Bukkit.createInventory(null, 3 * 9, "Team Chest");
    
    public HashMap<TeamUpgradeType, Integer> upgrades = new HashMap<TeamUpgradeType, Integer>(){{
    	for (TeamUpgradeType type : TeamUpgradeType.values())
    		put(type, 0);
    }};
    
    public Team(int id, @NotNull Base base) {
        this.id = id;
        this.base = base;
    }
    
    public int getId() {
    	return id;
    }
    
    public Base getBase() {
    	return base;
    }
    
    public void addMember(@NotNull GamePlayer gamePlayer) {
    	members.add(gamePlayer);
    }
    
    public boolean removeMember(@NotNull GamePlayer gamePlayer) {
    	return members.remove(gamePlayer);
    }
    
    public boolean containsMember(@NotNull UUID uuid) {
    	return members.stream().anyMatch(member -> member.uuid.equals(uuid));
    }
    
    public void clearMembers() {
    	members.clear();
    }
    
    public List<GamePlayer> getMembers() {
    	return members;
    }
    
    public Inventory getTeamChestInventory() {
    	return teamChestInventory;
    }
    
    public boolean hasBed() {
    	return hasBed;
    }
    
    public void destroyBed(World world) {
    	hasBed = false;
    	base.getBedBottom(world).getBlock().setType(Material.AIR, false);
    	base.getBedTop(world).getBlock().setType(Material.AIR, false);
    }
    
    public void restoreBed(World world) {
    	hasBed = true;
    	Material bedMaterial = TeamColorConverter.convertTeamColorToBedMaterial(base.getTeamColor());
    	
    	Block bedBottomBlock = base.getBedBottom(world).getBlock();
    	Block bedTopBlock = base.getBedTop(world).getBlock();
    	BlockFace bedFace = BlockFace.NORTH;
    	
    	if (bedTopBlock.getX() > bedBottomBlock.getX())
    		bedFace = BlockFace.EAST;
    	else if (bedTopBlock.getX() < bedBottomBlock.getX())
    		bedFace = BlockFace.WEST;
    	else if (bedTopBlock.getZ() > bedBottomBlock.getZ())
    		bedFace = BlockFace.SOUTH;
    	
    	bedBottomBlock.setType(bedMaterial, false);
    	Bed bedBottomData = (Bed) bedMaterial.createBlockData();
    	bedBottomData.setPart(Part.FOOT);
    	bedBottomData.setFacing(bedFace);
    	bedBottomBlock.setBlockData(bedBottomData, false);
    	
    	bedTopBlock.setType(bedMaterial, false);
    	Bed bedTopData = (Bed) bedMaterial.createBlockData();
    	bedTopData.setPart(Part.HEAD);
    	bedBottomData.setFacing(bedFace);
    	bedTopBlock.setBlockData(bedTopData, false);
    }
    
    public enum TeamUpgradeType {
    	HEAL_POOL,
    	MINING_BOOST,
    	ATTACK_BOOST,
    	PROTECTION_BOOST,
    	EXTRA_DRAGONS,
    	BED_RESTORE
    }
    
}
