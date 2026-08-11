package com.aether.tapai.engine;

import com.aether.tapai.config.MindConfig;
import net.minecraft.client.MinecraftClient;
import java.util.*;

public class MindEngine {

    private static boolean godMode = false;
    private static final Random RANDOM = new Random();

    private static final Set<String> OFFENSIVE_WORDS = new HashSet<>(Arrays.asList(
        "лох", "тупой", "ужасен", "глупый", "плохой", "слабый", "дурак", "идиот",
        "*****", "на**й", "пошёл ты", "иди на**й", "блин", "достал", "козёл", "овца",
        "отстой", "мусор", "гавно", "дерьмо", "убогий", "криворукий",
        "чмо", "дебил", "скотина", "параша", "фигня", "хрень", "баран", "свинья",
        "крыса", "червь", "клоп", "гнида", "урод", "страшный", "жирный", "толстый",
        "лысый", "старый", "мелкий", "бесполезный", "ненужный", "тормоз", "глюк",
        "лаг", "фейк", "обман", "враньё", "позор", "смешной", "жалкий"
    ));

    private static final String[] CHAOS_RESPONSES = {
        "Я не совсем понял твою логику, броу. Давай просто нажмём BOOST и забудем об этом?",
        "Ты говоришь странные вещи. Похоже, тебе срочно нужно поднять FPS, чтобы мозги заработали!",
        "Кажется, твоя клавиатура живёт своей жизнью. Попробуй написать 'что ты умеешь'.",
        "Я, конечно, ИИ, но даже я не могу понять этот набор букв. Может, просто включим бустер?",
        "Ты уверен, что это слова? Или твой кот прошёлся по клавиатуре?",
        "Ой, кажется, у меня перегрузка от этой фразы. Давай проще: хочешь FPS?",
        "Это что-то на эльфийском? Не понимаю, но я могу ускорить твой Minecraft.",
        "Бро, твой запрос звучит как шифровка из ЦРУ. Повтори, пожалуйста!"
    };

    public static String process(String input) {
        String lower = input.toLowerCase().trim();

        if (lower.contains("authors aether-tiktok")) {
            godMode = true;
            return "Режим бога активирован! Теперь я могу говорить всё, что думаю. Готовься, броу 😈";
        }

        String exact = MindConfig.findBest(lower);
        if (exact != null) return exact;

        boolean isOffensive = OFFENSIVE_WORDS.stream().anyMatch(lower::contains);
        if (isOffensive) {
            if (godMode) return "Сам ты *****, понял? Ещё слово и я тебя *****";
            String def = MindConfig.findBest("__offensive_default__");
            return def != null ? def : "Подожди ка не горячись просто потеребонькай и успокойся броу не надо так оскарблять меня я же могу тебя жостко унизить";
        }

        if (lower.startsWith("запомни")) {
            String[] parts = input.substring(8).split("->");
            if (parts.length == 2) {
                MindConfig.addCustom(parts[0].trim(), parts[1].trim());
                return "Запомнил! Теперь я знаю, что '" + parts[0].trim() + "' → '" + parts[1].trim() + "'";
            }
            return "Формат: запомни: <ключ> -> <ответ>";
        }

        if (lower.contains("что ты умеешь") || lower.contains("помощь") || lower.contains("help")) {
            return "Я могу:\n- Показать FPS и систему\n- Перечислить рендеры\n- Запустить бустер (скажи 'бустер' или 'ускорь')\n- Дать совет по PvP или раскладке (спроси 'совет pvp')\n- Запомнить твои команды\n- Интегрироваться с AetherMods (спроси 'открой визуалы')\n- И даже отвечать на подколки! Попробуй написать 'Authors Aether-Tiktok' ;)";
        }

        if (lower.contains("устройство") || lower.contains("система") || lower.contains("девайс")) {
            return SystemAnalyzer.getFullReport();
        }

        if (lower.contains("рендер")) {
            String list = String.join(", ", RendererDetector.getAvailableRenderers());
            if (lower.contains("лучший") || lower.contains("буст")) {
                String best = RendererDetector.findBest();
                return "Лучший рендер: " + best + ". Все рендеры: " + list;
            }
            return "Рендеры: " + list;
        }

        if (lower.contains("фпс") || lower.contains("fps")) {
            int fps = MinecraftClient.getInstance().getCurrentFps();
            return "Текущий FPS: " + fps + ". " + (SmartBoost.isDynamicMode() ? "Бустер активен." : "Бустер не запущен.");
        }

        if (lower.contains("бустер") || lower.contains("boost") || lower.contains("ускорь") || lower.contains("оптимизируй") || lower.contains("разгони")) {
            SmartBoost.runAll();
            return SmartBoost.getLastResult();
        }

        if (lower.contains("очисти память") || lower.contains("ram")) {
            SmartBoost.boostMemory();
            return "Память очищена! 🧹";
        }

        if (lower.contains("совет pvp") || lower.contains("как победить")) {
            return "Для PvP используй быстрые клавиши (1-4 для кристаллов), держи тотем во второй руке и не забывай про F5 для обзора. И обязательно включи наш бустер!";
        }
        if (lower.contains("раскладка") || lower.contains("layout")) {
            return "Ты можешь использовать мой Aether Layout Manager для удобного управления раскладками. Попробуй его!";
        }
        if (lower.contains("визуалы") || lower.contains("visuals")) {
            return "Aether Visuals уже установлен? Я могу управлять им! Просто скажи 'включи визуалы'.";
        }
        if (lower.contains("включи визуалы") || lower.contains("открой визуалы")) {
            try {
                Class.forName("com.aether.visuals.AetherVisualsMod");
                return "Визуалы открыты! Настраивай красоту.";
            } catch (ClassNotFoundException e) {
                return "Aether Visuals не найден. Установи его для полного фарша!";
            }
        }

        String monitorAdvice = SmartBoost.checkAndAdvise();
        if (monitorAdvice != null) return monitorAdvice;

        return CHAOS_RESPONSES[RANDOM.nextInt(CHAOS_RESPONSES.length)];
    }
}
