package filip.bedwars.game;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.arena.Base;

public class Team {
    private final int id;
    private final Base base;
    private final List<UUID> members;
    
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
    
    public void removeMember(@NotNull UUID uuid) {
    	members.remove(uuid);
    }
    
    public List<UUID> getMembers() {
    	return members;
    }
    
}
