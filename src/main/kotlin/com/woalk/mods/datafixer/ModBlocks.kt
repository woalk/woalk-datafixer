package com.woalk.mods.datafixer

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.references.BlockItemId
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour

object ModBlocks {
    val unknownBlock = register(id("unknown"), ::Block, BlockBehaviour.Properties.of())

    //region Helpers
    fun id(id: String): BlockItemId {
        val identifier = Identifier.fromNamespaceAndPath(MOD_ID, id)
        return BlockItemId.create(identifier, identifier)
    }

    fun register(
        id: ResourceKey<Block>, blockFactory: (BlockBehaviour.Properties) -> Block, settings: BlockBehaviour.Properties
    ): Block {
        println("Registering block: $id")
        val block = blockFactory(settings.setId(id))
        return Registry.register(BuiltInRegistries.BLOCK, id, block)
    }

    fun register(
        id: BlockItemId, blockFactory: (BlockBehaviour.Properties) -> Block, settings: BlockBehaviour.Properties
    ): Block {
        val block = register(id.block, blockFactory, settings)

        val blockItem = BlockItem(block, Item.Properties().useBlockDescriptionPrefix().setId(id.item))
        Registry.register(BuiltInRegistries.ITEM, id.item, blockItem)

        return block
    }
    //endregion

    fun registerAll() {
        ItemTooltipCallback.EVENT.register { stack, context, flag, components ->
            if (stack.`is`(unknownBlock.asItem())) {
                val text = Component.translatable("tooltip.wdf.unknown").withColor(TextColor.GRAY)
                if (flag.isAdvanced) {
                    components.add(1, text)
                } else {
                    components.add(text)
                }
            }
        }
    }
}