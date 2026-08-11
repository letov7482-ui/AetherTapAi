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
        messages.add("§6[AI]§f Привет! Я AetherTap AI. Спроси о FPS, рендерах или просто поболтай.");
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
            // ОБЯЗАТЕЛЬНО добавить и как selectable, и как drawable
            addSelectableChild(chatInput);
            addDrawableChild(chatInput);
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
            }).dimensions(this.width - 45, inputY, 35, 20).build());
        } else {
            int cx = this.width / 2 + 25;
            addDrawableChild(ButtonWidget.builder(Text.literal("🚀 ЗАПУСТИТЬ БУСТЕР"), btn -> {
                SmartBoost.runAll();
                messages.add("§6[AI]§f " + SmartBoost.getLastResult());
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
            int y = 15;
            for (int i = scroll; i < Math.min(messages.size(), scroll + 12); i++) {
                ctx.drawTextWithShadow(textRenderer, messages.get(i), 54, y, 0xFFFFFFFF);
                y += 14;
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
