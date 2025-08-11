package de.einpallux.placeholdy.listeners;

import de.einpallux.placeholdy.Placeholdy;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class PlaceholderListener implements Listener {

    private final Placeholdy plugin;

    public PlaceholderListener(Placeholdy plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {
        // Process config files of other plugins when they are enabled
        if (!event.getPlugin().equals(plugin)) {
            processPluginConfigs(event.getPlugin().getDataFolder());
        }
    }

    private void processPluginConfigs(File pluginFolder) {
        if (!pluginFolder.exists() || !pluginFolder.isDirectory()) {
            return;
        }

        // Process all YAML files in the plugin folder
        processDirectory(pluginFolder);
    }

    private void processDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                processDirectory(file); // Recursively process subdirectories
            } else if (file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")) {
                processConfigFile(file);
            }
        }
    }

    private void processConfigFile(File configFile) {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            boolean modified = false;

            // Get all keys from the config
            Set<String> keys = config.getKeys(true);

            for (String key : keys) {
                Object value = config.get(key);
                if (value instanceof String) {
                    String originalValue = (String) value;
                    String processedValue = plugin.getPlaceholderManager().replacePlaceholders(originalValue);

                    if (!originalValue.equals(processedValue)) {
                        config.set(key, processedValue);
                        modified = true;
                    }
                }
            }

            // Save the config if it was modified
            if (modified) {
                config.save(configFile);
                plugin.getLogger().info("Processed placeholders in: " + configFile.getPath());
            }

        } catch (IOException e) {
            plugin.getLogger().warning("Could not process config file: " + configFile.getPath() + " - " + e.getMessage());
        } catch (Exception e) {
            plugin.getLogger().warning("Error processing config file: " + configFile.getPath() + " - " + e.getMessage());
        }
    }
}