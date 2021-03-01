package com.vytruvius.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;


import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class opsCommand implements CommandExecutor {

	final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("OPScreen");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player player = Bukkit.getPlayer("Oreborn");

		try {
			player = (Player) sender;

		} catch (Exception e) {

			player = null;
		}

		if(args.length > 0) {

			if(args.length == 1) {

				if(args[0].equalsIgnoreCase("reload")) {

					if (sender instanceof Player) {

						if (player.hasPermission("opscreen.reload")) {

							Main.reloadPlugin();
							player.sendMessage(ChatColor.GREEN + "Plugin configuration reloaded.");
							return true;

						} else {
							
							player.sendMessage(ChatColor.RED + "No permission.");
							
						}

					} else {

						Main.reloadPlugin();
						System.out.println(ChatColor.GREEN + "Plugin configuration reloaded.");
						return true;

					}

				} else if (args[0].equalsIgnoreCase("list")) {

					if (player.hasPermission("opscreen.list")) {

						player.sendMessage(ChatColor.DARK_AQUA + "Username " + ChatColor.RED + "-" + ChatColor.DARK_AQUA + " Last Seen");

						for (String e : Main.verified) {

							UUID uuid = UUID.fromString(e);
							OfflinePlayer current = Bukkit.getOfflinePlayer(uuid);
							long millisLastOn = System.currentTimeMillis() - current.getLastPlayed();
							int hoursLastOn = (int) millisLastOn / 3600000;
							int daysLastOn = hoursLastOn / 24;
							if (hoursLastOn >= 24) {

								hoursLastOn = hoursLastOn % daysLastOn;
								player.sendMessage(ChatColor.AQUA + current.getName() + ChatColor.WHITE + " - " + ChatColor.AQUA + daysLastOn + " days, " + hoursLastOn + " hours");
							} else {

								player.sendMessage(ChatColor.AQUA + current.getName() + ChatColor.WHITE + " - " + ChatColor.AQUA + hoursLastOn + " hours");
							}

						}

					} else {
						
						player.sendMessage(ChatColor.RED + "No permission.");
						
					}

				} else if (args[0].equalsIgnoreCase("help")) {

					if (player.hasPermission("opscreen.help")) {

						Main.helpCommand(player);

					} else {
						
						player.sendMessage(ChatColor.RED + "No permission.");
						
					}

				} else if (args[0].equalsIgnoreCase("purge")) {

					if (sender instanceof Player) {

						if (player.hasPermission("opscreen.purge")) {

							player.sendMessage(ChatColor.GREEN + "Operator scan complete. " + Main.scanOperators() + " inactive operators purged.");
							return true;

						} else {
							
							player.sendMessage(ChatColor.RED + "No permission.");
							
						}
					} else {

						System.out.println(ChatColor.GREEN + "Operator scan complete. " + Main.scanOperators() + " inactive operators purged.");
						return true;

					}

				} else {

					if (player.hasPermission("opscreen.help")) {

						player.sendMessage(ChatColor.RED + "Please enter a valid number of arguments.");

					} else {

						player.sendMessage(ChatColor.RED + "No permission.");

					}
					
				}
				
			} else if (args.length == 2) {

				if (args[0].equalsIgnoreCase("uuid") || args[0].equalsIgnoreCase("userid")) {

					if (player.hasPermission("opscreen.uuid")) {

						player.sendMessage(ChatColor.GREEN + "UUID of " + ChatColor.AQUA + args[1] + ChatColor.GREEN + ": " + ChatColor.WHITE + Bukkit.getPlayer(args[1]).getUniqueId());

					} else {
						
						player.sendMessage(ChatColor.RED + "No permission.");
						
					}

				} else if ((args[0].equalsIgnoreCase("password") || args[0].equalsIgnoreCase("pass")) && Main.passwordEnabled == true) {

					if (player.hasPermission("opscreen.password")) {

						UUID uuid = player.getUniqueId();

						if (!(Main.verified.indexOf(uuid.toString()) == -1 )) {

							if (args[1].equals(Main.plugin.getConfig().getString("password"))) {

								if (!(Main.awaitingVerification.indexOf(uuid.toString()) == -1 )) {

									Main.awaitingVerification.remove(uuid.toString());
									player.sendMessage(ChatColor.GREEN + "Password successful.");
								} else {

									player.sendMessage(ChatColor.GREEN + "Already verified.");
								}

								return true;
							} else {

								player.sendMessage(ChatColor.RED + "Incorrect password.");

							}

						} else if (Main.verified.indexOf(uuid.toString()) == -1 ) {

							player.sendMessage(ChatColor.RED + "You do not have permission to run that command.");
						} 

						return true;

					} else {
						
						player.sendMessage(ChatColor.RED + "No permission.");
						
					}

				} else if (Main.passwordEnabled == false) {

					if (player.hasPermission("opscreen.password")) {

						player.sendMessage(ChatColor.GREEN + "A password is not enabled for this server.");

					} else {
						
						player.sendMessage(ChatColor.RED + "No permission.");
						
					}

				} else {

					if (player.hasPermission("opscreen.help")) {

						player.sendMessage(ChatColor.RED + "Please enter a valid number of arguments.");

					} else {

						player.sendMessage(ChatColor.RED + "No permission.");

					}

				}
			} else {

				if (player.hasPermission("opscreen.help")) {

					player.sendMessage(ChatColor.RED + "Please enter a valid number of arguments.");

				} else {

					player.sendMessage(ChatColor.RED + "No permission.");

				}
			}

		} else {

			if (player.hasPermission("opscreen.help")) {

				Main.helpCommand(player);

			} else {

				player.sendMessage(ChatColor.RED + "No permission.");

			}

		}

		return true;
	}


}
