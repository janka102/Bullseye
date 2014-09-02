package com.github.janka102.bullseye;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener {
    public final SignUtils signHandle = new SignUtils();
    public final Bullseye plugin;

    public SignListener(Bullseye bullseye) {
        plugin = bullseye;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // Called when a sign is created, and after the text is entered
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (signHandle.isBullseyeSign(event.getLine(0).trim(), false)) {
             //get the attached block
              Block eventSign = event.getBlock();
              BlockState signState = eventSign.getState();
              org.bukkit.material.Sign sign = (org.bukkit.material.Sign) signState.getData();
              Block attachedBlock = eventSign.getRelative(sign.getAttachedFace());

              if (signHandle.isValidBlock(attachedBlock)) {
                event.setLine(0, ChatColor.DARK_BLUE + event.getLine(0).trim());
                signState.update(true);

                 event.getPlayer().sendMessage(ChatColor.GREEN + "New Bullseye sign created!");
            } else {
                event.setLine(0, ChatColor.DARK_RED + event.getLine(0).trim());
                event.getBlock().getState().update(true);
            }
        }
    }
}