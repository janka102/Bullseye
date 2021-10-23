package com.github.janka102.bullseye;

import java.util.List;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.MetadataValue;

public class RedStoneTorchListener implements Listener {
    public final SignUtils signUtils;
    public final Bullseye plugin;

    public RedStoneTorchListener(final Bullseye bullseye) {
        plugin = bullseye;
        signUtils = new SignUtils(bullseye);
    }

    @EventHandler
    public void onRedstoneTorchPopOff(BlockPhysicsEvent event) {
        Block block = event.getBlock();

        if (block.hasMetadata("BullseyeDoNotDestroy")) {
            plugin.log.info(block + " tried to pop off");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRedstoneTorchBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.hasMetadata("BullseyeDoNotDestroy")) {
            plugin.log.info(event.getPlayer() + " tried to break " + block);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRedstoneTorchFlow(BlockFromToEvent event) {
        Block block = event.getToBlock();

        if (block.hasMetadata("BullseyeDoNotDestroy")) {
            plugin.log.info(event.getBlock().getType() + " tried to flow to " + block);
            // event.setCancelled(true);
            changeToAir(block);
            cancelTask(block);
        }
    }

    @EventHandler
    public void onRedstoneTorchExplode(EntityExplodeEvent event) {
        List<Block> blockList = event.blockList();

        for (Block block : blockList) {
            if (block.hasMetadata("BullseyeDoNotDestroy")) {
                plugin.log.info(event.getEntityType() + " exploded " + block);
                // If it drops, drop nothing instead
                changeToAir(block);
                cancelTask(block);
            }
        }
    }

    private void changeToAir(final Block block) {
        BlockState blockState = block.getState();
        blockState.setType(Material.AIR);
        blockState.update(true);
    }

    private void cancelTask(final Block block) {
        for (MetadataValue meta : block.getMetadata("BullseyeTaskId")) {
            if (Objects.equals(meta.getOwningPlugin(), plugin)) {
                int taskId = meta.asInt();

                if (taskId != -1 && plugin.getServer().getScheduler().isQueued(taskId)) {
                    // plugin.log.info("Canceled task: " + taskId);
                    plugin.getServer().getScheduler().cancelTask(taskId);
                }

                block.removeMetadata("BullseyeDoNotDestroy", plugin);
                block.removeMetadata("BullseyeTaskId", plugin);
                break;
            }
        }
    }
}
