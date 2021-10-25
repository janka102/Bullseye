package com.github.janka102.bullseye;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener {
    private final Bullseye plugin;
    private final SignUtils signUtils;

    public SignListener(final Bullseye bullseye) {
        plugin = bullseye;
        signUtils = new SignUtils(bullseye);
    }

    /**
     * Called when a sign is created, and after the text is entered.
     * Update the sign text color and notify the player if successfully created a Bullseye sign.
     *
     * @param event The sign event
     */
    @EventHandler
    public void onSignChange(final SignChangeEvent event) {
        final Sign sign = (Sign) event.getBlock().getState();

        if (signUtils.updateBullseyeSign(sign, event.getLines())) {
            event.getPlayer().sendMessage(ChatColor.GREEN + "New Bullseye sign created!");
        }
    }
}
