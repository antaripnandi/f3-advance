package com.f3advanced.client.screen;

import com.f3advanced.client.config.F3AdvancedConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ClothConfigHelper {
    public static Screen create(Screen parent) {
        F3AdvancedConfig config = F3AdvancedConfig.INSTANCE;
        me.shedaniel.clothconfig2.api.ConfigBuilder builder = me.shedaniel.clothconfig2.api.ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("F3 Advanced"))
                .setSavingRunnable(F3AdvancedConfig::save);
        me.shedaniel.clothconfig2.api.ConfigEntryBuilder entries = builder.entryBuilder();

        me.shedaniel.clothconfig2.api.ConfigCategory sections = builder.getOrCreateCategory(Component.literal("Sections"));
        sections.addEntry(bool(entries, "Always on", config.alwaysOn, value -> config.alwaysOn = value));
        sections.addEntry(bool(entries, "Position section", config.sections.position, value -> config.sections.position = value));
        sections.addEntry(bool(entries, "World section", config.sections.world, value -> config.sections.world = value));
        sections.addEntry(bool(entries, "Performance section", config.sections.performance, value -> config.sections.performance = value));
        sections.addEntry(bool(entries, "Player status section", config.sections.playerStatus, value -> config.sections.playerStatus = value));
        sections.addEntry(bool(entries, "Looking at section", config.sections.target, value -> config.sections.target = value));
        sections.addEntry(bool(entries, "System info section", config.sections.system, value -> config.sections.system = value));

        me.shedaniel.clothconfig2.api.ConfigCategory position = builder.getOrCreateCategory(Component.literal("Position"));
        position.addEntry(bool(entries, "Block coordinates", config.lines.blockCoordinates, value -> config.lines.blockCoordinates = value));
        position.addEntry(bool(entries, "Decimal coordinates", config.lines.decimalCoordinates, value -> config.lines.decimalCoordinates = value));
        position.addEntry(bool(entries, "Facing", config.lines.facing, value -> config.lines.facing = value));
        position.addEntry(bool(entries, "Chunk coordinates", config.lines.chunkCoordinates, value -> config.lines.chunkCoordinates = value));
        position.addEntry(bool(entries, "Slime chunk", config.lines.slimeChunk, value -> config.lines.slimeChunk = value));

        me.shedaniel.clothconfig2.api.ConfigCategory world = builder.getOrCreateCategory(Component.literal("World"));
        world.addEntry(bool(entries, "Biome", config.lines.biome, value -> config.lines.biome = value));
        world.addEntry(bool(entries, "Block light", config.lines.blockLight, value -> config.lines.blockLight = value));
        world.addEntry(bool(entries, "Sky light", config.lines.skyLight, value -> config.lines.skyLight = value));
        world.addEntry(bool(entries, "Mob spawn indicator", config.lines.mobSpawnIndicator, value -> config.lines.mobSpawnIndicator = value));
        world.addEntry(bool(entries, "Time of day", config.lines.timeOfDay, value -> config.lines.timeOfDay = value));
        world.addEntry(bool(entries, "Day count", config.lines.dayCount, value -> config.lines.dayCount = value));
        world.addEntry(bool(entries, "Weather", config.lines.weather, value -> config.lines.weather = value));
        world.addEntry(bool(entries, "Moon phase", config.lines.moonPhase, value -> config.lines.moonPhase = value));
        world.addEntry(bool(entries, "Dimension", config.lines.dimension, value -> config.lines.dimension = value));

        me.shedaniel.clothconfig2.api.ConfigCategory performance = builder.getOrCreateCategory(Component.literal("Performance"));
        performance.addEntry(bool(entries, "FPS", config.lines.fps, value -> config.lines.fps = value));
        performance.addEntry(bool(entries, "FPS graph", config.lines.fpsGraph, value -> config.lines.fpsGraph = value));
        performance.addEntry(bool(entries, "TPS/lag info", config.lines.tpsLag, value -> config.lines.tpsLag = value));

        me.shedaniel.clothconfig2.api.ConfigCategory player = builder.getOrCreateCategory(Component.literal("Player Status"));
        player.addEntry(bool(entries, "Speed", config.lines.speed, value -> config.lines.speed = value));
        player.addEntry(bool(entries, "Air/oxygen", config.lines.air, value -> config.lines.air = value));
        player.addEntry(bool(entries, "Hunger", config.lines.hunger, value -> config.lines.hunger = value));
        player.addEntry(bool(entries, "Potion effects", config.lines.effects, value -> config.lines.effects = value));

        me.shedaniel.clothconfig2.api.ConfigCategory target = builder.getOrCreateCategory(Component.literal("Looking At"));
        target.addEntry(bool(entries, "Target ID", config.lines.targetId, value -> config.lines.targetId = value));
        target.addEntry(bool(entries, "Target display name", config.lines.targetName, value -> config.lines.targetName = value));
        target.addEntry(bool(entries, "Target health", config.lines.targetHealth, value -> config.lines.targetHealth = value));

        me.shedaniel.clothconfig2.api.ConfigCategory system = builder.getOrCreateCategory(Component.literal("System Info"));
        system.addEntry(bool(entries, "Java version", config.lines.javaVersion, value -> config.lines.javaVersion = value));
        system.addEntry(bool(entries, "Memory", config.lines.memory, value -> config.lines.memory = value));
        system.addEntry(bool(entries, "GPU", config.lines.gpu, value -> config.lines.gpu = value));

        me.shedaniel.clothconfig2.api.ConfigCategory style = builder.getOrCreateCategory(Component.literal("Style"));
        style.addEntry(integer(entries, "Background ARGB", config.style.backgroundArgb, value -> config.style.backgroundArgb = value));
        style.addEntry(integer(entries, "Title color", config.style.titleColor, value -> config.style.titleColor = value));
        style.addEntry(integer(entries, "Label color", config.style.labelColor, value -> config.style.labelColor = value));
        style.addEntry(integer(entries, "Value color", config.style.valueColor, value -> config.style.valueColor = value));
        style.addEntry(integer(entries, "Good color", config.style.goodColor, value -> config.style.goodColor = value));
        style.addEntry(integer(entries, "Warning color", config.style.warningColor, value -> config.style.warningColor = value));
        style.addEntry(integer(entries, "Bad color", config.style.badColor, value -> config.style.badColor = value));
        style.addEntry(integer(entries, "Accent color", config.style.accentColor, value -> config.style.accentColor = value));
        style.addEntry(integer(entries, "HUD X", config.style.x, value -> config.style.x = value));
        style.addEntry(integer(entries, "HUD Y", config.style.y, value -> config.style.y = value));
        style.addEntry(integer(entries, "Padding", config.style.padding, value -> config.style.padding = value));

        return builder.build();
    }

    private static me.shedaniel.clothconfig2.api.AbstractConfigListEntry<Boolean> bool(
            me.shedaniel.clothconfig2.api.ConfigEntryBuilder entries,
            String name,
            boolean value,
            java.util.function.Consumer<Boolean> save) {
        return entries.startBooleanToggle(Component.literal(name), value).setSaveConsumer(save).build();
    }

    private static me.shedaniel.clothconfig2.api.AbstractConfigListEntry<Integer> integer(
            me.shedaniel.clothconfig2.api.ConfigEntryBuilder entries,
            String name,
            int value,
            java.util.function.Consumer<Integer> save) {
        return entries.startIntField(Component.literal(name), value).setSaveConsumer(save).build();
    }
}
