package com.github.janka102.bullseye;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class BullseyeSignListener implements Listener {
	public final Bullseye plugin;

	public BullseyeSignListener(Bullseye r_plugin) {
		plugin = r_plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public boolean isValidBlock(Block block) { //Checks if the block hit, or the block a sign placed on, can even handle a redstone torch.
		if(block == null) {
			return false;
		}
			Material blockType = block.getType();
			if (blockType == Material.AIR //blocks that don't work with redstone torches
					|| blockType == Material.STEP
					|| blockType == Material.TNT
					|| blockType.toString().contains("STAIRS")
					|| blockType.toString().contains("GLASS")
					|| blockType == Material.IRON_FENCE
					|| blockType == Material.CACTUS
					|| blockType == Material.WEB
					|| blockType.toString().contains("PISTON")
					|| blockType == Material.GLOWSTONE
					//|| blockType.toString().contains("SIGN")
					|| blockType == Material.ENDER_PORTAL_FRAME)
			{
				return false;
			}
		
		return true;
	}

	public boolean isBullseyeSign(String line) { //checks to make sure the sign is a Bullseye sign
		if(line.equalsIgnoreCase((ChatColor.DARK_BLUE + "[bullseye]").toString()) || line.equalsIgnoreCase(("[bullseye]").toString())
				|| line.equalsIgnoreCase((ChatColor.DARK_BLUE + "[bull]").toString()) || line.equalsIgnoreCase(("[bull]").toString())
				|| line.equalsIgnoreCase((ChatColor.DARK_BLUE + "[be]").toString()) || line.equalsIgnoreCase(("[be]").toString()))
		{
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event) { //Called when a sign is created, and after the text is entered

        if(isBullseyeSign(event.getLine(0).trim())) { //Check sign text for Bullseye text

        	if(event.getBlock().getType() == Material.SIGN_POST){ //If sign post, check of its 'pointing' at a special block Crafting table, Dispenser, etc.
 	        	Block signBlock = event.getBlock();
 	        	BlockFace signPostOrientation = null;
 	        	Byte wallSignData;
 	        	switch(event.getBlock().getData())
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
 	 	        		event.setLine(0, ChatColor.DARK_BLUE + event.getLine(0));
 	 	        		String[] signLines = event.getLines();
 	 	        		signBlock.setType(Material.WALL_SIGN);
 	 	        		signBlock.setData(wallSignData.byteValue());
 	 	        		//get the attached inventoryHolder
 	 	        		Block b = event.getBlock();
 	 	        		org.bukkit.material.Sign s = (org.bukkit.material.Sign) b.getState().getData();
 	 	        		Block attachedBlock = b.getRelative(s.getAttachedFace());
 	 	        		//gets coordinates of the inventoryHolder
 	 		        	int posX = attachedBlock.getX();
 	 		 	        int posY = attachedBlock.getY();
 	 		 	        int posZ = attachedBlock.getZ();
 	 		 	        // Notify player they have just created a new Bullseye block
 	 		 	        Player player = event.getPlayer();
 	 		 	        player.sendMessage(ChatColor.AQUA + "New Bullseye block created!");
 	 		 	        player.sendMessage(ChatColor.GOLD + "Location at x: " + posX + " y: " + posY + " z: " + posZ + ChatColor.GREEN + " Block type: " + attachedBlock.getType() );

 	 	        		Sign sign = (Sign)signBlock.getState();
 	 	        		for(int i = 0; i < signLines.length; i++) {
 	 	                   sign.setLine(i, signLines[i].toString());
 	 	        		}
 	 	        		sign.update(true);
 	 	        		return;
 	 	        	}
 	        	}
 	        }

        	Block b = event.getBlock();
        	org.bukkit.material.Sign s = (org.bukkit.material.Sign) b.getState().getData();
        	Block attachedBlock = b.getRelative(s.getAttachedFace());

        	if (isValidBlock(attachedBlock)) { //check if the player placed a sign on a correct block for Bullseye
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
        	else { //Signal to the player that the block attached to the sign wont work with Bullseye
        		event.setLine(0, ChatColor.DARK_RED + event.getLine(0));
        		event.getBlock().getState().update(true);
        	}
        }

	}
}