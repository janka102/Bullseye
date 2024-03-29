package com.github.janka102.bullseye;

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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArrowListener implements Listener {
    private final Bullseye plugin;
    private final SignUtils signUtils;

    public ArrowListener(final Bullseye bullseye) {
        plugin = bullseye;
        signUtils = new SignUtils(bullseye);
    }

    /**
     * Check when an arrow hits a block if it should trigger a Bullseye sign.
     *
     * @param event The projectile event to check
     */
    @EventHandler
    public void onArrowHit(final ProjectileHitEvent event) {
        final Projectile arrow = event.getEntity();
        final Block hitBlock = event.getHitBlock();
        // plugin.log.info("Hit block: " + hitBlock);

        // Only want arrows that hit blocks
        if (!(arrow instanceof Arrow) || hitBlock == null) {
            return;
        }

        final ProjectileSource shooter = arrow.getShooter();
        final Player player;

        // plugin.log.info("Shooter: " + shooter);

        if (shooter instanceof Player) {
            player = (Player) shooter;
        } else if (
                ((shooter instanceof Skeleton) && plugin.allowSkeletons) ||
                ((shooter instanceof BlockProjectileSource) && plugin.allowDispensers)
        ) {
            player = null;
        } else {
            // Unsupported source, or blocked by the config
            return;
        }

        // Get all Bullseye signs attached to block and change them to rs torch
        final List<Sign> hitBlockSigns = signUtils.findAllBullseyeSigns(hitBlock);
        // plugin.log.info("Found " + hitBlockSigns.size());

        if (signUtils.isValidBlock(hitBlock)) {
            for (final Sign bullseyeSign : hitBlockSigns) {
                // If it was invalid, make it now valid
                signUtils.setBullseyeSignValid(bullseyeSign);

                // Displays any message on the sign
                if (player != null) {
                    final String message = Arrays.stream(bullseyeSign.getLines())
                            .skip(1)
                            .collect(Collectors.joining(""))
                            .trim();

                    if (message.length() > 0) {
                        player.sendMessage("Bullseye! " + message);
                    }
                }

                signUtils.signToRedstone(bullseyeSign);
            }
        } else {
            for (final Sign bullseyeSign : hitBlockSigns) {
                // If it was valid, make it now invalid
                signUtils.setBullseyeSignInvalid(bullseyeSign);
            }
        }
    }
}
