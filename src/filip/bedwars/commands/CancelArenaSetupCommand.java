package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.utils.MessageSender;

public class CancelArenaSetupCommand implements ICommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != 0)
			return false;
		
		if (!(sender instanceof Player))
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(((Player) sender).getLocale(), "you-must-be-player"));
		
		if (BedwarsPlugin.getInstance().cancelArenaSetup((Player) sender))
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(((Player) sender).getLocale(), "arena-setup-cancelled"));
		else
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(((Player) sender).getLocale(), "arena-you-were-not-setup"));
		
		return true;
	}

	@Override
	public String getPermission() {
		return "setup";
	}

	@Override
	public String getName() {
		return "cancelarenasetup";
	}
	
}
