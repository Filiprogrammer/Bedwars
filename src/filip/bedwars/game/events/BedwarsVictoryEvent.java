package filip.bedwars.game.events;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsVictoryEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	private Set<Player> winners;
	
	public BedwarsVictoryEvent(Set<Player> winners) {
		this.winners = winners;
	}
	
	public Set<Player> getWinners() {
		return winners;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
