package com.github.janka102.bullseye;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class BullseyeSignHandler {
    
    //Checks if the a block can even handle a redstone torch.
    public boolean isValidBlock(Block block) {
        if(block == null) {
            return false;
        }
                
        Material blockType = block.getType();
        String blockName = blockType.toString().toLowerCase();
        
        //blocks that don't work with redstone torches
        if (blockType == Material.STEP
        || blockType == Material.TNT
        || blockType.toString().contains("STAIRS")
        || blockType.toString().contains("GLASS")
        || blockType == Material.IRON_FENCE
        || blockType == Material.CACTUS
        || blockType == Material.WEB
        || blockType.toString().contains("PISTON")
        || blockType == Material.GLOWSTONE
        || blockType == Material.ENDER_PORTAL_FRAME)
        {
            return false;
        }
        
        //user defined list of blocks to either blacklist
        if (Bullseye.blacklist) {
            if (Bullseye.blocks.toString().toLowerCase().contains(blockName)){
                return false;
            }
            return true;
        }
        // or whitelist
        else {
            if (Bullseye.blocks.toString().toLowerCase().contains(blockName)){
                return true;
            }
            return false;
        }
    }
    
    public boolean isNearWater(Block sign) {
        //get blocks to left, right, back, front and top of Block sign
        for(int z = -1; z <= 1; z++) {
            for(int x = -1; x <= 1; x++) {
                for(int y = 0; y <= 1; y++) {
                    if(x*x+y*y+z*z == 1) {
                        Block relBlock = sign.getRelative(x, y, z);
                        if(relBlock.getType() == Material.WATER || relBlock.getType() == Material.STATIONARY_WATER){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    //checks to make sure the sign is a Bullseye sign
    public boolean isBullseyeSign(String line) {
        if(line.equalsIgnoreCase(("[bullseye]").toString())
                || line.equalsIgnoreCase(("[bull]").toString())
                || line.equalsIgnoreCase(("[be]").toString())) 
        {
            return true;
        }
        return false;
    }
    
    public boolean isBullseyeSignBlue(String line) {
        if(line.equalsIgnoreCase((ChatColor.DARK_BLUE + "[bullseye]").toString())
                || line.equalsIgnoreCase((ChatColor.DARK_BLUE + "[bull]").toString())
                || line.equalsIgnoreCase((ChatColor.DARK_BLUE + "[be]").toString())) 
        {
            return true;
        }
        return false;
    }
    
    public boolean isBullseyeSignRed(String line) {
        if(line.equalsIgnoreCase((ChatColor.DARK_RED + "[bullseye]").toString())
                || line.equalsIgnoreCase((ChatColor.DARK_RED + "[bull]").toString())
                || line.equalsIgnoreCase((ChatColor.DARK_RED + "[be]").toString())) 
        {
            return true;
        }
        return false;
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
                            if (isBullseyeSign(hitSignSign.getLine(0)) || isBullseyeSignBlue(hitSignSign.getLine(0)) || isBullseyeSignRed(hitSignSign.getLine(0))) {
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
    
    public void updateSign(Block signBlock, String[] signLines) {
        Sign sign = (Sign)signBlock.getState();
          for(int i = 0; i < signLines.length; i++) {
             sign.setLine(i, signLines[i].toString());
          }
          sign.update(true);
    }
    
    //changes the Bullseye sign to a redstone torch for a specified time
    public void signToRestone(Bullseye plugin, final Sign bullseyeSign, final Block hitBlock, Material hitSignType, final Byte hitSignData, final boolean isPost) {
        final String[] lines = bullseyeSign.getLines();
        
        if (isPost) {
            hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(), (byte) 0x5,true);
        }
        else {
            byte data = hitBlock.getData(); // Correspond to the direction of the wall sign
            if (data == 0x2) { //South
                hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x4, true);
            }
            else if (data == 0x3) { //North
                hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x3, true);
            }
            else if (data == 0x4) { //East
                hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x2, true);
            }
            else if (data == 0x5) { //West
                hitBlock.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x1, true);
            }
            else { // Not South, North, East or West 
                Bullseye.getBullLogger().info("Strange Data!");
                return;
            }
        }

        //Run this after a delay of 25 ticks
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                try {
                    //clears the block
                    hitBlock.setType(Material.AIR);

                    if (isPost) {
                        hitBlock.setType(Material.SIGN_POST);
                        hitBlock.setTypeIdAndData(Material.SIGN_POST.getId(), hitSignData, true);
                    }
                    else {
                        hitBlock.setType(Material.WALL_SIGN);
                        hitBlock.setTypeIdAndData(Material.WALL_SIGN.getId(), hitSignData, true);
                    }

                    Sign signtemp = (Sign) hitBlock.getState();
                    
                    //restore the original text of the Bullseye sign back
                    for(int i = 0; i < lines.length; i++) {
                        signtemp.setLine(i, lines[i]);
                    }
                    signtemp.update(true);
                                        
                    //plugin.getServer().broadcastMessage(ChatColor.AQUA + "Changed sign back.");
                }
                catch (RuntimeException e)
                {
                    Bullseye.getBullLogger().severe("Error while updating redstone signToRestone :" + e.getClass() + ":" + e.getMessage());
                    return;
                }
            }
        }, 25 );
    }
    
}