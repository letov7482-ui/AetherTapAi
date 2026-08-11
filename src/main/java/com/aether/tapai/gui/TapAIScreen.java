package com.aether.tapai.gui;

import com.aether.tapai.engine.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import java.util.*;

public class TapAIScreen extends Screen {
    private TextFieldWidget chatInput;
    private final List<String> chatHistory = new ArrayList<>();
    private int scrollOffset = 0;
    private static final int MAX_VISIBLE = 12;
    private final Random rand = new Random();
    private final float[] px = new float[25], py = new float[25], ps = new float[25];
    private String pendingQuestion = null;
    private long questionTime = 0;
    private static final long RESPONSE_DELAY = 1500;

    public TapAIScreen() {
        super(Text.literal("AetherTap AI"));
        chatHistory.add("ИИ: Привет! Я AetherTap AI. Спроси о FPS, рендерах, системе или нажми BOOST.");
        for (int i = 0; i < 25; i++) {
            px[i] = rand.nextFloat() * 1000; py[i] = rand.nextFloat() * 1000;
            ps[i] = 0.1f + rand.nextFloat() * 0.3f;
        }
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int inputY = this.height - 30;

        chatInput = new TextFieldWidget(textRenderer, 10, inputY, this.width - 100, 20, Text.literal("Спроси или скажи 'запомни'..."));
        chatInput.setMaxLength(200);
        addSelectableChild(chatInput);
        setInitialFocus(chatInput);

        addDrawableChild(ButtonWidget.builder(Text.literal("▶"), btn -> {
            String msg = chatInput.getText().trim();
            if (!msg.isEmpty()) {
                chatHistory.add("Вы: " + msg);
                chatHistory.add("ИИ: Думаю над ответом...");
                pendingQuestion = msg;
                questionTime = System.currentTimeMillis();
                chatInput.setText("");
                scrollOffset = Math.max(0, chatHistory.size() - MAX_VISIBLE);
            }
        }).dimensions(this.width - 85, inputY, 30, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("⚡ BOOST"), btn -> {
            SmartBoost.runAll();
            chatHistory.add("ИИ: " + SmartBoost.getLastResult());
        }).dimensions(cx - 40, 35, 80, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("📋 Копировать"), btn -> {
            if (!chatHistory.isEmpty()) {
                String last = chatHistory.get(chatHistory.size() - 1);
                MinecraftClient.getInstance().keyboard.setClipboard(last);
            }
        }).dimensions(this.width - 70, 5, 65, 15).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Очистить"), btn -> {
            chatHistory.clear(); scrollOffset = 0;
            chatHistory.add("ИИ: Чат очищен.");
            pendingQuestion = null;
        }).dimensions(this.width - 140, 5, 65, 15).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("✕"), btn -> close())
            .dimensions(this.width - 20, 5, 15, 15).build());
    }

    @Override
    public void tick() {
        super.tick();
        if (pendingQuestion != null && System.currentTimeMillis() - questionTime >= RESPONSE_DELAY) {
            String response = MindEngine.process(pendingQuestion);
            chatHistory.set(chatHistory.size() - 1, "ИИ: " + response);
            pendingQuestion = null;
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        for (int x = 0; x < width; x += 40) for (int y = 0; y < height; y += 40)
            ctx.fill(x, y, x + 1, y + 1, 0xFF1A1A2E);
        for (int i = 0; i < 25; i++) {
            py[i] -= ps[i];
            if (py[i] < 0) { py[i] = height; px[i] = rand.nextFloat() * width; }
            ctx.fill((int) px[i], (int) py[i], (int) px[i] + 1, (int) py[i] + 1, 0x55FFB300);
        }
        ctx.drawCenteredTextWithShadow(textRenderer, "🧠 AetherTap AI", this.width / 2, 8, 0xFFFFD700);
        int y = 60;
        for (int i = scrollOffset; i < Math.min(chatHistory.size(), scrollOffset + MAX_VISIBLE); i++) {
            String msg = chatHistory.get(i);
            int color = msg.startsWith("Вы:") ? 0xFF55FFFF : 0xFFFF55FF;
            ctx.drawTextWithShadow(textRenderer, msg, 10, y, color);
            y += 12;
        }
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double h, double v) {
        scrollOffset = Math.max(0, Math.min(chatHistory.size() - MAX_VISIBLE, scrollOffset - (int)v));
        return true;
    }
                                }
