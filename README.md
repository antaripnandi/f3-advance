# F3 Advanced

F3 Advanced is a client-side Fabric mod that replaces the plain debug readout style with a cleaner, grouped, color-coded HUD. It is designed to read live Minecraft registries at runtime instead of hardcoding vanilla-only content lists, so modded biomes, dimensions, blocks, entities, items, and potion effects can display using their actual IDs and translated names.

## Version Strategy

This project uses Stonecutter so one shared codebase can target many Minecraft versions without maintaining a separate project for every subversion.

Minecraft/Fabric cannot reliably use one physical jar for all of `1.20.x`, `1.21.x`, and `26.1.x`: the Java target, Loom mode, mappings, and Fabric HUD APIs changed. Instead, F3 Advanced uses a small set of API-breakpoint jars. On Modrinth, upload each jar once and mark it compatible with the matching bucket after smoke testing:

| Build jar | Mark compatible with |
| --- | --- |
| `f3advanced-1.20.1-...jar` | `1.20`, `1.20.1` |
| `f3advanced-1.20.2-...jar` | `1.20.2` |
| `f3advanced-1.20.4-...jar` | `1.20.3`, `1.20.4` |
| `f3advanced-1.20.6-...jar` | `1.20.5`, `1.20.6` |
| `f3advanced-1.21.1-...jar` | `1.21`, `1.21.1` |
| `f3advanced-1.21.4-...jar` | `1.21.2`, `1.21.3`, `1.21.4` |
| `f3advanced-1.21.5-...jar` | `1.21.5` |
| `f3advanced-1.21.8-...jar` | `1.21.6`, `1.21.7`, `1.21.8` |
| `f3advanced-1.21.11-...jar` | `1.21.9`, `1.21.10`, `1.21.11` |
| `f3advanced-26.1.2-...jar` | `26.1`, `26.1.1`, `26.1.2` |
| `f3advanced-26.2-...jar` | `26.2` |
| `f3advanced-26.3-...jar` | `26.3` |

The supported game list is:

- Minecraft `1.20`, `1.20.1`, `1.20.2`, `1.20.3`, `1.20.4`, `1.20.5`, `1.20.6`
- Minecraft `1.21` through `1.21.11`
- Minecraft `26.1`, `26.1.1`, `26.1.2`, `26.2`, `26.3`

The shared implementation is client-only. HUD registration is isolated in `F3AdvancedHudRegistrar` because Fabric changed HUD APIs after `1.21.5` and again after `1.21.6`; 26.1 also uses the modern Fabric HUD API and the newer Loom/mapping flow.

## Building

Use Java 21 to run Gradle. The build config uses toolchains for older/newer targets:

```powershell
.\gradlew stonecutterSelect 1.21.1 build
.\gradlew stonecutterSelect 1.20.6 build
.\gradlew stonecutterSelect 1.21.11 build
.\gradlew stonecutterSelect 26.1.2 build
```

To copy the active version's built jar into `outputs/`:

```powershell
.\gradlew copyBuiltJarToOutputs
```

## Config

Settings persist in `config/f3advanced.json`. Every section and every individual HUD line has its own boolean toggle. The JSON config also exposes colors, HUD position, padding, and background transparency.

If Mod Menu is installed, F3 Advanced appears in the Mod Menu list and opens the built-in F3 Advanced config screen. The same screen can be opened in-game with `F3 + M`. Cloth Config is listed as an optional dependency target for richer config integration, but the included screen and JSON fallback work without it.

Whole-section keybinds are registered for position, world, performance, player status, target, and system info. They are unbound by default except for the `F3 + M` config chord.

## Compatibility Notes

- The mod never hardcodes vanilla biome/block/item/entity/dimension lists.
- Biomes and dimensions are resolved from the active level registry and translated from their registry IDs.
- Targeted block/entity IDs are read from registries at runtime.
- Potion effect names use translation keys, so modded effects display through their language files.
- Reduced debug info is respected: coordinates, world debug lines, and target internals are hidden when the player is under reduced debug info.
- TPS is shown as unavailable unless a future server-side companion packet is added; this avoids pretending exact server TPS is client-available.

## Testing Checklist

For each supported version line:

1. Build the active Stonecutter node.
2. Run the client.
3. Confirm the HUD appears, toggles persist, and `F3 + M` opens config.
4. Join or create a world with at least one content mod that adds biomes/blocks/entities.
5. Confirm biome names, target block/entity IDs, and translated names are not shown as unknown/raw fallback values unless the content mod lacks language entries.
