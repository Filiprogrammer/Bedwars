package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.BedwarsPlugin.SetupArenaResponse;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class AddArenaCommand implements ICommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != 3)
			return false;
		
		if (!(sender instanceof Player)) {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(MainConfig.getInstance().getLanguage(), "you-must-be-player"));
			return true;
		}
		
		int minPlayersToStart = 0;
		int playersPerTeam = 0;
		Player player = (Player) sender;
		
		try {
			minPlayersToStart = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(player.getLocale(), "min-start-player-must-be-number"));
			SoundPlayer.playSound("error", player);
		}
		
		try {
			playersPerTeam = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(player.getLocale(), "player-per-team-must-be-number"));
			SoundPlayer.playSound("error", player);
		}
		
		SetupArenaResponse setupArenaResponse = BedwarsPlugin.getInstance().setupArena(args[0], minPlayersToStart, playersPerTeam, (Player) sender);
		
		switch (setupArenaResponse) {
		case ARENA_IN_WORLD_ALREADY_SETTING_UP:
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(player.getLocale(), "arena-in-world-already-setting-up"));
			break;
		case ALREADY_SETTING_UP_ARENA:
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(player.getLocale(), "arena-already-setting-up"));
			break;
		case ARENA_IN_WORLD_ALREADY_EXISTS:
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(player.getLocale(), "world-has-arena"));
			break;
		case SUCCESS:
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(player.getLocale(), "arena-setup-started").replace("%mapname%", args[0]));
			break;
		}
		
		return true;
	}

	@Override
	public String getPermission() {
		return "setup";
	}

	@Override
	public String getName() {
		return "addarena";
	}
	
}
