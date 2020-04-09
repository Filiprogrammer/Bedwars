package filip.bedwars;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.onarandombox.MultiverseCore.MultiverseCore;

import filip.bedwars.commands.AddArenaCommand;
import filip.bedwars.commands.CancelArenaSetupCommand;
import filip.bedwars.commands.FinishArenaCommand;
import filip.bedwars.commands.HelpCommand;
import filip.bedwars.commands.ICommand;
import filip.bedwars.config.ArenaConfig;
import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.arena.setup.ArenaSetup;
import filip.bedwars.inventory.IClickable;
import filip.bedwars.inventory.IPlacable;
import filip.bedwars.inventory.IUsable;
import filip.bedwars.listener.inventory.InventoryClickListener;
import filip.bedwars.listener.player.BlockPlaceListener;
import filip.bedwars.listener.player.PlayerChangedWorldListener;
import filip.bedwars.listener.player.PlayerInteractListener;
import filip.bedwars.listener.player.PlayerQuitListener;

public class BedwarsPlugin extends JavaPlugin {

	private static BedwarsPlugin plugin;
	
	private MultiverseCore mv;
	
	private List<IClickable> clickables;
	private List<IUsable> usables;
	private List<IPlacable> placables;
	private List<ICommand> commands;
	private ICommand helpCommand;
	private List<ArenaSetup> arenaSetups;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		mv = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");
		
		clickables = new ArrayList<IClickable>();
		usables = new ArrayList<IUsable>();
		placables = new ArrayList<IPlacable>();
		commands = new ArrayList<ICommand>();
		arenaSetups = new ArrayList<ArenaSetup>();
		
		new PlayerInteractListener(this);
		new BlockPlaceListener(this);
		new InventoryClickListener(this);
		new PlayerQuitListener(this);
		new PlayerChangedWorldListener(this);
		new BedwarsCommandExecutor(this);
		commands.add(new AddArenaCommand());
		commands.add(new FinishArenaCommand());
		commands.add(new CancelArenaSetupCommand());
		helpCommand = new HelpCommand();
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public static BedwarsPlugin getInstance() {
		return plugin;
	}
	
	/**
     * Get a clickable.
     *
     * @param inventory
     * @return clickable
     */
    public IClickable getClickable(Inventory inventory, Player player) {
        for (int i = 0; i < clickables.size(); i++) {
            if (clickables.get(i).matches(inventory, player)) {
                return clickables.get(i);
            }
        }
        
        return null;
    }
    
    public void addClickable(IClickable clickable) {
    	clickables.add(clickable);
    }
    
    public void removeClickable(IClickable clickable) {
    	clickables.remove(clickable);
    }
    
    /**
     * Get a usable.
     *
     * @param inventory
     * @return usable
     */
    public IUsable getUsable(ItemStack itemStack, Player player) {
        for (int i = 0; i < usables.size(); i++) {
            if (usables.get(i).matches(itemStack, player)) {
                return usables.get(i);
            }
        }
        
        return null;
    }
    
    public void addUsable(IUsable usable) {
    	usables.add(usable);
    }
    
    public void removeUsable(IUsable usable) {
    	usables.remove(usable);
    }
    
    public IPlacable getPlacable(ItemStack itemStack, Player player) {
    	for (int i = 0; i < placables.size(); i++) {
            if (placables.get(i).matches(itemStack, player)) {
                return placables.get(i);
            }
        }
        
        return null;
    }
    
    public void addPlacable(IPlacable placable) {
    	placables.add(placable);
    }
    
    public void removePlacable(IPlacable placable) {
    	placables.remove(placable);
    }
	
    public List<ICommand> getCommands() {
    	return commands;
    }
    
    public ICommand getHelpCommand() {
    	return helpCommand;
    }
    
    public ArenaSetup getArenaSetup(Player setuper) {
    	for (ArenaSetup arenaSetup : arenaSetups)
    		if (arenaSetup.getSetuper().getUniqueId().equals(setuper.getUniqueId()))
    			return arenaSetup;
    	
    	return null;
    }
    
    public enum SetupArenaResponse {
    	ARENA_IN_WORLD_ALREADY_SETTING_UP,
    	ALREADY_SETTING_UP_ARENA,
    	ARENA_IN_WORLD_ALREADY_EXISTS,
    	SUCCESS
    };
    
    public SetupArenaResponse setupArena(String mapName, Player setuper) {
    	if (ArenaConfig.getInstance().getArena(setuper.getWorld()) != null)
			// An Arena already exists in this world
			return SetupArenaResponse.ARENA_IN_WORLD_ALREADY_EXISTS;
    	
    	for (ArenaSetup arenaSetup : arenaSetups) {
    		if (arenaSetup.getSetuper().getUniqueId().equals(setuper.getUniqueId()))
    			// The player is already setting up an arena
    			return SetupArenaResponse.ALREADY_SETTING_UP_ARENA;
    		
    		if (arenaSetup.getWorld().getName().equals(setuper.getWorld().getName()))
    			// An Arena in the setupers world is already being set up.
    			return SetupArenaResponse.ARENA_IN_WORLD_ALREADY_SETTING_UP;
    	}
    	
    	arenaSetups.add(new ArenaSetup(mapName, setuper));
    	return SetupArenaResponse.SUCCESS;
    }
    
    public boolean finishArenaSetup(Player setuper) {
    	for (ArenaSetup arenaSetup : arenaSetups) {
    		if (arenaSetup.getSetuper().getUniqueId().equals(setuper.getUniqueId())) {
    			Arena arena = arenaSetup.finish();
    			arenaSetups.remove(arenaSetup);
    			ArenaConfig.getInstance().addArena(arena);
    			ArenaConfig.getInstance().saveConfig();
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    public boolean cancelArenaSetup(Player setuper) {
    	for (ArenaSetup arenaSetup : arenaSetups) {
    		if (arenaSetup.getSetuper().getUniqueId().equals(setuper.getUniqueId())) {
    			arenaSetup.cancel();
    			arenaSetups.remove(arenaSetup);
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    public MultiverseCore getMultiverse() {
    	return mv;
    }
    
}
