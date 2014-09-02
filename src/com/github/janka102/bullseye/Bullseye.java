package com.github.janka102.bullseye;

import java.util.List;
import java.util.ListIterator;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Bullseye extends JavaPlugin {
    FileConfiguration config;
    Boolean allowDispensers;
    Boolean allowSkeletons;
    static Boolean blacklist;
    static List<String> blockList;

    private BullseyeLogger logger = new BullseyeLogger();

    @Override
    public void onEnable() {
        new ArrowListener(this);
        new RedStoneTorchListener(this);
        new SignListener(this);

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        config = this.getConfig();

        allowDispensers = config.getBoolean("allowDispensers");
        allowSkeletons = config.getBoolean("allowSkeletons");
        blacklist = config.getBoolean("blockList.blacklist");
        blockList = config.getStringList("blockList.blocks");

        ListIterator<String> iterator = blockList.listIterator();
        while (iterator.hasNext()) {
            iterator.set(iterator.next().toUpperCase());
        }

        logger.info("Bullseye enabled!");
    }

    @Override
    public void onDisable() {
        logger.info("Bullseye disabled!");
    }
}