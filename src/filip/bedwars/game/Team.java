package filip.bedwars.game;

import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.arena.Base;

public class Team {
    private final int id;
    private final Base base;
    private final List<UUID> members;
    private boolean hasBed = true;
    
    public Team(int id, @NotNull Base base, @NotNull List<UUID> members) {
        this.id = id;
        this.base = base;
        this.members = members;
    }
    
    public int getId() {
    	return id;
    }
    
    public Base getBase() {
    	return base;
    }
    
    public void addMember(@NotNull UUID uuid) {
    	members.add(uuid);
    }
    
    public boolean removeMember(@NotNull UUID uuid) {
    	return members.remove(uuid);
    }
    
    public boolean containsMember(@NotNull UUID uuid) {
    	return members.contains(uuid);
    }
    
    public void clearMembers() {
    	members.clear();
    }
    
    public List<UUID> getMembers() {
    	return members;
    }
    
    public boolean hasBed() {
    	return hasBed;
    }
    
    public void destroyBed(World world) {
    	hasBed = false;
    	base.getBedBottom(world).getBlock().setType(Material.AIR);
    	base.getBedTop(world).getBlock().setType(Material.AIR);
    }
    
}
