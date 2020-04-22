package filip.bedwars.game.action;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.Game;
import filip.bedwars.game.GameLogic;

public class ActionSendTitle extends Action {

	private final String titleKey;
	private final String subtitleKey;
	private final boolean includeSpectators;
	private final int fadeIn;
	private final int stay;
	private final int fadeOut;
	
	public ActionSendTitle(@Nullable String titleKey, @Nullable String subtitleKey, @Nullable Integer fadeIn, @Nullable Integer stay, @Nullable Integer fadeOut, @NotNull Boolean includeSpectators) {
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
				String title;
				String subtitle;
				
				if (titleKey == null)
					title = "";
				else
					title = MessagesConfig.getInstance().getStringValue(p.getLocale(), titleKey);
				
				if (subtitleKey == null)
					subtitle = "";
				else
					subtitle = MessagesConfig.getInstance().getStringValue(p.getLocale(), subtitleKey);
				
				p.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
			}
		} else {
			for (UUID uuid : game.getPlayers()) {
				Player p = Bukkit.getPlayer(uuid);
				
				if (p == null)
					continue;
				
				String title;
				String subtitle;
				
				if (titleKey == null)
					title = "";
				else
					title = MessagesConfig.getInstance().getStringValue(p.getLocale(), titleKey);
				
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
