package com.woalk.mods.datafixer
import com.mojang.datafixers.DataFixerBuilder
import me.voxelbill.easy_data_fix.common.api.DataFixerAPI
import me.voxelbill.easy_data_fix.common.api.DataFixerRegistry
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import net.minecraft.util.datafix.fixes.BlockRenameFix
import net.minecraft.util.datafix.fixes.ItemRenameFix
import net.minecraft.util.datafix.fixes.NamespacedTypeRenameFix
import net.minecraft.util.datafix.fixes.References
import net.minecraft.util.datafix.schemas.NamespacedSchema

class DatafixerPreLaunch: PreLaunchEntrypoint {
    override fun onPreLaunch() {
        val blockMapping = Config.instance.blockMapping()
        val itemMapping = Config.instance.itemMapping()
        val biomeMapping = Config.instance.biomeMapping()

        registerDataFix { builder ->
            val schema = builder.addSchema(DATA_VERSION_26_2, ::NamespacedSchema)
            // blocks
            builder.addFixer(
                BlockRenameFix.create(
                    schema,
                    "Woalk BlockFixer",
                    DataFixerAPI.createRenamer(blockMapping),
                )
            )
            builder.addFixer(
                ItemRenameFix.create(
                    schema,
                    "Woalk BlockItemFixer",
                    DataFixerAPI.createRenamer(blockMapping),
                )
            )
            // items
            builder.addFixer(
                ItemRenameFix.create(
                    schema,
                    "Woalk ItemFixer",
                    DataFixerAPI.createRenamer(itemMapping),
                )
            )
            // biomes
            builder.addFixer(
                NamespacedTypeRenameFix(
                    schema,
                    "Woalk BiomeFixer",
                    References.BIOME,
                    DataFixerAPI.createRenamer(biomeMapping),
                )
            )
            LOGGER.info("Registered DataFixer for version $DATA_VERSION_26_2")
        }
    }

    private fun registerDataFix(build: (builder: DataFixerBuilder) -> Unit) {
        DataFixerRegistry.addDataFix("Woalk Datafixer", build)
    }
}