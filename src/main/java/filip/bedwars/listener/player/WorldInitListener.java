package filip.bedwars.listener.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldInitListener implements Listener {
	
	private List<WorldInitHandler> handlers = new ArrayList<WorldInitHandler>();
	
	public WorldInitListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onWorldInit(WorldInitEvent event) {
		for (WorldInitHandler handler : handlers)
			handler.onWorldInit(event);
	}
	
	public void addHandler(WorldInitHandler handler) {
		handlers.add(handler);
	}
	
	public boolean removeHandler(WorldInitHandler handler) {
		return handlers.remove(handler);
	}
	
}
