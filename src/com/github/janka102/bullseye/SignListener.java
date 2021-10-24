package com.github.janka102.bullseye;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Attachable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener {
    public final SignUtils signUtils;
    public final Bullseye plugin;

    public SignListener(Bullseye bullseye) {
        plugin = bullseye;
        signUtils = new SignUtils(bullseye);
    }

    // Called when a sign is created, and after the text is entered
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        String firstLine = event.getLine(0);

        if (firstLine == null) {
            return;
        }

        firstLine = firstLine.trim();

        if (signUtils.isBullseyeSign(firstLine, false)) {
            // get the attached block
            Block eventSign = event.getBlock();
            BlockState signState = eventSign.getState();
            Attachable sign = (Attachable) signState.getData();
            Block attachedBlock = eventSign.getRelative(sign.getAttachedFace());

            if (signUtils.isValidBlock(attachedBlock)) {
                event.setLine(0, ChatColor.DARK_BLUE + firstLine);
                signState.update(true);

                event.getPlayer().sendMessage(ChatColor.GREEN + "New Bullseye sign created!");
            } else {
                event.setLine(0, ChatColor.DARK_RED + firstLine);
                signState.update(true);
            }
        }
    }
}
