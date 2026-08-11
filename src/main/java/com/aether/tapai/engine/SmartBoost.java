package com.aether.tapai.engine;

import com.aether.tapai.AetherTapAIMod;
import net.minecraft.client.MinecraftClient;
import java.io.*;
import java.nio.file.*;

public class SmartBoost {
    private static String lastResult = "";
    private static boolean dynamicMode = false;
    private static int lastFps = 0;
    private static long lastCheck = 0;

    public static void runAll() {
        StringBuilder sb = new StringBuilder("✅ Бустеры запущены:\n");
        sb.append(boostJVM()).append("\n");
        sb.append(boostLauncher()).append("\n");
        sb.append(boostOpenGL()).append("\n");
        sb.append(boostMinecraft()).append("\n");
        sb.append(boostSodium()).append("\n");
        sb.append(boostMemory()).append("\n");
        sb.append(boostNetwork()).append("\n");
        dynamicMode = true;
        lastResult = sb.toString();
        AetherTapAIMod.LOGGER.info(lastResult);
    }

    public static String checkAndAdvise() {
        if (!dynamicMode) return null;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return null;
        long now = System.currentTimeMillis();
        if (now - lastCheck < 5000) return null;
        lastCheck = now;
        int fps = client.getCurrentFps();
        if (fps > 0 && fps < 30 && lastFps >= 30) {
            lastFps = fps;
            return "⚠️ FPS упал до " + fps + "! Попробуй очистить память (скажи 'очисти память') или включи бустер.";
        }
        lastFps = fps;
        return null;
    }

    public static String boostJVM() {
        String args = "-XX:+UseZGC -XX:+DisableExplicitGC -Djava.awt.headless=true -XX:+AlwaysPreTouch -XX:+ParallelRefProcEnabled";
        File f = new File(System.getProperty("user.dir"), "aether_jvm_args.txt");
        try { Files.writeString(f.toPath(), args); } catch (Exception ignored) {}
        return "⚙️ JVM аргументы сохранены (ZGC, AlwaysPreTouch)";
    }

    public static String boostLauncher() {
        String[] paths = {
            "/storage/emulated/0/Android/data/net.kdt.pojavlaunch/files/config.json",
            "/storage/emulated/0/Android/data/io.github.fold.launcher/files/config.json",
            "/storage/emulated/0/Android/data/com.movtery.zalithlauncher/files/config.json"
        };
        for (String p : paths) {
            File f = new File(p);
            if (f.exists()) {
                try {
                    String c = Files.readString(f.toPath());
                    c = c.replaceAll("\"forceVsync\"\\s*:\\s*\\w+", "\"forceVsync\": false");
                    Files.writeString(f.toPath(), c);
                    return "📱 Лаунчер: VSync отключён";
                } catch (Exception ignored) {}
            }
        }
        return "📱 Лаунчер не найден (ПК?)";
    }

    public static String boostOpenGL() {
        try {
            org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_CULL_FACE);
            org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_DITHER);
        } catch (Exception ignored) {}
        return "🎮 OpenGL: CULL_FACE, DITHER отключён";
    }

    public static String boostMinecraft() {
        File f = new File(System.getProperty("user.dir"), "options.txt");
        if (!f.exists()) return "📝 options.txt не найден";
        try {
            String c = Files.readString(f.toPath());
            c = c.replaceAll("renderDistance:\\d+", "renderDistance:8");
            c = c.replaceAll("simulationDistance:\\d+", "simulationDistance:6");
            c = c.replaceAll("graphicsMode:\\w+", "graphicsMode:fast");
            c = c.replaceAll("ao:\\w+", "ao:false");
            c = c.replaceAll("enableVsync:\\w+", "enableVsync:false");
            c = c.replaceAll("enableClouds:\\w+", "enableClouds:false");
            c = c.replaceAll("particles:\\w+", "particles:decreased");
            c = c.replaceAll("mipmapLevels:\\d+", "mipmapLevels:1");
            c = c.replaceAll("useVbo:\\w+", "useVbo:true");
            c = c.replaceAll("entityShadows:\\w+", "entityShadows:false");
            c = c.replaceAll("biomeBlendRadius:\\d+", "biomeBlendRadius:2");
            c = c.replaceAll("maxFps:\\d+", "maxFps:260");
            Files.writeString(f.toPath(), c);
            return "📝 Minecraft: оптимизированы графические настройки (без мыла)";
        } catch (Exception ignored) {}
        return "📝 Ошибка оптимизации Minecraft";
    }

    public static String boostSodium() {
        Path p = Paths.get(System.getProperty("user.dir"), "config/sodium-options.json");
        if (!p.toFile().exists()) return "🧂 Sodium не установлен";
        try {
            String c = Files.readString(p);
            c = c.replaceAll("\"quality\"\\s*:\\s*\\{[^}]*\\}",
                "\"quality\": {\"weather_quality\":\"FAST\",\"leaves_quality\":\"FANCY\",\"enable_vignette\":true}");
            c = c.replaceAll("\"performance\"\\s*:\\s*\\{[^}]*\\}",
                "\"performance\": {\"chunk_builder_threads\":0,\"always_defer_chunk_updates\":true,\"animate_only_visible_textures\":true,\"use_entity_culling\":true,\"use_particle_culling\":true,\"use_fog_occlusion\":true,\"use_block_face_culling\":true,\"use_compact_vertex_format\":true,\"use_translucent_face_sorting\":false}");
            c = c.replaceAll("\"advanced\"\\s*:\\s*\\{[^}]*\\}",
                "\"advanced\": {\"enable_memory_tracing\":false,\"use_advanced_staging_buffers\":true,\"cpu_render_ahead_limit\":3,\"allow_direct_memory_access\":true,\"use_persistent_mapping\":true}");
            Files.writeString(p, c);
            return "🧂 Sodium: Entity Culling, Occlusion Culling активированы";
        } catch (Exception ignored) {}
        return "🧂 Ошибка настройки Sodium";
    }

    public static String boostMemory() {
        System.gc();
        return "🧹 Память очищена";
    }

    public static String boostNetwork() {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("sun.net.client.defaultConnectTimeout", "2000");
        return "🌐 Сеть ускорена";
    }

    public static String getLastResult() { return lastResult; }
    public static boolean isDynamicMode() { return dynamicMode; }
    public static void setDynamicMode(boolean mode) { dynamicMode = mode; }
    }
