package de.einpallux.placeholdy;

import de.einpallux.placeholdy.commands.PlaceholdyCommand;
import de.einpallux.placeholdy.config.ConfigManager;
import de.einpallux.placeholdy.listeners.PlaceholderListener;
import de.einpallux.placeholdy.placeholders.PlaceholderManager;
import de.einpallux.placeholdy.placeholderapi.PlaceholdyExpansion;
import de.einpallux.placeholdy.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Placeholdy extends JavaPlugin {

    private static Placeholdy instance;
    private ConfigManager configManager;
    private PlaceholderManager placeholderManager;
    private PlaceholdyExpansion placeholderExpansion;
    private MessageService messageService;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize config manager first
        this.configManager = new ConfigManager(this);

        // Load configuration before initializing placeholder manager
        configManager.loadConfig();

        // Now initialize placeholder manager
        this.placeholderManager = new PlaceholderManager(this);

        // Initialize messaging service (MiniMessage + replacements)
        this.messageService = new MessageService(this);

        // Register commands
        getCommand("placeholdy").setExecutor(new PlaceholdyCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlaceholderListener(this), this);

        // Register PlaceholderAPI expansion if available
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.placeholderExpansion = new PlaceholdyExpansion(this);
            if (this.placeholderExpansion.register()) {
                getLogger().info("PlaceholderAPI expansion registered successfully!");
                getLogger().info("Use %placeholdy_<placeholder>% in other plugins!");
            } else {
                getLogger().warning("Failed to register PlaceholderAPI expansion!");
            }
        } else {
            getLogger().info("PlaceholderAPI not found. Manual placeholder replacement only. (⚠ It's highly recommended to use PlaceholderAPI!)");
        }

        getLogger().info("Placeholdy has been enabled successfully!");
        getLogger().info("Loaded " + placeholderManager.getPlaceholderCount() + " placeholders from config.");
    }

    @Override
    public void onDisable() {
        // Unregister PlaceholderAPI expansion
        if (placeholderExpansion != null) {
            placeholderExpansion.unregister();
        }
        getLogger().info("Placeholdy has been disabled!");
    }

    public static Placeholdy getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public void reload() {
        configManager.loadConfig();
        placeholderManager.reloadPlaceholders();

        // Reload PlaceholderAPI expansion if available
        if (placeholderExpansion != null) {
            placeholderExpansion.unregister();
            this.placeholderExpansion = new PlaceholdyExpansion(this);
            if (this.placeholderExpansion.register()) {
                getLogger().info("PlaceholderAPI expansion reloaded successfully!");
            }
        }

        getLogger().info("Placeholdy has been reloaded successfully!");
    }
}