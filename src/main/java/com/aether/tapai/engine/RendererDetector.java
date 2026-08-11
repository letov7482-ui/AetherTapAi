package com.aether.tapai.engine;

import java.io.*;
import java.util.*;

public class RendererDetector {
    private static final String[] KNOWN = {"Vulkan", "Zink", "ANGLE", "LTW", "GL4ES", "MobileGLUES", "HolyGL4ES", "FasterGL4ES"};
    private static final String[] PATHS = {
        "/storage/emulated/0/Android/data/net.kdt.pojavlaunch/files",
        "/storage/emulated/0/Android/data/io.github.fold.launcher/files",
        "/storage/emulated/0/Android/data/com.movtery.zalithlauncher/files"
    };

    public static List<String> getAvailableRenderers() {
        Set<String> found = new LinkedHashSet<>();
        for (String p : PATHS) scan(new File(p), found, 0);
        String env = System.getenv("POJAV_RENDERER");
        if (env != null) found.add(env);
        if (found.isEmpty()) found.add("GL4ES");
        return new ArrayList<>(found);
    }

    private static void scan(File dir, Set<String> set, int depth) {
        if (depth > 3 || !dir.exists()) return;
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            for (String r : KNOWN) if (f.getName().toLowerCase().contains(r.toLowerCase())) set.add(r);
            if (f.isDirectory()) scan(f, set, depth + 1);
        }
    }

    public static String findBest() {
        return getAvailableRenderers().contains("Vulkan") ? "Vulkan" : "Zink";
    }
}
