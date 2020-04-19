package filip.bedwars;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import filip.bedwars.commands.ICommand;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.utils.MessageSender;

public class BedwarsCommandExecutor implements CommandExecutor {
	
	public BedwarsCommandExecutor(JavaPlugin plugin) {
		PluginCommand pluginCommand = plugin.getCommand("bw");
		pluginCommand.setExecutor(this);
		pluginCommand.setTabCompleter(new TabCompleter() {
			@Override
			public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
				if (!command.getName().equalsIgnoreCase("bw"))
					return null;
				
				List<String> ret = new ArrayList<String>();
				
				if (args.length != 0) {
					List<ICommand> commands = BedwarsPlugin.getInstance().getCommands();
					
					for (ICommand cmd : commands) {
						if (sender.hasPermission("filip.bedwars." + cmd.getPermission())) {
							String[] cmdArgs = new String[args.length - 1];
							
							for (int i = 0; i < cmdArgs.length; ++i)
								cmdArgs[i] = args[1 + i];
							
							List<String> suggestions = new ArrayList<String>();
							
							if (cmd.getName().equalsIgnoreCase(args[0])) {
								if (args.length != 1 && (args.length - 1) <= cmd.getArguments().length) {
									List<String> altSuggestions = cmd.getSuggestions(args.length - 2, args[args.length - 1]);
									
									if (altSuggestions == null)
										suggestions.add("<" + cmd.getArguments()[args.length - 2] + ">");
									else
										suggestions.addAll(altSuggestions);
								}
							} else if (args.length == 1 && cmd.getName().startsWith(args[0])) {
								suggestions.add(cmd.getName());
							}
							
							ret.addAll(suggestions);
						}
					}
				}
				
				return ret;
			}
		});
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!command.getName().equalsIgnoreCase("bw"))
			return false;
		
		if (!sender.hasPermission("filip.bedwars." + BedwarsPlugin.getInstance().getHelpCommand().getPermission()))
			return false;
		
		if (args.length != 0) {
			List<ICommand> commands = BedwarsPlugin.getInstance().getCommands();
			
			for (ICommand cmd : commands) {
				if (cmd.getName().equalsIgnoreCase(args[0])) {
					if (sender.hasPermission("filip.bedwars." + cmd.getPermission())) {
						String[] cmdArgs = new String[args.length - 1];
						
						for (int i = 0; i < cmdArgs.length; ++i)
							cmdArgs[i] = args[1 + i];
						
						if (!cmd.execute(sender, cmdArgs))
							break;
						
						return true;
					}
					
					String locale;
					
					if (sender instanceof Player)
						locale = ((Player)sender).getLocale();
					else
						locale = MainConfig.getInstance().getLanguage();
					
					MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(locale, "no-permission"));
				}
			}
		}
		
		return BedwarsPlugin.getInstance().getHelpCommand().execute(sender, args);
	}
	
}
