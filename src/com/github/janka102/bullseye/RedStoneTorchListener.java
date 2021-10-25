package com.github.janka102.bullseye;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class RedStoneTorchListener implements Listener {
    private static final Pattern MATERIAL_WALL_REPLACE = Pattern.compile("WALL_");

    public final SignUtils signUtils;
    public final Bullseye plugin;

    public RedStoneTorchListener(final Bullseye bullseye) {
        plugin = bullseye;
        signUtils = new SignUtils(bullseye);
    }

    /**
     * Check item drops if they still have Bullseye metadata then that means the RS torch broke.
     * Try and drop the original sign instead.
     *
     * @param event The entity spawn event to check
     */
    @EventHandler
    public void onItemDrop(final EntitySpawnEvent event) {
        if (event.getEntityType() != EntityType.DROPPED_ITEM) {
            return;
        }

        final Item item = (Item) event.getEntity();
        if (item.getItemStack().getType() != Material.REDSTONE_TORCH) {
            return;
        }

        final Block block = item.getWorld().getBlockAt(item.getLocation());
        final List<MetadataValue> metadata = block.getMetadata(SignUtils.BULLSEYE_SIGN_MATERIAL);
//        plugin.log.info("DroppedItem: " + item.getItemStack());
//        plugin.log.info("Item Location: " + item.getLocation());
//        plugin.log.info("Item IsBullseyeSign: " + (metadata.size() > 0));

        // Attempt to drop the sign instead if Bullseye metadata is found
        if (metadata.size() > 0) {
            // Get the previous sign material from the metadata value
            final Material signMaterial = metadata.stream()
                    .filter(metadataValue -> Objects.equals(metadataValue.getOwningPlugin(), plugin))
                    .reduce(null, (previousMaterial, metadataValue) -> {
                        // Cannot drop WALL_SIGNs directly, so replace it with normal sign types
                        final String materialName = MATERIAL_WALL_REPLACE.matcher(metadataValue.asString())
                                .replaceFirst("");
                        final Material newMaterial = Material.getMaterial(materialName);

                        return newMaterial != null ? newMaterial : previousMaterial;
                    }, (material1, material2) -> material2 != null ? material2 : material1);

            if (signMaterial != null) {
                try {
                    final ItemStack signStack = new ItemStack(signMaterial);
                    item.setItemStack(signStack);
                } catch (final RuntimeException e) {
                    plugin.log.warning("Failed to update drop back to a " + signMaterial);
                    event.setCancelled(true);
                }
            } else {
                // If metadata wasn't a valid material, then drop nothing instead
                event.setCancelled(true);
            }
        }

    }
}
