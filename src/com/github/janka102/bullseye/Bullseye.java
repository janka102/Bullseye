package com.github.janka102.bullseye;

import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Bullseye extends JavaPlugin {
    private static int BSTATS_PLUGIN_ID = 15841;
    private FileConfiguration config;

    public boolean allowDispensers;
    public boolean allowSkeletons;
    public int maxActiveTicks;
    public boolean isDenyList;
    public List<String> blockList;

    public Logger log;

    @Override
    public void onLoad() {
        log = this.getLogger();
    }

    @Override
    public void onEnable() {
        final PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ArrowListener(this), this);
        pluginManager.registerEvents(new RedStoneTorchListener(this), this);
        pluginManager.registerEvents(new SignListener(this), this);

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        config = this.getConfig();

        allowDispensers = config.getBoolean("allowDispensers");
        allowSkeletons = config.getBoolean("allowSkeletons");
        maxActiveTicks = config.getInt("maxActiveTicks");

        final String blockListType = config.getString("blockList.type");
        if (blockListType != null) {
            isDenyList = blockListType.equalsIgnoreCase("deny");
        } else {
            // maintain backward compatibility with versions < v0.9.0
            isDenyList = config.getBoolean("blockList.blacklist");
        }
        blockList = config.getStringList("blockList.blocks");

        final ListIterator<String> iterator = blockList.listIterator();
        while (iterator.hasNext()) {
            iterator.set(iterator.next().toUpperCase());
        }

        log.fine("Allow Dispensers: " + allowDispensers);
        log.fine("Allow Skeletons: " + allowSkeletons);
        log.fine("Max Active Ticks: " + maxActiveTicks);
        final int numBlocks = blockList.size();
        log.fine(String.format("Block List: type=%s, %d block%s", isDenyList ? "deny" : "allow", numBlocks, numBlocks == 1 ? "" : "s"));

        // add bStats metrics
        Metrics metrics = new Metrics(this, BSTATS_PLUGIN_ID);

        log.info("Bullseye enabled!");
    }

    @Override
    public void onDisable() {
        log.info("Bullseye disabled!");
    }
}
