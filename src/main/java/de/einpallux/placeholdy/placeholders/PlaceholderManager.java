package de.einpallux.placeholdy.placeholders;

import de.einpallux.placeholdy.Placeholdy;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderManager {

    private final Placeholdy plugin;
    private Map<String, String> placeholders;
    private final Pattern placeholderPattern = Pattern.compile("%placeholdy_([a-zA-Z0-9_]+)%");

    public PlaceholderManager(Placeholdy plugin) {
        this.plugin = plugin;
        this.placeholders = new HashMap<>();
        loadPlaceholders();
    }

    public void loadPlaceholders() {
        this.placeholders = plugin.getConfigManager().getPlaceholders();
    }

    public void reloadPlaceholders() {
        loadPlaceholders();
    }

    public String replacePlaceholders(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        Matcher matcher = placeholderPattern.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String placeholderName = matcher.group(1);
            String replacement = placeholders.get(placeholderName);

            if (replacement != null) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            } else {
                // Keep original placeholder if no replacement found
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public void addPlaceholder(String key, String value) {
        placeholders.put(key, value);
        plugin.getConfigManager().setPlaceholder(key, value);
    }

    public void removePlaceholder(String key) {
        placeholders.remove(key);
        plugin.getConfigManager().removePlaceholder(key);
    }

    public Map<String, String> getAllPlaceholders() {
        return new HashMap<>(placeholders);
    }

    public boolean hasPlaceholder(String key) {
        return placeholders.containsKey(key);
    }

    public String getPlaceholder(String key) {
        return placeholders.get(key);
    }

    public int getPlaceholderCount() {
        return placeholders.size();
    }
}