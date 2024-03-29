# Bullseye

<img src="images/logo.png" alt="Bullseye logo" width="128px" align="left"/>

Bullseye adds the ability for blocks to detect arrows with a sign. Allows you to create a Bullseye block (arrow detector block) by placing a sign, with special text, on any block. Whenever that block is hit with arrows, the sign will change to a redstone torch briefly.

This is different from a [Target block](https://minecraft.fandom.com/wiki/Target) because you can choose which sides the redstone signal will appear, you can customize the pulse length, and you can make it _completely hidden_ by having the torch appear on the back of the block.

<br>

## Features

- Make blocks able to detect arrows hitting them!
- The detection is shown by Bullseye signs turning into a redstone torch briefly
- No commands needed
- Can put multiple signs on one block
- Customize the time the redstone torch will be active in ticks
- Bullseye signs turn blue if placed on a valid block, and red if placed on an invalid block.
- Can configure which blocks to allow in an allow/deny list.
- Optional message to be displayed when the Bullseye block is hit with an arrow, message can be written on the last three lines.
    - Spaces needed between lines
    - Works with color
- Works with any block you can put a sign on!
    - If the block has an Inventory (Chest, Dispenser, Enchantment Table, etc.) crouch/sneak first (hold Shift) in order to put a sign on them
- If the redstone torch breaks for any reason, it will drop the original sign instead of the torch
- Option for arrows shot out of Dispensers to activate the signs (default true)
- Option for arrows shot by Skeletons to activate the signs (default false)
- Configure a maximum allowed tick duration (default 100 ticks, 5 seconds)

## Usage

1. Make a sign and put the text `[bullseys]`, `[bull]`, or `[be]` on the first line
   - Optionally put the number of ticks the redstone torch will activate for: `[be 40]` will be active for 2 seconds
   - Default 30 ticks
2. Optionally any text on the next 3 lines will be sent to you when you activate the sign
3. Now whenever you shoot the block the sign is on with an arrow, the sign will briefly turn into a redstone torch
   - Note: you don't have to hit the sign, just the block the sign is attached to
   - You can have multiple signs per block

![demo](images/demo.gif)

These are examples of **valid** signs:

![valid signs](images/valid-signs.png)

Here is an example of an **invalid** sign:

![invalid sign](images/invalid-signs.png)

## Known Issues

### Minecraft 1.14 only

With Spigot 1.14, the server shows an error in the logs similar to the following:

```
[Server thread/ERROR]: Block at 84, 3, 182 is Block{minecraft:redstone_wall_torch} but has net.minecraft.server.v1_14_R1.TileEntitySign@37dedbb0. Bukkit will attempt to fix this, but there may be additional damage that we cannot recover.
```

However, Bullseye works as expected so it can be ignored. This does not show on any other version tested from 1.13-1.19.1.

## To-Do

- Allow other projectiles besides arrows
- Add permissions
- ~~Customize per sign how long the redstone torch is active~~ (v0.10.0)
- ~~Maybe figure out how to get Chests and Enchanting tables to work correctly??~~ (v0.8)
- ~~Add config for which blocks to allow~~ (v0.7)
- ~~Fix water issue~~ (v0.5)
- ~~Add Furnaces, Crafting tables, etc.~~ (v0.3)

Have more ideas? Create a ticket! :)
