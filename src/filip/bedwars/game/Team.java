package filip.bedwars.game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.arena.Base;

public class Team {
    private final int id;
    private final Base base;
    private final List<GamePlayer> members = new ArrayList<GamePlayer>();
    private boolean hasBed = true;
    
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
    
    public boolean hasBed() {
    	return hasBed;
    }
    
    public void destroyBed(World world) {
    	hasBed = false;
    	base.getBedBottom(world).getBlock().setType(Material.AIR);
    	base.getBedTop(world).getBlock().setType(Material.AIR);
    }
    
}
