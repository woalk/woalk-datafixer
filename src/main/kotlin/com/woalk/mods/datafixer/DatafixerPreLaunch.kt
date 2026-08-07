package com.woalk.mods.datafixer

import com.mojang.datafixers.DataFixerBuilder
import me.voxelbill.easy_data_fix.common.api.DataFixerAPI
import me.voxelbill.easy_data_fix.common.api.DataFixerRegistry
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import net.minecraft.util.datafix.fixes.BlockRenameFix
import net.minecraft.util.datafix.schemas.V4885

class DatafixerPreLaunch: PreLaunchEntrypoint {
    override fun onPreLaunch() {
        val blockMapping = ConfigReader.blockMapping()

        registerDataFix { builder ->
            val schema = builder.addSchema(DATA_VERSION_26_2, ::V4885)
            builder.addFixer(
                BlockRenameFix.create(
                    schema,
                    "Woalk BlockFixer",
                    DataFixerAPI.createRenamer(blockMapping)
                )
            )
            LOGGER.info("Registered DataFixer for version $DATA_VERSION_26_2")
        }
    }

    private fun registerDataFix(build: (builder: DataFixerBuilder) -> Unit) {
        DataFixerRegistry.addDataFix("Woalk Datafixer", build)
    }
}