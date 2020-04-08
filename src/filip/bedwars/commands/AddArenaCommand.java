package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.BedwarsPlugin.SetupArenaResponse;
import filip.bedwars.utils.MessageSender;

public class AddArenaCommand implements ICommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != 1)
			return false;
		
		// TODO: Add localizations for the messages
		
		if (!(sender instanceof Player))
			MessageSender.sendMessage(sender, "You have to be a player");
		
		SetupArenaResponse setupArenaResponse = BedwarsPlugin.getInstance().setupArena(args[0], (Player) sender);
		
		switch (setupArenaResponse) {
		case ARENA_IN_WORLD_ALREADY_SETTING_UP:
			MessageSender.sendMessage(sender, "An arena in this world is already beeing set up");
			break;
		case ALREADY_SETTING_UP_ARENA:
			MessageSender.sendMessage(sender, "You are already setting up an arena");
			break;
		case ARENA_IN_WORLD_ALREADY_EXISTS:
			MessageSender.sendMessage(sender, "This world already has an arena");
			break;
		case SUCCESS:
			MessageSender.sendMessage(sender, "Began setup for arena with map name \"" + args[0] + "\"");
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
