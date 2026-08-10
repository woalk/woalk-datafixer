package com.woalk.mods.datafixer

import com.mojang.datafixers.schemas.Schema
import com.mojang.datafixers.types.templates.TypeTemplate
import net.minecraft.util.datafix.schemas.NamespacedSchema
import net.minecraft.util.datafix.schemas.V3439
import net.minecraft.util.datafix.schemas.V99
import java.util.function.Supplier

class MySchema(versionKey: Int, parent: Schema) : NamespacedSchema(versionKey, parent) {
    override fun registerBlockEntities(schema: Schema): Map<String, Supplier<TypeTemplate>> {
        val map = super.registerBlockEntities(schema)
        val signs = Config.INSTANCE.signMapping()
        val signTemplate = map["minecraft:sign"]
        for (signId in signs) {
            register(map, signId, signTemplate ?: Supplier { V99.sign(schema) })
        }
        return map
    }
}

class MySchema2(versionKey: Int, parent: Schema) : NamespacedSchema(versionKey, parent) {
    override fun registerBlockEntities(schema: Schema): Map<String, Supplier<TypeTemplate>> {
        val map = super.registerBlockEntities(schema)
        val signs = Config.INSTANCE.signMapping()
        val signTemplate = map["minecraft:sign"]
        for (signId in signs) {
            register(map, signId, signTemplate ?: Supplier { V3439.sign(schema) })
        }
        return map
    }
}