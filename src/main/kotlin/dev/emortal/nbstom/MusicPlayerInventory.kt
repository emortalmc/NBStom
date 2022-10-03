package dev.emortal.nbstom

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.MinecraftServer
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemHideFlag
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

object MusicPlayerInventory {
    val inventory = init()

    fun init(): Inventory {
        //val inventoryTitle = Component.text("\uF808\uE00B", NamedTextColor.WHITE)
        val inventoryTitle = Component.text("Music Discs", NamedTextColor.BLACK)
        val inventory = Inventory(InventoryType.CHEST_6_ROW, inventoryTitle)

        val itemStacks = Array(inventory.size) { ItemStack.AIR }

        var i = 10
        for (disc in MusicDisc.values()) {
            if ((i + 1) % 9 == 0) i += 2

            itemStacks[i] = ItemStack.builder(disc.material)
                .displayName(
                    Component.text(disc.description, NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)
                )
                .meta {
                    // For some reason the disc author lore requires this hide flag
                    it.hideFlag(ItemHideFlag.HIDE_POTION_EFFECTS)

                    if (disc == MusicDisc.MUSIC_DISC_WAIT) it.lore(Component.text("where are we now", TextColor.color(46, 17, 46)).decoration(TextDecoration.ITALIC, false))
                }
                .build()

            i++
        }


        itemStacks[40] = ItemStack.builder(Material.BARRIER)
            .displayName(
                Component.text("Stop", NamedTextColor.RED, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false)
            )
            .build()

        inventory.copyContents(itemStacks)


        inventory.addInventoryCondition { player, slot, _, inventoryConditionResult ->
            inventoryConditionResult.isCancel = true

            if (inventoryConditionResult.clickedItem == ItemStack.AIR) return@addInventoryCondition

            if (slot == 40) {
                MinecraftServer.getCommandManager().execute(player, "disc stop")
                return@addInventoryCondition
            }

            val nowPlayingDisc = MusicDisc.fromMaterial(inventoryConditionResult.clickedItem.material())
                ?: return@addInventoryCondition

            MinecraftServer.getCommandManager().execute(player, "disc ${nowPlayingDisc.shortName}")
        }

        return inventory
    }

}