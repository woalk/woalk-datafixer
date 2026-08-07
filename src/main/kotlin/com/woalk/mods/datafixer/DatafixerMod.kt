package com.woalk.mods.datafixer

import com.mojang.datafixers.DataFix
import com.mojang.datafixers.DataFixerBuilder
import me.voxelbill.easy_data_fix.common.api.DataFixerAPI
import me.voxelbill.easy_data_fix.common.api.DataFixerRegistry
import net.fabricmc.api.ModInitializer
import net.minecraft.util.datafix.fixes.BlockRenameFix
import net.minecraft.util.datafix.schemas.V4885
import org.slf4j.LoggerFactory

const val MOD_ID = "wdf"
const val DATA_VERSION_26_2 = 4903

val LOGGER = LoggerFactory.getLogger(MOD_ID)!!

class DatafixerMod : ModInitializer {
    override fun onInitialize() {
        ModBlocks.registerAll()
    }
}
