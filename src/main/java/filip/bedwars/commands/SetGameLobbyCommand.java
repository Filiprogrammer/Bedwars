package filip.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class SetGameLobbyCommand implements ICommand {

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull final String[] args) {
		if (args.length != getArguments().length)
			return false;

		if(sender instanceof Player) {
			Player player = (Player) sender;

			MainConfig.getInstance().setGameLobby(player.getLocation());
			MainConfig.getInstance().saveConfig();

			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(player.getLocale(), "lobby-set"));
			SoundPlayer.playSound("success", player);
		} else {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(MainConfig.getInstance().getLanguage(), "you-must-be-player"));
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
		return "setgamelobby";
	}

	@Override
	@NotNull
	public String[] getArguments() {
		return new String[0];
	}

}
