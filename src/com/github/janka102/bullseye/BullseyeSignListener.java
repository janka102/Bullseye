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

	public BullseyeSignListener(Bullseye b_plugin) {
		plugin = b_plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	//Called when a sign is created, and after the text is entered
	@EventHandler
	public void onSignChange(SignChangeEvent event) {

        if(signHandle.isBullseyeSign(event.getLine(0).trim())) {

        	//If sign post, check if its 'pointing' at a special block (crafting table, dispenser, etc.)
        	if(event.getBlock().getType() == Material.SIGN_POST){
 	        	Block signBlock = event.getBlock();
 	        	BlockFace signPostOrientation;
 	        	Byte wallSignData;
 	        	switch(signBlock.getData())
 	            {
 	           case 0: // '\0'
 	                signPostOrientation = BlockFace.WEST;
 	                wallSignData = Byte.valueOf((byte)3);
 	                break;

 	            case 4: // '\004'
 	                signPostOrientation = BlockFace.NORTH;
 	                wallSignData = Byte.valueOf((byte)4);
 	                break;

 	            case 8: // '\b'
 	                signPostOrientation = BlockFace.EAST;
 	                wallSignData = Byte.valueOf((byte)2);
 	                break;

 	            case 12: // '\f'
 	                signPostOrientation = BlockFace.SOUTH;
 	                wallSignData = Byte.valueOf((byte)5);
 	                break;

 	            default:
 	               	signPostOrientation = null;
 	                wallSignData = null;
 	            }
 	        	if (signPostOrientation != null && wallSignData != null) {
 	        		Block blockbehind = signBlock.getRelative(signPostOrientation.getOppositeFace());
 	        		Material blockType = blockbehind.getState().getType();
 	 	        	if(blockType == Material.DISPENSER
 	 	        			|| blockType == Material.FURNACE
 	 	        			|| blockType == Material.NOTE_BLOCK
 	 	        			|| blockType == Material.WORKBENCH
 	 	        			|| blockType == Material.JUKEBOX) {
 	 	        		
 	 	        		String[] signLines = event.getLines();
 	 	        		signBlock.setType(Material.WALL_SIGN);
 	 	        		signBlock.setData(wallSignData.byteValue());
 	 	        		
 	 	        		//get the attached inventoryHolder
 	 	        		Block b = event.getBlock();
 	 	        		org.bukkit.material.Sign s = (org.bukkit.material.Sign) b.getState().getData();
 	 	        		Block attachedBlock = b.getRelative(s.getAttachedFace());
 	 	        		
 	 	        		if ((blockType == Material.DISPENSER && !plugin.allowDispensers)
 	 	        				|| !signHandle.isValidBlock(attachedBlock)) {
 	 	        			event.setLine(0, ChatColor.DARK_RED + event.getLine(0));
 	 	        			event.getBlock().getState().update(true);
 	 	        			
 	 	        			signHandle.updateSign(signBlock, signLines);
 	 	        			
 	 	        			return;
 	 	        		}
 	 	        		event.setLine(0, ChatColor.DARK_BLUE + event.getLine(0));
 	 	        		
 	 	        		signHandle.updateSign(signBlock, signLines);
 	 	        		
 	 	        		//gets coordinates of the inventoryHolder
 	 		        	int posX = attachedBlock.getX();
 	 		 	        int posY = attachedBlock.getY();
 	 		 	        int posZ = attachedBlock.getZ();
 	 		 	        // Notify player they have just created a new Bullseye block
 	 		 	        Player player = event.getPlayer();
 	 		 	        player.sendMessage(ChatColor.AQUA + "New Bullseye block created!");
 	 		 	        player.sendMessage(ChatColor.GOLD + "Location at x: " + posX + " y: " + posY + " z: " + posZ + ChatColor.GREEN + " Block type: " + attachedBlock.getType() );

 	 	        		return;
 	 	        	}
 	        	}
 	        }

        	Block b = event.getBlock();
        	org.bukkit.material.Sign s = (org.bukkit.material.Sign) b.getState().getData();
        	Block attachedBlock = b.getRelative(s.getAttachedFace());

        	//check if the player placed a sign on a correct block for Bullseye
        	if (signHandle.isValidBlock(attachedBlock)) {
        		event.setLine(0, ChatColor.DARK_BLUE + event.getLine(0));
        		event.getBlock().getState().update(true);

	 	        //gets coordinates of the attached block
	        	int posX = attachedBlock.getX();
	 	        int posY = attachedBlock.getY();
	 	        int posZ = attachedBlock.getZ();
	 	        // Notify player they have just created a new Bullseye block
	 	        Player player = event.getPlayer();
	 	        player.sendMessage(ChatColor.AQUA + "New Bullseye block created!");
	 	        player.sendMessage(ChatColor.GOLD + "Location at x: " + posX + " y: " + posY + " z: " + posZ + ChatColor.GREEN + " Block type: " + attachedBlock.getType() );
        	}
        	//Signal to the player that the block attached to the sign wont work with Bullseye
        	else {
        		event.setLine(0, ChatColor.DARK_RED + event.getLine(0));
        		event.getBlock().getState().update(true);
        	}
        }

	}
}