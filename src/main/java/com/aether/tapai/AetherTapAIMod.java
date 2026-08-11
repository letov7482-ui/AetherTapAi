package com.aether.tapai;

import com.aether.tapai.engine.MindEngine;
import com.aether.tapai.engine.SmartBoost;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AetherTapAIMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("AetherTapAI");

    @Override
    public void onInitialize() {
        // Кнопка в главном меню для GUI
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen) {
                Screens.getButtons(screen).add(
                    ButtonWidget.builder(Text.literal("⚡ AetherTap"), btn -> {
                        client.setScreen(new BoostScreen());
                    }).dimensions(10, 10, 100, 20).build()
                );
            }
        });

        // Перехват сообщений в игровой чат
        ClientSendMessageEvents.ALLOW.register((message) -> {
            String msg = message.trim();
            if (msg.isEmpty()) return true;

            if (msg.equalsIgnoreCase("!boost") || msg.equalsIgnoreCase("!буст")) {
                SmartBoost.runAll();
                MinecraftClient.getInstance().player.sendMessage(
                    Text.literal("§6[AetherTap]§f " + SmartBoost.getLastResult()), false
                );
                return false;
            }

            if (msg.toLowerCase().startsWith("!ai ")) {
                String question = msg.substring(4).trim();
                String answer = MindEngine.process(question);
                MinecraftClient.getInstance().player.sendMessage(
                    Text.literal("§6[AI]§f " + answer), false
                );
                return false;
            }

            return true;
        });
    }

    // Простой GUI-экран с кнопкой Boost (можно расширить)
    private static class BoostScreen extends Screen {
        protected BoostScreen() {
            super(Text.literal("AetherTap"));
        }

        @Override
        protected void init() {
            int cx = this.width / 2;
            addDrawableChild(ButtonWidget.builder(Text.literal("🚀 ЗАПУСТИТЬ БУСТЕР"), btn -> {
                SmartBoost.runAll();
                // Показываем результат в чате и закрываем экран
                if (MinecraftClient.getInstance().player != null) {
                    MinecraftClient.getInstance().player.sendMessage(
                        Text.literal("§6[AetherTap]§f " + SmartBoost.getLastResult()), false
                    );
                }
                this.close();
            }).dimensions(cx - 80, this.height / 2, 160, 20).build());

            addDrawableChild(ButtonWidget.builder(Text.literal("Закрыть"), btn -> close())
                .dimensions(cx - 30, this.height / 2 + 25, 60, 20).build());
        }

        @Override
        public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
            ctx.fill(0, 0, this.width, this.height, 0xFF000000);
            ctx.drawCenteredTextWithShadow(textRenderer, "⚡ AetherTap AI ⚡", this.width / 2, 20, 0xFFFFD700);
            super.render(ctx, mouseX, mouseY, delta);
        }
    }
}
