package de.einpallux.placeholdy.commands;

import de.einpallux.placeholdy.Placeholdy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PlaceholdyCommand implements CommandExecutor, TabCompleter {

    private final Placeholdy plugin;

    public PlaceholdyCommand(Placeholdy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("placeholdy.admin")) {
            plugin.getMessageService().send(sender, "<red>You don't have permission to use this command!</red>");
            return true;
        }

        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reload();
                plugin.getMessageService().send(sender, "<green>Placeholdy has been reloaded successfully!</green>");
                break;

            case "list":
                listPlaceholders(sender);
                break;

            case "set":
                if (args.length < 3) {
                    plugin.getMessageService().send(sender, "<red>Usage: /placeholdy set <placeholder> <value></red>");
                    return true;
                }
                setPlaceholder(sender, args[1], String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
                break;

            case "remove":
                if (args.length < 2) {
                    plugin.getMessageService().send(sender, "<red>Usage: /placeholdy remove <placeholder></red>");
                    return true;
                }
                removePlaceholder(sender, args[1]);
                break;

            case "test":
                if (args.length < 2) {
                    plugin.getMessageService().send(sender, "<red>Usage: /placeholdy test <text with placeholders></red>");
                    return true;
                }
                testPlaceholder(sender, String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                break;

            default:
                sendHelpMessage(sender);
                break;
        }

        return true;
    }

    private void sendHelpMessage(CommandSender sender) {
        plugin.getMessageService().send(sender, "<gold>=== Placeholdy Commands ===</gold>");
        plugin.getMessageService().send(sender, "<yellow>/placeholdy reload</yellow><gray> - Reload the plugin</gray>");
        plugin.getMessageService().send(sender, "<yellow>/placeholdy list</yellow><gray> - List all placeholders</gray>");
        plugin.getMessageService().send(sender, "<yellow>/placeholdy set <placeholder> <value></yellow><gray> - Set a placeholder</gray>");
        plugin.getMessageService().send(sender, "<yellow>/placeholdy remove <placeholder></yellow><gray> - Remove a placeholder</gray>");
        plugin.getMessageService().send(sender, "<yellow>/placeholdy test <text></yellow><gray> - Test placeholder replacement</gray>");
        plugin.getMessageService().send(sender, "<gray>Note: With PlaceholderAPI installed, use %placeholdy_<name>% in other plugins</gray>");
    }

    private void listPlaceholders(CommandSender sender) {
        Map<String, String> placeholders = plugin.getPlaceholderManager().getAllPlaceholders();

        if (placeholders.isEmpty()) {
            plugin.getMessageService().send(sender, "<yellow>No placeholders configured!</yellow>");
            return;
        }

        plugin.getMessageService().send(sender, "<gold>=== Configured Placeholders ===</gold>");
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String line = "<yellow>%placeholdy_" + entry.getKey() + "%</yellow><gray> -> </gray><green>" + entry.getValue() + "</green>";
            plugin.getMessageService().send(sender, line);
        }
    }

    private void setPlaceholder(CommandSender sender, String key, String value) {
        plugin.getPlaceholderManager().addPlaceholder(key, value);
        plugin.getMessageService().send(sender, "<green>Placeholder <yellow>%placeholdy_" + key + "%</yellow> has been set to: <white>" + value + "</white></green>");
    }

    private void removePlaceholder(CommandSender sender, String key) {
        if (plugin.getPlaceholderManager().hasPlaceholder(key)) {
            plugin.getPlaceholderManager().removePlaceholder(key);
            plugin.getMessageService().send(sender, "<green>Placeholder <yellow>%placeholdy_" + key + "%</yellow> has been removed!</green>");
        } else {
            plugin.getMessageService().send(sender, "<red>Placeholder <yellow>%placeholdy_" + key + "%</yellow> does not exist!</red>");
        }
    }

    private void testPlaceholder(CommandSender sender, String text) {
        String result = plugin.getPlaceholderManager().replacePlaceholders(text);
        plugin.getMessageService().send(sender, "<gold>Original: </gold><white>" + text + "</white>");
        // Show MiniMessage-parsed result too
        plugin.getMessageService().send(sender, "<gold>Result: </gold>" + result);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("reload", "list", "set", "remove", "test"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            completions.addAll(plugin.getPlaceholderManager().getAllPlaceholders().keySet());
        }

        return completions;
    }
}