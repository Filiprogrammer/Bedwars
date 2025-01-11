package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class CancelArenaSetupCommand implements ICommand {

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull final String[] args) {
		if (args.length != getArguments().length)
			return false;

		if (!(sender instanceof Player)) {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(MainConfig.getInstance().getLanguage(), "you-must-be-player"));
			return true;
		}

		Player player = (Player) sender;
		final String locale = player.getLocale();

		if (BedwarsPlugin.getInstance().cancelArenaSetup(player)) {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(locale, "arena-setup-cancelled"));
			SoundPlayer.playSound("cancel", player);
		} else {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(locale, "arena-you-were-not-setup"));
			SoundPlayer.playSound("error", player);
		}

		return true;
	}

	@Override
	@NotNull
	public String getPermission() {
		return "setup";
	}

	@Override
	@NotNull
	public String getName() {
		return "cancelarenasetup";
	}

	@Override
	@NotNull
	public String[] getArguments() {
		return new String[0];
	}

}
