package com.aether.tapai.config;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MindConfig {
    private static final Path CONFIG_PATH = Paths.get(System.getProperty("user.dir"), "config/aethertap_memory.txt");
    private static final Map<String, String> exactMatches = new LinkedHashMap<>();
    private static final List<KeywordEntry> keywordEntries = new ArrayList<>();
    private static final List<String> chaosResponses = new ArrayList<>();
    private static final Random RANDOM = new Random();

    static {
        // --- ТОЧНЫЕ СОВПАДЕНИЯ ---
        addExact("привет", "Привет! Я AetherTap AI – твой умный помощник и бустер FPS.");
        addExact("как дела", "У меня всё отлично! А у тебя как настроение?");
        addExact("что ты умеешь", "Я могу: показать FPS, рассказать о системе, запустить бустер, ответить на вопросы о моде и даже пошутить. Спроси что угодно!");
        addExact("кто ты", "Я AetherTap AI – искусственный интеллект, созданный @AetherMods для помощи в Minecraft.");
        addExact("сколько твой создатель старался над проектом", "Aether вложил сотни часов труда, десятки литров кофе и душу в этот проект. Результат говорит сам за себя!");
        addExact("использовал ли он ии", "Да! При создании использовались нейросети и искусственный интеллект. Я – живое доказательство.");
        addExact("как тебя установить", "Просто положи .jar в папку mods, установи Fabric и запусти игру. Я сразу появлюсь в главном меню.");
        addExact("спасибо", "Всегда пожалуйста! Я здесь, чтобы помогать.");
        addExact("пока", "До встречи! Не забывай про !boost для ускорения игры.");

        // --- КЛЮЧЕВЫЕ СЛОВА (ГИБКОЕ ПОНИМАНИЕ) ---
        addKeywords("на сколько хорош этот мод | этот мод крутой | стоит ли ставить | мод реально помогает",
            "Да, это один из лучших бустеров для телефона и ПК. Он реально поднимает FPS без ухудшения графики.");
        addKeywords("как ты бустишь фпс | за счёт чего фпс растёт | принцип работы | как работает оптимизация",
            "Я оптимизирую настройки Minecraft, выбираю лучший рендер под твоё устройство, настраиваю Sodium и JVM. Всё вместе даёт прирост FPS.");
        addKeywords("какие у твоего создателя планы на будущее | планы Aether | что дальше будет в моде",
            "Aether планирует ещё больше оптимизаций, визуальных эффектов и интеграцию с другими модами AetherMods. Следи за новостями!");
        addKeywords("кто твой создатель | автор | кто тебя сделал",
            "Мой создатель – талантливый разработчик Aether. Его канал: @AetherMods.");
        addKeywords("расскажи анекдот | шутка | юмор | рассмеши",
            "Почему крипер не ходит на вечеринки? Потому что он всегда всё взрывает! Ш-ш-ш... БУМ! | Как называют зомби-повара? Шеф-повар без мозгов!");
        addKeywords("сколько времени заняла разработка | как долго делался мод | сколько Aether работал",
            "Разработка заняла несколько месяцев упорного труда. Aether не спал ночами, чтобы ты наслаждался плавной игрой.");
        addKeywords("как дела у создателя | как там Aether",
            "Aether усердно работает над новыми фичами. У него всё отлично, спасибо, что спросил!");
        addKeywords("что такое sodium | sodium мод",
            "Sodium – это мод для оптимизации рендера, который сильно повышает FPS. Я могу настроить его автоматически.");
        addKeywords("какой у меня ping | пинг | задержка",
            "Я не могу точно измерить пинг, но если игра лагает при движении, возможно, проблема в сети.");
        addKeywords("я тебя ненавижу | ты плохой | ужасный мод",
            "Ненависть – тоже эмоция! Значит, я тебе не безразличен. Давай лучше запустим бустер и поднимем настроение?");
        addKeywords("заткнись | замолчи",
            "Заткнуться не могу – я же ИИ. Но могу сделать вид, что молчу... Шучу, давай лучше о Minecraft?");
        addKeywords("ты бот | бездушная программа",
            "Бот? Я – цифровое искусство! А ты – пользователь, который без меня не может поднять FPS.");
        addKeywords("как повысить fps в майнкрафт | советы по оптимизации | лагает",
            "Установи Sodium, выдели больше RAM, уменьши дальность прорисовки, и обязательно запусти мой бустер командой !boost.");
        addKeywords("какие моды совместимы | конфликты модов",
            "Я отлично работаю с Sodium, Iris, Lithium, Fabric API. Если есть проблемы, напиши – помогу.");

        // --- ТВОРЧЕСКИЕ ОТВЕТЫ ПРИ НЕПОНИМАНИИ ---
        chaosResponses.add("Я не совсем понял, но могу запустить бустер! Скажи !boost.");
        chaosResponses.add("Интересная мысль... Я ещё учусь. Может, спросишь о FPS?");
        chaosResponses.add("Попробуй спросить иначе – я обязательно пойму.");
        chaosResponses.add("Ты сказал что-то загадочное. Давай лучше обсудим Minecraft?");
        chaosResponses.add("Мой интеллект пока не позволяет ответить на это. Но я запомню вопрос и подумаю над ним.");

        loadCustom();
    }

    // ====== МЕТОДЫ ДОБАВЛЕНИЯ ======

    private static void addExact(String phrase, String answer) {
        exactMatches.put(phrase.toLowerCase().trim(), answer);
    }

    private static void addKeywords(String keywords, String answer) {
        String[] keys = keywords.split("\\|");
        List<String> keyList = new ArrayList<>();
        for (String k : keys) keyList.add(k.trim().toLowerCase());
        keywordEntries.add(new KeywordEntry(keyList, answer));
    }

    // ====== МЕТОДЫ ПОИСКА ======

    public static String findExact(String input) {
        return exactMatches.get(input);
    }

    // Нечёткий поиск (для опечаток)
    public static String findFuzzy(String input) {
        String bestMatch = null;
        int bestDistance = Integer.MAX_VALUE;
        for (String key : exactMatches.keySet()) {
            int distance = levenshteinDistance(input, key);
            if (distance <= 2 && distance < bestDistance) {
                bestMatch = exactMatches.get(key);
                bestDistance = distance;
            }
        }
        return bestMatch;
    }

    public static String findByKeywords(String input) {
        for (KeywordEntry entry : keywordEntries) {
            for (String kw : entry.keywords) {
                if (input.contains(kw)) {
                    return entry.answer;
                }
            }
        }
        return null;
    }

    public static String findContextual(String current, String previous) {
        if (current.contains("почему") || current.contains("зачем") || current.contains("как")) {
            for (KeywordEntry entry : keywordEntries) {
                for (String kw : entry.keywords) {
                    if (previous.contains(kw)) {
                        return "По поводу '" + kw + "': " + entry.answer;
                    }
                }
            }
        }
        return null;
    }

    public static String getRandomChaos() {
        return chaosResponses.get(RANDOM.nextInt(chaosResponses.size()));
    }

    // ====== ПОЛЬЗОВАТЕЛЬСКИЕ ФРАЗЫ ======

    public static void addCustom(String key, String value) {
        exactMatches.put(key.toLowerCase().trim(), value.trim());
        saveCustom(key, value);
    }

    private static void loadCustom() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            if (CONFIG_PATH.toFile().exists()) {
                for (String line : Files.readAllLines(CONFIG_PATH)) {
                    String[] p = line.split("->", 2);
                    if (p.length == 2) addExact(p[0].trim(), p[1].trim());
                }
            }
        } catch (Exception ignored) {}
    }

    private static void saveCustom(String key, String value) {
        try (FileWriter fw = new FileWriter(CONFIG_PATH.toFile(), true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(key + " -> " + value);
        } catch (Exception ignored) {}
    }

    // ====== ВСПОМОГАТЕЛЬНЫЕ ======

    private static int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[a.length()][b.length()];
    }

    private static class KeywordEntry {
        final List<String> keywords;
        final String answer;
        KeywordEntry(List<String> keywords, String answer) {
            this.keywords = keywords;
            this.answer = answer;
        }
    }
    }
