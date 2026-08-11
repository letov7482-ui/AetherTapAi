package com.aether.tapai.engine;

import com.aether.tapai.config.MindConfig;
import net.minecraft.client.MinecraftClient;
import java.util.*;

public class MindEngine {
    private static final Random RANDOM = new Random();

    public static String process(String input) {
        String lower = input.toLowerCase().trim();
        if (lower.isEmpty()) return "Ты что-то хотел спросить?";

        // 1. Точное совпадение в памяти
        String exact = MindConfig.findExact(lower);
        if (exact != null) return randomVariant(exact);

        // 2. Поиск по ключевым словам (гибкое понимание)
        String keywordAnswer = MindConfig.findByKeywords(lower);
        if (keywordAnswer != null) return randomVariant(keywordAnswer);

        // 3. Специальные команды
        if (lower.contains("fps") || lower.contains("фпс")) {
            int fps = MinecraftClient.getInstance().getCurrentFps();
            return randomVariant("Текущий FPS: " + fps + ". Бустер " + (SmartBoost.isDynamicMode() ? "активен" : "не запущен") + ".");
        }
        if (lower.contains("бустер") || lower.contains("boost")) {
            SmartBoost.runAll();
            return SmartBoost.getLastResult();
        }
        if (lower.contains("очисти память") || lower.contains("ram")) {
            SmartBoost.boostMemory();
            return "Память очищена!";
        }

        // 4. Если совсем ничего не подошло – творческий ответ
        return MindConfig.getRandomChaos();
    }

    private static String randomVariant(String answer) {
        // Если ответ содержит несколько вариантов через "|", выбираем случайный
        if (answer.contains("|")) {
            String[] parts = answer.split("\\|");
            return parts[RANDOM.nextInt(parts.length)].trim();
        }
        return answer;
    }
}
