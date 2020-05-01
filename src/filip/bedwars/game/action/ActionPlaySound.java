package filip.bedwars.game.action;

import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import filip.bedwars.game.Game;
import filip.bedwars.game.GameLogic;
import filip.bedwars.utils.SoundPlayer;

public class ActionPlaySound extends Action {

	private final String sound;
	private final boolean includeSpectators;
	
	public ActionPlaySound(@NotNull String sound, @NotNull Boolean includeSpectators) {
		this.sound = sound;
		this.includeSpectators = includeSpectators;
	}

	@Override
	public void execute(@NotNull Game game, @NotNull GameLogic gameLogic) {
		if (includeSpectators)
			SoundPlayer.playSound(sound, gameLogic.getGameWorld().getWorld().getPlayers());
		else
			SoundPlayer.playSoundUUID(sound, game.getPlayers().stream().map(gp -> gp.uuid).collect(Collectors.toList()));
	}

	public static String[] getArgumentNames() {
		return new String[] { "sound", "include-spectators" };
	}

}
