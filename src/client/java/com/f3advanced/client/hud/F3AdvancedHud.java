package com.f3advanced.client.hud;

import com.f3advanced.client.config.F3AdvancedConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
//? if <26.1 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*/
//?}
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
//? if <1.21.11 {
import net.minecraft.resources.ResourceLocation;
//?} else {
/*import net.minecraft.resources.Identifier;
*/
//?}
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.lwjgl.opengl.GL11;

public final class F3AdvancedHud {
    private static final int GRAPH_SAMPLES = 48;
    private static final ArrayDeque<Integer> FPS = new ArrayDeque<>(GRAPH_SAMPLES);
    private static long lastFrameTime = System.nanoTime();
    /** Set to true by DebugScreenOverlayMixin when it renders; reset by the HudRenderCallback. */
    public static boolean renderedByMixin = false;

    private F3AdvancedHud() {
    }

//? if <26.1 {
    public static void render(Minecraft client, GuiGraphics graphics) {
//?} else {
/*  public static void render(Minecraft client, GuiGraphicsExtractor graphics) {
*/
//?}
//? if <26.2 {
        if (client.screen != null) {
            return;
        }
//?} else {
/*      if (client.gui.screen() != null) {
            return;
        }
*/
//?}
        LocalPlayer player = client.player;
        Level level = client.level;
//? if <26.2 {
        if (player == null || level == null || client.options.hideGui) {
            return;
        }
//?} else {
/*      if (player == null || level == null || client.gui.hud.isHidden()) {
            return;
        }
*/
//?}

        sampleFps(client);
        F3AdvancedConfig config = F3AdvancedConfig.INSTANCE;
        List<Row> rows = new ArrayList<>();
        boolean reduced = player.isReducedDebugInfo();

        if (config.sections.position && !reduced) addPosition(rows, player, level, config);
        if (config.sections.world && !reduced) addWorld(rows, player, level, config);
        if (config.sections.performance) addPerformance(rows, client, config);
        if (config.sections.playerStatus) addPlayer(rows, player, config);
        if (config.sections.target && !reduced) addTarget(rows, client, level, config);
        if (config.sections.system) addSystem(rows, client, config);

        drawRows(client, graphics, rows, config);
    }

    private static void addPosition(List<Row> rows, LocalPlayer player, Level level, F3AdvancedConfig config) {
        title(rows, "Position", config);
        BlockPos pos = player.blockPosition();
        if (config.lines.blockCoordinates) row(rows, "XYZ", "%d / %d / %d".formatted(pos.getX(), pos.getY(), pos.getZ()), config.style.valueColor);
        if (config.lines.decimalCoordinates) row(rows, "Decimal", "%.3f / %.3f / %.3f".formatted(player.getX(), player.getY(), player.getZ()), config.style.valueColor);
        if (config.lines.facing) row(rows, "Facing", facing(player), config.style.accentColor);
        if (config.lines.chunkCoordinates) row(rows, "Chunk", chunk(player), config.style.valueColor);
        if (config.lines.slimeChunk) {
            Boolean slime = slime(level, pos);
            row(rows, "Slime chunk", slime == null ? "seed unavailable" : slime ? "yes" : "no", slime == null ? config.style.warningColor : slime ? config.style.goodColor : config.style.badColor);
        }
    }

    private static void addWorld(List<Row> rows, LocalPlayer player, Level level, F3AdvancedConfig config) {
        title(rows, "World", config);
        BlockPos pos = player.blockPosition();
        if (config.lines.biome) row(rows, "Biome", biomeName(level, pos).getString(), config.style.accentColor);
        if (config.lines.blockLight) row(rows, "Block light", String.valueOf(level.getBrightness(LightLayer.BLOCK, pos)), config.style.valueColor);
        if (config.lines.skyLight) row(rows, "Sky light", String.valueOf(level.getBrightness(LightLayer.SKY, pos)), config.style.valueColor);
        if (config.lines.mobSpawnIndicator) {
            boolean canSpawn = canHostileSpawn(level, pos);
            row(rows, "Hostile spawn", canSpawn ? "possible" : "blocked", canSpawn ? config.style.badColor : config.style.goodColor);
        }
//? if <26.1 {
        long dayTime = level.getDayTime();
//?} else {
/*      long dayTime = level.getDefaultClockTime();
*/
//?}
        if (config.lines.timeOfDay) row(rows, "Time", ticksToClock(dayTime), config.style.valueColor);
        if (config.lines.dayCount) row(rows, "Day", String.valueOf(dayTime / 24000L), config.style.valueColor);
        if (config.lines.weather) row(rows, "Weather", weather(level), config.style.valueColor);
//? if <1.21.11 {
        if (config.lines.moonPhase) row(rows, "Moon", moon(level.getMoonPhase()), config.style.valueColor);
//?} else if <26.1 {
/*      if (config.lines.moonPhase) row(rows, "Moon", moon((int)(level.getDayTime() / 24000L % 8L)), config.style.valueColor);
*/
//?} else {
/*      if (config.lines.moonPhase) row(rows, "Moon", moon((int)(level.getDefaultClockTime() / 24000L % 8L)), config.style.valueColor);
*/
//?}
        if (config.lines.dimension) row(rows, "Dimension", dimensionName(level), config.style.accentColor);
    }

    private static void addPerformance(List<Row> rows, Minecraft client, F3AdvancedConfig config) {
        title(rows, "Performance", config);
        int fps = client.getFps();
        if (config.lines.fps) row(rows, "FPS", String.valueOf(fps), fpsColor(fps, config));
        if (config.lines.fpsGraph) row(rows, "Graph", graph(), fpsColor(fps, config));
        if (config.lines.tpsLag) row(rows, "TPS", "server value unavailable client-side", config.style.warningColor);
    }

    private static void addPlayer(List<Row> rows, LocalPlayer player, F3AdvancedConfig config) {
        title(rows, "Player", config);
        if (config.lines.speed) row(rows, "Speed", "%.2f m/s".formatted(player.getDeltaMovement().horizontalDistance() * 20.0D), config.style.valueColor);
        if (config.lines.air) row(rows, "Air", "%d / %d".formatted(player.getAirSupply(), player.getMaxAirSupply()), airColor(player, config));
        if (config.lines.hunger) row(rows, "Hunger", "%d / 20".formatted(player.getFoodData().getFoodLevel()), config.style.valueColor);
        if (config.lines.effects) row(rows, "Effects", effects(player.getActiveEffects()), config.style.accentColor);
    }

    private static void addTarget(List<Row> rows, Minecraft client, Level level, F3AdvancedConfig config) {
        HitResult hit = client.hitResult;
        if (hit == null || hit.getType() == HitResult.Type.MISS) {
            return;
        }

        title(rows, "Looking At", config);
        if (hit instanceof BlockHitResult blockHit) {
            BlockPos pos = blockHit.getBlockPos();
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();
//? if <1.21.11 {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
//?} else {
/*          Identifier id = BuiltInRegistries.BLOCK.getKey(block);
*/
//?}
            if (config.lines.targetId) row(rows, "Block ID", safeId(id), config.style.valueColor);
            if (config.lines.targetName) row(rows, "Block name", block.getName().getString(), config.style.accentColor);
        } else if (hit instanceof EntityHitResult entityHit) {
//? if <1.21.11 {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entityHit.getEntity().getType());
//?} else {
/*          Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(entityHit.getEntity().getType());
*/
//?}
            if (config.lines.targetId) row(rows, "Entity ID", safeId(id), config.style.valueColor);
            if (config.lines.targetName) row(rows, "Entity name", entityHit.getEntity().getDisplayName().getString(), config.style.accentColor);
            if (config.lines.targetHealth && entityHit.getEntity() instanceof LivingEntity living) {
                row(rows, "Health", "%.1f / %.1f".formatted(living.getHealth(), living.getMaxHealth()), healthColor(living, config));
            }
        }
    }

    private static void addSystem(List<Row> rows, Minecraft client, F3AdvancedConfig config) {
        title(rows, "System", config);
        Runtime runtime = Runtime.getRuntime();
        long used = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        long max = runtime.maxMemory() / 1048576L;
        if (config.lines.javaVersion) row(rows, "Java", System.getProperty("java.version"), config.style.valueColor);
        if (config.lines.memory) row(rows, "RAM", used + " / " + max + " MB", config.style.valueColor);
        if (config.lines.gpu) row(rows, "GPU", gpuRenderer(), config.style.valueColor);
    }

//? if <26.1 {
    private static void drawRows(Minecraft client, GuiGraphics graphics, List<Row> rows, F3AdvancedConfig config) {
//?} else {
/*  private static void drawRows(Minecraft client, GuiGraphicsExtractor graphics, List<Row> rows, F3AdvancedConfig config) {
*/
//?}
        if (rows.isEmpty()) {
            return;
        }
        int width = 0;
        int lineHeight = client.font.lineHeight + 2;
        for (Row row : rows) {
            width = Math.max(width, client.font.width(row.text()));
        }
        int x = config.style.x;
        int y = config.style.y;
        int pad = config.style.padding;
        graphics.fill(x - pad, y - pad, x + width + pad, y + rows.size() * lineHeight + pad, config.style.backgroundArgb);
        for (int i = 0; i < rows.size(); i++) {
            Row row = rows.get(i);
//? if <26.1 {
            graphics.drawString(client.font, row.text(), x, y + i * lineHeight, 0xFF000000 | row.color(), false);
//?} else {
/*          graphics.text(client.font, row.text(), x, y + i * lineHeight, 0xFF000000 | row.color(), false);
*/
//?}
        }
    }

    private static void sampleFps(Minecraft client) {
        long now = System.nanoTime();
        if (now <= lastFrameTime) {
            return;
        }
        int fps = client.getFps();
        FPS.addLast(fps);
        while (FPS.size() > GRAPH_SAMPLES) {
            FPS.removeFirst();
        }
        lastFrameTime = now;
    }

    private static String facing(LocalPlayer player) {
        Direction direction = player.getDirection();
        float degrees = Mth.wrapDegrees(player.getYRot());
        return direction.getName().toUpperCase(Locale.ROOT) + " (" + Math.round(degrees) + " deg)";
    }

    private static String chunk(LocalPlayer player) {
        BlockPos pos = player.blockPosition();
        ChunkPos chunk = new ChunkPos(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
//? if <26.1 {
        return chunk.x + ", " + chunk.z + " in " + SectionPos.sectionRelative(pos.getX()) + ", " + SectionPos.sectionRelative(pos.getY()) + ", " + SectionPos.sectionRelative(pos.getZ());
//?} else {
/*      return chunk.x() + ", " + chunk.z() + " in " + SectionPos.sectionRelative(pos.getX()) + ", " + SectionPos.sectionRelative(pos.getY()) + ", " + SectionPos.sectionRelative(pos.getZ());
*/
//?}
    }

    private static Boolean slime(Level level, BlockPos pos) {
        Minecraft client = Minecraft.getInstance();
        if (client.getSingleplayerServer() == null) {
            return null;
        }
        net.minecraft.server.level.ServerLevel serverLevel = client.getSingleplayerServer().getLevel(level.dimension());
        if (serverLevel == null) {
            return null;
        }
        ChunkPos chunk = new ChunkPos(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
//? if <26.1 {
        return net.minecraft.world.level.levelgen.WorldgenRandom.seedSlimeChunk(chunk.x, chunk.z, serverLevel.getSeed(), 987234911L).nextInt(10) == 0;
//?} else {
/*      return net.minecraft.world.level.levelgen.WorldgenRandom.seedSlimeChunk(chunk.x(), chunk.z(), serverLevel.getSeed(), 987234911L).nextInt(10) == 0;
*/
//?}
    }

    private static Component biomeName(Level level, BlockPos pos) {
        return level.getBiome(pos).unwrapKey()
//? if <1.21.11 {
                .map(key -> translationFromId("biome", key.location()))
//?} else {
/*              .map(key -> translationFromId("biome", key.identifier()))
*/
//?}
                .orElse(Component.literal("unknown"));
    }

    private static String dimensionName(Level level) {
//? if <1.21.11 {
        ResourceLocation id = level.dimension().location();
//?} else {
/*      Identifier id = level.dimension().identifier();
*/
//?}
        return Component.translatable("dimension." + id.getNamespace() + "." + id.getPath()).getString();
    }

//? if <1.21.11 {
    private static Component translationFromId(String type, ResourceLocation id) {
//?} else {
/*  private static Component translationFromId(String type, Identifier id) {
*/
//?}
        return Component.translatable(type + "." + id.getNamespace() + "." + id.getPath().replace('/', '.'));
    }

    private static boolean canHostileSpawn(Level level, BlockPos pos) {
        if (level.getDifficulty() == Difficulty.PEACEFUL || !level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir()) {
            return false;
        }
        BlockPos floor = pos.below();
        return level.getBlockState(floor).isFaceSturdy(level, floor, Direction.UP) && level.getBrightness(LightLayer.BLOCK, pos) == 0;
    }

    private static String ticksToClock(long dayTime) {
        long ticks = Math.floorMod(dayTime, 24000L);
        long minutes = (ticks + 6000L) * 60L / 1000L;
        return "%02d:%02d".formatted((minutes / 60L) % 24L, minutes % 60L);
    }

    private static String weather(Level level) {
        if (level.isThundering()) return "thunder";
        if (level.isRaining()) return "rain";
        return "clear";
    }

    private static String moon(int phase) {
        String[] names = {"full", "waning gibbous", "last quarter", "waning crescent", "new", "waxing crescent", "first quarter", "waxing gibbous"};
        return names[Math.floorMod(phase, names.length)];
    }

    private static int fpsColor(int fps, F3AdvancedConfig config) {
        if (fps >= 60) return config.style.goodColor;
        if (fps >= 30) return config.style.warningColor;
        return config.style.badColor;
    }

    private static int airColor(LocalPlayer player, F3AdvancedConfig config) {
        return player.getAirSupply() > player.getMaxAirSupply() / 3 ? config.style.goodColor : config.style.badColor;
    }

    private static int healthColor(LivingEntity entity, F3AdvancedConfig config) {
        float ratio = entity.getHealth() / Math.max(1.0F, entity.getMaxHealth());
        if (ratio > 0.6F) return config.style.goodColor;
        if (ratio > 0.3F) return config.style.warningColor;
        return config.style.badColor;
    }

    private static String effects(Collection<MobEffectInstance> effects) {
        if (effects.isEmpty()) {
            return "none";
        }
        List<String> parts = new ArrayList<>();
        for (MobEffectInstance effect : effects) {
            parts.add(Component.translatable(effect.getDescriptionId()).getString() + " " + formatTicks(effect.getDuration()));
        }
        return String.join(", ", parts);
    }

    private static String formatTicks(int ticks) {
        if (ticks < 0 || ticks > 20 * 60 * 60) {
            return "**:**";
        }
        int seconds = ticks / 20;
        return "%d:%02d".formatted(seconds / 60, seconds % 60);
    }

    private static String graph() {
        if (FPS.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder(FPS.size());
        for (int fps : FPS) {
            builder.append(fps >= 60 ? '|' : fps >= 30 ? ':' : '.');
        }
        return builder.toString();
    }

//? if <1.21.11 {
    private static String safeId(ResourceLocation id) {
//?} else {
/*  private static String safeId(Identifier id) {
*/
//?}
        return id == null ? "unknown" : id.toString();
    }

    private static String gpuRenderer() {
        String renderer = GL11.glGetString(GL11.GL_RENDERER);
        return renderer == null || renderer.isBlank() ? "unknown" : renderer;
    }

    private static void title(List<Row> rows, String title, F3AdvancedConfig config) {
        rows.add(new Row(ChatFormatting.BOLD + title, config.style.titleColor));
    }

    private static void row(List<Row> rows, String label, String value, int color) {
        rows.add(new Row(label + ": " + value, color));
    }

    private record Row(String text, int color) {
    }
}
