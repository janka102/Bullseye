package com.github.janka102.arrowdetect;

import org.bukkit.plugin.java.JavaPlugin;

public class ArrowDetect extends JavaPlugin {
	private static ADLogger logger = new ADLogger("ArrowDetect");
	public static ADLogger getADLogger()
	{
		return logger;
	}

    @Override
    public void onEnable() {
        new ArrowDetectorListener(this);
        logger.info("Enabled ArrowDetector!");
    }

    @Override
    public void onDisable() {        
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

}
