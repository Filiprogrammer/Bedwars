package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.config.ArenaConfig;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.utils.MessageSender;

public class ListArenasCommand implements ICommand {
	
	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull final String[] args) {
		if (args.length != getArguments().length)
			return false;
		
		final String locale;
		
		if (sender instanceof Player) {
			Player player = (Player) sender;
			locale = player.getLocale();
		} else {
			locale = MainConfig.getInstance().getLanguage();
		}
		
		if(ArenaConfig.getInstance().getArenaCount() == 0) {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(locale, "no-arena-found"));
			return true;
		}
		
		MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(locale, "arenas"));
		
		for (int i = 0; i < ArenaConfig.getInstance().getArenaCount(); ++i) {
			StringBuilder sb = new StringBuilder();
			sb.append("§d[");
			sb.append(i + 1);
			sb.append("]: §9");
			sb.append(ArenaConfig.getInstance().getArena(i).getMapName());
			MessageSender.sendMessage(sender, sb.toString());
		}

		return true;
	}

	@Override
	@NotNull
	public String getPermission() {
		return "base";
	}

	@Override
	@NotNull
	public String getName() {
		return "listarenas";
	}

	@Override
	@NotNull
	public String[] getArguments() {
		return new String[0];
	}
	
}
