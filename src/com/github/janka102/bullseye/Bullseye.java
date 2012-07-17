package com.github.janka102.bullseye;

import org.bukkit.plugin.java.JavaPlugin;

public class Bullseye extends JavaPlugin {
	public static BullLogger logger = new BullLogger("Bullseye");
	public static BullLogger getBullLogger() {
		return logger;
	}
	
	@Override
	public void onEnable() {
		new BullseyeListener(this);
		logger.info("Bullseye enabled!");
	}
	
	@Override
    public void onDisable() {
    }

}
