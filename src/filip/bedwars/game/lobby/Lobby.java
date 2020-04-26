package filip.bedwars.game.lobby;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.Countdown;
import filip.bedwars.game.Game;
import filip.bedwars.game.Team;
import filip.bedwars.game.TeamColor;
import filip.bedwars.inventory.ClickableInventory;
import filip.bedwars.inventory.IClickable;
import filip.bedwars.inventory.IUsable;
import filip.bedwars.inventory.ItemBuilder;
import filip.bedwars.inventory.UsableItem;
import filip.bedwars.utils.MessageSender;
import filip.bedwars.utils.PlayerUtils;
import filip.bedwars.utils.SoundPlayer;
import filip.bedwars.utils.TeamColorConverter;

public class Lobby {
	
	private Location spawnPoint;
	private Countdown countdown;
	private Game game;
	private List<IClickable> clickables = new ArrayList<>();
	private List<IUsable> usables = new ArrayList<>();
	
	public Lobby(Location spawnPoint, Game game) {
		this.spawnPoint = spawnPoint;
		this.game = game;
		
		this.countdown = new Countdown(MainConfig.getInstance().getGameLobbyCountdown()) {
			
			@Override
			public void onTick() {
				if (game.getPlayers().size() < game.getArena().getMinPlayersToStart()) {
					// Not enough players, countdown should be cancelled
					for (UUID uuid : game.getPlayers()) {
						Player player = Bukkit.getPlayer(uuid);
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "countdown-not-enough-player"));
						SoundPlayer.playSound("error", player);
					}
					
					cancel();
					return;
				}
				
				int secondsLeft = getSecondsLeft();
				
				if (secondsLeft == 0)
					return;
				
				if (secondsLeft == 1) {
					for (UUID uuid : game.getPlayers()) {
						Player player = Bukkit.getPlayer(uuid);
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "game-starts-in-one-second"));
						SoundPlayer.playSound("countdown-tick", player);
					}
				} else if ((secondsLeft % 10) == 0 || secondsLeft <= 5) {
					for (UUID uuid : game.getPlayers()) {
						Player player = Bukkit.getPlayer(uuid);
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "game-starts-in").replace("%seconds%", "" + secondsLeft));
						SoundPlayer.playSound("countdown-tick", player);
					}
				}
			}
			
			@Override
			public void onStart() {
				for(UUID uuid : game.getPlayers()) {
					MessageSender.sendMessageUUID(uuid, MessagesConfig.getInstance().getStringValue(Bukkit.getPlayer(uuid).getLocale(), "countdown-started"));
				}
			}
			
			@Override
			public boolean onFinish() {
				if (game.getPlayers().size() < game.getArena().getMinPlayersToStart()) {
					// Not enough players, countdown should be cancelled
					for (UUID uuid : game.getPlayers()) {
						Player player = Bukkit.getPlayer(uuid);
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "countdown-not-enough-player"));
						SoundPlayer.playSound("cancel", player);
					}
					
					cancel();
					return true;
				}
				
				game.startGame();
				return false;
			}
			
			@Override
			public void onCancel() {
				for(UUID uuid : game.getPlayers())
					MessageSender.sendMessageUUID(uuid, "The countdown was cancelled");
			}
		};
	}
	
	public Location getSpawnPoint() {
		return spawnPoint;
	}
	
	/**
	 * Teleport player into the lobby.
	 * @param uuid player UUID
	 */
	public void joinPlayer(Player player) {
		player.teleport(spawnPoint);
		PlayerUtils.playerReset(player);
		
		// Make sure only players of the same game see each other
		for (Player p : spawnPoint.getWorld().getPlayers()) {
			if (!game.getPlayers().contains(p.getUniqueId())) {
				PlayerUtils.hidePlayerEntity(p, player);
				PlayerUtils.hidePlayerEntity(player, p);
				PlayerUtils.hidePlayer(p, player);
				PlayerUtils.hidePlayer(player, p);
			}
		}
		
		if (!countdown.isRunning() && (game.getPlayers().size() >= game.getArena().getMinPlayersToStart()))
			countdown.start();
		
		IClickable clickable = new ClickableInventory(Bukkit.createInventory(null, 9 * 2, "Select a team"), player) {
			{
				for (Team team : game.getTeams()) {
					TeamColor teamColor = team.getBase().getTeamColor();
					ItemStack itemStack = new ItemBuilder()
							.setMaterial(Material.valueOf(teamColor.toString() + "_WOOL"))
							.setName(TeamColorConverter.convertTeamColorToStringForMessages(teamColor, player.getLocale()))
							.build();
					inventory.addItem(itemStack);
				}
			}
			
			@Override
			public void drag(InventoryDragEvent event) {}
			
			@Override
			public void click(InventoryClickEvent event) {
				Player p = (Player) event.getWhoClicked();
				
				if (p != player)
					return;
				
				int slot = event.getSlot();
				
				if (slot >= game.getTeams().size())
					return;
				
				UUID puuid = p.getUniqueId();
				Team newTeam = game.getTeams().get(slot);
				
				if (newTeam.getMembers().size() < game.getArena().getPlayersPerTeam()) {
					Team previousTeam = game.getTeamOfPlayer(puuid);
					
					if (previousTeam != null)
						previousTeam.removeMember(puuid);
					
					newTeam.addMember(puuid);
					event.getView().close();
					MessageSender.sendMessage(p,
							MessagesConfig.getInstance().getStringValue(p.getLocale(), "team-changed")
							.replace("%teamcolor%", TeamColorConverter.convertTeamColorToStringForMessages(newTeam.getBase().getTeamColor(), p.getLocale())));
					
					for (ItemStack itemStack : inventory.getContents()) {
						if (itemStack != null)
							itemStack.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
					}
					
					event.getCurrentItem().addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
					updateTeamSelectorLores();
				} else {
					MessageSender.sendMessage(p, "The team is full");
					SoundPlayer.playSound("error", p);
				}
			}
		};
		
		clickables.add(clickable);
		
		IUsable usable = new UsableItem(new ItemBuilder().setMaterial(Material.GLOWSTONE_DUST).setName("§rTeam Selector").build(), player) {
			@Override
			public void use(PlayerInteractEvent event) {
				Player p = event.getPlayer();
				
				if (p != player)
					return;
				
				if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
					p.openInventory(clickable.getInventory());
			}
		};
		
		usables.add(usable);
		player.getInventory().setItem(8, usable.getItemStack());
	}
	
	/**
	 * Remove player from lobby.
	 * @param uuid player UUID
	 */
	public void leavePlayer(Player player) {
		Iterator<IClickable> iterClickables = clickables.iterator();
		
		while (iterClickables.hasNext()) {
			IClickable clickable = iterClickables.next();
			
			if (clickable.getPlayer() == player) {
				BedwarsPlugin.getInstance().removeClickable(clickable);
				iterClickables.remove();
			}
		}
		
		Iterator<IUsable> iter = usables.iterator();
		
		while (iter.hasNext()) {
			IUsable usable = iter.next();
			
			if (usable.getPlayer() == player) {
				BedwarsPlugin.getInstance().removeUsable(usable);
				iter.remove();
			}
		}
		
		updateTeamSelectorLores();
		player.teleport(MainConfig.getInstance().getMainLobby());
	}
	
	public void cleanup() {
		for (IClickable clickable : clickables)
			BedwarsPlugin.getInstance().removeClickable(clickable);
		
		for (IUsable usable : usables)
			BedwarsPlugin.getInstance().removeUsable(usable);
	}
	
	public void updateTeamSelectorLores() {
		for (IClickable clickable : clickables) {
			ItemStack[] contents = clickable.getInventory().getContents();
			
			for (int i = 0; i < game.getTeams().size(); ++i) {
				List<UUID> memberUUIDs = game.getTeams().get(i).getMembers();
				List<String> lore = new ArrayList<>();
				
				for (UUID uuid : memberUUIDs)
					lore.add(Bukkit.getPlayer(uuid).getName());
				
				contents[i].setLore(lore);
			}
		}
	}
	
}
