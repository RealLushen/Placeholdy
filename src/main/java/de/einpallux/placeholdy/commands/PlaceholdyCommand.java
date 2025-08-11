package de.einpallux.placeholdy.commands;

import de.einpallux.placeholdy.Placeholdy;
import org.bukkit.ChatColor;
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
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reload();
                sender.sendMessage(ChatColor.GREEN + "Placeholdy has been reloaded successfully!");
                break;

            case "list":
                listPlaceholders(sender);
                break;

            case "set":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /placeholdy set <placeholder> <value>");
                    return true;
                }
                setPlaceholder(sender, args[1], String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
                break;

            case "remove":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /placeholdy remove <placeholder>");
                    return true;
                }
                removePlaceholder(sender, args[1]);
                break;

            case "test":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /placeholdy test <text with placeholders>");
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
        sender.sendMessage(ChatColor.GOLD + "=== Placeholdy Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/placeholdy reload" + ChatColor.WHITE + " - Reload the plugin");
        sender.sendMessage(ChatColor.YELLOW + "/placeholdy list" + ChatColor.WHITE + " - List all placeholders");
        sender.sendMessage(ChatColor.YELLOW + "/placeholdy set <placeholder> <value>" + ChatColor.WHITE + " - Set a placeholder");
        sender.sendMessage(ChatColor.YELLOW + "/placeholdy remove <placeholder>" + ChatColor.WHITE + " - Remove a placeholder");
        sender.sendMessage(ChatColor.YELLOW + "/placeholdy test <text>" + ChatColor.WHITE + " - Test placeholder replacement");
        sender.sendMessage(ChatColor.GRAY + "Note: With PlaceholderAPI installed, use %placeholdy_<name>% in other plugins");
    }

    private void listPlaceholders(CommandSender sender) {
        Map<String, String> placeholders = plugin.getPlaceholderManager().getAllPlaceholders();

        if (placeholders.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No placeholders configured!");
            return;
        }

        sender.sendMessage(ChatColor.GOLD + "=== Configured Placeholders ===");
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            sender.sendMessage(ChatColor.YELLOW + "%placeholdy_" + entry.getKey() + "%" +
                    ChatColor.WHITE + " -> " + ChatColor.GREEN + entry.getValue());
        }
    }

    private void setPlaceholder(CommandSender sender, String key, String value) {
        plugin.getPlaceholderManager().addPlaceholder(key, value);
        sender.sendMessage(ChatColor.GREEN + "Placeholder " + ChatColor.YELLOW + "%placeholdy_" + key + "%" +
                ChatColor.GREEN + " has been set to: " + ChatColor.WHITE + value);
    }

    private void removePlaceholder(CommandSender sender, String key) {
        if (plugin.getPlaceholderManager().hasPlaceholder(key)) {
            plugin.getPlaceholderManager().removePlaceholder(key);
            sender.sendMessage(ChatColor.GREEN + "Placeholder " + ChatColor.YELLOW + "%placeholdy_" + key + "%" +
                    ChatColor.GREEN + " has been removed!");
        } else {
            sender.sendMessage(ChatColor.RED + "Placeholder " + ChatColor.YELLOW + "%placeholdy_" + key + "%" +
                    ChatColor.RED + " does not exist!");
        }
    }

    private void testPlaceholder(CommandSender sender, String text) {
        String result = plugin.getPlaceholderManager().replacePlaceholders(text);
        sender.sendMessage(ChatColor.GOLD + "Original: " + ChatColor.WHITE + text);
        sender.sendMessage(ChatColor.GOLD + "Result: " + ChatColor.WHITE + result);
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