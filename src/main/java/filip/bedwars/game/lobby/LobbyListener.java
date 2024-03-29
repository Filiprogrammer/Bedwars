package filip.bedwars.game.lobby;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.plugin.java.JavaPlugin;

import filip.bedwars.config.MainConfig;

public class LobbyListener implements Listener {

	public LobbyListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		Player player = (Player) event.getEntity();
		
		if (player.getWorld().getName().equals(MainConfig.getInstance().getGameLobby().getWorld().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		Player player = (Player) event.getEntity();
		
		if (player.getWorld().getName().equals(MainConfig.getInstance().getGameLobby().getWorld().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		World world = event.getTo().getWorld();

		if (world.getName().equals(MainConfig.getInstance().getGameLobby().getWorld().getName())) {
			if (event.getTo().getY() < world.getMinHeight())
				event.setTo(MainConfig.getInstance().getGameLobby());
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if (player.getGameMode() != GameMode.CREATIVE) {
			if (player.getWorld().getName().equals(MainConfig.getInstance().getGameLobby().getWorld().getName())) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		
		if (player.getGameMode() != GameMode.CREATIVE) {
			if (player.getWorld().getName().equals(MainConfig.getInstance().getGameLobby().getWorld().getName())) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		
		if (player.getGameMode() != GameMode.CREATIVE) {
			if (player.getWorld().getName().equals(MainConfig.getInstance().getGameLobby().getWorld().getName())) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
		Player player = event.getPlayer();
		
		if (player.getGameMode() != GameMode.CREATIVE) {
			if (player.getWorld().getName().equals(MainConfig.getInstance().getGameLobby().getWorld().getName())) {
				event.setCancelled(true);
			}
		}
	}
	
}
