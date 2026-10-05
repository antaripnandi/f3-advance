package com.f3advanced.client.mixin;

import com.f3advanced.client.hud.F3AdvancedHud;
import net.minecraft.client.Minecraft;
//? if <26.1 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*/
//?}
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenOverlayMixin {
//? if <26.1 {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void f3advanced$replaceDebugOverlay(GuiGraphics graphics, CallbackInfo ci) {
//?} else {
/*  @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void f3advanced$replaceDebugOverlay(GuiGraphicsExtractor graphics, CallbackInfo ci) {
*/
//?}
        DebugScreenOverlay overlay = (DebugScreenOverlay)(Object)this;
        if (!overlay.showDebugScreen()) {
            return;
        }
        F3AdvancedHud.renderedByMixin = true;
        F3AdvancedHud.render(Minecraft.getInstance(), graphics);
        ci.cancel();
    }
}
