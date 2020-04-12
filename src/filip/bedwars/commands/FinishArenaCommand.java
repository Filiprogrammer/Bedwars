package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class FinishArenaCommand implements ICommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != 0)
			return false;
		
		if (!(sender instanceof Player)) {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(MainConfig.getInstance().getLanguage(), "you-must-be-player"));
			return true;
		}
		
		Player player = (Player) sender;
		
		if (BedwarsPlugin.getInstance().finishArenaSetup(player)) {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(player.getLocale(), "arena-setup-finish"));
			SoundPlayer.playSound("arena-setup", player);
		} else {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(player.getLocale(), "arena-you-were-not-setup"));
			SoundPlayer.playSound("error", player);
		}
		
		return true;
	}

	@Override
	public String getPermission() {
		return "setup";
	}

	@Override
	public String getName() {
		return "finisharena";
	}

}
