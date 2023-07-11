package filip.bedwars.game.lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import filip.bedwars.BedwarsPlugin;
import filip.bedwars.config.MainConfig;
import filip.bedwars.config.MessagesConfig;
import filip.bedwars.game.Countdown;
import filip.bedwars.game.Game;
import filip.bedwars.game.GamePlayer;
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
	private Map<UUID, BossBar> bossbars = new HashMap<>();
	private boolean showBossbar = MainConfig.getInstance().getLobbyBossBar();
	
	public Lobby(Location spawnPoint, Game game) {
		this.spawnPoint = spawnPoint;
		this.game = game;
		
		this.countdown = new Countdown(MainConfig.getInstance().getGameLobbyCountdown()) {
			
			@Override
			public void onTick() {
				if (game.getPlayers().size() < game.getArena().getMinPlayersToStart()) {
					// Not enough players, countdown should be cancelled
					for (GamePlayer gamePlayer : game.getPlayers()) {
						Player player = gamePlayer.getPlayer();
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
					for (GamePlayer gamePlayer : game.getPlayers()) {
						Player player = gamePlayer.getPlayer();
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "game-starts-in-one-second"));
						SoundPlayer.playSound("countdown-tick", player);
					}
				} else if ((secondsLeft % 10) == 0 || secondsLeft <= 5) {
					for (GamePlayer gamePlayer : game.getPlayers()) {
						Player player = gamePlayer.getPlayer();
						MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "game-starts-in").replace("%seconds%", "" + secondsLeft));
						SoundPlayer.playSound("countdown-tick", player);
					}
				}
				
				if (showBossbar) {
					for (UUID uuid : bossbars.keySet()) {
						BossBar bossbar = bossbars.get(uuid);
						bossbar.setProgress((double) secondsLeft / getTotalSeconds());
						
						if (secondsLeft == 1)
							bossbar.setTitle(MessagesConfig.getInstance().getStringValue(Bukkit.getPlayer(uuid).getLocale(), "game-starts-in-one-second"));
						else
							bossbar.setTitle(MessagesConfig.getInstance().getStringValue(Bukkit.getPlayer(uuid).getLocale(), "game-starts-in").replace("%seconds%", "" + secondsLeft));
					}
				}
			}
			
			@Override
			public void onStart() {
				for(GamePlayer gamePlayer : game.getPlayers()) {
					Player player = gamePlayer.getPlayer();
					MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "countdown-started"));
				}
			}
			
			@Override
			public boolean onFinish() {
				if (game.getPlayers().size() < game.getArena().getMinPlayersToStart()) {
					// Not enough players, countdown should be cancelled
					for (GamePlayer gamePlayer : game.getPlayers()) {
						Player player = gamePlayer.getPlayer();
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
				for(GamePlayer gamePlayer : game.getPlayers())
					MessageSender.sendMessage(gamePlayer.getPlayer(), "The countdown was cancelled");
				
				if (showBossbar) {
					for (BossBar bossbar : bossbars.values()) {
						bossbar.setProgress(1);
						bossbar.setTitle(null);
					}
				}
			}
		};
	}
	
	public Countdown getCountdown() {
		return countdown;
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
		
		if (showBossbar) {
			BossBar bossbar = Bukkit.createBossBar(null, MainConfig.getInstance().getLobbyBossBarColor(), BarStyle.SOLID);
			bossbar.addPlayer(player);
			bossbars.put(player.getUniqueId(), bossbar);
		}
		
		// Make sure only players of the same game see each other
		for (Player p : spawnPoint.getWorld().getPlayers()) {
			if (!game.containsPlayer(p.getUniqueId())) {
				PlayerUtils.hidePlayerEntity(p, player);
				PlayerUtils.hidePlayerEntity(player, p);
				PlayerUtils.hidePlayer(p, player);
				PlayerUtils.hidePlayer(player, p);
			}
		}
		
		if (!countdown.isRunning() && (game.getPlayers().size() >= game.getArena().getMinPlayersToStart()))
			countdown.start();
		
		IClickable clickable = new ClickableInventory(Bukkit.createInventory(null, 9 * 2, MessagesConfig.getInstance().getStringValue(player.getLocale(), "item-select-team")), player) {
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
					GamePlayer gamePlayer = game.getGamePlayer(puuid);
					Team previousTeam = gamePlayer.getTeam();
					
					if (previousTeam != null)
						previousTeam.removeMember(gamePlayer);
					
					newTeam.addMember(gamePlayer);
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
					MessageSender.sendMessage(p, MessagesConfig.getInstance().getStringValue(p.getLocale(), "team-full"));
					SoundPlayer.playSound("error", p);
				}
			}
		};
		
		clickables.add(clickable);
		
		IUsable usable = new UsableItem(new ItemBuilder().setMaterial(Material.GLOWSTONE_DUST).setName(MessagesConfig.getInstance().getStringValue(player.getLocale(), "item-select-team")).build(), player) {
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
		player.getInventory().setItem(4, usable.getItemStack());
		updateTeamSelectorLores();
		
		if (MainConfig.getInstance().getLobbySkipCountdown() > 0 && player.hasPermission("filip.bedwars.lobby.skip")) {
			usable = new UsableItem(new ItemBuilder().setMaterial(Material.DIAMOND).setName(MessagesConfig.getInstance().getStringValue(player.getLocale(), "item-skip-lobby")).build(), player) {
				@Override
				public void use(PlayerInteractEvent event) {
					Player p = event.getPlayer();
					
					if (p != player)
						return;
					
					if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
						skipLobbyCountdown(p);
				}
			};
			
			usables.add(usable);
			player.getInventory().setItem(0, usable.getItemStack());
		}
		
		usable = new UsableItem(new ItemBuilder().setMaterial(Material.RED_BED).setName(MessagesConfig.getInstance().getStringValue(player.getLocale(), "item-leave-game")).build(), player) {
			@Override
			public void use(PlayerInteractEvent event) {
				Player p = event.getPlayer();
				
				if (p != player)
					return;
				
				if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
					leavePlayer(p);
			}
		};
		
		usables.add(usable);
		player.getInventory().setItem(8, usable.getItemStack());
		
		usable = new UsableItem(new ItemBuilder().setMaterial(Material.BOOK).setName("§rTutorial").build(), player) {
			@Override
			public void use(PlayerInteractEvent event) {
				if (event.getPlayer() == player) {
					ItemStack bookTutorial = new ItemStack(Material.WRITTEN_BOOK);
					BookMeta bookMeta = (BookMeta) bookTutorial.getItemMeta();
					bookMeta.setTitle("Tutorial");
					bookMeta.setAuthor("Bedwars");
					bookMeta.addPage(MessagesConfig.getInstance().getStringValue(player.getLocale(), "tutorial-book-page-1").replace("\\n", "\n"));
					bookMeta.addPage(MessagesConfig.getInstance().getStringValue(player.getLocale(), "tutorial-book-page-2").replace("\\n", "\n"));
					bookMeta.addPage(MessagesConfig.getInstance().getStringValue(player.getLocale(), "tutorial-book-page-3").replace("\\n", "\n"));
					bookMeta.addPage(MessagesConfig.getInstance().getStringValue(player.getLocale(), "tutorial-book-page-4").replace("\\n", "\n"));
					bookMeta.addPage(MessagesConfig.getInstance().getStringValue(player.getLocale(), "tutorial-book-page-5").replace("\\n", "\n"));
					bookMeta.addPage(MessagesConfig.getInstance().getStringValue(player.getLocale(), "tutorial-book-page-6").replace("\\n", "\n"));
					bookTutorial.setItemMeta(bookMeta);
					player.openBook(bookTutorial);
				}
			}
		};
		
		usables.add(usable);
		player.getInventory().setItem(2, usable.getItemStack());
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
		
		if (showBossbar)
			bossbars.get(player.getUniqueId()).removeAll();
		
		updateTeamSelectorLores();
		player.teleport(MainConfig.getInstance().getMainLobby());
	}
	
	public void cleanup() {
		for (IClickable clickable : clickables)
			BedwarsPlugin.getInstance().removeClickable(clickable);
		
		for (IUsable usable : usables)
			BedwarsPlugin.getInstance().removeUsable(usable);
		
		if (showBossbar) {
			for (BossBar bossbar : bossbars.values())
				bossbar.removeAll();
		}
	}
	
	public void updateTeamSelectorLores() {
		for (IClickable clickable : clickables) {
			ItemStack[] contents = clickable.getInventory().getContents();
			
			for (int i = 0; i < game.getTeams().size(); ++i) {
				List<String> lore = new ArrayList<>();
				
				for (GamePlayer gp : game.getTeams().get(i).getMembers())
					lore.add(gp.getPlayer().getName());
				
				contents[i].setLore(lore);
			}
		}
	}
	
	public void skipLobbyCountdown(Player player) {
		if (!countdown.isRunning() || countdown.getSecondsLeft() <= MainConfig.getInstance().getLobbySkipCountdown()) {
			MessageSender.sendMessage(player, MessagesConfig.getInstance().getStringValue(player.getLocale(), "countdown-cannot-skip"));
			SoundPlayer.playSound("error", player);
			return;
		}
		
		countdown.setSecondsLeft(MainConfig.getInstance().getLobbySkipCountdown());
		
		for(GamePlayer gamePlayer : game.getPlayers())
			MessageSender.sendMessage(gamePlayer.getPlayer(), MessagesConfig.getInstance().getStringValue(gamePlayer.getPlayer().getLocale(), "countdown-skipped").replace("%seconds%", String.valueOf(game.getLobby().getCountdown().getSecondsLeft())).replace("%player%", player.getDisplayName()));
	}
	
}
