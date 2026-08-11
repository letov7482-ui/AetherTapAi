package com.aether.tapai.engine;

import com.aether.tapai.config.MindConfig;
import net.minecraft.client.MinecraftClient;
import java.util.*;

public class MindEngine {
    private static final Random RANDOM = new Random();
    private static final LinkedList<String> context = new LinkedList<>();
    private static final int CONTEXT_SIZE = 5;
    private static String mood = "нейтральное";

    private static final Set<String> PERFORMANCE_TERMS = new HashSet<>(Arrays.asList(
        "fps", "фпс", "лаг", "тормоз", "фриз", "производительн", "оптимизац", "буст", "boost",
        "рендер", "настройк", "график", "память", "ram", "поток", "процессор", "видеокарт",
        "sodium", "iris", "minecraft", "игра", "частота", "vsync", "разрешение"
    ));

    public static String process(String input) {
        String lower = input.toLowerCase().trim();
        if (lower.isEmpty()) return "Ты что-то хотел спросить?";

        context.addLast(lower);
        if (context.size() > CONTEXT_SIZE) context.removeFirst();

        String tone = analyzeTone(lower);
        updateMood(tone);

        // 1. Точное совпадение
        String exact = MindConfig.findExact(lower);
        if (exact != null) return applyMood(randomVariant(exact));

        // 2. Нечёткий поиск
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
        if (lower.equals("help") || lower.equals("помощь") || lower.equals("справка")) {
            return "Доступные команды: !boost, !boost pvp/mining/ultra, !status, !reset, !ai <вопрос>. Также можно обучить меня через 'запомни: фраза -> ответ'. Подробнее спроси 'что ты умеешь'.";
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

        // 7. Fallback для вопросов о производительности
        boolean hasPerformanceTerm = PERFORMANCE_TERMS.stream().anyMatch(lower::contains);
        if (hasPerformanceTerm) {
            return applyMood("Ты спрашиваешь о производительности? Попробуй команду !boost, или спроси 'как повысить FPS'.");
        }

        // 8. Fallback для вопросительных предложений
        if (lower.contains("?") || lower.startsWith("как") || lower.startsWith("что") || lower.startsWith("почему") || lower.startsWith("где") || lower.startsWith("когда")) {
            return applyMood("Хороший вопрос! Попробуй спросить иначе, например: 'как повысить FPS' или 'что ты умеешь'. Я обязательно помогу!");
        }

        // 9. Fallback если запрос про игру/мод
        if (lower.contains("майнкрафт") || lower.contains("minecraft") || lower.contains("мод") || lower.contains("aether")) {
            return applyMood("Кажется, ты спрашиваешь о моде или игре. Попробуй уточнить вопрос, я постараюсь ответить!");
        }

        // 10. Творческий ответ
        return applyMood(MindConfig.getRandomChaos());
    }

    private static String analyzeTone(String text) {
        String lower = text.toLowerCase();
        if (lower.contains("спасибо") || lower.contains("крут") || lower.contains("отлично") || lower.contains("молодец")) return "позитив";
        if (lower.contains("ненавижу") || lower.contains("плохой") || lower.contains("ужас") || lower.contains("тупой")) return "негатив";
        if (lower.contains("грустн") || lower.contains("печаль") || lower.contains("один")) return "грусть";
        return "нейтральное";
    }

    private static void updateMood(String tone) {
        switch (tone) {
            case "позитив": mood = "радостное"; break;
            case "негатив": mood = "дерзкое"; break;
            case "грусть": mood = "сочувствующее"; break;
            default: mood = "нейтральное";
        }
    }

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
