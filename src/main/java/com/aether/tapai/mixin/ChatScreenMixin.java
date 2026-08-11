package com.aether.tapai.mixin;

import com.aether.tapai.engine.MindEngine;
import com.aether.tapai.engine.SmartBoost;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void onSendMessage(String message, boolean addToHistory, CallbackInfo ci) {
        String msg = message.trim();
        if (msg.isEmpty()) return;

        // !boost
        if (msg.equalsIgnoreCase("!boost") || msg.equalsIgnoreCase("!буст")) {
            SmartBoost.runAll();
            SmartBoost.sendToChat();
            ci.cancel();
            return;
        }

        // !boost profile
        if (msg.toLowerCase().startsWith("!boost ")) {
            String[] parts = msg.split(" ", 2);
            if (parts.length == 2) {
                SmartBoost.runProfile(parts[1]);
                SmartBoost.sendToChat();
            }
            ci.cancel();
            return;
        }

        // !status
        if (msg.equalsIgnoreCase("!status") || msg.equalsIgnoreCase("!статус")) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal("§6[AetherTap]§f " + SmartBoost.getStatus()), false);
            }
            ci.cancel();
            return;
        }

        // !reset
        if (msg.equalsIgnoreCase("!reset") || msg.equalsIgnoreCase("!сброс")) {
            SmartBoost.resetAll();
            SmartBoost.sendToChat();
            ci.cancel();
            return;
        }

        // !ai <question>
        if (msg.toLowerCase().startsWith("!ai ")) {
            String question = msg.substring(4).trim();
            String answer = MindEngine.process(question);
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal("§6[AI]§f " + answer), false);
            }
            ci.cancel();
        }
    }
}
