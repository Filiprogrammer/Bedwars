package filip.bedwars.game;

public abstract class GameState {
	
	private String name;
	private Countdown countdown;
	
	public GameState(String name, Countdown countdown) {
		this.name = name;
		this.countdown = countdown;
	}
	
	public String getName() {
		return name;
	}
	
	public Countdown getCountdown() {
		return countdown;
	}
	
	public void initiate() {
		countdown.start();
	}
	
	public abstract void onInitiate();
	
}
