package filip.bedwars.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.utils.MessageSender;

public class HelpCommand implements ICommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String locale;
		
		if (sender instanceof Player) {
			Player player = (Player) sender;
			locale = player.getLocale();
		} else {
			locale = MainConfig.getInstance().getLanguage();
		}
		
		List<ICommand> commands = BedwarsPlugin.getInstance().getCommands();
		
		MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(locale, "available-commands"));
		for (ICommand command : commands) {
			if (sender.hasPermission("filip.bedwars." + command.getPermission())) {
				StringBuilder sb = new StringBuilder();
				sb.append("/bw " + command.getName());
				
				String[] arguments = command.getArguments();
				for (String argument : arguments) {
					sb.append(" <");
					sb.append(argument);
					sb.append(">");
				}
				
				MessageSender.sendMessage(sender,sb.toString());
			}
		}
		
		return true;
	}

	public String getPermission() {
		return "base";
	}

	public String getName() {
		return "";
	}

	public String[] getArguments() {
		return new String[0];
	}

}
