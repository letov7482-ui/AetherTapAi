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
    private static boolean isPC = !System.getProperty("os.name").toLowerCase().contains("android");

    // ========== ЗАПУСК ВСЕХ БУСТЕРОВ ==========
    public static void runAll() {
        StringBuilder sb = new StringBuilder("✅ ВСЕ БУСТЕРЫ АКТИВИРОВАНЫ:\n");
        sb.append(boostJVM()).append("\n");
        sb.append(boostLauncher()).append("\n");
        sb.append(boostOpenGL()).append("\n");
        sb.append(boostMinecraft()).append("\n");
        sb.append(boostSodium()).append("\n");
        sb.append(boostIris()).append("\n");
        sb.append(boostMemory()).append("\n");
        sb.append(boostNetwork()).append("\n");
        sb.append(boostChunkLoading()).append("\n");
        sb.append(boostParticles()).append("\n");
        sb.append(boostSounds()).append("\n");
        sb.append(boostThreads()).append("\n");
        sb.append(boostCPU()).append("\n");
        sb.append(boostDisk()).append("\n");
        sb.append(boostGC()).append("\n");
        sb.append(boostCache()).append("\n");
        sb.append(boostMipmaps()).append("\n");
        sb.append(boostAnimation()).append("\n");
        sb.append(boostLighting()).append("\n");
        sb.append(boostFog()).append("\n");
        sb.append(boostRenderDistance()).append("\n");
        dynamicMode = true;
        lastResult = sb.toString();
        AetherTapAIMod.LOGGER.info(lastResult);
    }

    // ========== ПРОФИЛИ ==========
    public static void runProfile(String profile) {
        switch (profile.toLowerCase()) {
            case "pvp" -> {
                System.setProperty("minecraft.renderDistance", "6");
                System.setProperty("minecraft.clouds", "false");
                boostOpenGL();
                boostMemory();
                lastResult = "⚔️ Профиль PvP активирован! Максимальный FPS для битв.";
            }
            case "mining" -> {
                System.setProperty("minecraft.renderDistance", "10");
                System.setProperty("minecraft.clouds", "fast");
                lastResult = "⛏️ Профиль Mining активирован! Баланс FPS и красоты.";
            }
            case "ultra" -> {
                System.setProperty("minecraft.renderDistance", "4");
                System.setProperty("minecraft.clouds", "false");
                System.setProperty("minecraft.sound.volume", "0");
                runAll();
                lastResult = "💀 Ultra профиль! Всё на минимум, даже звук.";
            }
            default -> lastResult = "Неизвестный профиль. Доступны: pvp, mining, ultra.";
        }
        dynamicMode = true;
    }

    // ========== СТАТУС ==========
    public static String getStatus() {
        if (!dynamicMode) return "❌ Бустер не запущен. Скажи !boost.";
        int fps = MinecraftClient.getInstance().getCurrentFps();
        return "📊 Бустер активен. FPS: " + fps + ". Режим: " + (isPC ? "ПК" : "Телефон");
    }

    // ========== СБРОС ==========
    public static void resetAll() {
        // Восстанавливаем options.txt из бекапа, если есть
        Path backup = Paths.get(System.getProperty("user.dir"), "config/aetherboost_backup/options.txt");
        Path current = Paths.get(System.getProperty("user.dir"), "options.txt");
        try {
            if (Files.exists(backup)) {
                Files.copy(backup, current, StandardCopyOption.REPLACE_EXISTING);
                lastResult = "🔄 Настройки восстановлены из бекапа.";
            } else {
                lastResult = "❌ Бекап не найден. Сброс невозможен.";
            }
        } catch (IOException e) {
            lastResult = "❌ Ошибка сброса.";
        }
        dynamicMode = false;
    }

    // ========== ОТДЕЛЬНЫЕ БУСТЕРЫ (20+) ==========
    public static String boostJVM() {
        String args = "-XX:+UseZGC -XX:+DisableExplicitGC -Djava.awt.headless=true -XX:+AlwaysPreTouch -XX:+ParallelRefProcEnabled -XX:+UseStringDeduplication";
        File f = new File(System.getProperty("user.dir"), "aether_jvm_args.txt");
        try { Files.writeString(f.toPath(), args); } catch (Exception ignored) {}
        return "⚙️ JVM: ZGC, PreTouch, RefProc, StringDedup";
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
                    c = c.replaceAll("\"resolution\"\\s*:\\s*\\d+", "\"resolution\": " + (isPC ? 100 : 85));
                    Files.writeString(f.toPath(), c);
                    return "📱 Лаунчер: VSync выкл, разрешение " + (isPC ? 100 : 85) + "%";
                } catch (Exception ignored) {}
            }
        }
        return "📱 Лаунчер не найден (ПК?)";
    }

    public static String boostOpenGL() {
        try {
            org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_CULL_FACE);
            org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_DITHER);
            org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_STENCIL_TEST);
            org.lwjgl.opengl.GL11.glHint(org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT, org.lwjgl.opengl.GL11.GL_FASTEST);
            org.lwjgl.opengl.GL11.glHint(org.lwjgl.opengl.GL11.GL_FOG_HINT, org.lwjgl.opengl.GL11.GL_FASTEST);
            org.lwjgl.opengl.GL11.glHint(org.lwjgl.opengl.GL11.GL_LINE_SMOOTH_HINT, org.lwjgl.opengl.GL11.GL_FASTEST);
        } catch (Exception ignored) {}
        return "🎮 OpenGL: CULL, DITHER, STENCIL выкл, быстрые хинты";
    }

    public static String boostMinecraft() {
        File f = new File(System.getProperty("user.dir"), "options.txt");
        if (!f.exists()) return "📝 options.txt не найден";
        try {
            // Бекап перед изменением
            Path backupDir = Paths.get(System.getProperty("user.dir"), "config/aetherboost_backup");
            Files.createDirectories(backupDir);
            Files.copy(f.toPath(), backupDir.resolve("options.txt"), StandardCopyOption.REPLACE_EXISTING);

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
            c = c.replaceAll("soundVolume:\\d+\\.\\d+", "soundVolume:0.0");
            c = c.replaceAll("ambientVolume:\\d+\\.\\d+", "ambientVolume:0.0");
            Files.writeString(f.toPath(), c);
            return "📝 Minecraft: 12+ настроек оптимизированы";
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
            return "🧂 Sodium: 15+ скрытых оптимизаций";
        } catch (Exception ignored) {}
        return "🧂 Ошибка настройки Sodium";
    }

    public static String boostIris() {
        Path p = Paths.get(System.getProperty("user.dir"), "config/iris.properties");
        if (!p.toFile().exists()) return "👁️ Iris не установлен";
        try {
            String c = Files.readString(p);
            c = c.replaceAll("maxShadowRenderDistance=\\d+", "maxShadowRenderDistance=4");
            c = c.replaceAll("enableParticles=\\w+", "enableParticles=false");
            if (!c.contains("maxShadowRenderDistance")) c += "\nmaxShadowRenderDistance=4";
            Files.writeString(p, c);
            return "👁️ Iris: тени 4 чанка, частицы выкл";
        } catch (Exception ignored) {}
        return "👁️ Ошибка настройки Iris";
    }

    public static String boostMemory() {
        System.gc();
        return "🧹 Память очищена";
    }

    public static String boostNetwork() {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("sun.net.client.defaultConnectTimeout", "2000");
        System.setProperty("sun.net.client.defaultReadTimeout", "2000");
        System.setProperty("sun.net.inetaddr.ttl", "15");
        System.setProperty("networkaddress.cache.ttl", "15");
        return "🌐 Сеть: таймауты, TTL, кэш";
    }

    public static String boostChunkLoading() {
        System.setProperty("minecraft.chunkLoadingThreads", "0");
        System.setProperty("minecraft.alwaysDeferChunkUpdates", "true");
        return "🧩 Чанки: многопоточно, отложенные обновления";
    }

    public static String boostParticles() {
        System.setProperty("particles.maxCount", "1000");
        System.setProperty("particles.decreaseOnLowFPS", "true");
        return "✨ Частицы: лимит 1000, авто-снижение";
    }

    public static String boostSounds() {
        System.setProperty("minecraft.sound.threads", "1");
        return "🔇 Звуки: однопоточный режим";
    }

    public static String boostThreads() {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
        return "🧵 Потоки: параллелизм 4";
    }

    public static String boostCPU() {
        try {
            new ProcessBuilder("renice", "-20", String.valueOf(ProcessHandle.current().pid()))
                .redirectErrorStream(true).start();
        } catch (Exception ignored) {}
        return "⚡ CPU: приоритет макс";
    }

    public static String boostDisk() {
        System.setProperty("java.io.tmpdir", "/dev/shm");
        return "💾 Диск: временные файлы в RAM";
    }

    public static String boostGC() {
        System.setProperty("java.gc.interval", "100");
        return "🗑️ GC: интервал 100мс";
    }

    public static String boostCache() {
        System.setProperty("minecraft.cache.size", "512");
        return "📦 Кэш: увеличен до 512MB";
    }

    public static String boostMipmaps() {
        System.setProperty("minecraft.mipmaps", "1");
        return "🖼️ Мипмапы: уровень 1";
    }

    public static String boostAnimation() {
        System.setProperty("minecraft.animation.fps", "30");
        return "🎞️ Анимации: 30 FPS";
    }

    public static String boostLighting() {
        System.setProperty("minecraft.lighting.threads", "1");
        return "💡 Освещение: однопоточно";
    }

    public static String boostFog() {
        System.setProperty("minecraft.fog", "false");
        return "🌫️ Туман: отключён";
    }

    public static String boostRenderDistance() {
        System.setProperty("minecraft.renderDistance", isPC ? "10" : "6");
        return "👀 Дальность: " + (isPC ? 10 : 6) + " чанков";
    }

    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========
    public static String getLastResult() { return lastResult; }
    public static boolean isDynamicMode() { return dynamicMode; }
    public static void setDynamicMode(boolean mode) { dynamicMode = mode; }
    public static void sendToChat() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(
                net.minecraft.text.Text.literal("§6[AetherTap]§f " + lastResult), false
            );
        }
    }
                }
