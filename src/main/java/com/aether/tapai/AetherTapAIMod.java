package com.aether.tapai;

import com.aether.tapai.gui.TapAIScreen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AetherTapAIMod implements ModInitializer {
    public static final String MOD_ID = "aethertapai";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("🧠 AetherTap AI — Legendary FPS Booster & AI Assistant");

        KeyBinding chatKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.aethertapai.chat", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, "AetherTap AI"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (chatKey.wasPressed()) client.setScreen(new TapAIScreen());
        });

        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (screen instanceof TitleScreen) {
                Screens.getButtons(screen).add(
                    ButtonWidget.builder(Text.literal("🧠 Tap AI"), btn ->
                        client.setScreen(new TapAIScreen())
                    ).dimensions(10, 10, 80, 20).build()
                );
            }
        });
    }
}
