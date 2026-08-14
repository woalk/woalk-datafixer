package com.woalk.mods.datafixer

class TestConfigObject(private val signs: List<String>): Config {
    override fun blockMapping() = mapOf("test:test_stone" to "minecraft:stone")

    override fun itemMapping() = mapOf("test:test_stick" to "minecraft:stick")

    override fun biomeMapping() = mapOf("test:test_biome" to "minecraft:cherry_grove")

    override fun signMapping() = signs

    override val generalConfig = GeneralConfig(true)
}