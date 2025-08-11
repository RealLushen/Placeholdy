package de.einpallux.placeholdy.config;

import de.einpallux.placeholdy.Placeholdy;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final Placeholdy plugin;
    private FileConfiguration config;

    public ConfigManager(Placeholdy plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        // Save default config if it doesn't exist
        plugin.saveDefaultConfig();
        // Reload config from file
        plugin.reloadConfig();
        // Get the configuration
        this.config = plugin.getConfig();

        // Ensure config is not null
        if (this.config == null) {
            plugin.getLogger().severe("Failed to load configuration file!");
            return;
        }

        // Create default placeholders if config is empty
        if (!config.contains("placeholders")) {
            createDefaultConfig();
        }
    }

    private void createDefaultConfig() {
        config.set("placeholders.servername", "Your Server Name");
        config.set("placeholders.serverip", "play.yourserver.com");
        config.set("placeholders.website", "https://yourserver.com");
        config.set("placeholders.discord", "https://discord.gg/yourserver");
        config.set("placeholders.store", "https://store.yourserver.com");
        config.set("placeholders.youtube", "https://youtube.com/yourchannel");
        config.set("placeholders.twitter", "https://twitter.com/yourserver");
        config.set("placeholders.instagram", "https://instagram.com/yourserver");

        plugin.saveConfig();
    }

    public Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();

        // Ensure config is loaded
        if (this.config == null) {
            plugin.getLogger().warning("Configuration is not loaded, returning empty placeholders map");
            return placeholders;
        }

        if (config.getConfigurationSection("placeholders") != null) {
            for (String key : config.getConfigurationSection("placeholders").getKeys(false)) {
                String value = config.getString("placeholders." + key);
                if (value != null) {
                    placeholders.put(key, value);
                }
            }
        }

        return placeholders;
    }

    public void setPlaceholder(String key, String value) {
        if (this.config == null) {
            plugin.getLogger().warning("Configuration is not loaded, cannot set placeholder");
            return;
        }
        config.set("placeholders." + key, value);
        plugin.saveConfig();
    }

    public void removePlaceholder(String key) {
        if (this.config == null) {
            plugin.getLogger().warning("Configuration is not loaded, cannot remove placeholder");
            return;
        }
        config.set("placeholders." + key, null);
        plugin.saveConfig();
    }

    public String getPlaceholder(String key) {
        if (this.config == null) {
            plugin.getLogger().warning("Configuration is not loaded, returning null for placeholder: " + key);
            return null;
        }
        return config.getString("placeholders." + key);
    }

    public boolean hasPlaceholder(String key) {
        if (this.config == null) {
            plugin.getLogger().warning("Configuration is not loaded, returning false for placeholder check: " + key);
            return false;
        }
        return config.contains("placeholders." + key);
    }
}