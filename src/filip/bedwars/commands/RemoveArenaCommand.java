package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import filip.bedwars.config.ArenaConfig;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class RemoveArenaCommand implements ICommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != getArguments().length)
			return false;
		
		String locale;
		Player player = null;
		
		if(sender instanceof Player) {
			player = (Player) sender;
			locale = player.getLocale();
		} else {
			locale = MainConfig.getInstance().getLanguage();
		}
		
		if(ArenaConfig.getInstance().removeArena(args[0])) {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(locale, "arena-removed").replace("%arenaname%", args[0]));
		
			if(player != null)
				SoundPlayer.playSound("success", player);
			
			ArenaConfig.getInstance().saveConfig();
		} else {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(locale, "arena-not-found").replace("%arenaname%", args[0]));
			
			if(player != null)
				SoundPlayer.playSound("error", player);
		}
			
		return true;
	}

	public String getPermission() {
		return "setup";
	}

	public String getName() {
		return "removearena";
	}
		
	public String[] getArguments() {
		return new String[] { "arenaname" };
	}
}
