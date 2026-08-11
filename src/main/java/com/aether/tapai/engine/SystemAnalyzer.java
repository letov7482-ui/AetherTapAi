package com.aether.tapai.engine;

public class SystemAnalyzer {
    public static String getDeviceType() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) return "Windows PC";
        if (os.contains("mac")) return "Mac";
        if (os.contains("linux")) {
            try { Class.forName("android.os.Build"); return "Android"; }
            catch (ClassNotFoundException e) { return "Linux PC"; }
        }
        return "Unknown";
    }

    public static String getFullReport() {
        return "Тип: " + getDeviceType() +
               "\nОС: " + System.getProperty("os.name") + " " + System.getProperty("os.version") +
               "\nJava: " + System.getProperty("java.version") +
               "\nЯдра: " + Runtime.getRuntime().availableProcessors() +
               "\nRAM: " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MB";
    }
}
