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
    private int currentTab = 0;

    private static final int SIDEBAR_WIDTH = 50;

    public ChatScreen() {
        super(Text.literal("AetherTap AI"));
        messages.add("AI|Привет! Я AetherTap AI. Спроси о FPS, рендерах или просто поболтай.");
    }

    @Override
    protected void init() {
        // Левая панель
        addDrawableChild(ButtonWidget.builder(Text.literal("💬"), btn -> {
            currentTab = 0; clearChildren(); init();
        }).dimensions(5, 40, 40, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("⚡"), btn -> {
            currentTab = 1; clearChildren(); init();
        }).dimensions(5, 65, 40, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("✕"), btn -> close())
            .dimensions(this.width - 20, 5, 15, 15).build());

        if (currentTab == 0) {
            int inputY = this.height - 30;
            chatInput = new TextFieldWidget(textRenderer, 54, inputY, this.width - 104, 20, Text.literal("Напиши сообщение..."));
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
                    scroll = Math.max(0, messages.size() - 10);
                }
            }).dimensions(this.width - 45, inputY, 35, 20).build());
        } else {
            int cx = this.width / 2 + 25;
            addDrawableChild(ButtonWidget.builder(Text.literal("🚀 ЗАПУСТИТЬ БУСТЕР"), btn -> {
                SmartBoost.runAll();
                messages.add("AI|" + SmartBoost.getLastResult());
            }).dimensions(cx - 80, this.height / 2, 160, 20).build());
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        // Левая панель
        ctx.fill(0, 0, SIDEBAR_WIDTH, this.height, 0xFF1A1A2E);
        ctx.fill(SIDEBAR_WIDTH, 0, SIDEBAR_WIDTH + 1, this.height, 0x50FFB300);
        ctx.drawCenteredTextWithShadow(textRenderer, "AI", 25, 12, 0xFFFFD700);

        if (currentTab == 0) {
            int y = 12;
            for (int i = scroll; i < Math.min(messages.size(), scroll + 12); i++) {
                String[] parts = messages.get(i).split("\\|", 2);
                String sender = parts[0];
                String text = parts[1];

                int textWidth = textRenderer.getWidth(text);
                int bubbleWidth = textWidth + 16;
                int bubbleX, textX;
                int bubbleY = y - 1;

                if (sender.equals("Вы")) {
                    // Голубое облачко пользователя (справа)
                    bubbleX = this.width - bubbleWidth - 12;
                    textX = bubbleX + 8;
                    ctx.fill(bubbleX, bubbleY, bubbleX + bubbleWidth, bubbleY + 14, 0xFF4282CC);
                    // обводка
                    ctx.fill(bubbleX, bubbleY, bubbleX + bubbleWidth, bubbleY + 1, 0xFFFFFFFF);
                    ctx.fill(bubbleX, bubbleY + 13, bubbleX + bubbleWidth, bubbleY + 14, 0xFFFFFFFF);
                    ctx.fill(bubbleX, bubbleY, bubbleX + 1, bubbleY + 14, 0xFFFFFFFF);
                    ctx.fill(bubbleX + bubbleWidth - 1, bubbleY, bubbleX + bubbleWidth, bubbleY + 14, 0xFFFFFFFF);
                    ctx.drawTextWithShadow(textRenderer, text, textX, y, 0xFFFFFFFF);
                } else {
                    // Золотистое облачко AI (слева)
                    bubbleX = 54;
                    textX = bubbleX + 8;
                    ctx.fill(bubbleX, bubbleY, bubbleX + bubbleWidth, bubbleY + 14, 0xFF4D3B1F);
                    // обводка
                    ctx.fill(bubbleX, bubbleY, bubbleX + bubbleWidth, bubbleY + 1, 0xFFFFD700);
                    ctx.fill(bubbleX, bubbleY + 13, bubbleX + bubbleWidth, bubbleY + 14, 0xFFFFD700);
                    ctx.fill(bubbleX, bubbleY, bubbleX + 1, bubbleY + 14, 0xFFFFD700);
                    ctx.fill(bubbleX + bubbleWidth - 1, bubbleY, bubbleX + bubbleWidth, bubbleY + 14, 0xFFFFD700);
                    ctx.drawTextWithShadow(textRenderer, text, textX, y, 0xFFFFFFFF);
                }
                y += 16;
            }
        } else {
            ctx.drawCenteredTextWithShadow(textRenderer, "Нажми кнопку для запуска бустера", this.width / 2 + 25, 30, 0xFFFFFFFF);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double h, double v) {
        if (mx > SIDEBAR_WIDTH) {
            scroll = Math.max(0, Math.min(messages.size() - 12, scroll - (int)v));
        }
        return true;
    }
}
