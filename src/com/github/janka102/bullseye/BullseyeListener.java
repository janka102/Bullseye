package com.github.janka102.bullseye;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.BlockIterator;

public class BullseyeListener implements Listener {
	public final BullseyeSignHandler signHandle = new BullseyeSignHandler();
	public final Bullseye plugin;
	public String lines = "";
	
	public BullseyeListener(Bullseye b_plugin) {
		plugin = b_plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public Block[] getHitBlockSign(Block hit){
		Block[] signs = new Block[5];
		int numSigns = 0;
		for(int z = -1; z <= 1; z++) {
    		for(int x = -1; x <= 1; x++) {
    			for(int y = 0; y <= 1; y++) {
    				if(x*x+y*y+z*z == 1) {
    					// get the block to the North, South, East, West, and Top of the block hit
    					Block hitSign = hit.getRelative(x, y, z);
    					
    					if(hitSign.getState() instanceof Sign) {
        					Sign hitSignSign = (Sign)hitSign.getState();
        					
        					//checks to see if the sign next to the block hit is a Bullseye sign
            				if (signHandle.isBullseyeSign(hitSignSign.getLine(0)) || signHandle.isBullseyeSignBlue(hitSignSign.getLine(0)) || signHandle.isBullseyeSignRed(hitSignSign.getLine(0))) {
            					org.bukkit.material.Sign s = (org.bukkit.material.Sign) hitSign.getState().getData();
            				    Block attachedBlock = hitSign.getRelative(s.getAttachedFace());
            				    
            				    //checks to make sure the sign is attached to the block hit
            					if(attachedBlock.equals(hit)) {
            						signs[numSigns] = hitSign;
            						numSigns++;
          						}
           					}
    					}
   					}
   				}
  			}
   		}
		return signs;
	}

	@EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
		Player p = null;
		Entity projectile = event.getEntity();
        if(!(projectile instanceof Arrow)) {
        	 return;
        }

        Arrow arrow = (Arrow)projectile;
        Entity entity = arrow.getShooter();
        
        if (entity instanceof Skeleton) {
        	//Don't allow skeletons to activate the signs
        	return;
        }
        else if(entity instanceof Player) {
        	p = (Player)entity;
        }
        else if (!(arrow.getShooter() instanceof LivingEntity) && !plugin.allowDispensers) {
        	//Dispenser, but disabled in config
        	return;
        }


        World world = arrow.getWorld();
        
        BlockIterator bi = new BlockIterator(world, arrow.getLocation().toVector(), arrow.getVelocity().normalize(), 0, 4);
        Block hit = null;
        Material type = null;
        
        //make sure the check skips blocks arrows can go through
        while(bi.hasNext()) {
            hit = bi.next();
            type = hit.getType();
            if(type != Material.AIR
            		&& type != Material.WATER
            		&& type != Material.STATIONARY_WATER
            		&& type != Material.LAVA
         			&& type != Material.STATIONARY_LAVA
            		&& type != Material.LONG_GRASS
            		&& type != Material.REDSTONE_WIRE
            		&& type != Material.SAPLING
            		&& type != Material.WEB
            		&& type != Material.YELLOW_FLOWER
            		&& type != Material.RED_ROSE
            		&& type != Material.DIODE_BLOCK_OFF
            		&& type != Material.DIODE_BLOCK_ON
            		&& !type.toString().contains("TRIPWIRE") //for 1.3.1
            		&& type != Material.REDSTONE_TORCH_ON
            		&& type != Material.REDSTONE_TORCH_OFF
            		&& type != Material.TORCH
            		&& type != Material.VINE
            		&& type != Material.RAILS
            		&& type != Material.DETECTOR_RAIL
            		&& type != Material.POWERED_RAIL)
            {
            	//type = hit.getType();
            	break;
            }
        }
        
        //if the block hit is a Bullseye sign, set hit to the attached block
        if (type.toString().contains("SIGN")) {
        	String signLine = ((Sign)hit.getState()).getLine(0);
            
        	if (signHandle.isBullseyeSign(signLine) || signHandle.isBullseyeSignBlue(signLine) || signHandle.isBullseyeSignRed(signLine)) {
        		
        		org.bukkit.material.Sign s = (org.bukkit.material.Sign) hit.getState().getData();
    		    Block attachedBlock = hit.getRelative(s.getAttachedFace());
    		    hit = attachedBlock;
        	}
        }
        	
        if(signHandle.isValidBlock(hit)) {
        	//get all bullseye signs attched to the hit block and change them to a redstone torch
        	Block[] hitBlockSign = getHitBlockSign(hit);
        	boolean h2oSigns = true;
        	for (Block bullseyeSign : hitBlockSign) {
        		if (bullseyeSign == null) {
            		continue;
            	}
            	Sign hitSignSign = (Sign) bullseyeSign.getState();
            	
            	if (signHandle.isBullseyeSignRed(hitSignSign.getLine(0)) || signHandle.isBullseyeSign(hitSignSign.getLine(0))) {
        			
        			String newLine = hitSignSign.getLine(0).replace(ChatColor.DARK_RED.toString(), "");
        			newLine = ChatColor.DARK_BLUE.toString() + newLine;
        			String[] lines = hitSignSign.getLines();
        			lines[0] = newLine;
        			
        			signHandle.updateSign(bullseyeSign, lines);
        		}
            	
            	if(signHandle.isNearWater(bullseyeSign)) {
            		//only send the message once, even if more than one sign is affected
            		if (h2oSigns && p != null){
            			p.sendMessage(ChatColor.RED + "Couldn't activate some Bullseye signs with water near them!");
            			h2oSigns = false;
            		}
        			continue;
        		}
            	//checks if there is a message the player wants shown on hit
    			if(p != null &&
    					(hitSignSign.getLine(1).trim().length() >= 1
    						|| hitSignSign.getLine(2).trim().length() >= 1
    						|| hitSignSign.getLine(3).trim().length() >= 1)) {
    				p.sendMessage("Bullseye! You hit " + hitSignSign.getLine(1) + hitSignSign.getLine(2) + hitSignSign.getLine(3) +"!");
    			}
    			if (bullseyeSign.getType() == Material.SIGN_POST) {
    				//change the sign to a redstone torch as SIGN_POST
    				signHandle.signToRestone(plugin, hitSignSign, bullseyeSign, bullseyeSign.getType(), bullseyeSign.getData(), true);	
    			}
    			else if (bullseyeSign.getType() == Material.WALL_SIGN) {
    				//change the sign to a redstone torch as WALL_SIGN
    				signHandle.signToRestone(plugin, hitSignSign, bullseyeSign, bullseyeSign.getType(), bullseyeSign.getData(), false);
    			}
        	}
		}
        else {
        	Block[] hitBlockSign = getHitBlockSign(hit);
        	for (Block bullseyeSign : hitBlockSign) {
        		if (bullseyeSign == null) {
            		continue;
            	}
        		Sign bullSign = (Sign)bullseyeSign.getState();
        		        		
        		if (signHandle.isBullseyeSignBlue(bullSign.getLine(0)) || signHandle.isBullseyeSign(bullSign.getLine(0))) {
        			
        			String newLine = bullSign.getLine(0).replace(ChatColor.DARK_BLUE.toString(), "");
        			newLine = ChatColor.DARK_RED.toString() + newLine;
        			String[] lines = bullSign.getLines();
        			lines[0] = newLine;
        			signHandle.updateSign(bullseyeSign, lines);
        		}
        	}
        }
	}

}
