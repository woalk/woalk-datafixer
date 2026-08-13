package com.woalk.mods.datafixer

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jsoizo.kotlincsv.csvReader
import com.jsoizo.kotlincsv.reader.readAllFromFile
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.Paths

interface Config {
    fun blockMapping(): Map<String, String>
    fun itemMapping(): Map<String, String>
    fun biomeMapping(): Map<String, String>
    fun signMapping(): List<String>
    val generalConfig: GeneralConfig

    companion object {
        var replacementConfig: Config? = null

        @JvmStatic
        val instance: Config
            get() = replacementConfig ?: ConfigReader
    }
}

private object ConfigReader : Config {
    override fun blockMapping() = readCsv("block_mapping.csv")
    override fun itemMapping() = readCsv("item_mapping.csv")
    override fun biomeMapping() = readCsv("biome_mapping.csv")
    override fun signMapping() = readTxt("sign_list.txt")

    override val generalConfig: GeneralConfig by lazy {
        val json = readJson("config.json")
        GeneralConfig(
            unknownEnabled = json?.get("unknownEnabled")?.asBoolean ?: true
        )
    }

    private fun readJson(fileName: String): JsonObject? {
        val path = Paths.get(FabricLoader.getInstance().configDir.toString(), "datafixer", fileName)
        if (!path.toFile().exists()) {
             LOGGER.warn("Config file {} not found, using defaults", fileName)
            return null
        }
        try {
            Files.newBufferedReader(path).use { reader ->
                return Gson().fromJson(reader, JsonObject::class.java)
            }
        } catch (e: Exception) {
            LOGGER.error("Error reading config file {}", fileName, e)
            return null
        }
    }

    private fun readCsv(fileName: String): Map<String, String> {
        val path = Paths.get(FabricLoader.getInstance().configDir.toString(), "datafixer", fileName)
        if (!path.toFile().exists()) {
            LOGGER.warn("Config CSV file {} not found", fileName)
            return emptyMap()
        }
        try {
            val csvReader = csvReader()
            val rows = csvReader.readAllFromFile(path.toString())
            if (rows.firstOrNull()?.size != 2 && rows.firstOrNull()?.size != 4) {
                LOGGER.error(
                    "Invalid config CSV file {}: Expected 2 or 4 columns, found {}",
                    fileName, rows.firstOrNull()?.size
                )
                return emptyMap()
            }
            val map = rows.toList().filter { it.first().run { this.lowercase() == this } }.associate {
                if (it.size >= 4) {
                    Pair("${it[0]}:${it[1]}", "${it[2]}:${it[3]}")
                } else if (it.size >= 2) {
                    Pair(it[0], it[1])
                } else {
                    Pair(it.toString(), "")
                }
            }
            return map
        } catch (e: Exception) {
            LOGGER.error("Error reading config CSV file {}", fileName, e)
            return emptyMap()
        }
    }

    fun readTxt(fileName: String): List<String> {
        val path = Paths.get(FabricLoader.getInstance().configDir.toString(), "datafixer", fileName)
        if (!path.toFile().exists()) {
            LOGGER.warn("Config TXT file {} not found", fileName)
            return emptyList()
        }
        return try {
            Files.readAllLines(path)
        } catch (e: Exception) {
            LOGGER.error("Error reading config TXT file {}", fileName, e)
            emptyList()
        }
    }
}

data class GeneralConfig(
    val unknownEnabled: Boolean
)