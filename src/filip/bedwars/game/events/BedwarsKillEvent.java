package filip.bedwars.game.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsKillEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Player killer;
	private Player victim;
	
	public BedwarsKillEvent(Player killer, Player victim) {
		this.killer = killer;
		this.victim = victim;
	}
	
	public Player getKiller() {
		return killer;
	}
	
	public Player getVictim() {
		return victim;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
