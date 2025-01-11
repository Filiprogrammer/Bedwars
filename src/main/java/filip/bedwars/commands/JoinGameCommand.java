package filip.bedwars.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import filip.bedwars.config.ArenaConfig;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.GameManager;
import filip.bedwars.game.arena.Arena;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.SoundPlayer;

public class JoinGameCommand implements ICommand {

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull final String[] args) {
		if (args.length != getArguments().length)
			return false;

		if (!(sender instanceof Player)) {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(MainConfig.getInstance().getLanguage(), "you-must-be-player"));
			return true;
		}

		Arena arena = ArenaConfig.getInstance().getArena(args[0]);
		Player player = (Player) sender;
		final String locale = player.getLocale();

		if (arena == null) {
			MessageSender.sendMessage(sender, MessagesConfig.getInstance().getStringValue(locale, "arena-not-found").replace("%arenaname%", args[0]));
			SoundPlayer.playSound("error", player);
		} else {
			GameManager.getInstance().joinGame(arena, player);
		}

		return true;
	}

	@Override
	@NotNull
	public String getPermission() {
		return "play.command";
	}

	@Override
	@NotNull
	public String getName() {
		return "joingame";
	}

	@Override
	@NotNull
	public String[] getArguments() {
		return new String[] { "mapname" };
	}

	@Override
	@Nullable
	public List<String> getSuggestions(final int argIndex, @NotNull final String arg) {
		if (argIndex == 0) {
			List<String> ret = new ArrayList<String>();

			for (int i = 0; i < ArenaConfig.getInstance().getArenaCount(); ++i) {
				final String mapName = ArenaConfig.getInstance().getArena(i).getMapName();

				if (mapName.startsWith(arg))
					ret.add(mapName);
			}

			return ret;
		}

		return null;
	}

}
