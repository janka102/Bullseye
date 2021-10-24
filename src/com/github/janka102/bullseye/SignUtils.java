package com.github.janka102.bullseye;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class SignUtils {
    private static final Pattern BULLSEYE_TAG = Pattern.compile("^\\[(bullseye|bull|be)]$", Pattern.CASE_INSENSITIVE);
    private static final String BULLSEYE_DO_NOT_DESTROY = "BullseyeDoNotDestroy";
    private static final String BULLSEYE_TASK_ID = "BullseyeTaskId";

    private final Bullseye plugin;

    public SignUtils(final Bullseye bullseye) {
        plugin = bullseye;
    }

    // Checks the block against the list in the config
    public boolean isValidBlock(final Block block) {
        if (block == null) {
            return false;
        }

        final String blockName = block.getType().toString().toUpperCase();

        // Blocks to block from the config
        if (plugin.isDenyList) {
            return !plugin.blockList.contains(blockName);
        } else {
            return plugin.blockList.contains(blockName);
        }
    }

    public void setBullseyeSignValid(final Sign sign) {
        setBullseyeSignValid(sign, sign.getLines());
    }

    public void setBullseyeSignInvalid(final Sign sign) {
        setBullseyeSignInvalid(sign, sign.getLines());
    }

    public void setBullseyeSignColor(final Sign sign, final ChatColor color) {
        setBullseyeSignColor(sign, color, sign.getLines());
    }

    public void setBullseyeSignValid(final Sign sign, final String[] lines) {
        setBullseyeSignColor(sign, ChatColor.DARK_BLUE, lines);
    }

    public void setBullseyeSignInvalid(final Sign sign, final String[] lines) {
        setBullseyeSignColor(sign, ChatColor.DARK_RED, lines);
    }

    public void setBullseyeSignColor(final Sign sign, final ChatColor color, final String[] lines) {
        final String firstLine = ChatColor.stripColor(lines[0].trim());

        if (BULLSEYE_TAG.matcher(firstLine).matches()) {
            lines[0] = color + firstLine;

            setLines(sign, lines);
        }
    }

    public boolean updateBullseyeSign(final Sign sign) {
        return updateBullseyeSign(sign, sign.getLines());
    }

    public boolean updateBullseyeSign(final Sign sign, final String[] lines) {
        if (lines.length < 1 || !isBullseyeSign(lines[0])) {
            return false;
        }

        if (isValidBlock(getAttachedBlock(sign))) {
            setBullseyeSignValid(sign, lines);
            return true;
        }

        setBullseyeSignInvalid(sign, lines);
        return false;
    }

    // Checks if sign is a Bullseye sign based on first line
    public boolean isBullseyeSign(final String line) {
        if (line == null) {
            return false;
        }

        return BULLSEYE_TAG.matcher(ChatColor.stripColor(line.trim())).matches();
    }

    public BlockFace getFacing(final Sign sign) {
        final BlockData signData = sign.getBlockData();

        if (signData instanceof WallSign) {
            // wall sign
            return ((WallSign) signData).getFacing();
        } else if (signData instanceof org.bukkit.block.data.type.Sign) {
            // sign post
            return BlockFace.UP;
        } else {
            plugin.log.warning("Unknown sign type! " + signData);
            return null;
        }
    }

    public Block getAttachedBlock(final Sign sign) {
        final BlockFace facing = getFacing(sign);

        return sign.getBlock().getRelative(facing.getOppositeFace());
    }

    // Get Bullseye signs attached to a block
    public List<Sign> findAllBullseyeSigns(final Block block) {
        final List<Sign> signs = new ArrayList<>();
        final List<BlockFace> directions = Arrays.asList(
                BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP);

        for (final BlockFace blockFace : directions) {
            final Block relativeBlock = block.getRelative(blockFace);
            final BlockState blockState = relativeBlock.getState();

            if (blockState instanceof Sign) {
                final Sign sign = (Sign) blockState;

                // Checks to see if the sign next to the block hit is a Bullseye sign
                // And if it's actually attached to the original block
                if (isBullseyeSign(sign.getLine(0))) {
                    final Block attachedBlock = getAttachedBlock(sign);

                    if (attachedBlock.equals(block)) {
                        signs.add(sign);
                    }
                }
            }
        }

        return signs;
    }

    // Update a signs' text
    public void setLines(final Sign sign, final String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            sign.setLine(i, lines[i]);
        }

        if (!sign.update(true)) {
            plugin.log.warning("Sign was not updated! " + sign);
        }
    }

    // Changes a Bullseye sign to a redstone torch for a specified time
    public void signToRedstone(final Sign sign, final long delay) {
        Block signBlock = sign.getBlock();
        BlockData signData = sign.getBlockData();
        final String[] lines = sign.getLines();

        final Material signMaterial = sign.getType();
        final BlockFace signFace = getFacing(sign);

        sign.setMetadata(BULLSEYE_DO_NOT_DESTROY, new FixedMetadataValue(plugin, true));

//         plugin.log.info(signData.toString());
        if (signFace == BlockFace.UP) {
            sign.setType(Material.REDSTONE_TORCH);
        } else {
            sign.setType(Material.REDSTONE_WALL_TORCH);
            Directional torch = (Directional) sign.getBlockData();
            torch.setFacing(signFace);
            sign.setBlockData(torch);
        }

        if (!sign.update(true)) {
            plugin.log.warning("Sign was not updated to a redstone torch! " + sign);
        }
//         plugin.log.info(sign.getBlockData().toString());

        // Run this after a delay to restore the torch back to a sign
        // Resets the block to a sign
        int taskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            try {
                final Material currentMaterial = sign.getBlock().getBlockData().getMaterial();
//                plugin.log.info("Current Material: " + currentMaterial);

                if (currentMaterial == Material.REDSTONE_TORCH || currentMaterial == Material.REDSTONE_WALL_TORCH) {
                    sign.setType(signMaterial);
                    sign.setBlockData(signData);

                    if (!sign.update(true)) {
                        plugin.log.warning("Redstone torch was not updated back to a sign! " + sign);
                    }
//                     plugin.log.info(signBlockState.getData().toString());

                    // Restore the original text of the Bullseye sign back
                    setLines(sign, lines);
                }
            } catch (RuntimeException e) {
                plugin.log.severe("Error in signToRedstone: " + e.getClass() + ": " + e.getMessage());
            }

            sign.removeMetadata(BULLSEYE_DO_NOT_DESTROY, plugin);
            sign.removeMetadata(BULLSEYE_TASK_ID, plugin);
        }, delay);

        signBlock.setMetadata(BULLSEYE_TASK_ID, new FixedMetadataValue(plugin, taskId));
    }
}
