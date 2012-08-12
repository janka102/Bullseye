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

        if(signHandle.isBullseyeSign(event.getLine(0).trim())) {

        	//If sign post, check if its 'pointing' at a special block (workbench, dispenser, etc.)
        	if(event.getBlock().getType() == Material.SIGN_POST){
 	        	signBlock = event.getBlock();
 	        	BlockFace signOrientation;
 	        	Byte wallSignData;
 	        	
 	        	switch(signBlock.getData())
 	            {
 	            case 0: // '\0'
 	                signOrientation = BlockFace.WEST;
 	                wallSignData = Byte.valueOf((byte)3);
 	                break;

 	            case 4: // '\004'
 	                signOrientation = BlockFace.NORTH;
 	                wallSignData = Byte.valueOf((byte)4);
 	                break;

 	            case 8: // '\b'
 	                signOrientation = BlockFace.EAST;
 	                wallSignData = Byte.valueOf((byte)2);
 	                break;

 	            case 12: // '\f'
 	                signOrientation = BlockFace.SOUTH;
 	                wallSignData = Byte.valueOf((byte)5);
 	                break;

 	            default:
 	               	signOrientation = null;
 	                wallSignData = null;
 	            }
 	        	
 	        	if (signOrientation != null && wallSignData != null) {
 	        		Block blockbehind = signBlock.getRelative(signOrientation.getOppositeFace());
 	        		blockType = blockbehind.getState().getType();
 	 	        	if (blockType == Material.DISPENSER
 	 	        	 || blockType == Material.FURNACE
 	 	       		 || blockType == Material.NOTE_BLOCK
 	 	        	 || blockType == Material.WORKBENCH
 	 	        	 || blockType == Material.JUKEBOX)
 	 	        	{
 	 	        		signBlock.setType(Material.WALL_SIGN);
 	 	        		signBlock.setData(wallSignData.byteValue());
 	 	        		String[] lines = event.getLines();
 	 	        		
 	 	        		if (signHandle.isValidBlock(blockbehind)) {
 	 	        			lines[0] = ChatColor.DARK_BLUE + lines[0];
 	 	        			signHandle.updateSign(signBlock, lines);
 	 	        		}
 	 	        		else {
 	 	        			lines[0] = ChatColor.DARK_RED + lines[0];
 	 	        			signHandle.updateSign(signBlock, lines);
 	 	        		}
 	 	        	}
 	        	}
        	}
        }
        
        //get the attached block
  		Block b = event.getBlock();
  		org.bukkit.material.Sign s = (org.bukkit.material.Sign) b.getState().getData();
  		Block attachedBlock = b.getRelative(s.getAttachedFace());
  		

  		if (blockType == Material.DISPENSER && !plugin.allowDispensers) {
  			
    		event.setLine(0, ChatColor.DARK_RED + event.getLine(0));
    		event.getBlock().getState().update(true);
    		return;
  		}
  		//check if the player placed a sign on a correct block for Bullseye
  		else if (signHandle.isValidBlock(attachedBlock)) {
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