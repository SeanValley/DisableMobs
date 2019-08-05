package com.Jakeob.DisableMobs;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DisableMobs extends JavaPlugin {
	public static Logger log;
  
	public void onDisable() {
		DisableMobs.log.info("DisableMobs has been disabled!");
	}
  
	public void onEnable() {
		DisableMobs.log  = Logger.getLogger("Minecraft");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new DisableMobsEntityListener(this), this);
		loadConfiguration();
    
		int amountRemoved = clearUnwantedMobs();
		
		if(amountRemoved > 0) {
			DisableMobs.log.info("Removed " + amountRemoved + " mobs!");
		}

		DisableMobs.log.info("DisableMobs has been enabled!");
	}
  
	public void loadConfiguration() {
		getConfig().options().header(
		  "###############################################\n"
		+ "Disable Mobs 2.2\n"
		+ "For Bukkit Build 1.14.4\n"
		+ "By: Jakeob22\n"
		+ "Select 'true' if you want them to spawn\n"
		+ "Select 'false' if you don't want them to spawn\n"
		+ "It's that easy!!!\n"
		+ "###############################################\n");
    
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
  
	//clears mobs set to false, returns how many mobs were cleared
	private int clearUnwantedMobs() {
		int amountRemoved = 0;
		List<World> worlds = this.getServer().getWorlds();
		for(World world : worlds) {
			List<Entity> entities = world.getEntities();
			for(Entity entity : entities) {
				String typeName = entity.getType().toString().toLowerCase();
				boolean allowed = this.getConfig().getBoolean(world + "." + typeName);
				if(!allowed && !typeName.equalsIgnoreCase("player")) {
					entity.remove();
					amountRemoved++;
				}
			}
		}
	  
		return amountRemoved;
	}
	
	//clears specified mobs, returns how many mobs were cleared
	private int clearMob(String mobName, String worldName) {
		World world = this.getServer().getWorld(worldName);
		if(world != null) {
			int amountRemoved = 0;
			List<Entity> entities = world.getEntities();
			for(Entity entity : entities) {
				String typeName = entity.getType().toString().toLowerCase();
				if(typeName.equalsIgnoreCase(mobName)) {
					entity.remove();
					amountRemoved++;
				}
			}
			return amountRemoved;
		}
		return 0;
	}
	
	//sets specified mob to specified setting in config, returns how many mobs were removed
	private int setConfig(String mobName, String worldName, boolean setting) {
		this.getConfig().set(worldName + "." + mobName, setting);
		this.saveConfig();
		
		if(setting == false) {
			return clearMob(mobName, worldName);
		}
		
		return 0;
	}
  
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		boolean isPlayer = sender instanceof Player;
		boolean hasKillPermission = true;
		boolean hasConfigPermission = true;
		
		if(isPlayer) {
			Player player = (Player) sender;
			if(!player.isOp()) {
				if(!player.hasPermission("dm.kill")) {
					hasKillPermission = false;
				}
				
				if(!player.hasPermission("dm.config")) {
					hasConfigPermission = false;
				}
			}
			
		}
		
		if (cmd.getName().equalsIgnoreCase("killmobs")) {
			if(hasKillPermission) {
				if(args.length > 0) {
					String worldName = args[0];
					World world = getServer().getWorld(worldName);
					if(world != null) {
						List<Entity> entities = world.getEntities();
						if(entities != null && entities.size() > 0) {
							int amountRemoved = 0;
							for (Entity entity : world.getEntities()) {
								if (entity.getType() != EntityType.PLAYER && getConfig().contains(worldName + "." + entity.getType().toString().toLowerCase())) {
									entity.remove();
									amountRemoved++;
								}
							}
							
							if(isPlayer) {
								((Player) sender).sendMessage(ChatColor.GREEN + "Removed " + amountRemoved + " mobs from " + worldName + ".");
							}else {
								DisableMobs.log.info("Removed " + amountRemoved + " mobs from " + worldName + ".");
							}
						}else {
							if(isPlayer) {
								((Player) sender).sendMessage(ChatColor.GREEN + "Removed 0 mobs from " + worldName + ".");
							}else {
								DisableMobs.log.info("Removed 0 mobs from " + worldName + ".");
							}
						}
					}else {					
						if(isPlayer) {
							((Player) sender).sendMessage(ChatColor.RED + "World: " + worldName + " doesn't exist.");
						}else {
							DisableMobs.log.info("World: " + worldName + " doesn't exist.");
						}
					}
				}else {
					if(isPlayer) {
						((Player) sender).sendMessage(ChatColor.RED + "Usage: /killmobs [world]");
					}else {
						DisableMobs.log.info("Usage: killmobs [world]");
					}
				}
			}else {
				((Player) sender).sendMessage(ChatColor.RED + "You don't have permission to use this command.");
			}
			
			return true;
		}else if(cmd.getName().equalsIgnoreCase("setmob")){
			if(hasConfigPermission) {
				if(args.length == 3) {
					if(args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
						boolean setting = Boolean.valueOf(args[2]);
						int amountRemoved = setConfig(args[0], args[1], setting);
						
						if(isPlayer) {
							((Player) sender).sendMessage(ChatColor.GREEN + "Mob: " + args[0] + " set to " + setting + " for the world: " + args[1]);
						}else {
							DisableMobs.log.info("Mob: " + args[0] + " set to " + setting + " for the world: " + args[1]);
						}
						
						if(setting == false) {
							if(isPlayer) {
								((Player) sender).sendMessage(ChatColor.GREEN + "Removed " + amountRemoved + " of " + args[0]);
							}else {
								DisableMobs.log.info("Removed " + amountRemoved + " of " + args[0]);
							}
						}
					}else {
						if(isPlayer) {
							((Player) sender).sendMessage(ChatColor.RED + "Usage: /setmob [mob] [world] [true/false]");
						}else {
							DisableMobs.log.info("Usage: setmob [mob] [world] [true/false]");
						}
					}
				}else {
					if(isPlayer) {
						((Player) sender).sendMessage(ChatColor.RED + "Usage: /setmob [mob] [world] [true/false]");
					}else {
						DisableMobs.log.info("Usage: setmob [mob] [world] [true/false]");
					}
				}
			}else {
				((Player) sender).sendMessage(ChatColor.RED + "You don't have permission to use this command.");
			}
			return true;
		}
		return false;
	}
}