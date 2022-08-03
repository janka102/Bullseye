# Changelog

## Version 0.10.0

- Add custom activation time by adding ticks to the tag line
  - `[bullseye 40]` will have the sign activate for 40 ticks (2 seconds)
- Fixed water breaking an activated Bullseye sign (again...)
- Fixed Skeletons and dispensers able to trigger Bullseye blocks
- Verified support for 1.13-1.19.1

---

<details>
<summary>Older versions</summary>

## Version 0.9.1

- Fix issue where players could turn any amount of redstone torches into signs

## Version 0.9.0

- Support for 1.13 - 1.17
- Completely fix issue with breaking activated redstone torches and having them drop
    - Now it drops the original sign
- Change blockList to allow/deny list

## Version 0.8.1

- Support for 1.12
- Note: 1.13 is working, but glitchy. Not recommend to use for 1.13. It will require more code changes to work properly

## Version 0.8.0

- If you can put a sign on it, it will work (got all blocks working)
- Flowing water can't stop a Bullseye sign!
- Better detection of the block hit by an arrow
- Prevented explosions from dropping the redstone torch and cancel putting the sign back
- Added an allowSkeleton option in the config to allow skeletons to activate Bullseye signs
- Rewrote a lot of code because the old stuff was deprecated
- Improvements and bug fixes
- I did a 0.1.0 increase mainly because of just how much code was rewritten

## Version 0.7.1

- Highly recommended for users of v0.7.0
- Fixed a bug that made all signs created think they were bullseye signs.

## Version 0.7

- Added config.yml
- Whitelist/Blacklist for which blocks to allow as Bullseye blocks.
- Add option for arrows launched from dispensers to activate the Bullseye signs.
    - Default is true and can be changed in the config.yml
- Built with CB1.3.1-R1.0, works with CB1.2.5
- Some code optimization.

## Version 0.6

- Code optimization.
- More work on the previous water bug.
- Built with 1.2.5-R5.0 and working in 1.3.1-R0.1
    - In 1.3.1 when using a fly hack (such as zombe's) arrows don't shoot right.
- Fixed issue where if a player hit a Bullseye sign, only that sign would change, even if there were others on the
  attached block.

## Version 0.5

- Highly recommended for all users
- Fixed an issue where v0.4 would only work with previously made signs, and not new ones.
- Confirmed it working on CraftBukkit 1.2.5-R5.0.

## Version 0.4 (broken)

- Bug fix: when placed near water, could potentially lead to an infinite amount of redstone torches

## Version 0.3

- Note blocks, Crafting tables, and Jukeboxes work like Furnaces and Dispensers.
- Ender portal frames added to "invalid" list.

## Version 0.2

- Added ability for furnaces and dispensers to be Bullseye blocks.

## Version 0.1

- Initial release

- If you place a water bucket directly on a redstone torch when its active, the torch will drop and the sign will still
  change back, allowing for an infinite amount of redstone torches
- If changing the whitelist/blacklist blocks, the first line on an already made Bullseye sign won't change color until
  it is shot.

</details>
