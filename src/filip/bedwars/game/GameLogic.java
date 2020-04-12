package filip.bedwars.game;

import java.util.List;

import filip.bedwars.game.arena.Arena;
import filip.bedwars.world.GameWorld;

public class GameLogic {

	private Game game;
	private Arena arena;
	private GameWorld gameWorld;
	private List<Team> teams;
	private GameState gameState;
	
	public GameLogic(List<Team> teams, Game game, Arena arena, GameWorld gameWorld) {
		this.game = game;
		this.arena = arena;
		this.gameWorld = gameWorld;
		this.teams = teams;
		this.gameState = new GameState("PHASE_1", new Countdown(300) {
			
			@Override
			public void onTick() {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
			}
			
			@Override
			public boolean onFinish() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
			}
		}) {
			
			@Override
			public void onInitiate() {
				
			}
		};
		this.gameState.initiate();
	}
	
}
