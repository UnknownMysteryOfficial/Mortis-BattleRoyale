package org.mortisdevelopment.mortisBattleRoyale.resurgence;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.mortisdevelopment.mortisBattleRoyale.MortisBattleRoyale;

import java.io.File;
import java.util.*;

public class LootDropRs {
    private final MortisBattleRoyale plugin;
    private final Map<ItemStack, Integer> lootTable = new HashMap<>();

    public LootDropRs(MortisBattleRoyale plugin) {
        this.plugin = plugin;
        loadLootTable();
        startLootDropTask();
    }

    private File getFile(String name) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.saveResource(name, true);
        }
        return file;
    }

    private void loadLootTable() {
        File file = getFile("loots.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> lootConfig = config.getStringList("loots");
        for (String loot : lootConfig) {
            String[] parts = loot.split(":");
            Material material = Material.valueOf(parts[0]);
            int amount = Integer.parseInt(parts[1]);
            int probability = Integer.parseInt(parts[2]);
            lootTable.put(new ItemStack(material, amount), probability);
        }
    }

    private void startLootDropTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    if (world.getName().startsWith("resurgence")) {
                        spawnLootChest(world);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20 * 60);
    }

    private void spawnLootChest(World world) {
        Random random = new Random();
        Location chestLocation = getRandomLocationWithinBorder(world);

        world.getBlockAt(chestLocation).setType(Material.CHEST);
        Chest chest = (Chest) world.getBlockAt(chestLocation).getState();

        for (ItemStack item : getRandomLoot()) {
            chest.getBlockInventory().addItem(item);
        }

        for (World world1 : Bukkit.getWorlds()) {
            if (world1.getName().startsWith("resurgence")) {
                for (Player player : world1.getPlayers()) {
                    player.sendMessage("§6A loot chest has spawned at §e" + formatLocation(chestLocation) + "§6!");
                }
            }
        }

    }

    private List<ItemStack> getRandomLoot() {
        List<ItemStack> loot = new ArrayList<>();
        Random random = new Random();

        for (Map.Entry<ItemStack, Integer> entry : lootTable.entrySet()) {
            int probability = entry.getValue();
            if (random.nextInt(100) + 1 <= probability) {
                loot.add(entry.getKey());
            }
        }

        return loot;
    }


    private Location getRandomLocationWithinBorder(World world) {
        Random random = new Random();
        Location center = world.getWorldBorder().getCenter();
        double size = world.getWorldBorder().getSize() / 2;
        double x = center.getX() + (random.nextDouble() * 2 - 1) * size;
        double z = center.getZ() + (random.nextDouble() * 2 - 1) * size;
        double y = world.getHighestBlockYAt((int) x, (int) z);
        return new Location(world, x, y, z);
    }

    private String formatLocation(Location location) {
        return "X: " + location.getBlockX() + " Y: " + location.getBlockY() + " Z: " + location.getBlockZ();
    }
}
