package com.woalk.mods.datafixer

import com.jsoizo.kotlincsv.csvReader
import com.jsoizo.kotlincsv.reader.readFromFile
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Paths

object ConfigReader {
    fun blockMapping() = readCsv("block_mapping.csv")

    private fun readCsv(fileName: String): Map<String, String> {
        val path = Paths.get(FabricLoader.getInstance().configDir.toString(), "datafixer", fileName)
        if (!path.toFile().exists()) {
            LOGGER.error("Config file {} not found", fileName)
            return emptyMap()
        }
        val csvReader = csvReader()
        return csvReader.readFromFile(path.toString()) { rows ->
            return@readFromFile rows.toList().associate {
                Pair(it[0], it[1])
            }
        }
    }
}