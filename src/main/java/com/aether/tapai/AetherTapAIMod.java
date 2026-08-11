package com.aether.tapai;

import com.aether.tapai.engine.MindEngine;
import com.aether.tapai.engine.SmartBoost;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AetherTapAIMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("AetherTapAI");

    @Override
    public void onInitialize() {
        // Кнопка в главном меню
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen) {
                Screens.getButtons(screen).add(
                    ButtonWidget.builder(Text.literal("⚡ AetherTap"), btn -> {
                        SmartBoost.runAll();
                        SmartBoost.sendToChat();
                    }).dimensions(10, 10, 100, 20).build()
                );
            }
        });

        // Перехват сообщений чата
        ClientSendMessageEvents.ALLOW.register((message) -> {
            String msg = message.trim();
            if (msg.isEmpty()) return true;

            // Бустер
            if (msg.equalsIgnoreCase("!boost") || msg.equalsIgnoreCase("!буст")) {
                SmartBoost.runAll();
                SmartBoost.sendToChat();
                return false;
            }

            // Профили
            if (msg.toLowerCase().startsWith("!boost ")) {
                String[] parts = msg.split(" ", 2);
                if (parts.length == 2) {
                    SmartBoost.runProfile(parts[1]);
                    SmartBoost.sendToChat();
                }
                return false;
            }

            // Статус
            if (msg.equalsIgnoreCase("!status") || msg.equalsIgnoreCase("!статус")) {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("§6[AetherTap]§f " + SmartBoost.getStatus()), false);
                }
                return false;
            }

            // Сброс
            if (msg.equalsIgnoreCase("!reset") || msg.equalsIgnoreCase("!сброс")) {
                SmartBoost.resetAll();
                SmartBoost.sendToChat();
                return false;
            }

            // ИИ
            if (msg.toLowerCase().startsWith("!ai ")) {
                String question = msg.substring(4).trim();
                String answer = MindEngine.process(question);
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("§6[AI]§f " + answer), false);
                }
                return false;
            }

            return true;
        });
    }
}
