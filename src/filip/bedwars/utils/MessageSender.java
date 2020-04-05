package filip.bedwars.utils;

import java.util.List;

import org.bukkit.command.CommandSender;

import filip.bedwars.config.MessagesConfig;

public class MessageSender {

	/**
	 * Send a Message to the given sender.
	 * @param sendTo Player or Console that should receive the message
	 * @param msg Message that should be sent
	 */
	public static void sendMessage(CommandSender sendTo, String msg) {
		sendTo.sendMessage(MessagesConfig.getInstance().getStringValue("prefix") + msg);
	}
	
	/**
	 * Send a Message to a group.
	 * @param receivers List of players and console that should receive the message
	 * @param msg Message that should be sent
	 */
	public static void sendMessage(List<CommandSender> receivers, String msg) {
		for(CommandSender cs : receivers) {
			cs.sendMessage(MessagesConfig.getInstance().getStringValue("prefix") + msg);
		}
		
	}
	
}
