# EDD's ToyBox: Collection of Technical Mods

## Features (aka TODO list)

Added:
- `Village Marker` Shows some info about the Villages
- `Chunk Loading Minimap` Displays the Loaded/Slime Chunks
- `Piston Helper` Counts the blocks moved by pistons and more!
- `TPS` Tick Speed Control
- `Change Biome` Command to change the biome ingame

TODO:
- `Block Updates`
- `Bounding Boxes`
- `Server-side Render`

## Commands
- `/changeBiome <x> <y> <z> <x'> <y'> <z'> <biomeID>` changes the biome in the selected area 
- `/tps client/server <ticks per second>` sets the client/server's TPS
- `/tps warp <X ticks to warp>` executes X ticks as fast as possible
- `/minimap` opens the Chunk Loading Minimap

## Extra
- You can set some features in liteloader's settings menu
- You can use the `Piston Helper` by right-clicking the piston base with no item in the main hand
(click again to remove the text)

## Build
Run `./gradlew build` on the projects' root directory. The litemod will be located in `build\libs`.
