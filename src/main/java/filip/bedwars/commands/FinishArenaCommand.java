package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.BedwarsPlugin.FinishArenaSetupResponse;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class FinishArenaCommand implements ICommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != getArguments().length)
			return false;
		
		if (!(sender instanceof Player)) {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(MainConfig.getInstance().getLanguage(), "you-must-be-player"));
			return true;
		}
		
		Player player = (Player) sender;
		FinishArenaSetupResponse finishArenaSetupResponse = BedwarsPlugin.getInstance().finishArenaSetup(player);
		
		switch (finishArenaSetupResponse) {
		case ARENA_CREATED:
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "arena-setup-finish"));
			SoundPlayer.playSound("arena-setup", player);
			break;
		case NO_ARENA_SETTING_UP:
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "arena-you-were-not-setup"));
			SoundPlayer.playSound("error", player);
			break;
		case NOT_ENOUGH_BASES:
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "arena-not-enough-bases"));
			SoundPlayer.playSound("error", player);
			break;
		}
		
		return true;
	}

	public String getPermission() {
		return "setup";
	}

	public String getName() {
		return "finisharena";
	}
	
	public String[] getArguments() {
		return new String[0];
	}

}
