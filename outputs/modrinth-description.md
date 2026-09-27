# F3 Advanced

F3 Advanced replaces Minecraft's vanilla F3 debug overlay with a cleaner, organized, color-coded HUD for Fabric.

## Features

- Replaces the actual F3 screen instead of drawing a second overlay on top.
- Organized sections for position, world, performance, player status, target info, and system info.
- Per-line toggles for every HUD value, plus whole-section toggles.
- Runtime registry lookups for biomes, blocks, entities, effects, and dimensions, so modded content displays correctly.
- Respects reduced debug info and avoids exposing restricted debug details.
- Mod Menu integration with Cloth Config when installed.
- JSON config fallback at `config/f3advanced.json`.
- Adjustable colors, position, padding, and background transparency.
- F3+M opens the config screen directly.

## Compatibility

This is a client-side Fabric mod. It does not require the server to install F3 Advanced.

Recommended optional mods:

- Mod Menu
- Cloth Config

## Current Release Buckets

- `mc26.1.1` jar for Minecraft 26.1.1
- `mc26.1.2` jar for Minecraft 26.1.2

Earlier 1.21.x builds are included in the repository outputs as separate compatibility jars.
