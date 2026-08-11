package com.aether.tapai;

import com.aether.tapai.gui.ChatScreen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AetherTapAIMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("AetherTapAI");

    @Override
    public void onInitialize() {
        LOGGER.info("🧠 AetherTap AI starting...");
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen) {
                Screens.getButtons(screen).add(
                    ButtonWidget.builder(Text.literal("⚡ AetherTap"), btn -> {
                        client.setScreen(new ChatScreen());
                    }).dimensions(10, 10, 100, 20).build()
                );
            }
        });
    }
}
