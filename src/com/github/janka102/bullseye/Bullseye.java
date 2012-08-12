package com.github.janka102.bullseye;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Bullseye extends JavaPlugin {
	FileConfiguration config;
	Boolean allowDispensers;
	static Boolean blacklist;
	static List<String> blocks;
	
	public static BullLogger logger = new BullLogger("Bullseye");
	public static BullLogger getBullLogger() {
		return logger;
	}
	
	@Override
	public void onEnable() {
		new BullseyeListener(this);
		new BullseyeSignListener(this);		
		
		this.getConfig().options().copyDefaults(true);
        this.saveConfig();
		config = this.getConfig();
		
		allowDispensers = this.getConfig().getBoolean("allowDispensers");
        blacklist = this.getConfig().getBoolean("blockList.blacklist");
        blocks = this.getConfig().getStringList("blockList.blocks");
        
		logger.info("Bullseye enabled!");
	}
	
	@Override
    public void onDisable() {
		logger.info("Bullseye disabled!");
    }

}