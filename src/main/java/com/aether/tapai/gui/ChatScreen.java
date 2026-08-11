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

    public ChatScreen() {  // <-- public
        super(Text.literal("AetherTap AI"));
        messages.add("AI: " + MindEngine.process("привет"));
    }

    @Override
    protected void init() {
        int inputY = this.height - 30;

        chatInput = new TextFieldWidget(textRenderer, 10, inputY, this.width - 100, 20, Text.literal("Сообщение..."));
        chatInput.setMaxLength(200);
        addSelectableChild(chatInput);
        setInitialFocus(chatInput);

        addDrawableChild(ButtonWidget.builder(Text.literal("▶"), btn -> {
            String msg = chatInput.getText().trim();
            if (!msg.isEmpty()) {
                messages.add("Вы: " + msg);
                messages.add("AI: " + MindEngine.process(msg));
                chatInput.setText("");
                scroll = Math.max(0, messages.size() - 10);
            }
        }).dimensions(this.width - 85, inputY, 30, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("⚡ BOOST"), btn -> {
            SmartBoost.runAll();
            messages.add("AI: " + SmartBoost.getLastResult());
        }).dimensions(this.width / 2 - 40, 35, 80, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("✕"), btn -> close())
            .dimensions(this.width - 20, 5, 15, 15).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        super.render(ctx, mouseX, mouseY, delta);
        int y = 60;
        for (int i = scroll; i < Math.min(messages.size(), scroll + 10); i++) {
            String msg = messages.get(i);
            int color = msg.startsWith("Вы:") ? 0xFF55FFFF : 0xFFFF55FF;
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
