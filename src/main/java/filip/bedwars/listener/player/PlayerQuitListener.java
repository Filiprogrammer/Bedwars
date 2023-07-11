package filip.bedwars.listener.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import filip.bedwars.BedwarsPlugin;

public class PlayerQuitListener implements Listener {
	
	public PlayerQuitListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		BedwarsPlugin.getInstance().cancelArenaSetup(event.getPlayer());
	}
	
}
