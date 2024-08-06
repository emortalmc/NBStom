package dev.emortal.nbstom.commands;

import dev.emortal.nbstom.MusicDisc;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.Arrays;

public class MusicPlayerInventory {

    private static MusicPlayerInventory INSTANCE;
    private final Inventory inventory;

    public MusicPlayerInventory() {
        //val inventoryTitle = Component.text("\uF808\uE00B", NamedTextColor.WHITE)
        Component inventoryTitle = Component.text("Music Discs", NamedTextColor.BLACK);
        Inventory inventory = new Inventory(InventoryType.CHEST_6_ROW, inventoryTitle);

        ItemStack[] itemStacks = new ItemStack[inventory.getSize()];
        Arrays.fill(itemStacks, ItemStack.AIR);

        var i = 10;
        for (MusicDisc disc : MusicDisc.values()) {
            if ((i + 1) % 9 == 0) i += 2;

            itemStacks[i] = ItemStack.builder(disc.getMaterial())
                    .set(ItemComponent.ITEM_NAME, Component.text(disc.getDescription(), NamedTextColor.AQUA))
                    .set(ItemComponent.HIDE_TOOLTIP)
                    .set(ItemComponent.HIDE_ADDITIONAL_TOOLTIP)
                    .build();

            i++;
        }


        itemStacks[40] = ItemStack.builder(Material.BARRIER)
                .set(ItemComponent.ITEM_NAME, Component.text("Stop", NamedTextColor.RED, TextDecoration.BOLD))
                .build();

        inventory.copyContents(itemStacks);


        inventory.addInventoryCondition((player, slot, clickType, inventoryConditionResult) -> {
            inventoryConditionResult.setCancel(true);

            if (inventoryConditionResult.getClickedItem() == ItemStack.AIR) return;

            if (slot == 40) {
                MusicCommand.stop(player);
                return;
            }

            MusicDisc nowPlayingDisc = MusicDisc.fromMaterial(inventoryConditionResult.getClickedItem().material());

            MusicCommand.playDisc(player, nowPlayingDisc.getShortName());
        });

        this.inventory = inventory;
    }

    public static Inventory getInventory() {
        if (INSTANCE == null) {
            INSTANCE = new MusicPlayerInventory();
        }

        return INSTANCE.inventory;
    }
}