package com.aether.tapai.engine;

import com.aether.tapai.config.MindConfig;
import net.minecraft.client.MinecraftClient;
import java.util.*;

public class MindEngine {
    private static final Random RANDOM = new Random();
    // Контекст: последние 5 сообщений
    private static final LinkedList<String> context = new LinkedList<>();
    private static final int CONTEXT_SIZE = 5;
    // Псевдо-настроение (меняется от тональности)
    private static String mood = "нейтральное";

    public static String process(String input) {
        String lower = input.toLowerCase().trim();
        if (lower.isEmpty()) return "Ты что-то хотел спросить?";

        // Сохраняем в контекст
        context.addLast(lower);
        if (context.size() > CONTEXT_SIZE) context.removeFirst();

        // Анализ тональности
        String tone = analyzeTone(lower);
        updateMood(tone);

        // 1. Точное совпадение (приоритет)
        String exact = MindConfig.findExact(lower);
        if (exact != null) return applyMood(randomVariant(exact));

        // 2. Нечёткий поиск (опечатки)
        String fuzzy = MindConfig.findFuzzy(lower);
        if (fuzzy != null) return applyMood(randomVariant(fuzzy));

        // 3. Поиск по ключевым словам
        String keywordAnswer = MindConfig.findByKeywords(lower);
        if (keywordAnswer != null) return applyMood(randomVariant(keywordAnswer));

        // 4. Динамические команды
        if (lower.contains("fps") || lower.contains("фпс")) {
            int fps = MinecraftClient.getInstance().getCurrentFps();
            String evaluation = fps >= 60 ? "отличный" : (fps >= 30 ? "средний" : "низкий");
            return applyMood("Текущий FPS: " + fps + ", это " + evaluation + " показатель. Бустер " + (SmartBoost.isDynamicMode() ? "активен" : "не запущен") + ".");
        }
        if (lower.contains("бустер") || lower.contains("boost")) {
            SmartBoost.runAll();
            return applyMood(SmartBoost.getLastResult());
        }
        if (lower.contains("очисти память") || lower.contains("ram")) {
            SmartBoost.boostMemory();
            return applyMood("Память очищена! 🧹");
        }
        if (lower.contains("рендер") || lower.contains("renderer")) {
            return applyMood("Доступные рендеры: " + String.join(", ", RendererDetector.getAvailableRenderers()) +
                   ". Лучший: " + RendererDetector.findBest());
        }
        if (lower.contains("система") || lower.contains("устройство")) {
            return applyMood(SystemAnalyzer.getFullReport());
        }

        // 5. Контекстный анализ
        if (lower.length() < 10 && context.size() >= 2) {
            String prev = context.get(context.size() - 2);
            String contextAware = MindConfig.findContextual(lower, prev);
            if (contextAware != null) return applyMood(contextAware);
        }

        // 6. Обучение через "запомни"
        if (lower.startsWith("запомни")) {
            String[] parts = input.substring(7).split("->");
            if (parts.length == 2) {
                MindConfig.addCustom(parts[0].trim(), parts[1].trim());
                return applyMood("Запомнил! Теперь я знаю, что '" + parts[0].trim() + "' → '" + parts[1].trim() + "'");
            }
            return "Формат: запомни: <ключ> -> <ответ>";
        }

        // 7. Творческий ответ
        return applyMood(MindConfig.getRandomChaos());
    }

    // Анализ тональности (упрощённый)
    private static String analyzeTone(String text) {
        String lower = text.toLowerCase();
        if (lower.contains("спасибо") || lower.contains("крут") || lower.contains("отлично") || lower.contains("молодец")) return "позитив";
        if (lower.contains("ненавижу") || lower.contains("плохой") || lower.contains("ужас") || lower.contains("тупой")) return "негатив";
        if (lower.contains("грустн") || lower.contains("печаль") || lower.contains("один")) return "грусть";
        return "нейтральное";
    }

    // Обновление настроения
    private static void updateMood(String tone) {
        switch (tone) {
            case "позитив": mood = "радостное"; break;
            case "негатив": mood = "дерзкое"; break;
            case "грусть": mood = "сочувствующее"; break;
            default: mood = "нейтральное";
        }
    }

    // Применение настроения к ответу
    private static String applyMood(String answer) {
        switch (mood) {
            case "радостное": return answer + " 😊";
            case "дерзкое": return answer + " 😏";
            case "сочувствующее": return answer + " 🙁";
            default: return answer;
        }
    }

    private static String randomVariant(String answer) {
        if (answer.contains("|")) {
            String[] parts = answer.split("\\|");
            return parts[RANDOM.nextInt(parts.length)].trim();
        }
        return answer;
    }
}
