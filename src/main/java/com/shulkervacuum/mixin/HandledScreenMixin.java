package com.shulkervacuum.mixin;

import com.shulkervacuum.util.ShulkerVacuumUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenHandler;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    // TODO: Make a configurable option to disable/enable auto-transfer of items.
    private boolean isShulkerBoxRendered = false;

    @Inject(method = "close", at = @At(value = "HEAD"))
    private void close(CallbackInfo ci) {
        isShulkerBoxRendered = false;
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!isShulkerBoxRendered) {
            // Delay required to fetch the correct items in the Shulker Box inventory.
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(this::handleShulkerBoxItemTransfer, 100, TimeUnit.MILLISECONDS);
            isShulkerBoxRendered = true;
        }
    }

    @Inject(method = "keyPressed", at = @At(value = "HEAD"))
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (keyCode == GLFW.GLFW_KEY_R) {
            handleShulkerBoxItemTransfer();
        }
    }

    private void handleShulkerBoxItemTransfer() {
        MinecraftClient instance = MinecraftClient.getInstance();
        if (instance != null && instance.player != null) {
            ScreenHandler currentScreenHandler = instance.player.currentScreenHandler;

            if (ShulkerVacuumUtil.isShulkerBoxScreenHandler(currentScreenHandler)) {
                ShulkerVacuumUtil.moveItemsToShulkerBox(currentScreenHandler, instance);
            }
        }
    }
}