package org.mortisdevelopment.mortisBattleRoyale.resurgence;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.mortisdevelopment.mortisBattleRoyale.MortisBattleRoyale;

public class KillsPlaceholder extends PlaceholderExpansion{

    private final MortisBattleRoyale plugin;

    public KillsPlaceholder(MortisBattleRoyale plugin) {
        this.plugin = plugin;
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
        if (params.equalsIgnoreCase("kills")) {
            int kills = plugin.getMatchStartRs().getPlayerKills(player.getUniqueId());
            return String.valueOf(kills);
        }
        return "0";
    }
}
