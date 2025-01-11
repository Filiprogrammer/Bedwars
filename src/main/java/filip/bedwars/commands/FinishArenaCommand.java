package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.BedwarsPlugin.FinishArenaSetupResponse;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class FinishArenaCommand implements ICommand {

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull final String[] args) {
		if (args.length != getArguments().length)
			return false;

		if (!(sender instanceof Player)) {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(MainConfig.getInstance().getLanguage(), "you-must-be-player"));
			return true;
		}

		Player player = (Player) sender;
		final FinishArenaSetupResponse finishArenaSetupResponse = BedwarsPlugin.getInstance().finishArenaSetup(player);
		final String locale = player.getLocale();

		switch (finishArenaSetupResponse) {
		case ARENA_CREATED:
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(locale, "arena-setup-finish"));
			SoundPlayer.playSound("arena-setup", player);
			break;
		case NO_ARENA_SETTING_UP:
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(locale, "arena-you-were-not-setup"));
			SoundPlayer.playSound("error", player);
			break;
		case NOT_ENOUGH_BASES:
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(locale, "arena-not-enough-bases"));
			SoundPlayer.playSound("error", player);
			break;
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
		return "finisharena";
	}

	@Override
	@NotNull
	public String[] getArguments() {
		return new String[0];
	}

}
