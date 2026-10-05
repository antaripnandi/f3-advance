package com.f3advanced.client;

import com.f3advanced.client.config.F3AdvancedConfig;
import com.f3advanced.client.screen.F3AdvancedConfigScreen;
import com.f3advanced.client.hud.F3AdvancedHud;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? if <26.1 {
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//?} else {
/*import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
*/
//?}
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
//? if <26.3 {
import org.lwjgl.glfw.GLFW;
//?}

public final class F3AdvancedClient implements ClientModInitializer {
    public static final String MOD_ID = "f3advanced";

    private static KeyMapping openConfig;
    private static KeyMapping togglePosition;
    private static KeyMapping toggleWorld;
    private static KeyMapping togglePerformance;
    private static KeyMapping togglePlayerStatus;
    private static KeyMapping toggleTarget;
    private static KeyMapping toggleSystem;
    private static boolean chordWasDown;

    @Override
    public void onInitializeClient() {
        F3AdvancedConfig.load();
        registerKeys();
        com.f3advanced.client.command.F3AdvancedCommands.register();
        ClientTickEvents.END_CLIENT_TICK.register(F3AdvancedClient::onClientTick);
//? if <26.1 {
        net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback.EVENT.register((graphics, tick) -> {
            Minecraft client = Minecraft.getInstance();
            boolean alreadyRendered = F3AdvancedHud.renderedByMixin;
            F3AdvancedHud.renderedByMixin = false;
            if (F3AdvancedConfig.INSTANCE.alwaysOn && !alreadyRendered) {
                F3AdvancedHud.render(client, graphics);
            }
        });
//?} else {
/*      net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.attachElementAfter(
                net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements.CHAT,
                net.minecraft.resources.Identifier.fromNamespaceAndPath("f3advanced", "hud"),
                (graphics, deltaTracker) -> {
                    Minecraft client = Minecraft.getInstance();
                    boolean alreadyRendered = F3AdvancedHud.renderedByMixin;
                    F3AdvancedHud.renderedByMixin = false;
                    if (F3AdvancedConfig.INSTANCE.alwaysOn && !alreadyRendered) {
                        F3AdvancedHud.render(client, graphics);
                    }
                }
        );
*/
//?}
    }

    public static void openConfigScreen(Minecraft client) {
//? if <26.2 {
        client.setScreen(F3AdvancedConfigScreen.create(client.screen));
//?} else {
/*      client.gui.setScreen(F3AdvancedConfigScreen.create(client.gui.screen()));
*/
//?}
    }

    private static void registerKeys() {
//? if <26.3 {
        openConfig = register("open_config", GLFW.GLFW_KEY_M);
//?} else {
/*      openConfig = register("open_config", InputConstants.KEY_M);
*/
//?}
        togglePosition = register("toggle_position", InputConstants.UNKNOWN.getValue());
        toggleWorld = register("toggle_world", InputConstants.UNKNOWN.getValue());
        togglePerformance = register("toggle_performance", InputConstants.UNKNOWN.getValue());
        togglePlayerStatus = register("toggle_player_status", InputConstants.UNKNOWN.getValue());
        toggleTarget = register("toggle_target", InputConstants.UNKNOWN.getValue());
        toggleSystem = register("toggle_system", InputConstants.UNKNOWN.getValue());
    }

//? if >=1.21.11 {
/*  private static net.minecraft.client.KeyMapping.Category category;
    private static net.minecraft.client.KeyMapping.Category getCategory() {
        if (category == null) {
            category = net.minecraft.client.KeyMapping.Category.register(net.minecraft.resources.Identifier.fromNamespaceAndPath("f3advanced", "f3advanced"));
        }
        return category;
    }
*/
//?}

    private static KeyMapping register(String name, int key) {
//? if <1.21.11 {
        KeyMapping mapping = new KeyMapping(
                "key.f3advanced." + name,
                InputConstants.Type.KEYSYM,
                key,
                "category.f3advanced");
//?} else if <26.3 {
/*      KeyMapping mapping = new KeyMapping(
                "key.f3advanced." + name,
                InputConstants.Type.KEYSYM,
                key,
                getCategory());
*/
//?} else {
/*      KeyMapping mapping = new KeyMapping(
                "key.f3advanced." + name,
                key,
                getCategory());
*/
//?}
//? if <26.1 {
        return KeyBindingHelper.registerKeyBinding(mapping);
//?} else {
/*      return KeyMappingHelper.registerKeyMapping(mapping);
*/
//?}
    }

    private static void onClientTick(Minecraft client) {
        if (client.player == null) {
            return;
        }

//? if <1.21.11 {
        long window = client.getWindow().getWindow();
        boolean chordDown = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_F3)
                && InputConstants.isKeyDown(window, GLFW.GLFW_KEY_M);
//?} else if <26.3 {
/*      com.mojang.blaze3d.platform.Window window = client.getWindow();
        boolean chordDown = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_F3)
                && InputConstants.isKeyDown(window, GLFW.GLFW_KEY_M);
*/
//?} else {
/*      boolean chordDown = InputConstants.isKeyDown(InputConstants.KEY_F3)
                && InputConstants.isKeyDown(InputConstants.KEY_M);
*/
//?}
        if (chordDown && !chordWasDown) {
            openConfigScreen(client);
        }
        chordWasDown = chordDown;

        while (togglePosition.consumeClick()) toggleSection("position", () -> F3AdvancedConfig.INSTANCE.sections.position = !F3AdvancedConfig.INSTANCE.sections.position);
        while (toggleWorld.consumeClick()) toggleSection("world", () -> F3AdvancedConfig.INSTANCE.sections.world = !F3AdvancedConfig.INSTANCE.sections.world);
        while (togglePerformance.consumeClick()) toggleSection("performance", () -> F3AdvancedConfig.INSTANCE.sections.performance = !F3AdvancedConfig.INSTANCE.sections.performance);
        while (togglePlayerStatus.consumeClick()) toggleSection("player", () -> F3AdvancedConfig.INSTANCE.sections.playerStatus = !F3AdvancedConfig.INSTANCE.sections.playerStatus);
        while (toggleTarget.consumeClick()) toggleSection("target", () -> F3AdvancedConfig.INSTANCE.sections.target = !F3AdvancedConfig.INSTANCE.sections.target);
        while (toggleSystem.consumeClick()) toggleSection("system", () -> F3AdvancedConfig.INSTANCE.sections.system = !F3AdvancedConfig.INSTANCE.sections.system);
    }

    private static void toggleSection(String section, Runnable toggle) {
        toggle.run();
        F3AdvancedConfig.save();
    }
}
