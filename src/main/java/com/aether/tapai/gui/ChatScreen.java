package com.aether.tapai.gui;

import com.aether.tapai.engine.MindEngine;
import com.aether.tapai.engine.SmartBoost;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

public class ChatScreen extends Screen {
    private TextFieldWidget chatInput;
    private final List<String> messages = new ArrayList<>();
    private int scroll = 0;
    
    // 0 = чат, 1 = бустер
    private int currentTab = 0;

    public ChatScreen() {
        super(Text.literal("AetherTap AI"));
        messages.add("[AI] Привет! Я AetherTap AI. Спроси о FPS, рендерах или просто поболтай.");
    }

    @Override
    protected void init() {
        int tabY = 35;
        int inputY = this.height - 30;

        // ---- Вкладки ----
        addDrawableChild(ButtonWidget.builder(Text.literal("💬 Chat"), btn -> {
            currentTab = 0;
            clearChildren(); init();
        }).dimensions(this.width / 2 - 80, tabY, 60, 18).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("⚡ Boost"), btn -> {
            currentTab = 1;
            clearChildren(); init();
        }).dimensions(this.width / 2 + 20, tabY, 60, 18).build());

        // ---- Кнопка закрыть ----
        addDrawableChild(ButtonWidget.builder(Text.literal("✕"), btn -> close())
            .dimensions(this.width - 20, 5, 15, 15).build());

        // ---- Содержимое вкладок ----
        if (currentTab == 0) {
            // Поле ввода
            chatInput = new TextFieldWidget(textRenderer, 10, inputY, this.width - 100, 20, Text.literal("Напиши сообщение..."));
            chatInput.setMaxLength(200);
            addSelectableChild(chatInput);
            setInitialFocus(chatInput);

            // Кнопка отправить
            addDrawableChild(ButtonWidget.builder(Text.literal("▶"), btn -> {
                String msg = chatInput.getText().trim();
                if (!msg.isEmpty()) {
                    messages.add("[Вы] " + msg);
                    messages.add("[AI] " + MindEngine.process(msg));
                    chatInput.setText("");
                    scroll = Math.max(0, messages.size() - 10);
                }
            }).dimensions(this.width - 85, inputY, 30, 20).build());

        } else {
            // Вкладка Boost
            int cx = this.width / 2;
            addDrawableChild(ButtonWidget.builder(Text.literal("🚀 ЗАПУСТИТЬ БУСТЕР"), btn -> {
                SmartBoost.runAll();
                messages.add("[AI] " + SmartBoost.getLastResult());
            }).dimensions(cx - 80, this.height / 2, 160, 20).build());
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        super.render(ctx, mouseX, mouseY, delta);
        
        // Заголовок
        ctx.drawCenteredTextWithShadow(textRenderer, "AetherTap AI", this.width / 2, 10, 0xFFFFFFFF);

        // История сообщений
        int y = 60;
        for (int i = scroll; i < Math.min(messages.size(), scroll + 10); i++) {
            String msg = messages.get(i);
            int color = msg.startsWith("[Вы]") ? 0xFF55FFFF : 0xFFFFFFFF; // Голубой для пользователя, белый для AI
            ctx.drawTextWithShadow(textRenderer, msg, 10, y, color);
            y += 12;
        }
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double h, double v) {
        scroll = Math.max(0, Math.min(messages.size() - 10, scroll - (int)v));
        return true;
    }
}
