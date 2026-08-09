package com.woalk.mods.datafixer

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jsoizo.kotlincsv.csvReader
import com.jsoizo.kotlincsv.reader.readFromFile
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.Paths

object ConfigReader {
    fun blockMapping() = readCsv("block_mapping.csv")
    fun itemMapping() = readCsv("item_mapping.csv")
    fun biomeMapping() = readCsv("biome_mapping.csv")

    val generalConfig: GeneralConfig by lazy {
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
            LOGGER.warn("Config file {} not found", fileName)
            return emptyMap()
        }
        try {
            val csvReader = csvReader()
            return csvReader.readFromFile(path.toString()) { rows ->
                if (rows.first().size != 2) {
                    LOGGER.error("Invalid config file {}: Expected 2 columns, found {}", fileName, rows.first().size)
                    return@readFromFile emptyMap()
                }
                return@readFromFile rows.toList().associate {
                    Pair(it[0], it[1])
                }
            }
        } catch (e: Exception) {
            LOGGER.error("Error reading config file {}", fileName, e)
            return emptyMap()
        }
    }
}

data class GeneralConfig(
    val unknownEnabled: Boolean
)