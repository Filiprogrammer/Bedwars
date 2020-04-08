package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.utils.MessageSender;

public class FinishArenaCommand implements ICommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != 0)
			return false;
		
		// TODO: Add localizations for the messages
		
		if (!(sender instanceof Player))
			MessageSender.sendMessage(sender, "You have to be a player");
		
		if (BedwarsPlugin.getInstance().finishArenaSetup((Player) sender))
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(((Player)sender).getLocale(), "arena-setup-finish"));
		else
			MessageSender.sendMessage(sender, "You were not setting up an arena anyway.");
		
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
