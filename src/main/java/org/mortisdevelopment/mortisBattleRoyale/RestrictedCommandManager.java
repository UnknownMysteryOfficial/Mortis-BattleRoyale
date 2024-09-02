package org.mortisdevelopment.mortisBattleRoyale;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class RestrictedCommandManager implements Listener {
    private final JavaPlugin plugin;
    private List<String> restrictedCommands;

    public RestrictedCommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadRestrictedCommands();
    }

    private void loadRestrictedCommands() {
        FileConfiguration config = plugin.getConfig();
        this.restrictedCommands = config.getStringList("restricted-commands.commands");
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().split(" ")[0].substring(1);
        String playerWorld = event.getPlayer().getWorld().getName();

        if ((playerWorld.toLowerCase().startsWith("battleroyale") || playerWorld.toLowerCase().startsWith("resurgence")) && isCommandRestricted(command)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot execute this command in this world.");
        }
    }

    private boolean isCommandRestricted(String command) {
        for (String restricted : restrictedCommands) {
            if (command.equalsIgnoreCase(restricted)) {
                return true;
            }
        }
        return false;
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        loadRestrictedCommands();
    }
}
