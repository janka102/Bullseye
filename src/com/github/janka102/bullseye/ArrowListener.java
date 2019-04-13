package com.github.janka102.bullseye;

import java.util.List;
import java.util.ListIterator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.BlockIterator;

public class ArrowListener implements Listener {
    public final SignUtils signUtils = new SignUtils();
    public final Bullseye plugin;

    public ArrowListener(Bullseye bullseye) {
        plugin = bullseye;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        // Only want arrows
        if (event.getEntityType() != EntityType.ARROW) {
            return;
        }

        Player player = null;
        Projectile arrow = event.getEntity();
        ProjectileSource shooter = arrow.getShooter();

        if (shooter instanceof Player) {
            player = (Player) shooter;
        } else if (shooter instanceof Skeleton) {
            // Only allow Skeletons if it's true in config.yml
            if (!plugin.allowSkeletons) {
                return;
            }
        } else if (!plugin.allowDispensers) {
            // Must be a Dispenser
            // Only allow if it's true in config.yml
            return;
        }

        // Creates an iterator of blocks in a line in direction of arrow
        BlockIterator bi = new BlockIterator(arrow.getWorld(), arrow.getLocation().toVector(),
                arrow.getVelocity().normalize(), 0, 3);
        Block hitBlock = null;
        Material type = null;

        while (bi.hasNext()) {
            hitBlock = bi.next();
            type = hitBlock.getType();

            // Skip blocks arrows can go through
            if (type.isSolid()) {
                break;
            }
        }

        // If it ran to the end of the iterator, there is not a block here
        if (!bi.hasNext()) {
            return;
        }

        // If hitBlock is a sign, set hitBlock to the attached block
        if (type == Material.WALL_SIGN || type == Material.SIGN_POST) {
            org.bukkit.material.Sign sign = (org.bukkit.material.Sign) hitBlock.getState().getData();
            hitBlock = hitBlock.getRelative(sign.getAttachedFace());
        }

        // plugin.getLogger().info(hitBlock.getType().toString());

        // Get all Bullseye signs attached to block and change them to rs torch
        List<Block> hitBlockSigns = signUtils.getBullseyeSigns(hitBlock);
        ListIterator<Block> iterator = hitBlockSigns.listIterator();

        if (signUtils.isValidBlock(hitBlock)) {
            // boolean waterMessage = true;

            while (iterator.hasNext()) {
                Block bullseyeSignBlock = iterator.next();
                Sign bullseyeSign = (Sign) bullseyeSignBlock.getState();
                String[] lines = bullseyeSign.getLines();

                // If it was invalid, make it now valid
                if (signUtils.isInvalidBullseyeSign(lines[0])) {
                    lines[0] = ChatColor.DARK_BLUE.toString()
                            + lines[0].trim().replace(ChatColor.DARK_RED.toString(), "");
                    signUtils.updateSign(bullseyeSignBlock, lines);
                }

                // Skip if it's next to water
                // if (signUtils.isNearLiquid(bullseyeSignBlock)) {
                // // Only send the message once, even if more than one sign is affected
                // if (waterMessage && player != null) {
                // player.sendMessage(ChatColor.RED + "Couldn't activate some Bullseye signs
                // with water near them!");
                // waterMessage = false;
                // }
                // continue;
                // }

                // Displays any message on the sign
                if (player != null) {
                    String message = bullseyeSign.getLine(1) + bullseyeSign.getLine(2) + bullseyeSign.getLine(3);

                    if (message.trim().length() > 0) {
                        player.sendMessage("Bullseye! " + message);
                    }
                }

                signUtils.signToRestone(plugin, bullseyeSignBlock);
            }
        } else {
            while (iterator.hasNext()) {
                Block bullseyeSignBlock = iterator.next();
                Sign bullseyeSign = (Sign) bullseyeSignBlock.getState();
                String[] lines = bullseyeSign.getLines();

                // If it was valid, make it now invalid
                if (signUtils.isValidBullseyeSign(lines[0])) {
                    lines[0] = ChatColor.DARK_RED.toString()
                            + lines[0].trim().replace(ChatColor.DARK_BLUE.toString(), "");
                    signUtils.updateSign(bullseyeSignBlock, lines);
                }
            }
        }
    }
}
