package filip.bedwars.listener.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import filip.bedwars.BedwarsPlugin;

public class PlayerQuitListener implements Listener {
	
	private List<PlayerQuitHandler> handlers = new ArrayList<PlayerQuitHandler>();
	
	public PlayerQuitListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		BedwarsPlugin.getInstance().cancelArenaSetup(event.getPlayer());
		
		for (PlayerQuitHandler handler : handlers)
			handler.onQuit(event);
	}
	
	public void addHandler(PlayerQuitHandler handler) {
		handlers.add(handler);
	}
	
	public boolean removeHandler(PlayerQuitHandler handler) {
		return handlers.remove(handler);
	}
	
}
