package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import filip.bedwars.config.ArenaConfig;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.GameManager;
import filip.bedwars.game.arena.Arena;
import filip.bedwars.utils.MessageSender;

public class JoinGameCommand implements ICommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != 1)
			return false;
		
		if (!(sender instanceof Player)) {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(MainConfig.getInstance().getLanguage(), "you-must-be-player"));
			return true;
		}
		
		Arena arena = ArenaConfig.getInstance().getArena(args[0]);
		String locale = ((Player) sender).getLocale();
		
		if (arena == null)
			// TODO: Read message from config
			MessageSender.sendMessage(sender, "Arena with the name " + args[0] + " was not found");
		else
			GameManager.getInstance().joinGame(arena, (Player) sender);
		
		return true;
	}

	@Override
	public String getPermission() {
		return "play";
	}

	@Override
	public String getName() {
		return "joingame";
	}
	
}
