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
    private int currentTab = 0; // 0 = чат, 1 = буст

    // Размеры левой панели
    private static final int SIDEBAR_WIDTH = 50;
    private static final int SIDEBAR_X = 0;
    private static final int CONTENT_X = SIDEBAR_WIDTH + 4;
    private static final int CONTENT_WIDTH_OFFSET = SIDEBAR_WIDTH + 8;

    public ChatScreen() {
        super(Text.literal("AetherTap AI"));
        messages.add("§6[AI]§f Привет! Я AetherTap AI. Спроси о FPS, рендерах или просто поболтай.");
    }

    @Override
    protected void init() {
        // Левая панель (огородка)
        addDrawableChild(ButtonWidget.builder(Text.literal("💬"), btn -> {
            currentTab = 0;
            clearChildren(); init();
        }).dimensions(SIDEBAR_X + 5, 40, 40, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("⚡"), btn -> {
            currentTab = 1;
            clearChildren(); init();
        }).dimensions(SIDEBAR_X + 5, 65, 40, 20).build());

        // Кнопка закрыть
        addDrawableChild(ButtonWidget.builder(Text.literal("✕"), btn -> close())
            .dimensions(this.width - 20, 5, 15, 15).build());

        if (currentTab == 0) {
            // Поле ввода
            int inputY = this.height - 30;
            chatInput = new TextFieldWidget(textRenderer, CONTENT_X, inputY, this.width - CONTENT_WIDTH_OFFSET - 45, 20, Text.literal("Напиши сообщение..."));
            chatInput.setMaxLength(200);
            addSelectableChild(chatInput);
            setInitialFocus(chatInput);

            // Кнопка отправить
            addDrawableChild(ButtonWidget.builder(Text.literal("▶"), btn -> {
                String msg = chatInput.getText().trim();
                if (!msg.isEmpty()) {
                    messages.add("§b[Вы]§f " + msg);
                    messages.add("§6[AI]§f " + MindEngine.process(msg));
                    chatInput.setText("");
                    scroll = Math.max(0, messages.size() - 10);
                }
            }).dimensions(this.width - 40, inputY, 30, 20).build());
        } else {
            // Кнопка Boost
            int cx = this.width / 2 + SIDEBAR_WIDTH / 2;
            addDrawableChild(ButtonWidget.builder(Text.literal("🚀 ЗАПУСТИТЬ БУСТЕР"), btn -> {
                SmartBoost.runAll();
                messages.add("§6[AI]§f " + SmartBoost.getLastResult());
            }).dimensions(cx - 80, this.height / 2, 160, 20).build());
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Тёмный фон
        renderBackground(ctx, mouseX, mouseY, delta);

        // Левая панель (огородка)
        ctx.fill(SIDEBAR_X, 0, SIDEBAR_WIDTH, this.height, 0xFF1A1A2E);
        ctx.fill(SIDEBAR_WIDTH, 0, SIDEBAR_WIDTH + 1, this.height, 0x50FFB300); // разделитель

        // Заголовок в панели
        ctx.drawCenteredTextWithShadow(textRenderer, "AI", SIDEBAR_WIDTH / 2, 12, 0xFFFFD700);

        // Основная область
        if (currentTab == 0) {
            // История сообщений
            int y = 15;
            for (int i = scroll; i < Math.min(messages.size(), scroll + 12); i++) {
                String msg = messages.get(i);
                ctx.drawTextWithShadow(textRenderer, msg, CONTENT_X, y, 0xFFFFFFFF);
                y += 14;
            }
        } else {
            ctx.drawCenteredTextWithShadow(textRenderer, "Нажми кнопку для запуска бустера", this.width / 2, 30, 0xFFFFFFFF);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double h, double v) {
        if (mx > SIDEBAR_WIDTH) { // скролл только в области чата
            scroll = Math.max(0, Math.min(messages.size() - 12, scroll - (int)v));
        }
        return true;
    }
}
