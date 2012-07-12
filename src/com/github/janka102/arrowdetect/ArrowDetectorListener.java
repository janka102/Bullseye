package com.github.janka102.arrowdetect;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.BlockIterator;

public class ArrowDetectorListener implements Listener {
	private final ArrowDetect plugin;
	private boolean isWall;
	public String lines = "";

	public ArrowDetectorListener(ArrowDetect r_plugin)
	{
		plugin = r_plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public boolean isValidLocation(Block block)
	{
		if(block == null)
			return false;

		Block tempBlock = block.getRelative(BlockFace.DOWN);

		if (tempBlock.getType() == Material.AIR
				|| tempBlock.getType() == Material.STEP
				|| tempBlock.getType() == Material.TNT
				|| tempBlock.getType() == Material.COBBLESTONE_STAIRS
				|| tempBlock.getType() == Material.WOOD_STAIRS
				|| tempBlock.getType() == Material.BRICK_STAIRS
				|| tempBlock.getType() == Material.SMOOTH_STAIRS
				|| tempBlock.getType() == Material.THIN_GLASS
				|| tempBlock.getType() == Material.IRON_FENCE
				|| tempBlock.getType() == Material.CACTUS
				|| tempBlock.getType() == Material.WEB
				|| tempBlock.getType() == Material.PISTON_BASE
				|| tempBlock.getType() == Material.PISTON_EXTENSION
				|| tempBlock.getType() == Material.PISTON_MOVING_PIECE
				|| tempBlock.getType() == Material.PISTON_STICKY_BASE
				|| tempBlock.getType() == Material.GLOWSTONE
				|| tempBlock.getType() == Material.WALL_SIGN
				|| tempBlock.getType() == Material.SIGN_POST)
		{
			return false;
		}
		else
			return true;
	}

	public boolean isValidWallLocation(Block block)
	{
		BlockFace face = BlockFace.DOWN;
		switch(block.getData())
		{
		case 0x2: //South
			face = BlockFace.WEST;
			break;

		case 0x3: //North
			face = BlockFace.EAST;
			break;

		case 0x4: //east
			face = BlockFace.SOUTH;
			break;

		case 0x5: //West
			face = BlockFace.NORTH;
			break;
		}
		Block tempBlock = block.getRelative(face);

		if (tempBlock.getType() == Material.AIR
				|| tempBlock.getType() == Material.STEP
				|| tempBlock.getType() == Material.TNT
				|| tempBlock.getType() == Material.COBBLESTONE_STAIRS
				|| tempBlock.getType() == Material.WOOD_STAIRS
				|| tempBlock.getType() == Material.BRICK_STAIRS
				|| tempBlock.getType() == Material.SMOOTH_STAIRS
				|| tempBlock.getType() == Material.THIN_GLASS
				|| tempBlock.getType() == Material.IRON_FENCE
				|| tempBlock.getType() == Material.CACTUS
				|| tempBlock.getType() == Material.WEB
				|| tempBlock.getType() == Material.PISTON_BASE
				|| tempBlock.getType() == Material.PISTON_EXTENSION
				|| tempBlock.getType() == Material.PISTON_MOVING_PIECE
				|| tempBlock.getType() == Material.PISTON_STICKY_BASE
				|| tempBlock.getType() == Material.GLOWSTONE
				|| tempBlock.getType() == Material.WALL_SIGN
				|| tempBlock.getType() == Material.SIGN_POST)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
<<<<<<< HEAD

=======
	
>>>>>>> Added more checks to see if block is valid. Now text shows on the sign that it was placed on an invalid block, if that is true. Also, it now ignores if the sign is next to but not connected to the block hit.
	public boolean isValidBlock(Block block)
	{
		if(block == null)
			return false;

		if (block.getType() == Material.AIR
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
<<<<<<< HEAD
			return false;
		}
		else
			return true;
=======
			//plugin.getServer().broadcastMessage(ChatColor.DARK_RED + "INVALID BLOCK!");
			return false;
		}
		else
			//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + "VALID BLOCK!");
			return true;
	}
	
	public void signWarning(Block block)
	{
		Sign sign = (Sign) block.getState();
		
		sign.setLine(1, ChatColor.DARK_RED + "Sign is on");
		sign.setLine(2, ChatColor.DARK_RED + "a BAD BLOCK");
		sign.update();
>>>>>>> Added more checks to see if block is valid. Now text shows on the sign that it was placed on an invalid block, if that is true. Also, it now ignores if the sign is next to but not connected to the block hit.
	}

	public void signWarning(Block block)
	{
		Sign sign = (Sign) block.getState();

		sign.setLine(1, ChatColor.DARK_RED + "Sign is on");
		sign.setLine(2, ChatColor.DARK_RED + "a BAD BLOCK");
		sign.update();
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) //Called when a sign is created, and after the text entered
	{
		Block b = event.getBlock();
    	org.bukkit.material.Sign s = (org.bukkit.material.Sign) b.getState().getData();
        Block attachedBlock = b.getRelative(s.getAttachedFace());
<<<<<<< HEAD

=======
        
>>>>>>> Added more checks to see if block is valid. Now text shows on the sign that it was placed on an invalid block, if that is true. Also, it now ignores if the sign is next to but not connected to the block hit.
		if (isValidBlock(attachedBlock))
		{
	    	int posX = 0;
	    	int posY = 0;
	    	int posZ = 0;
<<<<<<< HEAD

=======
	        
>>>>>>> Added more checks to see if block is valid. Now text shows on the sign that it was placed on an invalid block, if that is true. Also, it now ignores if the sign is next to but not connected to the block hit.
	        if(event.getLine(0).equalsIgnoreCase("[ad]")) {
	        	posX = attachedBlock.getX();
	 	        posY = attachedBlock.getY();
	 	        posZ = attachedBlock.getZ();
<<<<<<< HEAD

=======
	 	        
>>>>>>> Added more checks to see if block is valid. Now text shows on the sign that it was placed on an invalid block, if that is true. Also, it now ignores if the sign is next to but not connected to the block hit.
	 	        Player player = event.getPlayer();
	 	        player.sendMessage(ChatColor.AQUA + "New arrow detecting block created!");
	 	        player.sendMessage(ChatColor.GOLD + "Location at x: " + posX + " y: " + posY + " z: " + posZ + ChatColor.GREEN + " Block type: " + attachedBlock.getType() );
	        }
		}
		else {
			event.setLine(1, ChatColor.DARK_RED + "Sign is on");
			event.setLine(2, ChatColor.DARK_RED + "a BAD BLOCK");
			event.getBlock().getState().update(true);
        }
	}

	@EventHandler
    public void onProjectileHit(ProjectileHitEvent event)
	{
		Entity projectile = event.getEntity();
        if(!(projectile instanceof Arrow)){
        	 return;
        }

        Arrow arrow = (Arrow)projectile;
        Entity entity = arrow.getShooter();
        if(!(entity instanceof Player)) {
        	return;
        }
        //Player p = (Player)entity;
        World world = arrow.getWorld();
        BlockIterator bi = new BlockIterator(world, arrow.getLocation().toVector(), arrow.getVelocity().normalize(), 0, 4);
        Block hit = null;

        while(bi.hasNext())
        {
            hit = bi.next();
            if(isValidBlock(hit))
            {
                break;
            }
        }
<<<<<<< HEAD

=======
        
>>>>>>> Added more checks to see if block is valid. Now text shows on the sign that it was placed on an invalid block, if that is true. Also, it now ignores if the sign is next to but not connected to the block hit.
        if(isValidBlock(hit))
        {
        	//hitBlock = hit;
        	//p.sendMessage(hit.getType().toString());
        	for(int z = -1; z <= 1; z++) {
        		for(int x = -1; x <= 1; x++) {
        			for(int y = 0; y <= 1; y++) {
        				if(x*x+y*y+z*z == 1) {
        					Block hitSign = hit.getRelative(x, y, z);
        					if (hitSign.getState() instanceof Sign) {
        						Sign hitSignSign = (Sign)hitSign.getState();
        						if (hitSignSign.getLine(0).equalsIgnoreCase("[ad]")){
<<<<<<< HEAD

        					    	org.bukkit.material.Sign s = (org.bukkit.material.Sign) hitSign.getState().getData();
        					        Block attachedBlock = hitSign.getRelative(s.getAttachedFace());

=======
        							
        					    	org.bukkit.material.Sign s = (org.bukkit.material.Sign) hitSign.getState().getData();
        					        Block attachedBlock = hitSign.getRelative(s.getAttachedFace());
        					        
>>>>>>> Added more checks to see if block is valid. Now text shows on the sign that it was placed on an invalid block, if that is true. Also, it now ignores if the sign is next to but not connected to the block hit.
        							if(attachedBlock.equals(hit)) {
            							signToRestone(hitSignSign, hitSign, hitSign.getType(), hitSign.getData());
        							}
        						}
        					}
        				}
        			}
        		}
        	}
        }
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (event.getBlock().getState() instanceof Sign)
		{
			Sign signObject = (Sign) event.getBlock().getState();
			if (signObject.getLine(0).equalsIgnoreCase("[ad]"))
			{
				if (signObject.getLine(0).equalsIgnoreCase("[ad]"))
				{
					event.getPlayer().sendMessage("[ArrowDetector] Succesfully removed this sign!");
				}
			}
		}
	}

	public void signToRestone(final Sign hitSign, final Block hitBlock, Material hitSignType, final Byte hitSignData)
	{
		final String[] lines;
		if (hitSign.getType() == Material.SIGN_POST)
		{
			isWall = false;
			hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(), (byte) 0x5,true);
		}
		else if (hitSign.getType() == Material.WALL_SIGN)
		{
			isWall = true;
			byte data = hitBlock.getData(); // Correspond to the direction of the wall sign
			if (data == 0x2) //South
			{
				hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x4, true);
			}
			else if (data == 0x3) //North
			{
				hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x3, true);
			}
			else if (data == 0x4) //East
			{
				hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x2, true);
			}
			else if (data == 0x5) //West
			{
				hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x1, true);
			}
			else // Not West East North South ...
			{
				ArrowDetect.getADLogger().info("Strange Data !");
			}
		}
		lines = hitSign.getLines();
<<<<<<< HEAD

=======
		
>>>>>>> Added more checks to see if block is valid. Now text shows on the sign that it was placed on an invalid block, if that is true. Also, it now ignores if the sign is next to but not connected to the block hit.
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				try {
					hitBlock.setType(Material.AIR);

					if (isWall)
					{
						hitBlock.setType(Material.WALL_SIGN);
						hitBlock.setTypeIdAndData(Material.WALL_SIGN.getId(), hitSignData, true);
					}
					else
					{
						hitBlock.setType(Material.SIGN_POST);
						hitBlock.setTypeIdAndData(Material.SIGN_POST.getId(), hitSignData, true);
					}

					if (hitSign instanceof Sign) {
						Sign signtemp = (Sign) hitBlock.getState();
						for(int i = 0;1 < 4;i++) {
							signtemp.setLine(i, lines[i]);
							signtemp.update(true);
						}
						//plugin.getServer().broadcastMessage(ChatColor.AQUA + "Torch(s) stay on for 2 secs only!");
					}
				}
				catch (RuntimeException e)
				{
					ArrowDetect.getADLogger().severe("Error while updating redstone signToRestone :"+e.getClass()+":"+e.getMessage());
					return;
				}
			}
		}, 40L);
	}
}