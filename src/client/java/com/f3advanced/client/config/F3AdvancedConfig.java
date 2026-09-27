package com.f3advanced.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class F3AdvancedConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("f3advanced.json");
    public static F3AdvancedConfig INSTANCE = new F3AdvancedConfig();

    public Sections sections = new Sections();
    public Lines lines = new Lines();
    public Style style = new Style();
    public boolean alwaysOn = false;

    public static void load() {
        if (!Files.exists(PATH)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(PATH)) {
            F3AdvancedConfig loaded = GSON.fromJson(reader, F3AdvancedConfig.class);
            if (loaded != null) {
                INSTANCE = loaded;
            }
        } catch (IOException ignored) {
            INSTANCE = new F3AdvancedConfig();
        }
        INSTANCE.ensureDefaults();
    }

    public static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(PATH)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (IOException ignored) {
        }
    }

    private void ensureDefaults() {
        if (sections == null) sections = new Sections();
        if (lines == null) lines = new Lines();
        if (style == null) style = new Style();
    }

    public static final class Sections {
        public boolean position = true;
        public boolean world = true;
        public boolean performance = true;
        public boolean playerStatus = true;
        public boolean target = true;
        public boolean system = false;
    }

    public static final class Lines {
        public boolean blockCoordinates = true;
        public boolean decimalCoordinates = true;
        public boolean facing = true;
        public boolean chunkCoordinates = true;
        public boolean slimeChunk = true;
        public boolean biome = true;
        public boolean blockLight = true;
        public boolean skyLight = true;
        public boolean mobSpawnIndicator = true;
        public boolean timeOfDay = true;
        public boolean dayCount = true;
        public boolean weather = true;
        public boolean moonPhase = true;
        public boolean dimension = true;
        public boolean fps = true;
        public boolean fpsGraph = true;
        public boolean tpsLag = true;
        public boolean speed = true;
        public boolean air = true;
        public boolean hunger = true;
        public boolean effects = true;
        public boolean targetId = true;
        public boolean targetName = true;
        public boolean targetHealth = true;
        public boolean javaVersion = true;
        public boolean memory = true;
        public boolean gpu = true;
    }

    public static final class Style {
        public int backgroundArgb = 0x88000000;
        public int titleColor = 0x55FFFF;
        public int labelColor = 0xD8DEE9;
        public int valueColor = 0xECEFF4;
        public int goodColor = 0xA3BE8C;
        public int warningColor = 0xEBCB8B;
        public int badColor = 0xBF616A;
        public int accentColor = 0x88C0D0;
        public int padding = 5;
        public int x = 6;
        public int y = 6;
    }
}
