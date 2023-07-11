package filip.bedwars.sign;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import filip.bedwars.config.ArenaConfig;
import filip.bedwars.config.JoinSignConfig;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.GameManager;
import filip.bedwars.game.arena.Arena;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class GameJoinSignListener implements Listener {

	public GameJoinSignListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		
		if (player.hasPermission("filip.bedwars.setup")) {
			if (event.getLine(0).equalsIgnoreCase("[bedwars]")) {
				String mapName = event.getLine(1);
				event.setLine(0, MainConfig.getInstance().getJoinSignLine(0).replace("%arenaname%", mapName));
				event.setLine(1, MainConfig.getInstance().getJoinSignLine(1).replace("%arenaname%", mapName));
				event.setLine(2, MainConfig.getInstance().getJoinSignLine(2).replace("%arenaname%", mapName));
				event.setLine(3, MainConfig.getInstance().getJoinSignLine(3).replace("%arenaname%", mapName));
				GameJoinSign joinSign = new GameJoinSign(event.getBlock().getLocation(), mapName);
				JoinSignConfig.getInstance().addJoinSign(joinSign);
				JoinSignConfig.getInstance().saveConfig();
			}
		}
	}
	
	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		
		if (!player.hasPermission("filip.bedwars.play.sign"))
			return;
		
		if (!(block.getState() instanceof Sign))
			return;
		
		GameJoinSign joinSign = JoinSignConfig.getInstance().getGameJoinSignAt(block.getLocation());
		
		if (joinSign == null)
			return;
		
		String mapName = joinSign.getMapName();
		Arena arena = ArenaConfig.getInstance().getArena(mapName);
		
		if (arena == null) {
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "arena-not-found").replace("%arenaname%", mapName));
			SoundPlayer.playSound("error", player);
			return;
		}
		
		GameManager.getInstance().joinGame(arena, event.getPlayer());
	}
	
	@EventHandler
	public void onSignBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		
		if (!(block.getState() instanceof Sign))
			return;
		
		GameJoinSign joinSign = JoinSignConfig.getInstance().getGameJoinSignAt(block.getLocation());
		
		if (joinSign == null)
			return;
		
		JoinSignConfig.getInstance().removeJoinSign(joinSign);
		JoinSignConfig.getInstance().saveConfig();
	}
	
}
