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
import java.util.Random;

public class ChatScreen extends Screen {
    private TextFieldWidget chatInput;
    private final List<String> messages = new ArrayList<>();
    private final List<Long> messageTimes = new ArrayList<>(); // время появления
    private int scroll = 0;
    private int currentTab = 0;

    // Для фона
    private final Random rand = new Random();
    private final float[] px = new float[30], py = new float[30], ps = new float[30];
    private long tickCount = 0;

    public ChatScreen() {
        super(Text.literal("AetherTap AI"));
        addMessage("AI", "Привет! Я AetherTap AI. Спроси о FPS, рендерах или просто поболтай.");
        for (int i = 0; i < 30; i++) {
            px[i] = rand.nextFloat() * 1000;
            py[i] = rand.nextFloat() * 1000;
            ps[i] = 0.1f + rand.nextFloat() * 0.3f;
        }
    }

    private void addMessage(String sender, String text) {
        messages.add(sender + "|" + text);
        messageTimes.add(System.currentTimeMillis());
    }

    @Override
    protected void init() {
        int tabY = 35;
        int inputY = this.height - 30;

        // Вкладки
        addDrawableChild(ButtonWidget.builder(Text.literal("💬 Чат"), btn -> {
            currentTab = 0; clearChildren(); init();
        }).dimensions(this.width / 2 - 80, tabY, 60, 18).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("⚡ Буст"), btn -> {
            currentTab = 1; clearChildren(); init();
        }).dimensions(this.width / 2 + 20, tabY, 60, 18).build());

        // Закрыть
        addDrawableChild(ButtonWidget.builder(Text.literal("✕"), btn -> close())
            .dimensions(this.width - 20, 5, 15, 15).build());

        if (currentTab == 0) {
            chatInput = new TextFieldWidget(textRenderer, 10, inputY, this.width - 100, 20, Text.literal("Напиши сообщение..."));
            chatInput.setMaxLength(200);
            addSelectableChild(chatInput);
            setInitialFocus(chatInput);

            addDrawableChild(ButtonWidget.builder(Text.literal("▶"), btn -> {
                String msg = chatInput.getText().trim();
                if (!msg.isEmpty()) {
                    addMessage("Вы", msg);
                    addMessage("AI", MindEngine.process(msg));
                    chatInput.setText("");
                    scroll = Math.max(0, messages.size() - 10);
                }
            }).dimensions(this.width - 85, inputY, 30, 20).build());
        } else {
            int cx = this.width / 2;
            addDrawableChild(ButtonWidget.builder(Text.literal("🚀 ЗАПУСТИТЬ БУСТЕР"), btn -> {
                SmartBoost.runAll();
                addMessage("AI", SmartBoost.getLastResult());
            }).dimensions(cx - 80, this.height / 2, 160, 20).build());
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        for (int x = 0; x < width; x += 40)
            for (int y = 0; y < height; y += 40)
                ctx.fill(x, y, x + 1, y + 1, 0xFF1A1A2E);

        tickCount++;
        for (int i = 0; i < 30; i++) {
            py[i] -= ps[i];
            if (py[i] < 0) { py[i] = height; px[i] = rand.nextFloat() * width; }
            float alpha = 0.3f + (float) Math.sin(tickCount * 0.05 + i) * 0.2f;
            int color = ((int)(alpha * 255) << 24) | 0x00FFB300;
            ctx.fill((int) px[i], (int) py[i], (int) px[i] + 2, (int) py[i] + 2, color);
        }

        ctx.drawCenteredTextWithShadow(textRenderer, "⚡ AetherTap AI ⚡", this.width / 2, 10, 0xFFFFD700);
        ctx.fill(10, 55, this.width - 10, 56, 0x50FFB300);

        // Облачка сообщений
        int y = 62;
        long now = System.currentTimeMillis();
        for (int i = scroll; i < Math.min(messages.size(), scroll + 10); i++) {
            String[] parts = messages.get(i).split("\\|", 2);
            String sender = parts[0];
            String text = parts[1];

            // Анимация появления (плавное увеличение прозрачности)
            long elapsed = now - messageTimes.get(i);
            float alpha = Math.min(1.0f, elapsed / 300.0f); // 300 мс анимация
            int alphaInt = (int)(alpha * 255);

            int textWidth = textRenderer.getWidth(text);
            int bubbleWidth = textWidth + 16;
            int bubbleHeight = 16;
            int bubbleX, bubbleY = y - 2;
            int textX;

            if (sender.equals("Вы")) {
                // Голубое облачко справа
                bubbleX = this.width - 10 - bubbleWidth;
                textX = bubbleX + 8;
                int bgColor = (alphaInt << 24) | 0x004282CC; // голубой
                ctx.fill(bubbleX, bubbleY, bubbleX + bubbleWidth, bubbleY + bubbleHeight, bgColor);
                // Обводка
                ctx.fill(bubbleX, bubbleY, bubbleX + bubbleWidth, bubbleY + 1, 0x80FFFFFF);
                ctx.fill(bubbleX, bubbleY + bubbleHeight - 1, bubbleX + bubbleWidth, bubbleY + bubbleHeight, 0x80FFFFFF);
                ctx.fill(bubbleX, bubbleY, bubbleX + 1, bubbleY + bubbleHeight, 0x80FFFFFF);
                ctx.fill(bubbleX + bubbleWidth - 1, bubbleY, bubbleX + bubbleWidth, bubbleY + bubbleHeight, 0x80FFFFFF);
            } else {
                // Золотистое облачко слева
                bubbleX = 10;
                textX = bubbleX + 8;
                int bgColor = (alphaInt << 24) | 0x004D3B1F; // тёмно-золотой
                ctx.fill(bubbleX, bubbleY, bubbleX + bubbleWidth, bubbleY + bubbleHeight, bgColor);
                // Обводка
                ctx.fill(bubbleX, bubbleY, bubbleX + bubbleWidth, bubbleY + 1, 0x80FFB300);
                ctx.fill(bubbleX, bubbleY + bubbleHeight - 1, bubbleX + bubbleWidth, bubbleY + bubbleHeight, 0x80FFB300);
                ctx.fill(bubbleX, bubbleY, bubbleX + 1, bubbleY + bubbleHeight, 0x80FFB300);
                ctx.fill(bubbleX + bubbleWidth - 1, bubbleY, bubbleX + bubbleWidth, bubbleY + bubbleHeight, 0x80FFB300);
            }

            int textColor = (alphaInt << 24) | 0x00FFFFFF;
            ctx.drawTextWithShadow(textRenderer, text, textX, y, textColor);
            y += 18;
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double h, double v) {
        scroll = Math.max(0, Math.min(messages.size() - 10, scroll - (int)v));
        return true;
    }
                         }
