package com.github.janka102.bullseye;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUtils {
    public static final String BULLSEYE_SIGN_MATERIAL = "BullseyeSignMaterial";
    public static final String BULLSEYE_TASK_ID = "BullseyeTaskId";
    private static final int DEFAULT_TICK_ACTIVATION = 30;

    private static final Pattern BULLSEYE_TAG = Pattern.compile("^\\[(bullseye|bull|be)( (?<ticks>\\d+))?]$", Pattern.CASE_INSENSITIVE);
    private static final Pattern MATERIAL_WALL_REPLACE = Pattern.compile("WALL_");

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

    public boolean isBullseyeSign(final Sign sign) {
        return isBullseyeSign(sign.getLine(0));
    }

    public BlockFace getFacing(final BlockData blockData) {
        if (blockData instanceof Directional) {
            // wall
            return ((Directional) blockData).getFacing();
        } else if ((blockData instanceof org.bukkit.block.data.type.Sign) || blockData.getMaterial().equals(Material.REDSTONE_TORCH)) {
            // upright
            return BlockFace.UP;
        } else {
            plugin.log.warning("Unknown facing type! " + blockData);
            return null;
        }
    }

    public Block getAttachedBlock(final BlockState blockState) {
        final BlockFace facing = getFacing(blockState.getBlockData());

        return blockState.getBlock().getRelative(facing.getOppositeFace());
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
                if (isBullseyeSign(sign)) {
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

    public int getActivateTicks(final Sign sign) {
        final Matcher matcher = BULLSEYE_TAG.matcher(ChatColor.stripColor(sign.getLine(0)));
        if (!matcher.matches()) {
            return -1;
        }

        // Default value if custom value not found
        final String ticks = matcher.group("ticks");
        if (ticks == null || ticks.isEmpty()) {
            return DEFAULT_TICK_ACTIVATION;
        }

        try {
            final int signTicks = Integer.parseUnsignedInt(ticks);
            return Math.min(signTicks, plugin.maxActiveTicks);
        } catch (final NumberFormatException e) {
            // Shouldn't ever happen since the regex checks for digits, but just in case
            return -1;
        }
    }

    public Material getMaterialFromMetadata(MetadataValue metadataValue) {
        // Cannot drop WALL_SIGNs directly, so replace it with normal sign types
        final String materialName = MATERIAL_WALL_REPLACE.matcher(metadataValue.asString()).replaceFirst("");
        final Material signMaterial = Material.getMaterial(materialName);

        return signMaterial;
    }

    // Changes a Bullseye sign to a redstone torch for a specified time
    public void signToRedstone(final Sign sign) {
        final int activateTicks = getActivateTicks(sign);

        if (activateTicks < 0) {
            plugin.log.fine("Sign has ticks: " + activateTicks);
            return;
        }

        final BlockData signData = sign.getBlockData();
        final String[] lines = sign.getLines();

        final Material signMaterial = sign.getType();
        final BlockFace signFace = getFacing(signData);

        sign.setMetadata(BULLSEYE_SIGN_MATERIAL, new FixedMetadataValue(plugin, signMaterial.toString()));

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
            final Optional<MetadataValue> metadata = sign.getMetadata(SignUtils.BULLSEYE_SIGN_MATERIAL).stream()
                    .filter(metadataValue -> Objects.equals(metadataValue.getOwningPlugin(), plugin))
                    .findFirst();

            // make sure it still has metadata
            metadata.ifPresent(metadataValue -> {
                try {
                    final Material originalSignMaterial = getMaterialFromMetadata(metadataValue);

                    final BlockData currentBlockData = sign.getBlock().getBlockData();
                    final Material currentMaterial = currentBlockData.getMaterial();
                    // plugin.log.info("Current Material: " + currentMaterial);

                    // make sure metadata is valid and it's a redstone torch
                    if (originalSignMaterial != null
                            && (currentMaterial == Material.REDSTONE_TORCH || currentMaterial == Material.REDSTONE_WALL_TORCH)) {
                        final BlockFace currentFacing = getFacing(currentBlockData);

                        // make sure the redstone torch is facing the same direction
                        if (currentFacing.equals(signFace)) {
                            sign.setType(originalSignMaterial);
                            sign.setBlockData(signData);

                            if (!sign.update(true)) {
                                plugin.log.warning("Redstone torch was not updated back to a sign! " + sign);
                            }
                            // plugin.log.info(signBlockState.getData().toString());

                            // Restore the original text of the Bullseye sign back
                            updateBullseyeSign(sign, lines);
                        }
                    }

                    sign.removeMetadata(BULLSEYE_SIGN_MATERIAL, plugin);
                    sign.removeMetadata(BULLSEYE_TASK_ID, plugin);
                } catch (RuntimeException e) {
                    plugin.log.severe("Error in signToRedstone: " + e.getClass() + ": " + e.getMessage());
                }
            });
        }, activateTicks);

        sign.setMetadata(BULLSEYE_TASK_ID, new FixedMetadataValue(plugin, taskId));
    }
}
