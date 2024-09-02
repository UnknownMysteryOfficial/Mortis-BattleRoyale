package org.mortisdevelopment.mortisBattleRoyale.lobby;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.mortisdevelopment.mortisBattleRoyale.MortisBattleRoyale;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LobbyRsSetCommand implements TabExecutor {
    private final MortisBattleRoyale plugin;

    public LobbyRsSetCommand(MortisBattleRoyale plugin) {
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
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        File file = getFile("lobbies.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        File msgFile = getFile("messages.yml");
        FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
        String lobbySetDm = ChatColor.translateAlternateColorCodes('&', msgConfig.getString("lobby-set-dm") + " ");

        if (commandSender instanceof Player){
            Player player = (Player) commandSender;
            if (player.hasPermission("mortis.battleroyale.lobbyrs")){
                Location location = player.getLocation();
                config.set("deathmatch.location", location);
                try {
                    config.save(file);
                    player.sendMessage(lobbySetDm);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else{
                player.sendMessage(ChatColor.RED + "You don't have the permission to do that!");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        return List.of();
    }
}
