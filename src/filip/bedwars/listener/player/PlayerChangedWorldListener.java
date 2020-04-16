package filip.bedwars.listener.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.utils.MessageSender;

public class PlayerChangedWorldListener implements Listener {
	
	public PlayerChangedWorldListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		if (BedwarsPlugin.getInstance().cancelArenaSetup(event.getPlayer()))
			MessageSender.sendMessage(event.getPlayer(), MessagesConfig.getInstance().getStringValue(event.getPlayer().getLocale(), "arena-setup-cancelled"));
	}
	
}
