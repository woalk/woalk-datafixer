![Logo](./src/main/resources/assets/wdf/icon.png)
# Woalk Datafixer

Woalk Datafixer is a Fabric mod designed to handle missing or changed blocks, items, and biomes when loading Minecraft worlds.
It allows for seamless remapping of registry entries using CSV mapping tables and provides a fallback for items that are completely missing from the registries.

This mod is designed for long-running modded multiplayer servers that,
when updating to a new Minecraft version,
have to swap out old mods with newer ones, often having other namespaces or item names,
and need to preserve world data.

## Features

- **DataFixer Mapping**: Automatically replaces old block, item, and biome IDs with new ones during world loading using the standard Minecraft DataFixer system.
- **Fallback Item**: Any items that are still missing and not covered by the mapping tables are replaced with a special "Unknown" item (`wdf:unknown`) instead of being lost, helping to preserve inventories and world state.
- **CSV Configuration**: Easy to configure using simple CSV files.

## Compatibility

- **Minecraft Version**: 26.2 (Fabric)
- **Dependencies**: 
    - Fabric API
    - Easy Data Fix

For multiplayer servers, the mod needs to be installed on both client and server to ensure that the fallback item is properly registered.
However, it can also be used as a server-side-only mod by disabling the fallback block in the configuration (see below).

## Configuration

The mod reads configuration from the `config/datafixer/` directory in your Minecraft instance.

### Mappings

You can create the following CSV files for mapping registry entries:

- `block_mapping.csv`
- `item_mapping.csv`
- `biome_mapping.csv`

The CSV files should be simple tables without headers. Each row represents a mapping. Two formats are supported:

**2-column format (Combined ID):**
```csv
old_namespace:old_path,new_namespace:new_path
```

**4-column format (Split namespace and path):**
```csv
old_namespace,old_path,new_namespace,new_path
```

**Example (`block_mapping.csv`):**
```csv
immersive_weathering:rooted_grass_block,minecraft:grass_block
croptopia:cinnamon_log,minecraft:jungle_log
byg:yellow_birch_leaves,minecraft:birch_leaves
```

*Note: The block mapping table is also used to remap the corresponding BlockItems.*

### General Configuration

Create a `config.json` file in the same directory for general settings:

```json
{
  "unknownEnabled": true
}
```

- `unknownEnabled` (default: `true`): If set to `false`, the fallback "Unknown" item/block will be disabled.
  This allows the mod to be used as a **server-side-only** mod, as no new blocks or items are registered.

## License

This project is licensed under [GNU GPLv3](./LICENSE.txt).
