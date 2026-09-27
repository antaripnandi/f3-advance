package com.f3advanced.client.screen;

import com.f3advanced.client.config.F3AdvancedConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
//? if <26.1 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*/
//?}
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public final class F3AdvancedConfigScreen extends Screen {
    private final Screen parent;
    private final List<Page> pages = new ArrayList<>();
    private int pageIndex;

    private F3AdvancedConfigScreen(Screen parent) {
        super(Component.literal("F3 Advanced"));
        this.parent = parent;
        buildPages();
    }

    public static Screen create(Screen parent) {
        if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
            return ClothConfigHelper.create(parent);
        }
        return new F3AdvancedConfigScreen(parent);
    }

    @Override
    protected void init() {
        rebuildWidgets();
    }

    @Override
    public void onClose() {
        F3AdvancedConfig.save();
//? if <26.2 {
        Minecraft.getInstance().setScreen(parent);
//?} else {
/*      Minecraft.getInstance().gui.setScreen(parent);
*/
//?}
    }

    @Override
//? if <26.1 {
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
//?} else {
/*  public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
*/
//?}
//? if <1.21.4 {
/*      renderBackground(graphics, mouseX, mouseY, delta);
*/
//?}
//? if <26.1 {
        graphics.drawCenteredString(font, title, width / 2, 12, 0xFFFFFF);
        graphics.drawCenteredString(font, pages.get(pageIndex).title(), width / 2, 30, 0x88C0D0);
//?} else {
/*      graphics.centeredText(font, title, width / 2, 12, 0xFFFFFF);
        graphics.centeredText(font, pages.get(pageIndex).title(), width / 2, 30, 0x88C0D0);
*/
//?}
//? if <26.1 {
        super.render(graphics, mouseX, mouseY, delta);
//?} else {
/*      super.extractRenderState(graphics, mouseX, mouseY, delta);
*/
//?}
    }

    protected void rebuildWidgets() {
        clearWidgets();
        int navY = height - 52;
        addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            pageIndex = Math.floorMod(pageIndex - 1, pages.size());
            rebuildWidgets();
        }).bounds(width / 2 - 105, navY, 40, 20).build());
        addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            pageIndex = Math.floorMod(pageIndex + 1, pages.size());
            rebuildWidgets();
        }).bounds(width / 2 + 65, navY, 40, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
                .bounds(width / 2 - 50, height - 28, 100, 20).build());

        Page page = pages.get(pageIndex);
        int y = 52;
        int left = Math.max(16, width / 2 - 170);
        int buttonWidth = Math.min(340, width - 32);
        for (Toggle toggle : page.toggles()) {
            addRenderableWidget(Button.builder(label(toggle), button -> {
                toggle.flip().accept(!toggle.get().getAsBoolean());
                F3AdvancedConfig.save();
                button.setMessage(label(toggle));
            }).bounds(left, y, buttonWidth, 20).build());
            y += 24;
            if (y > height - 82) {
                break;
            }
        }
    }

    private Component label(Toggle toggle) {
        return Component.literal((toggle.get().getAsBoolean() ? "[on] " : "[off] ") + toggle.label());
    }

    private void buildPages() {
        F3AdvancedConfig config = F3AdvancedConfig.INSTANCE;
        pages.add(new Page("Sections", List.of(
                toggle("Always on", () -> config.alwaysOn, value -> config.alwaysOn = value),
                toggle("Position section", () -> config.sections.position, value -> config.sections.position = value),
                toggle("World section", () -> config.sections.world, value -> config.sections.world = value),
                toggle("Performance section", () -> config.sections.performance, value -> config.sections.performance = value),
                toggle("Player status section", () -> config.sections.playerStatus, value -> config.sections.playerStatus = value),
                toggle("Looking at section", () -> config.sections.target, value -> config.sections.target = value),
                toggle("System info section", () -> config.sections.system, value -> config.sections.system = value)
        )));
        pages.add(new Page("Position", List.of(
                toggle("Block coordinates", () -> config.lines.blockCoordinates, value -> config.lines.blockCoordinates = value),
                toggle("Decimal coordinates", () -> config.lines.decimalCoordinates, value -> config.lines.decimalCoordinates = value),
                toggle("Facing", () -> config.lines.facing, value -> config.lines.facing = value),
                toggle("Chunk coordinates", () -> config.lines.chunkCoordinates, value -> config.lines.chunkCoordinates = value),
                toggle("Slime chunk", () -> config.lines.slimeChunk, value -> config.lines.slimeChunk = value)
        )));
        pages.add(new Page("World", List.of(
                toggle("Biome", () -> config.lines.biome, value -> config.lines.biome = value),
                toggle("Block light", () -> config.lines.blockLight, value -> config.lines.blockLight = value),
                toggle("Sky light", () -> config.lines.skyLight, value -> config.lines.skyLight = value),
                toggle("Mob spawn indicator", () -> config.lines.mobSpawnIndicator, value -> config.lines.mobSpawnIndicator = value),
                toggle("Time of day", () -> config.lines.timeOfDay, value -> config.lines.timeOfDay = value),
                toggle("Day count", () -> config.lines.dayCount, value -> config.lines.dayCount = value),
                toggle("Weather", () -> config.lines.weather, value -> config.lines.weather = value),
                toggle("Moon phase", () -> config.lines.moonPhase, value -> config.lines.moonPhase = value),
                toggle("Dimension", () -> config.lines.dimension, value -> config.lines.dimension = value)
        )));
        pages.add(new Page("Performance", List.of(
                toggle("FPS", () -> config.lines.fps, value -> config.lines.fps = value),
                toggle("FPS graph", () -> config.lines.fpsGraph, value -> config.lines.fpsGraph = value),
                toggle("TPS/lag info", () -> config.lines.tpsLag, value -> config.lines.tpsLag = value)
        )));
        pages.add(new Page("Player Status", List.of(
                toggle("Speed", () -> config.lines.speed, value -> config.lines.speed = value),
                toggle("Air/oxygen", () -> config.lines.air, value -> config.lines.air = value),
                toggle("Hunger", () -> config.lines.hunger, value -> config.lines.hunger = value),
                toggle("Potion effects", () -> config.lines.effects, value -> config.lines.effects = value)
        )));
        pages.add(new Page("Looking At", List.of(
                toggle("Target ID", () -> config.lines.targetId, value -> config.lines.targetId = value),
                toggle("Target display name", () -> config.lines.targetName, value -> config.lines.targetName = value),
                toggle("Target health", () -> config.lines.targetHealth, value -> config.lines.targetHealth = value)
        )));
        pages.add(new Page("System Info", List.of(
                toggle("Java version", () -> config.lines.javaVersion, value -> config.lines.javaVersion = value),
                toggle("Memory", () -> config.lines.memory, value -> config.lines.memory = value),
                toggle("GPU", () -> config.lines.gpu, value -> config.lines.gpu = value)
        )));
    }

    private Toggle toggle(String label, BooleanSupplier get, Consumer<Boolean> flip) {
        return new Toggle(label, get, flip);
    }

    private record Page(String title, List<Toggle> toggles) {
    }

    private record Toggle(String label, BooleanSupplier get, Consumer<Boolean> flip) {
    }
}
