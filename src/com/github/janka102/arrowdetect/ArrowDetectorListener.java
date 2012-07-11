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
				|| tempBlock.getType() == Material.PISTON_BASE
				|| tempBlock.getType() == Material.PISTON_EXTENSION
				|| tempBlock.getType() == Material.PISTON_MOVING_PIECE
				|| tempBlock.getType() == Material.PISTON_STICKY_BASE
				|| tempBlock.getType() == Material.GLOWSTONE
				|| tempBlock.getType() == Material.REDSTONE_LAMP_ON
				|| tempBlock.getType() == Material.REDSTONE_LAMP_OFF)
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
				|| tempBlock.getType() == Material.PISTON_BASE
				|| tempBlock.getType() == Material.PISTON_EXTENSION
				|| tempBlock.getType() == Material.PISTON_MOVING_PIECE
				|| tempBlock.getType() == Material.PISTON_STICKY_BASE
				|| tempBlock.getType() == Material.GLOWSTONE
				|| tempBlock.getType() == Material.REDSTONE_LAMP_ON
				|| tempBlock.getType() == Material.REDSTONE_LAMP_OFF)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public void signWarning(Block block, int code)
	{
		Sign sign = (Sign) block.getState();
		switch(code)
		{
		case 1:
			sign.setLine(2, "Bad block");
			sign.setLine(3, "Behind sign");
			sign.update();
			break;

		default:
			break;
		}
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event) //Called when a sign is created, and the text edited
	{
		if (event.getLine(0).equalsIgnoreCase("[ad]"))
		{
			
			Block b = event.getBlock();
	    	int posX = 0;
	    	int posY = 0;
	    	int posZ = 0;
	    	
	    	org.bukkit.material.Sign s = (org.bukkit.material.Sign) b.getState().getData();
	        Block attachedBlock = b.getRelative(s.getAttachedFace());
	        posX = attachedBlock.getX();
	        posY = attachedBlock.getY();
	        posZ = attachedBlock.getZ();
	        
	        Player player = event.getPlayer();
	        player.sendMessage(ChatColor.AQUA + "New arrow detecting block created!");
	        player.sendMessage(ChatColor.GOLD + "Located at x: " + posX + " y: " + posY + " z: " + posZ + ChatColor.GREEN + " Block type: " + attachedBlock.getType() );
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
        //Player p = (Player)arrow.getShooter();
        World world = arrow.getWorld();
        BlockIterator bi = new BlockIterator(world, arrow.getLocation().toVector(), arrow.getVelocity().normalize(), 0, 4);
        Block hit = null;
        
        while(bi.hasNext())
        {
            hit = bi.next();
            if(hit.getTypeId()!=0) //Grass/etc should be added probably since arrows doesn't collide with them
            {
                break;
            }
        }
        
        if(hit.getTypeId()==35) //Hit wool!
        {
        	//hitBlock = hit;
        	//p.sendMessage("Hit wool!");
        	for(int z = -1; z <= 1; z++) {
        		for(int x = -1; x <= 1; x++) {
        			for(int y = 0; y <= 1; y++) {
        				if(x*x+y*y+z*z == 1) {
        					Block hitSign = hit.getRelative(x, y, z);
        					if (hitSign.getState() instanceof Sign) {
        						Sign hitSignSign = (Sign)hitSign.getState();
        						if (hitSignSign.getLine(0).equalsIgnoreCase("[ad]")){
        							signToRestone(hitSignSign, hitSign, hitSign.getType(), hitSign.getData());
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
		if (hitSign.getType() == Material.SIGN_POST)
		{
			isWall = false;
			if (!isValidLocation(hitBlock))
			{
				signWarning(hitBlock, 1);
			}
			else
			{
				hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(), (byte) 0x5,true);
			}
		}
		else if (hitSign.getType() == Material.WALL_SIGN)
		{
			isWall = true;
			byte data = hitBlock.getData(); // Correspond to the direction of the wall sign
			if (data == 0x2) //South
			{
				if (!isValidWallLocation(hitBlock))
				{
					signWarning(hitBlock, 1);
				}
				else
				{
					hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x4, true);
				}
			}
			else if (data == 0x3) //North
			{
				if (!isValidWallLocation(hitBlock))
				{
					signWarning(hitBlock, 1);
				}
				else
				{
					hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x3, true);
				}
			}
			else if (data == 0x4) //East
			{
				if (!isValidWallLocation(hitBlock))
				{
					signWarning(hitBlock, 1);
				}
				else
				{
					hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x2, true);
				}
			}
			else if (data == 0x5) //West
			{
				if (!isValidWallLocation(hitBlock))
				{
					signWarning(hitBlock, 1);
				}
				else
				{
					hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x1, true);
				}
			}
			else // Not West East North South ...
			{
				ArrowDetect.getADLogger().info("Strange Data !");
			}
		}
		
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
						signtemp.setLine(0, "[AD]");
						signtemp.update(true);
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