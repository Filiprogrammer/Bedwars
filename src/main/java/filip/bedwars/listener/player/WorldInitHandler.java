package filip.bedwars.listener.player;

import org.bukkit.event.world.WorldInitEvent;

// TODO: Shouldn't this be in filip.bedwars.listener.world ?
public abstract class WorldInitHandler {

	public abstract void onWorldInit(WorldInitEvent event);
	
}
