package org.mortisdevelopment.mortisBattleRoyale.gui;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.mortisdevelopment.mortisBattleRoyale.MortisBattleRoyale;

import java.io.File;

public class GUIHandler implements Listener {

    private final MortisBattleRoyale plugin;

    public GUIHandler(MortisBattleRoyale plugin) {
        this.plugin = plugin;
    }

    private File getFile(String name) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.saveResource(name, true);
        }
        return file;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        File file = getFile("lobbies.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        Location location_br = config.getLocation("battleroyale.location");
        Location location_dm = config.getLocation("deathmatch.location");

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        final String br_menu = ChatColor.RED + "Battle Royale";

        if (br_menu.equals(event.getView().getTitle())) {
            event.setCancelled(true);
            switch (event.getCurrentItem().getType()){
                case DIAMOND_SWORD:
                    player.sendMessage(ChatColor.GREEN + "Joining battle royale lobby.....");
                    player.closeInventory();
                    if (location_br != null) {
                        player.teleport(location_br);
                    }else{
                        player.sendMessage(ChatColor.RED + "No spawn location found for battle royale lobby.");
                    }
                    break;
                case DIAMOND_AXE:
                    player.sendMessage(ChatColor.GREEN + "Joining deathmatch lobby.....");
                    player.closeInventory();
                    if (location_dm != null) {
                        player.teleport(location_dm);
                    }else{
                        player.sendMessage(ChatColor.RED + "No spawn location found for deathmatch lobby.");
                    }
                    break;
            }
        }
    }
}