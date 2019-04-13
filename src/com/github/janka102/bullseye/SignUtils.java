package com.github.janka102.bullseye;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.metadata.FixedMetadataValue;

public class SignUtils {
    // Checks the block against the list in the config
    public boolean isValidBlock(Block block) {
        if (block == null) {
            return false;
        }

        String blockName = block.getType().toString().toUpperCase();

        // Blocks to block from the config
        if (Bullseye.blacklist) {
            return !Bullseye.blockList.contains(blockName);
        } else {
            return Bullseye.blockList.contains(blockName);
        }
    }

    // Checks if sign is a Bullseye sign based on first line
    public boolean isBullseyeSign(String line) {
        return isBullseyeSign(line, true);
    }

    public boolean isBullseyeSign(String line, Boolean allColors) {
        if (allColors) {
            return line.matches("(?i)^(\u00a7[14r])?\\s*\\[(bullseye|bull|be)\\]$");
        }

        return line.matches("(?i)^\\[(bullseye|bull|be)\\]$");
    }

    public boolean isBullseyeSign(String line, ChatColor color) {
        return line.matches("(?i)^" + color + "\\s*\\[(bullseye|bull|be)\\]$");
    }

    public boolean isBullseyeSign(String line, ChatColor color, Boolean includeRegular) {
        if (includeRegular) {
            return isBullseyeSign(line, color) || isBullseyeSign(line, false);
        }

        return isBullseyeSign(line, color);
    }

    public boolean isValidBullseyeSign(String line) {
        return isBullseyeSign(line, ChatColor.DARK_BLUE, true);
    }

    public boolean isValidBullseyeSign(String line, Boolean includeRegular) {
        return isBullseyeSign(line, ChatColor.DARK_BLUE, includeRegular);
    }

    public boolean isInvalidBullseyeSign(String line) {
        return isBullseyeSign(line, ChatColor.DARK_RED, true);
    }

    public boolean isInvalidBullseyeSign(String line, Boolean includeRegular) {
        return isBullseyeSign(line, ChatColor.DARK_RED, includeRegular);
    }

    // Get Bullseye signs attached to a block
    public List<Block> getBullseyeSigns(Block block) {
        List<Block> signs = new ArrayList<Block>();

        // Get blocks to the North, South, East, West, and Top of the hit block
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x * x + y * y + z * z == 1) {
                        Block relativeBlock = block.getRelative(x, y, z);

                        if (relativeBlock.getState() instanceof Sign) {
                            Sign relativeSign = (Sign) relativeBlock.getState();

                            // Checks to see if the sign next to the block hit is a Bullseye sign
                            if (isBullseyeSign(relativeSign.getLine(0))) {
                                org.bukkit.material.Sign sign = (org.bukkit.material.Sign) relativeBlock.getState()
                                        .getData();
                                Block attachedBlock = relativeBlock.getRelative(sign.getAttachedFace());

                                // Checks to make sure the sign is attached to the original block
                                if (attachedBlock.equals(block)) {
                                    signs.add(relativeBlock);
                                }
                            }
                        }
                    }
                }
            }
        }

        return signs;
    }

    // Updated a signs text
    public void updateSign(Block signBlock, String[] signLines) {
        Sign sign = (Sign) signBlock.getState();

        for (int i = 0; i < signLines.length; i++) {
            sign.setLine(i, signLines[i].toString());
        }

        sign.update(true);
    }

    // Checks if a block is next to a liquid
    public boolean isNearLiquid(Block block) {
        // Get blocks to the North, South, East, West, and Top of the sign
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x * x + y * y + z * z == 1) {
                        if (block.getRelative(x, y, z).isLiquid()) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    // Changes a Bullseye sign to a redstone torch for a specified time
    public void signToRestone(final Bullseye plugin, final Block signBlock) {
        BlockState signBlockState = signBlock.getState();
        Sign bullseyeSign = (Sign) signBlockState;
        org.bukkit.material.Sign materialSign = ((org.bukkit.material.Sign) bullseyeSign.getData());
        final String[] lines = bullseyeSign.getLines();
        final Material signType = signBlock.getType();
        final BlockFace signFace = materialSign.getFacing();
        BlockFace attachedFace = materialSign.getAttachedFace().getOppositeFace();

        signBlock.setMetadata("BullseyeDoNotDestroy", new FixedMetadataValue(plugin, true));

        // plugin.getLogger().info(signBlockState.getData().toString());
        signBlockState.setType(Material.REDSTONE_TORCH_ON);
        ((org.bukkit.material.RedstoneTorch) signBlockState.getData()).setFacingDirection(attachedFace);
        signBlockState.update(true);
        // plugin.getLogger().info(signBlockState.getData().toString());

        // Run this after a delay of 25 ticks
        int taskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                try {
                    BlockState signBlockState = signBlock.getState();

                    // Resets the block
                    signBlockState.setType(signType);
                    ((org.bukkit.material.Sign) signBlockState.getData()).setFacingDirection(signFace);
                    signBlockState.update(true);
                    // plugin.getLogger().info(signBlockState.getData().toString());

                    Sign sign = (Sign) signBlock.getState();

                    // Restore the original text of the Bullseye sign back
                    for (int i = 0; i < lines.length; i++) {
                        sign.setLine(i, lines[i]);
                    }
                    sign.update(true);

                    signBlock.removeMetadata("BullseyeDoNotDestroy", plugin);
                    signBlock.removeMetadata("BullseyeTaskId", plugin);
                } catch (RuntimeException e) {
                    plugin.getLogger().severe("Error in signToRestone: " + e.getClass() + ": " + e.getMessage());
                    return;
                }
            }
        }, 25);

        signBlock.setMetadata("BullseyeTaskId", new FixedMetadataValue(plugin, taskId));
    }
}
