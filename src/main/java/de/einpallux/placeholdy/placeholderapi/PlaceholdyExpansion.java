package de.einpallux.placeholdy.placeholderapi;

import de.einpallux.placeholdy.Placeholdy;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholdyExpansion extends PlaceholderExpansion {

    private final Placeholdy plugin;

    public PlaceholdyExpansion(Placeholdy plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "placeholdy";
    }

    @Override
    public @NotNull String getAuthor() {
        return "EinPallux";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This ensures the expansion persists through /papi reload
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        // Return the value for the requested placeholder
        return plugin.getPlaceholderManager().getPlaceholder(params);
    }
}