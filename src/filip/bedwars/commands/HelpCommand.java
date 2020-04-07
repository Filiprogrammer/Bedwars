package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import filip.bedwars.config.MainConfig;

public class HelpCommand implements ICommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String locale;
		
		if (sender instanceof Player)
			locale = ((Player)sender).getLocale();
		else
			locale = MainConfig.getInstance().getLanguage();
		
		// TODO: print help
		// MessageSender.sendMessage(sender, msg);
		return false;
	}

	@Override
	public String getPermission() {
		return "base";
	}

	@Override
	public String getName() {
		return "";
	}

}
