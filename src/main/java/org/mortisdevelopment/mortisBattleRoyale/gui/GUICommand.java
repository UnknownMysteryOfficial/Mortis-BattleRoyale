package org.mortisdevelopment.mortisBattleRoyale.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mortisdevelopment.mortisBattleRoyale.MortisBattleRoyale;

import java.io.File;
import java.util.ArrayList;

public class GUICommand implements CommandExecutor {

    private final MortisBattleRoyale plugin;

    public GUICommand(MortisBattleRoyale plugin) {
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
        File file = getFile("config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        int timer  = config.getInt("battleroyale.pvp-enabled-time");

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            World world = player.getWorld();

            if (!world.getName().startsWith("battleroyale") | !world.getName().startsWith("resurgence")){

                if (player.hasPermission("mortis.battleroyale.gui")){

                    Inventory br_menu = Bukkit.createInventory(player, 27, ChatColor.RED + "Battle Royale");

                    ItemStack battleroyale = new ItemStack(Material.DIAMOND_SWORD);
                    ItemMeta brmeta = battleroyale.getItemMeta();
                    brmeta.setDisplayName(ChatColor.DARK_AQUA + "Battle Royale Mode");
                    brmeta.setUnbreakable(true);
                    brmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    brmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    brmeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
                    brmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                    ArrayList<String> brLore = new ArrayList<>();
                    brLore.add(ChatColor.GOLD + " ");
                    brLore.add(ChatColor.GREEN + "Click to join the lobby for");
                    brLore.add(ChatColor.GREEN + "battle royale (solo). Be the");
                    brLore.add(ChatColor.GREEN + "last one standing to win!");
                    brLore.add(ChatColor.GREEN + " ");
                    brLore.add(ChatColor.RED + "Settings");
                    brLore.add(ChatColor.RED + "- PvP disabled for " + timer + " seconds.");
                    brLore.add(ChatColor.RED + "- No respawns.");
                    brLore.add(ChatColor.RED + "- Match spectating is enabled.");
                    brLore.add(ChatColor.RED + "- Border shrinking every 5 seconds.");
                    brmeta.setLore(brLore);

                    battleroyale.setItemMeta(brmeta);

                    ItemStack deathmatch = new ItemStack(Material.DIAMOND_AXE);
                    ItemMeta dmmeta = deathmatch.getItemMeta();
                    dmmeta.setDisplayName(ChatColor.DARK_PURPLE + "Resurgence Mode");
                    dmmeta.setUnbreakable(true);
                    dmmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    dmmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    dmmeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
                    dmmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                    ArrayList<String> dmLore = new ArrayList<>();
                    dmLore.add(ChatColor.GOLD + " ");
                    dmLore.add(ChatColor.GREEN + "Click to join the lobby for");
                    dmLore.add(ChatColor.GREEN + "resurgence (solo). Get the");
                    dmLore.add(ChatColor.GREEN + "most of the kills to win!");
                    dmLore.add(ChatColor.GREEN + " ");
                    dmLore.add(ChatColor.RED + "Settings");
                    dmLore.add(ChatColor.RED + "- PvP enabled from beginning.");
                    dmLore.add(ChatColor.RED + "- Unlimited respawns.");
                    dmLore.add(ChatColor.RED + "- Get the most kills until the border shrinks completely");
                    dmLore.add(ChatColor.RED + "- Match spectating is disabled.");
                    dmLore.add(ChatColor.RED + "- Border shrinking every 5 seconds.");
                    dmmeta.setLore(dmLore);

                    deathmatch.setItemMeta(dmmeta);

                    ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                    ItemMeta fillerMeta = filler.getItemMeta();
                    fillerMeta.setDisplayName(" ");
                    fillerMeta.setUnbreakable(true);
                    fillerMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    fillerMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    fillerMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
                    fillerMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                    ArrayList<String> fillerLore = new ArrayList<>();
                    fillerLore.add(ChatColor.GOLD + " ");
                    fillerMeta.setLore(fillerLore);

                    filler.setItemMeta(fillerMeta);

                    br_menu.setItem(10, battleroyale);
                    br_menu.setItem(16, deathmatch);
                    br_menu.setItem(0, filler);
                    br_menu.setItem(1, filler);
                    br_menu.setItem(2, filler);
                    br_menu.setItem(3, filler);
                    br_menu.setItem(4, filler);
                    br_menu.setItem(5, filler);
                    br_menu.setItem(6, filler);
                    br_menu.setItem(7, filler);
                    br_menu.setItem(8, filler);
                    br_menu.setItem(9, filler);
                    br_menu.setItem(11, filler);
                    br_menu.setItem(12, filler);
                    br_menu.setItem(13, filler);
                    br_menu.setItem(14, filler);
                    br_menu.setItem(15, filler);
                    br_menu.setItem(17, filler);
                    br_menu.setItem(18, filler);
                    br_menu.setItem(19, filler);
                    br_menu.setItem(20, filler);
                    br_menu.setItem(21, filler);
                    br_menu.setItem(22, filler);
                    br_menu.setItem(23, filler);
                    br_menu.setItem(24, filler);
                    br_menu.setItem(25, filler);
                    br_menu.setItem(26, filler);

                    player.openInventory(br_menu);
                }else{
                    player.sendMessage(ChatColor.RED + "You don't have the permission to do that!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You can't run this command while in an ongoing match.");
            }
        }
            return true;
    }
}
