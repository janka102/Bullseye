package com.github.janka102.bullseye;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class BullseyeSignListener implements Listener {
    public final BullseyeSignHandler signHandle = new BullseyeSignHandler();
    public final Bullseye plugin;
    private Material blockType;
    private Block signBlock;

    public BullseyeSignListener(Bullseye b_plugin) {
        plugin = b_plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    //Called when a sign is created, and after the text is entered
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