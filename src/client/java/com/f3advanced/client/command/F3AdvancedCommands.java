package com.f3advanced.client.command;

import com.f3advanced.client.F3AdvancedClient;
import com.f3advanced.client.config.F3AdvancedConfig;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class F3AdvancedCommands {
    private static final Map<String, SettingHandler> SETTINGS = new LinkedHashMap<>();

    private record SettingHandler(String label, Supplier<Boolean> getter, Consumer<Boolean> setter) {}

    static {
        // Sections & Global
        registerSetting("always_on", "Always-On HUD",
                () -> F3AdvancedConfig.INSTANCE.alwaysOn,
                v -> F3AdvancedConfig.INSTANCE.alwaysOn = v);
        registerSetting("position", "Position Section",
                () -> F3AdvancedConfig.INSTANCE.sections.position,
                v -> F3AdvancedConfig.INSTANCE.sections.position = v);
        registerSetting("world", "World Section",
                () -> F3AdvancedConfig.INSTANCE.sections.world,
                v -> F3AdvancedConfig.INSTANCE.sections.world = v);
        registerSetting("performance", "Performance Section",
                () -> F3AdvancedConfig.INSTANCE.sections.performance,
                v -> F3AdvancedConfig.INSTANCE.sections.performance = v);
        registerSetting("player", "Player Section",
                () -> F3AdvancedConfig.INSTANCE.sections.playerStatus,
                v -> F3AdvancedConfig.INSTANCE.sections.playerStatus = v);
        registerSetting("target", "Target Section",
                () -> F3AdvancedConfig.INSTANCE.sections.target,
                v -> F3AdvancedConfig.INSTANCE.sections.target = v);
        registerSetting("system", "System Section",
                () -> F3AdvancedConfig.INSTANCE.sections.system,
                v -> F3AdvancedConfig.INSTANCE.sections.system = v);

        // Lines
        registerSetting("coords", "Block Coordinates",
                () -> F3AdvancedConfig.INSTANCE.lines.blockCoordinates,
                v -> F3AdvancedConfig.INSTANCE.lines.blockCoordinates = v);
        registerSetting("decimal", "Decimal Coordinates",
                () -> F3AdvancedConfig.INSTANCE.lines.decimalCoordinates,
                v -> F3AdvancedConfig.INSTANCE.lines.decimalCoordinates = v);
        registerSetting("facing", "Facing Direction",
                () -> F3AdvancedConfig.INSTANCE.lines.facing,
                v -> F3AdvancedConfig.INSTANCE.lines.facing = v);
        registerSetting("chunk", "Chunk Coordinates",
                () -> F3AdvancedConfig.INSTANCE.lines.chunkCoordinates,
                v -> F3AdvancedConfig.INSTANCE.lines.chunkCoordinates = v);
        registerSetting("slime", "Slime Chunk Indicator",
                () -> F3AdvancedConfig.INSTANCE.lines.slimeChunk,
                v -> F3AdvancedConfig.INSTANCE.lines.slimeChunk = v);
        registerSetting("biome", "Biome Line",
                () -> F3AdvancedConfig.INSTANCE.lines.biome,
                v -> F3AdvancedConfig.INSTANCE.lines.biome = v);
        registerSetting("light", "Light Levels",
                () -> F3AdvancedConfig.INSTANCE.lines.blockLight,
                v -> {
                    F3AdvancedConfig.INSTANCE.lines.blockLight = v;
                    F3AdvancedConfig.INSTANCE.lines.skyLight = v;
                });
        registerSetting("time", "Time of Day",
                () -> F3AdvancedConfig.INSTANCE.lines.timeOfDay,
                v -> F3AdvancedConfig.INSTANCE.lines.timeOfDay = v);
        registerSetting("day", "Day Count",
                () -> F3AdvancedConfig.INSTANCE.lines.dayCount,
                v -> F3AdvancedConfig.INSTANCE.lines.dayCount = v);
        registerSetting("weather", "Weather Line",
                () -> F3AdvancedConfig.INSTANCE.lines.weather,
                v -> F3AdvancedConfig.INSTANCE.lines.weather = v);
        registerSetting("fps", "FPS Line",
                () -> F3AdvancedConfig.INSTANCE.lines.fps,
                v -> F3AdvancedConfig.INSTANCE.lines.fps = v);
        registerSetting("fpsgraph", "FPS Graph",
                () -> F3AdvancedConfig.INSTANCE.lines.fpsGraph,
                v -> F3AdvancedConfig.INSTANCE.lines.fpsGraph = v);
        registerSetting("tps", "TPS / Lag Indicator",
                () -> F3AdvancedConfig.INSTANCE.lines.tpsLag,
                v -> F3AdvancedConfig.INSTANCE.lines.tpsLag = v);
        registerSetting("speed", "Player Speed",
                () -> F3AdvancedConfig.INSTANCE.lines.speed,
                v -> F3AdvancedConfig.INSTANCE.lines.speed = v);
        registerSetting("air", "Air Line",
                () -> F3AdvancedConfig.INSTANCE.lines.air,
                v -> F3AdvancedConfig.INSTANCE.lines.air = v);
        registerSetting("hunger", "Hunger / Saturation",
                () -> F3AdvancedConfig.INSTANCE.lines.hunger,
                v -> F3AdvancedConfig.INSTANCE.lines.hunger = v);
        registerSetting("effects", "Active Potion Effects",
                () -> F3AdvancedConfig.INSTANCE.lines.effects,
                v -> F3AdvancedConfig.INSTANCE.lines.effects = v);
        registerSetting("memory", "Memory Usage",
                () -> F3AdvancedConfig.INSTANCE.lines.memory,
                v -> F3AdvancedConfig.INSTANCE.lines.memory = v);
        registerSetting("gpu", "GPU Info",
                () -> F3AdvancedConfig.INSTANCE.lines.gpu,
                v -> F3AdvancedConfig.INSTANCE.lines.gpu = v);
    }

    private static void registerSetting(String name, String label, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        SETTINGS.put(name.toLowerCase(), new SettingHandler(label, getter, setter));
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    private static <T> RequiredArgumentBuilder<FabricClientCommandSource, T> argument(String name, com.mojang.brigadier.arguments.ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            LiteralArgumentBuilder<FabricClientCommandSource> root = literal("f3");

            // /f3 -> open GUI
            root.executes(ctx -> {
                openGui(ctx.getSource());
                return 1;
            });

            // /f3 config -> open GUI
            root.then(literal("config")
                    .executes(ctx -> {
                        openGui(ctx.getSource());
                        return 1;
                    }));

            // /f3 gui -> open GUI
            root.then(literal("gui")
                    .executes(ctx -> {
                        openGui(ctx.getSource());
                        return 1;
                    }));

            // /f3 help
            root.then(literal("help")
                    .executes(ctx -> {
                        sendHelp(ctx.getSource());
                        return 1;
                    }));

            // /f3 reset
            root.then(literal("reset")
                    .executes(ctx -> {
                        F3AdvancedConfig.INSTANCE = new F3AdvancedConfig();
                        F3AdvancedConfig.save();
                        ctx.getSource().sendFeedback(Component.literal("§b[F3 Advanced] §aAll settings reset to defaults!"));
                        return 1;
                    }));

            // /f3 toggle <name>
            LiteralArgumentBuilder<FabricClientCommandSource> toggleNode = literal("toggle");
            for (Map.Entry<String, SettingHandler> entry : SETTINGS.entrySet()) {
                final SettingHandler handler = entry.getValue();
                toggleNode.then(literal(entry.getKey())
                        .executes(ctx -> {
                            boolean newVal = !handler.getter().get();
                            handler.setter().accept(newVal);
                            F3AdvancedConfig.save();
                            ctx.getSource().sendFeedback(Component.literal(
                                    "§b[F3 Advanced] §f" + handler.label() + " is now " + (newVal ? "§aENABLED" : "§cDISABLED")));
                            return 1;
                        }));
            }
            root.then(toggleNode);

            // /f3 set <name> <true|false>
            LiteralArgumentBuilder<FabricClientCommandSource> setNode = literal("set");
            for (Map.Entry<String, SettingHandler> entry : SETTINGS.entrySet()) {
                final SettingHandler handler = entry.getValue();
                setNode.then(literal(entry.getKey())
                        .then(argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    boolean newVal = BoolArgumentType.getBool(ctx, "enabled");
                                    handler.setter().accept(newVal);
                                    F3AdvancedConfig.save();
                                    ctx.getSource().sendFeedback(Component.literal(
                                            "§b[F3 Advanced] §f" + handler.label() + " set to " + (newVal ? "§aENABLED" : "§cDISABLED")));
                                    return 1;
                                })));
            }
            root.then(setNode);

            // /f3 get <name>
            LiteralArgumentBuilder<FabricClientCommandSource> getNode = literal("get");
            for (Map.Entry<String, SettingHandler> entry : SETTINGS.entrySet()) {
                final SettingHandler handler = entry.getValue();
                getNode.then(literal(entry.getKey())
                        .executes(ctx -> {
                            boolean current = handler.getter().get();
                            ctx.getSource().sendFeedback(Component.literal(
                                    "§b[F3 Advanced] §f" + handler.label() + ": " + (current ? "§aENABLED" : "§cDISABLED")));
                            return 1;
                        }));
            }
            root.then(getNode);

            dispatcher.register(root);
        });
    }

    private static void openGui(FabricClientCommandSource source) {
        Minecraft client = source.getClient();
        client.execute(() -> F3AdvancedClient.openConfigScreen(client));
    }

    private static void sendHelp(FabricClientCommandSource source) {
        source.sendFeedback(Component.literal("§b=== F3 Advanced Commands ==="));
        source.sendFeedback(Component.literal("§e/f3 §7or §e/f3 config §7- Open configuration GUI §8(Shortcut: §bF3 + M§8)"));
        source.sendFeedback(Component.literal("§e/f3 toggle <setting> §7- Toggle a section or line"));
        source.sendFeedback(Component.literal("§e/f3 set <setting> <true|false> §7- Explicitly enable/disable"));
        source.sendFeedback(Component.literal("§e/f3 get <setting> §7- View current status"));
        source.sendFeedback(Component.literal("§e/f3 reset §7- Reset all settings to defaults"));
        source.sendFeedback(Component.literal("§7Available settings: §fposition, world, performance, player, target, system, always_on, fps, fpsgraph, coords, biome, light, weather, time, day, speed, hunger, memory, gpu..."));
    }
}
