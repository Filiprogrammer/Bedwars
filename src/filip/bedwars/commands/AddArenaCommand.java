package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.utils.MessageSender;

public class AddArenaCommand implements ICommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != 1)
			return false;
		
		if (!(sender instanceof Player)) {
			// TODO: Add localizations for this message
			MessageSender.sendMessage(sender, "You have to be a player");
		}
		
		if (!BedwarsPlugin.getInstance().setupArena(args[0], (Player) sender)) {
			// TODO: Add localizations for this message
			MessageSender.sendMessage(sender, "An Arena in this world is already being set up or you are already setting up an arena.");
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
