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
    private int scroll = 0;
    private int currentTab = 0; // 0 – чат, 1 – буст

    // Декоративные элементы (как в вашем фирменном стиле)
    private final Random rand = new Random();
    private final float[] px = new float[25], py = new float[25], ps = new float[25];
    private long tickCount = 0;

    public ChatScreen() {
        super(Text.literal("AetherTap AI"));
        messages.add("AI|Привет! Я AetherTap AI. Спроси о FPS, рендерах или просто поболтай.");
        for (int i = 0; i < 25; i++) {
            px[i] = rand.nextFloat() * 1000;
            py[i] = rand.nextFloat() * 1000;
            ps[i] = 0.1f + rand.nextFloat() * 0.3f;
        }
    }

    @Override
    protected void init() {
        // Верхняя панель с вкладками
        int tabY = 8;
        addDrawableChild(ButtonWidget.builder(Text.literal("💬 Чат"), btn -> {
            currentTab = 0; clearChildren(); init();
        }).dimensions(this.width / 2 - 80, tabY, 60, 18).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("⚡ Буст"), btn -> {
            currentTab = 1; clearChildren(); init();
        }).dimensions(this.width / 2 + 20, tabY, 60, 18).build());

        // Кнопка закрытия
        addDrawableChild(ButtonWidget.builder(Text.literal("✕"), btn -> close())
                .dimensions(this.width - 20, 5, 15, 15).build());

        if (currentTab == 0) {
            // Поле ввода и кнопка отправки внизу
            int inputY = this.height - 30;
            chatInput = new TextFieldWidget(textRenderer, 10, inputY, this.width - 100, 20, Text.literal("Напишите сообщение..."));
            chatInput.setMaxLength(200);
            addSelectableChild(chatInput);
            addDrawableChild(chatInput);
            setInitialFocus(chatInput);

            addDrawableChild(ButtonWidget.builder(Text.literal("▶"), btn -> {
                String msg = chatInput.getText().trim();
                if (!msg.isEmpty()) {
                    messages.add("Вы|" + msg);
                    messages.add("AI|" + MindEngine.process(msg));
                    chatInput.setText("");
                    scroll = Math.max(0, messages.size() - 12);
                }
            }).dimensions(this.width - 85, inputY, 75, 20).build());
        } else {
            // Кнопка Boost по центру
            int cx = this.width / 2;
            addDrawableChild(ButtonWidget.builder(Text.literal("🚀 ЗАПУСТИТЬ БУСТЕР"), btn -> {
                SmartBoost.runAll();
                messages.add("AI|" + SmartBoost.getLastResult());
            }).dimensions(cx - 80, this.height / 2, 160, 20).build());
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // 1. Абсолютно чёрный фон для максимальной контрастности
        ctx.fill(0, 0, this.width, this.height, 0xFF000000);

        // 2. Лёгкая сетка (не мешает чтению)
        for (int x = 0; x < this.width; x += 40)
            for (int y = 0; y < this.height; y += 40)
                ctx.fill(x, y, x + 1, y + 1, 0xFF1A1A2E);

        // 3. Золотые частицы (очень тусклые, только для атмосферы)
        tickCount++;
        for (int i = 0; i < 25; i++) {
            py[i] -= ps[i];
            if (py[i] < 0) { py[i] = this.height; px[i] = rand.nextFloat() * this.width; }
            ctx.fill((int) px[i], (int) py[i], (int) px[i] + 2, (int) py[i] + 2, 0x33FFB300);
        }

        // 4. Заголовок
        ctx.drawCenteredTextWithShadow(textRenderer, "⚡ AetherTap AI ⚡", this.width / 2, 12, 0xFFFFD700);

        // 5. Разделительная линия
        ctx.fill(10, 30, this.width - 10, 31, 0x50FFB300);

        // 6. Сообщения
        if (currentTab == 0) {
            int y = 36;
            for (int i = scroll; i < Math.min(messages.size(), scroll + 12); i++) {
                String[] parts = messages.get(i).split("\\|", 2);
                String sender = parts[0];
                String text = parts[1];
                // Яркий жёлтый для AI, голубой для пользователя
                int color = sender.equals("AI") ? 0xFFFFD700 : 0xFF55FFFF;
                ctx.drawTextWithShadow(textRenderer, text, 10, y, color);
                y += 14;
            }
        } else {
            ctx.drawCenteredTextWithShadow(textRenderer, "Нажмите на кнопку для запуска бустера", this.width / 2, 40, 0xFFFFFFFF);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double h, double v) {
        scroll = Math.max(0, Math.min(messages.size() - 12, scroll - (int)v));
        return true;
    }
                    }
