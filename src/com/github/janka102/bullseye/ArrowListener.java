package com.github.janka102.bullseye;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;
import java.util.ListIterator;

public class ArrowListener implements Listener {
    public final SignUtils signUtils;
    public final Bullseye plugin;

    public ArrowListener(Bullseye bullseye) {
        plugin = bullseye;
        signUtils = new SignUtils(bullseye);
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        Projectile arrow = event.getEntity();
        Block hitBlock = event.getHitBlock();

        // Only want arrows that hit blocks
        if (!(arrow instanceof Arrow) || hitBlock == null) {
            return;
        }

        Player player = null;
        ProjectileSource shooter = arrow.getShooter();

        if (shooter instanceof Player) {
            player = (Player) shooter;
        } else if (
            !(shooter instanceof Skeleton && plugin.allowSkeletons) &&
            !(shooter instanceof BlockProjectileSource && plugin.allowDispensers)
        ) {
            return;
        }

         // plugin.log.info(hitBlock.toString());

        // Get all Bullseye signs attached to block and change them to rs torch
        List<Block> hitBlockSigns = signUtils.getBullseyeSigns(hitBlock);
        ListIterator<Block> iterator = hitBlockSigns.listIterator();

        // plugin.log.info("Found " + hitBlockSigns.size());

        if (signUtils.isValidBlock(hitBlock)) {
            // boolean waterMessage = true;

            while (iterator.hasNext()) {
                Block bullseyeSignBlock = iterator.next();
                Sign bullseyeSign = (Sign) bullseyeSignBlock.getState();
                String[] lines = bullseyeSign.getLines();

                // If it was invalid, make it now valid
                if (signUtils.isInvalidBullseyeSign(lines[0])) {
                    lines[0] = ChatColor.DARK_BLUE
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

                signUtils.signToRedstone(bullseyeSignBlock);
            }
        } else {
            while (iterator.hasNext()) {
                Block bullseyeSignBlock = iterator.next();
                Sign bullseyeSign = (Sign) bullseyeSignBlock.getState();
                String[] lines = bullseyeSign.getLines();

                // If it was valid, make it now invalid
                if (signUtils.isValidBullseyeSign(lines[0])) {
                    lines[0] = ChatColor.DARK_RED
                            + lines[0].trim().replace(ChatColor.DARK_BLUE.toString(), "");
                    signUtils.updateSign(bullseyeSignBlock, lines);
                }
            }
        }
    }
}
