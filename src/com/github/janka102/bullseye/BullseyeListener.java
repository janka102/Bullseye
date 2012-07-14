package com.github.janka102.bullseye;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.BlockIterator;

public class BullseyeListener implements Listener {
	public final Bullseye plugin;
	private boolean isWall;
	public String lines = "";
	
	public BullseyeListener(Bullseye r_plugin) {
		plugin = r_plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public boolean isValidBlock(Block block) { //Checks if the block hit, or the block a sign placed on, can even handle a redstone torch.
		if(block == null) {
			return false;
		}

		if (block.getType() == Material.AIR //blocks that don't work with redstone torches
				|| block.getType() == Material.STEP
				|| block.getType() == Material.TNT
				|| block.getType() == Material.COBBLESTONE_STAIRS
				|| block.getType() == Material.WOOD_STAIRS
				|| block.getType() == Material.BRICK_STAIRS
				|| block.getType() == Material.SMOOTH_STAIRS
				|| block.getType() == Material.THIN_GLASS
				|| block.getType() == Material.IRON_FENCE
				|| block.getType() == Material.CACTUS
				|| block.getType() == Material.WEB
				|| block.getType() == Material.PISTON_BASE
				|| block.getType() == Material.PISTON_EXTENSION
				|| block.getType() == Material.PISTON_MOVING_PIECE
				|| block.getType() == Material.PISTON_STICKY_BASE
				|| block.getType() == Material.GLOWSTONE
				|| block.getType() == Material.WALL_SIGN
				|| block.getType() == Material.SIGN_POST)
		{
			return false;
		}
		else {
			return true;
		}
	}
	
	public void signWarning(Block block) { //Adds text to a Bullseye sign placed on an invalid  block.
		Sign sign = (Sign) block.getState();

		sign.setLine(1, ChatColor.DARK_RED + "Sign is on");
		sign.setLine(2, ChatColor.DARK_RED + "a BAD BLOCK");
		sign.update();
	}
	
	public boolean isBullseyeSign(String line) { //checks to make sure the sign is a Bullseye sign
		if(line.equalsIgnoreCase("[be]")
				|| line.equalsIgnoreCase("[bullseye]")
				|| line.equalsIgnoreCase("[bull]"))
		{
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event) { //Called when a sign is created, and after the text is entered
		
        if(isBullseyeSign(event.getLine(0))) { //check sign text for Bullseye text

        	Block b = event.getBlock();
        	org.bukkit.material.Sign s = (org.bukkit.material.Sign) b.getState().getData();
        	Block attachedBlock = b.getRelative(s.getAttachedFace());

        	if (isValidBlock(attachedBlock)) { //check if the player placed a sign on a correct block for Bullseye
        		int posX = 0;
        		int posY = 0;
        		int posZ = 0;

	        	posX = attachedBlock.getX();
	 	        posY = attachedBlock.getY();
	 	        posZ = attachedBlock.getZ();
	 	        // Notify player they have just created a new Bullseye block
	 	        Player player = event.getPlayer();
	 	        player.sendMessage(ChatColor.AQUA + "New Bullseye block created!");
	 	        player.sendMessage(ChatColor.GOLD + "Location at x: " + posX + " y: " + posY + " z: " + posZ + ChatColor.GREEN + " Block type: " + attachedBlock.getType() );
        	}
        	else { //signal to the player that the block attached to the sign wont work with Bullseye
        		event.setLine(2, ChatColor.DARK_RED + "Sign is on");
        		event.setLine(3, ChatColor.DARK_RED + "a BAD BLOCK");
        		event.getBlock().getState().update(true);
        	}
        }

	}

	@EventHandler
    public void onProjectileHit(ProjectileHitEvent event) { //called when any projectile comes into contact with something
		Entity projectile = event.getEntity();
        if(!(projectile instanceof Arrow)) { //narrow the search for just arrows
        	 return;
        }

        Arrow arrow = (Arrow)projectile;
        Entity entity = arrow.getShooter();
        if(!(entity instanceof Player)) { //check to see if the player was the one who shot the arrow
        	return;
        }
        Player p = (Player)entity;
        World world = arrow.getWorld();
        BlockIterator bi = new BlockIterator(world, arrow.getLocation().toVector(), arrow.getVelocity().normalize(), 0, 4);
        Block hit = null;

        while(bi.hasNext()) {
            hit = bi.next();
            if(hit.getType()!=Material.AIR)
            {
                break;
            }
        }

        if(isValidBlock(hit)) { //check if the block hit by the arrow would be a block that a Bullseye sign might go
        	//hitBlock = hit;
        	//p.sendMessage(hit.getType().toString());
        	for(int z = -1; z <= 1; z++) {
        		for(int x = -1; x <= 1; x++) {
        			for(int y = 0; y <= 1; y++) {
        				if(x*x+y*y+z*z == 1) {
        					Block hitSign = hit.getRelative(x, y, z);  // get the blocks to the North, South, East, West, Top and Bottom of the block hit
        					if(hitSign.getState() instanceof Sign) {
            					Sign hitSignSign = (Sign)hitSign.getState();
                				if (isBullseyeSign(hitSignSign.getLine(0))) { //checks to see if the sign next to the block hit is a Bullseye sign
                					org.bukkit.material.Sign s = (org.bukkit.material.Sign) hitSign.getState().getData();
                				    Block attachedBlock = hitSign.getRelative(s.getAttachedFace());
                					if(attachedBlock.equals(hit)) { //checks to make sure the sign is attached to the block hit
                						//Bullseye!!
                						if(hitSignSign.getLine(1).trim().length() >= 1) { //checks if there is a message the player wants shown on hit
                							p.sendMessage("You hit " + hitSignSign.getLine(1).trim() + "! Bullseye!!");
                						}
                						signToRestone(hitSignSign, hitSign, hitSign.getType(), hitSign.getData()); //going to change the sign to a redstone torch
              						}
               					}
        					}
       					}
       				}
      			}
       		}
       	}
	}

	public void signToRestone(final Sign hitSign, final Block hitBlock, Material hitSignType, final Byte hitSignData) { //changes the Bullseye sign to a redstone torch for a specified time
		final String[] lines = hitSign.getLines();
		if (hitSign.getType() == Material.SIGN_POST) { //if the Bullseye sign is a sign post
			isWall = false; //it is NOT a wall sign here
			hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(), (byte) 0x5,true); //make the Bullseye sign into a redstone torch
		}
		else if (hitSign.getType() == Material.WALL_SIGN) { //if the Bullseye sign is a wall sign
			isWall = true;//it IS a wall sign here
			byte data = hitBlock.getData(); // Correspond to the direction of the wall sign
			if (data == 0x2) { //South
				hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x4, true); //make the Bullseye sign into a redstone torch
			}
			else if (data == 0x3) { //North
				hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x3, true); //make the Bullseye sign into a redstone torch
			}
			else if (data == 0x4) { //East
				hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x2, true); //make the Bullseye sign into a redstone torch
			}
			else if (data == 0x5) { //West
				hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x1, true); //make the Bullseye sign into a redstone torch
			}
			else { // Not West East North South ...
				Bullseye.getBullLogger().info("Strange Data !");
			}
		}

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { //run this after a delay
			public void run() {
				try {
					hitBlock.setType(Material.AIR); //clear the block

					if (isWall) { //if a wall sign, put it back as one
						hitBlock.setType(Material.WALL_SIGN);
						hitBlock.setTypeIdAndData(Material.WALL_SIGN.getId(), hitSignData, true);
					}
					else { //if a sign post, put it back as one
						hitBlock.setType(Material.SIGN_POST);
						hitBlock.setTypeIdAndData(Material.SIGN_POST.getId(), hitSignData, true);
					}
					Sign signtemp = (Sign) hitBlock.getState();
					for(int i = 0;i < 4;i++) { //restore the original text of the Bullseye sign
						signtemp.setLine(i, lines[i]);
						signtemp.update(true);
					}
					
					//plugin.getServer().broadcastMessage(ChatColor.AQUA + "Changed sign back.");
				}
				catch (RuntimeException e)
				{
					Bullseye.getBullLogger().severe("Error while updating redstone signToRestone :" + e.getClass() + ":" + e.getMessage());
					return;
				}
			}
		}, 25L );
	}
}
