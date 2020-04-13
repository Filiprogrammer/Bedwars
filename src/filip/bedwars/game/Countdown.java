package filip.bedwars.game;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import filip.bedwars.BedwarsPlugin;

public abstract class Countdown {

	private final int totalSeconds;
	private int secondsLeft;
	private BukkitTask task = null;
	private BukkitRunnable bukkitRunnable;
	
	public Countdown(int totalSeconds) {
		this.totalSeconds = totalSeconds;
		secondsLeft = totalSeconds;
	}
	
	/**
	 * Start the countdown if it was not already started.
	 * @return false if the countdown was already started
	 */
	public boolean start() {
		if (!isRunning()) {
			secondsLeft = totalSeconds;
			bukkitRunnable = new BukkitRunnable() {
				@Override
				public void run() {
					if (secondsLeft == totalSeconds)
						onStart();
					
					--secondsLeft;
					
					if (secondsLeft == 0) {
						if (onFinish()) {
							secondsLeft = totalSeconds;
						} else {
							bukkitRunnable.cancel();
							task.cancel();
							task = null;
						}
					}
					
					onTick();
				}
			};
			task = bukkitRunnable.runTaskTimer(BedwarsPlugin.getInstance(), 0, 20L);
			return true;
		}
		
		return false;
    }
	
	public void cancel() {
		if (isRunning()) {
			bukkitRunnable.cancel();
			task.cancel();
			task = null;
			onCancel();
		}
	}
	
	public boolean isRunning() {
		if (task == null)
			return false;
		
		return !task.isCancelled();
	}
	
	public int getSecondsLeft() {
		return secondsLeft;
	}
	
	public void setSecondsLeft(int secondsLeft) {
		this.secondsLeft = secondsLeft;
	}
	
	public int getTotalSeconds() {
		return totalSeconds;
	}
	
	/**
	 * Called every second.
	 */
	public abstract void onTick();
	
	/**
	 * Called when the countdown is started.
	 */
	public abstract void onStart();
	
	/**
	 * Called when the countdown is finished.
	 * @return should restart
	 */
	public abstract boolean onFinish();
	
	/**
	 * Called when the countdown is canceled.
	 */
	public abstract void onCancel();

}
