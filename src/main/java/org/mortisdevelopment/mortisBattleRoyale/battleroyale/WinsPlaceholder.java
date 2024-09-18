package org.mortisdevelopment.mortisBattleRoyale.battleroyale;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.mortisdevelopment.mortisBattleRoyale.MortisBattleRoyale;

import java.io.File;
import java.util.UUID;

public class WinsPlaceholder extends PlaceholderExpansion {

    private final MortisBattleRoyale plugin;

    public WinsPlaceholder(MortisBattleRoyale plugin) {
        this.plugin = plugin;
    }

    private File getFile(String name) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.saveResource(name, true);
        }
        return file;
    }

    @Override
    public String getIdentifier() {
        return "mortisbattleroyale";
    }

    @Override
    public String getAuthor() {
        return "MrErrorX";
    }

    @Override
    public String getVersion() {
        return "1.3.4";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("wins")) {
            File data = getFile("data.yml");
            FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(data);
            UUID uuid = player.getUniqueId();
            String path = uuid.toString() + ".br-wins";
            int currentWins = dataConfig.getInt(path, 0);
            return String.valueOf(currentWins);
        }
        return "0";
    }
}
