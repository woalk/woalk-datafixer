# ![Logo](./src/main/resources/assets/wdf/icon.png){height=32px} Woalk Datafixer

Woalk Datafixer is a Fabric mod designed to handle missing or changed blocks, items, and biomes when loading Minecraft worlds. It allows for seamless remapping of registry entries using CSV mapping tables and provides a fallback for items that are completely missing from the registries.

## Features

- **DataFixer Mapping**: Automatically replaces old block, item, and biome IDs with new ones during world loading using the standard Minecraft DataFixer system.
- **Fallback Item**: Any items that are still missing and not covered by the mapping tables are replaced with a special "Unknown" item (`wdf:unknown`) instead of being lost, helping to preserve inventories and world state.
- **CSV Configuration**: Easy to configure using simple CSV files.

## Compatibility

- **Minecraft Version**: 26.2 (Fabric)
- **Dependencies**: 
    - Fabric API
    - Easy Data Fix

## Configuration

The mod reads mapping tables from the `config/datafixer/` directory in your Minecraft instance. You can create the following files:

- `block_mapping.csv`
- `item_mapping.csv`
- `biome_mapping.csv`

### CSV Format

The CSV files should be simple two-column tables without headers. Each row represents a mapping:

```csv
old_namespace:old_path,new_namespace:new_path
```

**Example (`block_mapping.csv`):**
```csv
immersive_weathering:rooted_grass_block,minecraft:grass_block
croptopia:cinnamon_log,minecraft:jungle_log
byg:yellow_birch_leaves,minecraft:birch_leaves
```

*Note: The block mapping table is also used to remap the corresponding BlockItems.*

## License

This project is licensed under [GNU GPLv3](./LICENSE.txt).
