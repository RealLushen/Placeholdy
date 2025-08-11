package de.einpallux.placeholdy.util;

import de.einpallux.placeholdy.Placeholdy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Centralized MiniMessage rendering and sending utility.
 */
public final class MessageService {

    private final Placeholdy plugin;
    private final MiniMessage miniMessage;

    public MessageService(@NotNull Placeholdy plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
    }

    public Component render(@Nullable String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }

        // First, apply Placeholdy placeholders like %placeholdy_key%
        final String replaced = plugin.getPlaceholderManager().replacePlaceholders(input);
        // Then, parse MiniMessage formatting
        return miniMessage.deserialize(replaced);
    }

    public void send(@NotNull CommandSender recipient, @Nullable String message) {
        recipient.sendMessage(render(message));
    }

    public void sendPlain(@NotNull CommandSender recipient, @Nullable String message) {
        recipient.sendMessage(message == null ? Component.empty() : Component.text(message));
    }
}


