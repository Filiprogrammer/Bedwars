package filip.bedwars.game;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;

public class Team {
    private final int id;
	private final TeamColor dyeColor;
    private final ChatColor chatColor;
    private final List<UUID> members;
    
    public Team(int id, TeamColor dyeColor, ChatColor chatColor, List<UUID> members) {
        this.id = id;
        this.dyeColor = dyeColor;
        this.chatColor = chatColor;
        this.members = members;
    }
    
    public int getId() {
    	return id;
    }
    
    public TeamColor getDyeColor() {
    	return dyeColor;
    }
    
    public ChatColor getChatColor() {
    	return chatColor;
    }
    
    public void addMember(UUID uuid) {
    	members.add(uuid);
    }
    
    public void removeMember(UUID uuid) {
    	members.remove(uuid);
    }
    
    public List<UUID> getMembers() {
    	return members;
    }
    
}
