package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.utils.MessageSender;

public class FinishArenaCommand implements ICommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != 0)
			return false;
		
		if (!(sender instanceof Player)) {
			// TODO: Add localizations for this message
			MessageSender.sendMessage(sender, "You have to be a player");
		}
		
		BedwarsPlugin.getInstance().finishArenaSetup((Player) sender);
		
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
