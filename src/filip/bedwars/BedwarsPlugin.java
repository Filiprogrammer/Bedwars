package filip.bedwars;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class BedwarsPlugin extends JavaPlugin {

	BedwarsCommand cmd = null;
	
	@Override
	public void onEnable() {
		cmd = new BedwarsCommand();
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return cmd.onCommand(sender, command, label, args);
	}
	
	public BedwarsPlugin getInstance() {
		return this;
	}
	
}
