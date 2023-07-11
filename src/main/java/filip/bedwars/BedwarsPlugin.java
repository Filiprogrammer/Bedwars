package filip.bedwars;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
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
import filip.bedwars.commands.JoinGameCommand;
import filip.bedwars.commands.LeaveCommand;
import filip.bedwars.commands.ListArenasCommand;
import filip.bedwars.commands.ReloadCommand;
import filip.bedwars.commands.RemoveArenaCommand;
import filip.bedwars.commands.SetGameLobbyCommand;
import filip.bedwars.commands.SetMainLobbyCommand;
import filip.bedwars.commands.SkipCountdownCommand;
import filip.bedwars.config.ArenaConfig;
import filip.bedwars.game.Game;
import filip.bedwars.game.GameManager;
import filip.bedwars.game.arena.Arena;
import filip.bedwars.game.arena.setup.ArenaSetup;
import filip.bedwars.game.lobby.LobbyListener;
import filip.bedwars.inventory.IClickable;
import filip.bedwars.inventory.IPlacable;
import filip.bedwars.inventory.IUsable;
import filip.bedwars.listener.inventory.InventoryClickListener;
import filip.bedwars.listener.player.BlockPlaceListener;
import filip.bedwars.listener.player.IPacketListener;
import filip.bedwars.listener.player.PacketReader;
import filip.bedwars.listener.player.PlayerChangedWorldListener;
import filip.bedwars.listener.player.PlayerInteractListener;
import filip.bedwars.listener.player.PlayerQuitListener;
import filip.bedwars.listener.player.WorldInitHandler;
import filip.bedwars.listener.player.WorldInitListener;
import filip.bedwars.sign.GameJoinSignListener;
import filip.bedwars.utils.ReflectionUtils;

public class BedwarsPlugin extends JavaPlugin {

	private static BedwarsPlugin plugin;
	
	private MultiverseCore mv;
	private String serverVersion;
	
	private List<IClickable> clickables = new ArrayList<IClickable>();
	private List<IUsable> usables = new ArrayList<IUsable>();
	private List<IPlacable> placables = new ArrayList<IPlacable>();
	private List<ICommand> commands = new ArrayList<ICommand>();
	private ICommand helpCommand;
	private List<ArenaSetup> arenaSetups = new ArrayList<ArenaSetup>();
	private List<PacketReader> packetReaders = new ArrayList<PacketReader>();
	private WorldInitListener worldInitListener;
	
	public ReflectionUtils reflectionUtils;
	
	@Override
	public void onEnable() {
		plugin = this;
		reflectionUtils = new ReflectionUtils();
		
		mv = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");
		
		new PlayerInteractListener(this);
		new BlockPlaceListener(this);
		new InventoryClickListener(this);
		new PlayerQuitListener(this);
		new PlayerChangedWorldListener(this);
		worldInitListener = new WorldInitListener(this);
		new LobbyListener(this);
		new GameJoinSignListener(this);
		new BedwarsCommandExecutor(this);
		commands.add(new AddArenaCommand());
		commands.add(new FinishArenaCommand());
		commands.add(new CancelArenaSetupCommand());
		commands.add(new ReloadCommand());
		commands.add(new JoinGameCommand());
		commands.add(new SetMainLobbyCommand());
		commands.add(new SetGameLobbyCommand());
		commands.add(new RemoveArenaCommand());
		commands.add(new ListArenasCommand());
		commands.add(new LeaveCommand());
		commands.add(new SkipCountdownCommand());
		helpCommand = new HelpCommand();
		
		// TODO: Well fuck, cannot get this to work anymore
		/*try {
 			Class<?> tagsBlockClass = net.minecraft.tags.BlockTags.class;
			Field DRAGON_IMMUNEField = null;
			Field witherImmuneField = null;
			for (Field f : tagsBlockClass.getFields()) {
				System.out.println("Field: " + f.getName());
				if (net.minecraft.tags.BlockTags.DRAGON_IMMUNE == f.get(null)) {
					System.out.println("DRAGON_IMMUNE!!!");
					DRAGON_IMMUNEField = f;
					break;
				}
			}
			for (Field f : tagsBlockClass.getFields()) {
				System.out.println("Field: " + f.getName());
				if (net.minecraft.tags.BlockTags.WITHER_IMMUNE == f.get(null)) {
					System.out.println("WITHER_IMMUNE!!!");
					witherImmuneField = f;
					break;
				}
			}

 			//Field DRAGON_IMMUNEField = tagsBlockClass.getField("DRAGON_IMMUNE");
 			DRAGON_IMMUNEField.setAccessible(true);
			System.out.println("declfields: ");
			for (Field f : Field.class.getFields()) {
				System.out.println(f.getName());
			}
			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			modifiers.setInt(DRAGON_IMMUNEField, DRAGON_IMMUNEField.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
			//Field witherImmuneField = Class.forName("net.minecraft.tags.BlockTags").getField("WITHER_IMMUNE");
 			DRAGON_IMMUNEField.set(null, witherImmuneField.get(null));
 		} catch (Exception e) {
 			e.printStackTrace();
 		}*/
	}
	
	@Override
	public void onDisable() {
		for (Game game : new ArrayList<Game>(GameManager.getInstance().getGames()))
			game.endGame();
		
		for (PacketReader reader : packetReaders)
			reader.uninject();
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
    	ARENA_WITH_THAT_NAME_ALREADY_EXISTS,
    	SUCCESS
    };
    
    public SetupArenaResponse setupArena(String mapName, int minPlayersToStart, int playersPerTeam, Player setuper) {
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
    	
    	if(ArenaConfig.getInstance().getArena(mapName) != null)
    		// There is already an arena with the same name
    		return SetupArenaResponse.ARENA_WITH_THAT_NAME_ALREADY_EXISTS;
    	
    	arenaSetups.add(new ArenaSetup(mapName, minPlayersToStart, playersPerTeam, setuper));
    	return SetupArenaResponse.SUCCESS;
    }
    
    public enum FinishArenaSetupResponse {
    	NO_ARENA_SETTING_UP,
    	NOT_ENOUGH_BASES,
    	ARENA_CREATED
    };
    
    public FinishArenaSetupResponse finishArenaSetup(Player setuper) {
    	for (ArenaSetup arenaSetup : arenaSetups) {
    		if (arenaSetup.getSetuper().getUniqueId().equals(setuper.getUniqueId())) {
    			Arena arena = arenaSetup.finish();
    			if(arena == null)
    				return FinishArenaSetupResponse.NOT_ENOUGH_BASES;
    			arenaSetups.remove(arenaSetup);
    			ArenaConfig.getInstance().addArena(arena);
    			ArenaConfig.getInstance().saveConfig();
    			return FinishArenaSetupResponse.ARENA_CREATED;
    		}
    	}
    	
    	return FinishArenaSetupResponse.NO_ARENA_SETTING_UP;
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
    
    public void addPacketListener(Player player, IPacketListener packetListener) {
    	PacketReader packetReader = null;
    	
    	for (PacketReader pr : packetReaders) {
    		if (pr.getPlayer().equals(player)) {
    			packetReader = pr;
    			break;
    		}
    	}
    	
    	if (packetReader == null) {
    		packetReader = new PacketReader(player);
    		packetReaders.add(packetReader);
    	}
    	
    	packetReader.addListener(packetListener);
    }
    
    public boolean removePacketListener(Player player, IPacketListener packetListener) {
    	PacketReader packetReader = null;
    	
    	for (PacketReader pr : packetReaders) {
    		if (pr.getPlayer().equals(player)) {
    			packetReader = pr;
    			break;
    		}
    	}
    	
    	if (packetReader == null)
    		return false;
    	
    	boolean ret = packetReader.removeListener(packetListener);
    	
		if (!packetReader.hasListeners()) {
			packetReader.uninject();
			return packetReaders.remove(packetReader);
		}
    	
    	return ret;
    }
    
    public void addWorldInitHandler(WorldInitHandler handler) {
    	worldInitListener.addHandler(handler);
    }
    
    public boolean removeWorldInitHandler(WorldInitHandler handler) {
    	return worldInitListener.removeHandler(handler);
    }
    
    public MultiverseCore getMultiverse() {
    	return mv;
    }
    
    public String getServerVersion() {
    	if (serverVersion == null)
    		serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    	
    	return serverVersion;
    }
    
}
