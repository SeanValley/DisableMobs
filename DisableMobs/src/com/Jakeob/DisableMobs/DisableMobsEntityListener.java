package com.Jakeob.DisableMobs;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class DisableMobsEntityListener implements Listener {
	public DisableMobs plugin;
  
	public DisableMobsEntityListener(DisableMobs instance) {
		this.plugin = instance;
	}
  
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		String world = event.getLocation().getWorld().getName();
		String typeName = event.getEntityType().toString().toLowerCase();
    
		boolean containsCreature = this.plugin.getConfig().contains(world + "." + typeName);
		
		if(containsCreature){
			boolean allowed = this.plugin.getConfig().getBoolean(world + "." + typeName);
			if (!allowed) {
				event.setCancelled(true);
			}
		}
	}
}