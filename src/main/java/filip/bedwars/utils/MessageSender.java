package filip.bedwars.utils;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;

public class MessageSender {

	/**
	 * Send a message to the given sender.
	 * @param sendTo Player or Console that should receive the message
	 * @param msg Message that should be sent
	 */
	public static void sendMessage(@NotNull CommandSender sendTo, String msg) {
		String locale;
		
		if (sendTo instanceof Player)
			locale = ((Player)sendTo).getLocale();
		else
			locale = MainConfig.getInstance().getLanguage();
		
		if(msg == null) 
			msg = "§cA message was not found! Please check your messages.yml for §d\"" + locale + "\" §cor contact the server administrator!";
		
		sendTo.sendMessage((MessagesConfig.getInstance().getStringValue(locale, "prefix") + msg));
	}
	
	/**
	 * Send a message to a group.
	 * @param receivers List of players and console that should receive the message
	 * @param msg Message that should be sent
	 */
	public static void sendMessage(@NotNull List<CommandSender> receivers, String msg) {
		for(CommandSender cs : receivers) {
			String locale;
			
			if (cs instanceof Player)
				locale = ((Player)cs).getLocale();
			else
				locale = MainConfig.getInstance().getLanguage();
			
			cs.sendMessage((MessagesConfig.getInstance().getStringValue(locale, "prefix") + msg));
		}
	}
	
	/**
	 * Send a message to a player.
	 * @param receivers List of players as UUID
	 * @param msg Message that should be sent
	 */
	public static void sendMessageUUID(UUID receiver, String msg) {
		Player player = Bukkit.getPlayer(receiver);
		
		if (player != null) {
			String locale = player.getLocale();
			
			if(msg == null) 
				msg = "§cA message was not found! Please check your messages.yml for §d\"" + locale + "\" §cor contact the server administrator!";
			
			player.sendMessage((MessagesConfig.getInstance().getStringValue(locale, "prefix") + msg));
		}
	}
	
	/**
	 * Send a message to a group.
	 * @param receivers List of players as UUID
	 * @param msg Message that should be sent
	 */
	public static void sendMessageUUID(@NotNull List<UUID> receivers, String msg) {
		for(UUID uuid : receivers) {
			Player player = Bukkit.getPlayer(uuid);
			
			if (player != null) {
				String locale = player.getLocale();
				player.sendMessage((MessagesConfig.getInstance().getStringValue(locale, "prefix") + msg));
			}
		}
	}
	
	/**
	 * Print a warning in the console.
	 * @param msg Warning that is sent
	 */
	public static void sendWarning(String msg) {
		if(msg == null) 
			msg = "§cAn error occured! The warning you should receive was null! I really don't know what happened, this definitely should NOT happen!";
		
		Bukkit.getConsoleSender().sendMessage("§6[WARNING]: §e" + msg);
	}
	
}
