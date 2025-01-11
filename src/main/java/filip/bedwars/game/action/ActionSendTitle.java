package filip.bedwars.game.action;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.Game;
import filip.bedwars.game.GameLogic;
import filip.bedwars.game.GamePlayer;

public class ActionSendTitle extends Action {

	private final String titleKey;
	private final String subtitleKey;
	private final boolean includeSpectators;
	private final int fadeIn;
	private final int stay;
	private final int fadeOut;

	public ActionSendTitle(@Nullable final String titleKey, @Nullable final String subtitleKey, @Nullable final Integer fadeIn, @Nullable final Integer stay, @Nullable final Integer fadeOut, @NotNull final Boolean includeSpectators) {
		this.titleKey = titleKey;
		this.subtitleKey = subtitleKey;
		this.includeSpectators = includeSpectators;
		this.fadeIn = (fadeIn == null) ? 10 : fadeIn;
		this.stay = (stay == null) ? 70 : stay;
		this.fadeOut = (fadeOut == null) ? 20 : fadeOut;
	}

	@Override
	public void execute(@NotNull Game game, @NotNull GameLogic gameLogic) {
		if(titleKey == null && subtitleKey == null)
			return;

		if (includeSpectators) {
			for (Player p : gameLogic.getGameWorld().getWorld().getPlayers()) {
				final String title;
				if (titleKey == null)
					title = "";
				else
					title = MessagesConfig.getInstance().getStringValue(p.getLocale(), titleKey);

				final String subtitle;
				if (subtitleKey == null)
					subtitle = "";
				else
					subtitle = MessagesConfig.getInstance().getStringValue(p.getLocale(), subtitleKey);

				p.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
			}
		} else {
			for (GamePlayer gamePlayer : game.getPlayers()) {
				Player p = gamePlayer.getPlayer();

				if (p == null)
					continue;

				final String title;
				if (titleKey == null)
					title = "";
				else
					title = MessagesConfig.getInstance().getStringValue(p.getLocale(), titleKey);

				final String subtitle;
				if (subtitleKey == null)
					subtitle = "";
				else
					subtitle = MessagesConfig.getInstance().getStringValue(p.getLocale(), subtitleKey);

				p.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
			}
		}
	}

	public static String[] getArgumentNames() {
		return new String[] { "title", "subtitle", "fade-in", "stay", "fade-out", "include-spectators" };
	}

}
