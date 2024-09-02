package org.mortisdevelopment.mortisBattleRoyale.resurgence;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.mortisdevelopment.mortisBattleRoyale.MortisBattleRoyale;

import java.io.File;

public class MatchLeaveRs implements CommandExecutor {

    private final MortisBattleRoyale plugin;

    public MatchLeaveRs(MortisBattleRoyale plugin) {
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
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        File file = getFile("config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        String tpWorldName = config.getString("resurgence.tp-world");
        assert tpWorldName != null;
        World tpWorld = Bukkit.getWorld(tpWorldName);

        if (commandSender instanceof Player player){
            World world = player.getWorld();
            if (world.getName().startsWith("resurgence")){
                if (tpWorld != null) {
                    Location tpLocation = tpWorld.getSpawnLocation();
                    player.sendMessage(ChatColor.GREEN + "You have successfully left the match.");
                    player.teleport(tpLocation);
                    player.setGameMode(GameMode.SURVIVAL);
                }
            }
        }
        return true;
    }
}
