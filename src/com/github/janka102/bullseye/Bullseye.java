package com.github.janka102.bullseye;

import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Bullseye extends JavaPlugin {
    FileConfiguration config;
    Boolean allowDispensers;
    Boolean allowSkeletons;
    static Boolean isDenyList;
    static List<String> blockList;

    public Logger log;

    @Override
    public void onLoad() {
        log = this.getLogger();
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ArrowListener(this), this);
        pluginManager.registerEvents(new RedStoneTorchListener(this), this);
        pluginManager.registerEvents(new SignListener(this), this);

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        config = this.getConfig();

        allowDispensers = config.getBoolean("allowDispensers");
        allowSkeletons = config.getBoolean("allowSkeletons");

        String blockListType = config.getString("blockList.type");
        if (blockListType != null) {
            isDenyList = blockListType.equalsIgnoreCase("deny");
        } else {
            // maintain backward compatibility with versions < v0.9.0
            isDenyList = config.getBoolean("blockList.blacklist");
        }
        blockList =  config.getStringList("blockList.blocks");

        ListIterator<String> iterator = blockList.listIterator();
        while (iterator.hasNext()) {
            iterator.set(iterator.next().toUpperCase());
        }

        log.info("Bullseye enabled!");
    }

    @Override
    public void onDisable() {
        log.info("Bullseye disabled!");
    }
}
