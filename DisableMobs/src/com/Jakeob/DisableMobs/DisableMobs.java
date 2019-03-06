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
		+ "Disable Mobs 2.0\n"
		+ "For Bukkit Build 1.13.2 R0.1\n"
		+ "By: Jakeob22\n"
		+ "Select 'true' if you want them to spawn\n"
		+ "Select 'false' if you don't want them to spawn\n"
		+ "It's that easy!!!\n"
		+ "###############################################\n");
    
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
  
	private int clearUnwantedMobs() {
		int amountRemoved = 0;
		List<World> worlds = this.getServer().getWorlds();
		for(World world : worlds) {
			List<Entity> entities = world.getEntities();
			for(Entity entity : entities) {
				String typeName = entity.getType().toString().toLowerCase();
				boolean allowed = this.getConfig().getBoolean(world + "." + typeName);
				if(!allowed) {
					entity.remove();
					amountRemoved++;
				}
			}
		}
	  
		return amountRemoved;
	}
  
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		boolean isPlayer = sender instanceof Player;
		boolean hasPermission = true;
		
		if(isPlayer) {
			Player player = (Player) sender;
			if(!player.isOp() && !player.hasPermission("dm.kill")) {
				hasPermission = false;
			}
		}
		if ((cmd.getName().equalsIgnoreCase("killmobs")) && hasPermission) {
			int amountRemoved = 0;
			if(args.length > 0) {
				String worldName = args[0];
				World world = getServer().getWorld(worldName);
				if(world != null) {
					List<Entity> entities = world.getEntities();
					if(entities != null && entities.size() > 0) {
						for (Entity entity : world.getEntities()) {
							if (entity.getType() != EntityType.PLAYER && getConfig().contains(worldName + "." + entity.getType().toString().toLowerCase())) {
								entity.remove();
								amountRemoved++;
							}
						}
						
						if(isPlayer) {
							((Player) sender).sendMessage(ChatColor.GREEN + "Removed " + amountRemoved + " mobs from " + worldName + ".");
							DisableMobs.log.info("test. " + amountRemoved);
						}else {
							DisableMobs.log.info("Removed " + amountRemoved + " mobs from " + worldName + ".");
						}
					}else {
						if(isPlayer) {
							((Player) sender).sendMessage(ChatColor.GREEN + "Removed 0 mobs from " + worldName + ".");
							DisableMobs.log.info("test. " + amountRemoved);
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
					((Player) sender).sendMessage(ChatColor.RED + "You must specify a world to clear.");
				}else {
					DisableMobs.log.info("You must specify a world to clear.");
				}
			}
			return true;
		}
		return false;
	}
}