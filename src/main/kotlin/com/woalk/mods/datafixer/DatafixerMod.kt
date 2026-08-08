package com.woalk.mods.datafixer

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

const val MOD_ID = "wdf"
const val DATA_VERSION_26_2 = 4903

val LOGGER = LoggerFactory.getLogger(MOD_ID)!!

class DatafixerMod : ModInitializer {
    override fun onInitialize() {
        ModBlocks.registerAll()
    }
}
