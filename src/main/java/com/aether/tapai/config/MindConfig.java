package com.aether.tapai.config;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MindConfig {
    private static final Path CONFIG_PATH = Paths.get(System.getProperty("user.dir"), "config/aethertap_memory.txt");
    private static final Map<String, String> exactMatches = new LinkedHashMap<>();   // точные фразы
    private static final List<KeywordEntry> keywordEntries = new ArrayList<>();      // ключевые слова
    private static final List<String> chaosResponses = new ArrayList<>();            // творческие ответы
    private static final Random RANDOM = new Random();

    static {
        // --- Точные совпадения (приоритет) ---
        addExact("привет", "Привет! Я AetherTap AI. Спроси о чём угодно.");
        addExact("как дела", "У меня всё отлично! Я же ИИ, мне не нужен сон. А у тебя?");
        addExact("что ты умеешь", "Я могу показать FPS, рассказать о системе, запустить бустер, ответить на вопросы о моде и даже пошутить.");
        addExact("кто ты", "Я AetherTap AI – умный помощник и бустер FPS, созданный @AetherMods.");

        // --- Ключевые слова → ответы (гибкое понимание) ---
        addKeywords("на сколько хорош этот мод | этот мод хорош | мод крутой | стоит ли ставить",
            "Этот мод – один из лучших бустеров для телефона и ПК. Он реально поднимает FPS и не портит графику.");
        addKeywords("как ты бустишь фпс | за счёт чего фпс растёт | как работает бустер",
            "Я оптимизирую настройки игры, выбираю лучший рендер, настраиваю Sodium и JVM. Всё вместе даёт прирост FPS.");
        addKeywords("какие у твоего создателя планы на будущее | планы создателя | что дальше",
            "Aether планирует добавить ещё больше оптимизаций, визуальных эффектов и интеграцию с другими модами. Следи за @AetherMods!");
        addKeywords("какой у меня fps | покажи fps | сколько фпс",
            "Секунду, проверяю... Скажи '!ai фпс' ещё раз, я покажу точную цифру.");
        addKeywords("как тебя установить | установка мода | как поставить",
            "Просто закинь .jar файл в папку mods, установи Fabric и запусти игру.");
        addKeywords("кто твой создатель | кто тебя сделал | автор мода",
            "Мой создатель – талантливый разработчик Aether. Его канал: @AetherMods");
        addKeywords("расскажи анекдот | шутка | юмор",
            "Почему крипер не ходит на вечеринки? Потому что он всегда всё взрывает! Ш-ш-ш... БУМ!");
        addKeywords("как дела у создателя | как там Aether",
            "Aether усердно работает над новыми фичами. У него всё отлично, спасибо, что спросил!");
        addKeywords("что такое sodium | sodium мод",
            "Sodium – это мод для оптимизации рендера, который значительно повышает FPS. Я могу настроить его автоматически.");
        addKeywords("какой у меня ping | пинг",
            "Я не могу точно измерить пинг, но если игра лагает при движении, возможно, проблема в сети.");
        addKeywords("что ты делаешь | чем занимаешься",
            "Я всегда здесь, жду твоих вопросов и готов ускорить твой Minecraft.");
        addKeywords("пока | до свидания",
            "До встречи! Не забывай про !boost.");

        // --- Творческие ответы на случай непонятого ---
        chaosResponses.add("Я не совсем понял, но могу запустить бустер! Скажи !boost.");
        chaosResponses.add("Интересная мысль... Я ещё учусь. Может, спросишь о FPS?");
        chaosResponses.add("Попробуй спросить иначе, я постараюсь понять.");
        chaosResponses.add("Ты сказал что-то загадочное. Давай лучше обсудим Minecraft.");

        loadCustom();
    }

    // --- Методы добавления ---
    private static void addExact(String phrase, String answer) {
        exactMatches.put(phrase.toLowerCase().trim(), answer);
    }

    private static void addKeywords(String keywords, String answer) {
        String[] keys = keywords.split("\\|");
        keywordEntries.add(new KeywordEntry(
            Arrays.stream(keys).map(String::trim).map(String::toLowerCase).toList(),
            answer
        ));
    }

    // --- Поиск ---
    public static String findExact(String input) {
        return exactMatches.get(input);
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

    public static String getRandomChaos() {
        return chaosResponses.get(RANDOM.nextInt(chaosResponses.size()));
    }

    // --- Пользовательские фразы (из файла) ---
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

    // Внутренний класс для хранения ключевых слов
    private static class KeywordEntry {
        final List<String> keywords;
        final String answer;

        KeywordEntry(List<String> keywords, String answer) {
            this.keywords = keywords;
            this.answer = answer;
        }
    }
            }
