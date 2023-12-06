package com.shulkervacuum.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.ShulkerBoxSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShulkerVacuumUtil {

    public static boolean isShulkerBoxScreenHandler(ScreenHandler screenHandler) {
        return isScreenHandlerInstanceOfClass(screenHandler, ShulkerBoxScreenHandler.class);
    }

    public static boolean isScreenHandlerInstanceOfClass(ScreenHandler screenHandler, Class<? extends ScreenHandler> screenHandlerClass) {
        return screenHandlerClass.isInstance(screenHandler);
    }

    public static void moveItemsToShulkerBox(ScreenHandler screenHandler, MinecraftClient minecraftClient) {
        if (screenHandler != null && minecraftClient != null) {
            List<Slot> shulkerBoxSlotList = new ArrayList<>();
            List<Slot> playerSlotList = new ArrayList<>();

            // Get the player's inventory and the item mask from the Shulker Box inventory.
            fillShulkerAndPlayerSlotLists(shulkerBoxSlotList, playerSlotList, screenHandler);
            Set<Item> shulkerBoxUniqueItems = getShulkerBoxUniqueItems(shulkerBoxSlotList);

            // Move items from player's inventory to the Shulker Box inventory
            // if they match the mask (items already in the Shulker Box).
            moveItemsToShulkerBox(minecraftClient, playerSlotList, shulkerBoxUniqueItems);
        }
    }

    private static void fillShulkerAndPlayerSlotLists(List<Slot> shulkerBoxSlotList, List<Slot> playerSlotList, ScreenHandler screenHandler) {
        if (screenHandler != null && shulkerBoxSlotList != null && playerSlotList != null) {
            for (Slot slot : screenHandler.slots) {
                if (slot instanceof ShulkerBoxSlot) {
                    shulkerBoxSlotList.add(slot);
                } else {
                    playerSlotList.add(slot);
                }
            }
        }
    }

    private static Set<Item> getShulkerBoxUniqueItems(List<Slot> shulkerBoxSlotList) {
        Set<Item> shulkerBoxUniqueItemSet = new HashSet<>();
        if (shulkerBoxSlotList != null && !shulkerBoxSlotList.isEmpty()) {
            for (Slot slot : shulkerBoxSlotList) {
                if (slot.hasStack()) {
                    shulkerBoxUniqueItemSet.add(slot.getStack().getItem());
                }
            }
        }
        return shulkerBoxUniqueItemSet;
    }

    private static void moveItemsToShulkerBox(MinecraftClient mcClient, List<Slot> playerSlotList, Set<Item> shulkerBoxItemMask) {
        for (Slot slot : playerSlotList) {
            Item item = slot.getStack().getItem();
            if (item != Items.AIR && shulkerBoxItemMask.stream().anyMatch(i -> i == item)) {
                if (mcClient != null && mcClient.interactionManager != null && mcClient.player != null) {
                    mcClient.interactionManager.clickSlot(
                            mcClient.player.currentScreenHandler.syncId,
                            slot.id,
                            0,
                            SlotActionType.QUICK_MOVE,
                            mcClient.player
                    );
                }
            }
        }
    }

}
