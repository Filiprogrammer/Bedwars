package filip.bedwars.utils;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;

public class MessageSender {

	/**
	 * Send a Message to the given sender.
	 * @param sendTo Player or Console that should receive the message
	 * @param msg Message that should be sent
	 */
	public static void sendMessage(CommandSender sendTo, String msg) {
		String locale;
		
		if (sendTo instanceof Player)
			locale = ((Player)sendTo).getLocale();
		else
			locale = MainConfig.getInstance().getLanguage();
		
		sendTo.sendMessage(MessagesConfig.getInstance().getStringValue(locale, "prefix") + msg);
	}
	
	/**
	 * Send a Message to a group.
	 * @param receivers List of players and console that should receive the message
	 * @param msg Message that should be sent
	 */
	public static void sendMessage(List<CommandSender> receivers, String msg) {
		for(CommandSender cs : receivers) {
			String locale;
			
			if (cs instanceof Player)
				locale = ((Player)cs).getLocale();
			else
				locale = MainConfig.getInstance().getLanguage();
			
			cs.sendMessage(MessagesConfig.getInstance().getStringValue(locale, "prefix") + msg);
		}
		
	}
	
}
