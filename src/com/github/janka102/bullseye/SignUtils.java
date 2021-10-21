package com.github.janka102.bullseye;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.RedstoneWallTorch;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.metadata.FixedMetadataValue;

public class SignUtils {
    private final Bullseye plugin;

    public SignUtils(final Bullseye bullseye) {
        plugin = bullseye;
    }

    // Checks the block against the list in the config
    public boolean isValidBlock(Block block) {
        if (block == null) {
            return false;
        }

        String blockName = block.getType().toString().toUpperCase();

        // Blocks to block from the config
        if (Bullseye.isDenyList) {
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
            return line.matches("(?i)^(\u00a7[14r])?\\s*\\[(bullseye|bull|be)]$");
        }

        return line.matches("(?i)^\\[(bullseye|bull|be)]$");
    }

    public boolean isBullseyeSign(String line, ChatColor color) {
        return line.matches("(?i)^" + color + "\\s*\\[(bullseye|bull|be)]$");
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
        List<Block> signs = new ArrayList<>();

        // Get blocks to the North, South, East, West, and Top of the hit block
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x * x + y * y + z * z == 1) {
                        Block relativeBlock = block.getRelative(x, y, z);
                        BlockState blockState = relativeBlock.getState();

                        if (blockState instanceof Sign) {
                            Sign signState = (Sign) blockState;

                            // Checks to see if the sign next to the block hit is a Bullseye sign
                            if (isBullseyeSign(signState.getLine(0))) {
                                BlockData signData = signState.getBlockData();
                                final BlockFace facing;

                                if (signData instanceof WallSign) {
                                    // wall sign
                                    facing = ((WallSign) signData).getFacing();
                                } else if (signData instanceof org.bukkit.block.data.type.Sign) {
                                    // sign post
                                    facing = BlockFace.UP;
                                } else {
                                    plugin.log.severe("Unknown sign block: " + relativeBlock);
                                    continue;
                                }

                                Block attachedBlock = relativeBlock.getRelative(facing.getOppositeFace());
                                plugin.log.info(facing.toString());
                                plugin.log.info(relativeBlock.toString());
                                plugin.log.info(attachedBlock.toString());

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
            sign.setLine(i, signLines[i]);
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
    public void signToRedstone(final Block signBlock) {
        BlockState signBlockState = signBlock.getState();
        Sign bullseyeSign = (Sign) signBlockState;
        BlockData signData = signBlock.getBlockData();
        final String[] lines = bullseyeSign.getLines();

        final Material signType = signBlock.getType();
        final BlockFace signFace;
        BlockFace rotation;

        if (signData instanceof WallSign) {
            // wall sign
            signFace = ((WallSign) signData).getFacing();
            rotation = null;
        } else if (signData instanceof org.bukkit.block.data.type.Sign) {
            // sign post
            signFace = BlockFace.UP;
            rotation = ((org.bukkit.block.data.type.Sign) signData).getRotation();
        } else {
            plugin.log.severe("Unknown sign block: " + signBlock);
            return;
        }

        signBlock.setMetadata("BullseyeDoNotDestroy", new FixedMetadataValue(plugin, true));

         // plugin.log.info(signData.toString());
         if (signFace == BlockFace.UP) {
             signBlockState.setType(Material.REDSTONE_TORCH);
         } else {
             signBlockState.setType(Material.REDSTONE_WALL_TORCH);
             Directional torch = (Directional) signBlockState.getBlockData();
             torch.setFacing(signFace);
             signBlockState.setBlockData(torch);
         }
         signBlockState.update(true);
         // plugin.log.info(signBlockState.getBlockData().toString());

        // Run this after a delay to restore the torch back to a sign
        int taskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            try {
                BlockState signBlockState1 = signBlock.getState();

                // Resets the block
                signBlockState1.setType(signType);
                BlockData signData1 = signBlockState1.getBlockData();
                // plugin.log.info(signBlockState1.getBlockData().toString());

                if (rotation == null) {
                    // wall sign
                    ((WallSign) signData1).setFacing(signFace);
                } else {
                    // sign post
                    ((org.bukkit.block.data.type.Sign) signData1).setRotation(rotation);
                }

                signBlockState1.setBlockData(signData1);
                signBlockState1.update(true);
                // plugin.log.info(signBlockState.getData().toString());

                // Restore the original text of the Bullseye sign back
                updateSign(signBlock, lines);

                signBlock.removeMetadata("BullseyeDoNotDestroy", plugin);
                signBlock.removeMetadata("BullseyeTaskId", plugin);
            } catch (RuntimeException e) {
                plugin.log.severe("Error in signToRedstone: " + e.getClass() + ": " + e.getMessage());
            }
        }, 25);

        signBlock.setMetadata("BullseyeTaskId", new FixedMetadataValue(plugin, taskId));
    }
}
