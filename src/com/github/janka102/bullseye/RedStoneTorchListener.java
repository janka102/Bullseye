package com.github.janka102.bullseye;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.Objects;
import java.util.Optional;

public class RedStoneTorchListener implements Listener {
    private final String BULLSEYE_DO_NOT_DELETE = "BullseyeDoNotDelete";

    public final SignUtils signUtils;
    public final Bullseye plugin;

    public RedStoneTorchListener(final Bullseye bullseye) {
        plugin = bullseye;
        signUtils = new SignUtils(bullseye);
    }

    /**
     * Add metadata to dropped redstone torches, if it was dropped right on an active Bullseye torch.
     * This will be checked in another event to know if it should be turned into a sign or not.
     *
     * Note: This event happens before ItemSpawnEvent allowing the metadata to be checked there later.
     *
     * @param event The event to check if it should add metadata to the item.
     */
    @EventHandler
    public void onItemDrop(final PlayerDropItemEvent event) {
        final Item item = event.getItemDrop();
        final Material itemMaterial = item.getItemStack().getType(); // plugin.log.info("onItemDrop material: " + itemMaterial);
        if (itemMaterial != Material.REDSTONE_TORCH) {
            return;
        }

        final Block block = item.getWorld().getBlockAt(item.getLocation()); // plugin.log.info("onItemDrop block: " + block);
        if (!block.getState().getType().equals(Material.REDSTONE_WALL_TORCH)) {
            return;
        }

        final Optional<MetadataValue> blockMetadata = block.getMetadata(SignUtils.BULLSEYE_SIGN_MATERIAL).stream()
                .filter(metadataValue -> Objects.equals(metadataValue.getOwningPlugin(), plugin))
                .findFirst();

        blockMetadata.ifPresent(metadataValue -> {
            // plugin.log.info("onItemDrop: Metadata set");
            item.setMetadata(BULLSEYE_DO_NOT_DELETE, new FixedMetadataValue(plugin, true));
        });
    }

    /**
     * Check item drops if they still have Bullseye metadata then that means the RS torch broke.
     * Try and drop the original sign instead.
     *
     * However, if water breaks the redstone torch, it creates the item drop before actually breaking the block.
     * This is why extra metadata is set on the item that drops and is checked before changing to a sign.
     *
     * Note: This event happens after PlayerDropItemEvent which adds the metadata.
     *
     * @param event The item spawn event to check
     */
    @EventHandler
    public void onItemSpawn(final ItemSpawnEvent event) {
        if (event.getEntityType() != EntityType.DROPPED_ITEM) {
            return;
        }

        final Item item = event.getEntity();
        final Material itemMaterial = item.getItemStack().getType(); // plugin.log.info("onItemSpawn material: " + itemMaterial);
        if (itemMaterial != Material.REDSTONE_TORCH) {
            return;
        }

        // if it has this metadata it means it was dropped by a player and not broken
        final Optional<MetadataValue> itemMetadata = item.getMetadata(BULLSEYE_DO_NOT_DELETE).stream()
                .filter(metadataValue -> Objects.equals(metadataValue.getOwningPlugin(), plugin))
                .findFirst();
        if (itemMetadata.isPresent()) {
            // plugin.log.info("onItemSpawn: item metadata present");
            item.removeMetadata(BULLSEYE_DO_NOT_DELETE, plugin);
            return;
        }
        // plugin.log.info("onItemSpawn: item metadata not present");

        final Block block = item.getWorld().getBlockAt(item.getLocation());
        // plugin.log.info("onItemSpawn block: " + block);

        final Optional<MetadataValue> metadata = block.getMetadata(SignUtils.BULLSEYE_SIGN_MATERIAL).stream()
                .filter(metadataValue -> Objects.equals(metadataValue.getOwningPlugin(), plugin))
                .findFirst();

        // Attempt to drop the sign instead if Bullseye metadata is found
        metadata.ifPresent(metadataValue -> {
            // plugin.log.info("onItemSpawn block metadata: " + metadataValue.asString());

            // Get the previous sign material from the metadata value
            final Material signMaterial = signUtils.getMaterialFromMetadata(metadataValue);

            // If there is a valid material
            if (signMaterial != null) {

                // If it was broken, the block should be empty, and we want to replace it with the original sign.
                // However, if water breaks the redstone torch, it creates the drop before actually breaking.
                // This is way extra metadata is set on the item that drops and is checked before this.

                // if (block.isEmpty()) {
                try {
                    final ItemStack signStack = new ItemStack(signMaterial);
                    item.setItemStack(signStack);
                    block.removeMetadata(SignUtils.BULLSEYE_SIGN_MATERIAL, plugin);
                    // plugin.log.info("onItemSpawn: Replaced drop with: " + signMaterial);
                } catch (final RuntimeException e) {
                    plugin.log.warning("Failed to update drop back to a " + signMaterial);
                    event.setCancelled(true);
                }
                // }
            } else {
                // If metadata wasn't a valid material
                event.setCancelled(true);
            }
        });
    }
}
