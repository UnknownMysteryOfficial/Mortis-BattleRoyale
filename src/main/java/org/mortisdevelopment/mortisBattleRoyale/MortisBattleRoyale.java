package org.mortisdevelopment.mortisBattleRoyale;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.mortisdevelopment.mortisBattleRoyale.battleroyale.LootDropBr;
import org.mortisdevelopment.mortisBattleRoyale.battleroyale.MatchLeaveBr;
import org.mortisdevelopment.mortisBattleRoyale.battleroyale.WinsPlaceholder;
import org.mortisdevelopment.mortisBattleRoyale.lobby.LobbyRsSetCommand;
import org.mortisdevelopment.mortisBattleRoyale.resurgence.LootDropRs;
import org.mortisdevelopment.mortisBattleRoyale.resurgence.MatchLeaveRs;
import org.mortisdevelopment.mortisBattleRoyale.gui.GUICommand;
import org.mortisdevelopment.mortisBattleRoyale.gui.GUIHandler;
import org.mortisdevelopment.mortisBattleRoyale.battleroyale.MatchStartBr;
import org.mortisdevelopment.mortisBattleRoyale.lobby.LobbyBrSetCommand;
import org.mortisdevelopment.mortisBattleRoyale.resurgence.MatchStartRs;
import org.mortisdevelopment.mortisBattleRoyale.resurgence.KillsPlaceholder;

import java.io.File;

public final class MortisBattleRoyale extends JavaPlugin {

    private MatchStartRs matchStartRs;

    @Override
    public void onEnable() {
        // Plugin startup logic
        File file = getFile("lobbies.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        File file2 = getFile("messages.yml");
        FileConfiguration config2 = YamlConfiguration.loadConfiguration(file2);

        File file3 = getFile("loots.yml");
        FileConfiguration configuration3 = YamlConfiguration.loadConfiguration(file3);

        File file4 = getFile("data.yml");
        FileConfiguration configuration4 = YamlConfiguration.loadConfiguration(file3);

        saveDefaultConfig();
        new LootDropBr(this);
        new LootDropRs(this);
        new KillsPlaceholder(this).register();
        new WinsPlaceholder(this).register();
        matchStartRs = new MatchStartRs(this);
        getServer().getPluginManager().registerEvents(new MatchStartBr(this), this);
        getServer().getPluginManager().registerEvents(new MatchStartRs(this), this);
        getServer().getPluginManager().registerEvents(new GUIHandler(this), this);
        getServer().getPluginManager().registerEvents(new RestrictedCommandManager(this), this);
        getCommand("setlobbybr").setExecutor(new LobbyBrSetCommand(this));
        getCommand("setlobbyrs").setExecutor(new LobbyRsSetCommand(this));
        getCommand("br").setExecutor(new GUICommand(this));
        getCommand("brleave").setExecutor(new MatchLeaveBr(this));
        getCommand("rsleave").setExecutor(new MatchLeaveRs(this));
    }

    private File getFile(String name) {
        File file = new File(getDataFolder(), name);
        if (!file.exists()) {
            saveResource(name, true);
        }
        return file;
    }

    public MatchStartRs getMatchStartRs() {
        return matchStartRs;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
