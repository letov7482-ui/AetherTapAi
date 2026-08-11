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

        // Перехват сообщений чата (Fabric 1.21.4)
        ClientSendMessageEvents.SEND.register((message, context) -> {
            String msg = message.trim();
            if (msg.isEmpty()) return;

            // Бустер
            if (msg.equalsIgnoreCase("!boost") || msg.equalsIgnoreCase("!буст")) {
                SmartBoost.runAll();
                SmartBoost.sendToChat();
                context.cancel(); // не отправляем на сервер
                return;
            }

            // Профили
            if (msg.toLowerCase().startsWith("!boost ")) {
                String[] parts = msg.split(" ", 2);
                if (parts.length == 2) {
                    SmartBoost.runProfile(parts[1]);
                    SmartBoost.sendToChat();
                }
                context.cancel();
                return;
            }

            // Статус
            if (msg.equalsIgnoreCase("!status") || msg.equalsIgnoreCase("!статус")) {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("§6[AetherTap]§f " + SmartBoost.getStatus()), false);
                }
                context.cancel();
                return;
            }

            // Сброс
            if (msg.equalsIgnoreCase("!reset") || msg.equalsIgnoreCase("!сброс")) {
                SmartBoost.resetAll();
                SmartBoost.sendToChat();
                context.cancel();
                return;
            }

            // ИИ
            if (msg.toLowerCase().startsWith("!ai ")) {
                String question = msg.substring(4).trim();
                String answer = MindEngine.process(question);
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("§6[AI]§f " + answer), false);
                }
                context.cancel();
            }
        });
    }
}
