package filip.bedwars.listener.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.utils.MessageSender;

public class PlayerChangedWorldListener implements Listener {
	
	public PlayerChangedWorldListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		if (BedwarsPlugin.getInstance().cancelArenaSetup(event.getPlayer()))
			// TODO: Add localizations for the messages
			MessageSender.sendMessage(event.getPlayer(), "Arena setup was cancelled");
	}
	
}
