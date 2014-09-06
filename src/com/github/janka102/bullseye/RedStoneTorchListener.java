package com.github.janka102.bullseye;

import java.util.List;
import java.util.ListIterator;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.MetadataValue;

public class RedStoneTorchListener implements Listener {
    public final SignUtils signUtils = new SignUtils();
    public final Bullseye plugin;

    public RedStoneTorchListener(Bullseye bullseye) {
        plugin = bullseye;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onRedstoneTorchPopOff(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();

        if (type == Material.REDSTONE_TORCH_ON
            || type == Material.WALL_SIGN
            || type == Material.SIGN_POST) {

            if (block.hasMetadata("BullseyeDoNotDestroy")) {
                // plugin.getLogger().info(block + " tried to pop off");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onRedstoneTorchBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.REDSTONE_TORCH_ON
            && block.hasMetadata("BullseyeDoNotDestroy")) {
            // plugin.getLogger().info(event.getPlayer() + " tried to break " + block);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRedstoneTorchReplace(BlockFromToEvent event) {
        Block block = event.getToBlock();

        if (block.getType() == Material.REDSTONE_TORCH_ON
            && block.hasMetadata("BullseyeDoNotDestroy")) {
            // plugin.getLogger().info(event.getBlock().getType() + " tried to flow to " + block.toString());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRedstoneTorchExplode(EntityExplodeEvent event) {
        List<Block> blockList = event.blockList();
        ListIterator<Block> iterator = blockList.listIterator();

        while (iterator.hasNext()) {
            Block block = iterator.next();

            if (block.getType() == Material.REDSTONE_TORCH_ON
                && block.hasMetadata("BullseyeDoNotDestroy")) {
                // plugin.getLogger().info(event.getEntityType() + " exploded " + block.toString());
                // If it drops, drop a sign instead of a redstone torch
                block.setType(Material.SIGN_POST);

                ListIterator<MetadataValue> metadata = block.getMetadata("BullseyeTaskId").listIterator();

                while (metadata.hasNext()) {
                    MetadataValue meta = metadata.next();
                    if (meta.getOwningPlugin().equals(plugin)) {
                        if (meta.asInt() != -1 && plugin.getServer().getScheduler().isQueued(meta.asInt())) {
                            // plugin.getLogger().info("Canceled task: " + meta.asInt());
                            plugin.getServer().getScheduler().cancelTask(meta.asInt());
                        }
                        break;
                    }
                }
            }
        }
    }
}
