package com.vytruvius.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener {

	static ArrayList<String> awaitingVerification = new ArrayList();
	static ArrayList<String> verified = new ArrayList();
	static Set<OfflinePlayer> operators;
	public static Main plugin;
	public static boolean passwordEnabled;

	@Override
	public void onEnable() {
		System.out.println(ChatColor.GREEN + "OP-SCREENING ENABLED.");

		this.getConfig().options().copyDefaults();
		saveDefaultConfig();

		plugin = this;
		passwordEnabled = this.getConfig().getBoolean("passwordEnabled");
		operators = Bukkit.getOperators();

		updateVerified();
		scanOperators();

		getCommand("ops").setExecutor(new opsCommand());
		Bukkit.getPluginManager().registerEvents(this, this);

	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {

		if (passwordEnabled == true) {

			Player player = e.getPlayer();
			UUID uuid = player.getUniqueId();

			if (player.isOp() == true) {

				for (OfflinePlayer p : operators) {

					if (verified.indexOf(p.getUniqueId().toString()) == -1) {

						player.setOp(false);
					} else {

						awaitingVerification.add(uuid.toString());
						player.sendMessage(ChatColor.GREEN + "You must enter the operator password before permissions are granted.");
					}
				}

			}
		}

	}

	@EventHandler
	public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {

		String args[] = e.getMessage().split(" ");
		int count = 0;
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();

		if ((!(awaitingVerification.indexOf(uuid.toString()) == -1)) && (!(args[0].equalsIgnoreCase("/ops") && (args[1].equalsIgnoreCase("pass") || args[1].equalsIgnoreCase("password"))))) {

			e.setCancelled(true);
			player.sendMessage(ChatColor.GREEN + "You must enter the operator password before permissions are granted.");
		}

		if (args.length == 2 && args[0].equalsIgnoreCase("/op")) {

			OfflinePlayer user = Bukkit.getOfflinePlayer(args[1]);
			UUID tempid = user.getUniqueId();
			operators = Bukkit.getOperators();

			if (verified.indexOf(tempid.toString()) == -1 ) {

				count++;

			}

			if (count > 0) {

				e.setCancelled(true);
				player.sendMessage(ChatColor.RED + "That user is not an authorized server operator.");
			}

		}

	}

	@EventHandler
	public void onServerCommand(ServerCommandEvent e) {

		String args[] = e.getCommand().split(" ");
		int count = 0;

		if (args.length == 2 && args[0].equalsIgnoreCase("op")) {

			OfflinePlayer user = Bukkit.getOfflinePlayer(args[1]);
			UUID tempid = user.getUniqueId();
			operators = Bukkit.getOperators();

			if (verified.indexOf(tempid.toString()) == -1 ) {

				count++;

			}

			if (count > 0) {

				e.setCancelled(true);
				System.out.println(ChatColor.RED + "That user is not an authorized server operator.");
			}


		}

	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {

		UUID uuid = e.getPlayer().getUniqueId();

		if (!(awaitingVerification.indexOf(uuid.toString()) == -1 )) {

			e.setCancelled(true);
		}
	}

	//This method adds all verified users listed in the config file to a String ArrayList called 'verified'
	//To be run on plugin enable. Values are also updated upon /ops reload. 
	public static void updateVerified() {

		verified.clear();

		FileConfiguration config = plugin.getConfig();
		List<String> OPList = config.getStringList("OPList");
		for (String f : OPList) {

			verified.add(f);
		}

	}

	public static int scanOperators() {

		int opsUpdated = 0;

		for (OfflinePlayer p : operators) {

			if (verified.indexOf(p.getUniqueId().toString()) == -1) {

				p.setOp(false);
				opsUpdated++;
			} 
		}

		operators = Bukkit.getOperators();
		return opsUpdated;
	}
	
	public static void reloadPlugin() {
		
		plugin.reloadConfig();
		updateVerified();
		passwordEnabled = Main.plugin.getConfig().getBoolean("passwordEnabled");
		operators = Bukkit.getOperators();
		
	}

	public static void helpCommand(Player player) {

		player.sendMessage(ChatColor.DARK_AQUA + "________oO " + ChatColor.RED + "OP Screen" + ChatColor.DARK_AQUA + " Oo________");
		player.sendMessage(ChatColor.AQUA + "/ops help " + ChatColor.WHITE + "-" + ChatColor.BLUE + " displays a list of plugin commands.");
		player.sendMessage(ChatColor.AQUA + "/ops reload " + ChatColor.WHITE + "-" + ChatColor.BLUE + " reloads the plugin configuration.");
		player.sendMessage(ChatColor.AQUA + "/ops list " + ChatColor.WHITE + "-" + ChatColor.BLUE + " displays a list of verified operators and last login.");
		player.sendMessage(ChatColor.AQUA + "/ops purge " + ChatColor.WHITE + "-" + ChatColor.BLUE + " purges unverified operators from the server.");
		player.sendMessage(ChatColor.AQUA + "/ops pass | password [password] " + ChatColor.WHITE + "-" + ChatColor.BLUE + " allows verified OP's to enter the server's operator password.");
		player.sendMessage(ChatColor.AQUA + "/ops userid | uuid [name] " + ChatColor.WHITE + "-" + ChatColor.BLUE + " fetches the UUID of a player");

	}


}

