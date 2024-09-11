package org.mortisdevelopment.mortisBattleRoyale.resurgence;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.ItemStack;
import org.mortisdevelopment.mortisBattleRoyale.MortisBattleRoyale;

import java.io.File;
import java.util.*;

public class MatchStartRs implements Listener {
    private final MortisBattleRoyale plugin;
    private final Map<UUID, Integer> playerKills = new HashMap<>();

    public MatchStartRs(MortisBattleRoyale plugin) {
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
    public void onPlayerWorldJoin(PlayerChangedWorldEvent event) {
        File file = getFile("lobbies.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        int playerSize = config.getInt("resurgence.player-size");
        String lobbyWorldName = config.getString("resurgence.world-name");

        File file1 = getFile("config.yml");
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file1);
        int borderSize = configuration.getInt("resurgence.world-size");

        Player player = event.getPlayer();
        World world = player.getWorld();

        if (world.getName().equalsIgnoreCase(lobbyWorldName)) {
            int currentPlayers = world.getPlayers().size();

            if (currentPlayers >= playerSize) {
                for (Player p : world.getPlayers()) {
                    p.sendMessage(ChatColor.GREEN + "Match starting! Preparing the resurgence world...");
                }
                createResurgenceWorldAsync("resurgence", World.Environment.NORMAL, "TerraformGenerator", borderSize);
            } else {
                File msgFile = getFile("messages.yml");
                FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
                String notEnoughPlayersMsg = ChatColor.translateAlternateColorCodes('&', msgConfig.getString("rs-not-enough-players", "&cWaiting for enough players to start the match."));
                player.sendMessage(notEnoughPlayersMsg);
            }
        }
    }

    public void createResurgenceWorldAsync(String baseWorldName, World.Environment environment, String generator, int initialBorderSize) {
        String worldName = generateUniqueWorldName(baseWorldName);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            WorldCreator worldCreator = new WorldCreator(worldName);
            worldCreator.environment(environment);

            if (generator != null) {
                worldCreator.generator(generator);
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                World world = worldCreator.createWorld();

                if (world != null) {
                    setupResurgenceWorld(world, initialBorderSize);
                }
            });
        });
    }

    private String generateUniqueWorldName(String baseWorldName) {
        int counter = 1;
        String worldName = baseWorldName + "_" + counter;

        while (Bukkit.getWorld(worldName) != null) {
            counter++;
            worldName = baseWorldName + "_" + counter;
        }

        return worldName;
    }

    private void setupResurgenceWorld(World world, int initialBorderSize) {
        File file = getFile("lobbies.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        String lobbyWorld = config.getString("resurgence.world-name");

        Location spawnLocation = world.getSpawnLocation();
        world.getWorldBorder().setCenter(spawnLocation);
        world.getWorldBorder().setSize(initialBorderSize);
        world.setPVP(true);

        startBorderShrinkTask(world, initialBorderSize);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().getName().equalsIgnoreCase(lobbyWorld)) {
                teleportToSkyAndGiveElytra(player, world);
            }
        }
    }

    private void teleportToSkyAndGiveElytra(Player player, World world) {
        Location highLocation = new Location(world, world.getSpawnLocation().getX(), world.getMaxHeight(), world.getSpawnLocation().getZ());
        player.teleport(highLocation);

        ItemStack originalChestplate = player.getInventory().getChestplate();

        if (originalChestplate != null) {
            player.setMetadata("originalChestplate", new FixedMetadataValue(plugin, originalChestplate));
        }

        ItemStack elytra = new ItemStack(Material.ELYTRA);
        player.getInventory().setChestplate(elytra);

        monitorPlayerFlight(player);
    }

    private void monitorPlayerFlight(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnGround()) {
                    if (player.hasMetadata("originalChestplate") && !player.getMetadata("originalChestplate").isEmpty()) {
                        ItemStack originalChestplate = (ItemStack) player.getMetadata("originalChestplate").get(0).value();
                        player.getInventory().setChestplate(originalChestplate);
                        player.removeMetadata("originalChestplate", plugin);
                    } else {
                        player.getInventory().setChestplate(null);
                    }
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 10);
    }



    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        if (player.getWorld().getName().startsWith("resurgence")){
            event.setKeepInventory(true);
            event.setKeepLevel(true);
            event.getDrops().clear();

            if (killer != null) {
                UUID killerId = killer.getUniqueId();
                playerKills.put(killerId, playerKills.getOrDefault(killerId, 0) + 1);
            }

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                World world = player.getWorld();
                Location spawnLocation = world.getSpawnLocation();
                Location highLocation = new Location(world, spawnLocation.getX(), world.getMaxHeight(), spawnLocation.getZ());
                teleportToSkyAndGiveElytra(player, world);
            }, 1L);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (world.getName().startsWith("resurgence")){
            Location spawnLocation = world.getSpawnLocation();
            Location highLocation = new Location(world, spawnLocation.getX(), world.getMaxHeight(), spawnLocation.getZ());

            event.setRespawnLocation(highLocation);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                teleportToSkyAndGiveElytra(player, world);
            }, 1L);
        }
    }

    private void startBorderShrinkTask(World world, int initialBorderSize) {
        new BukkitRunnable() {
            int borderSize = initialBorderSize;

            @Override
            public void run() {
                if (borderSize > 0) {
                    borderSize -= 5;
                    world.getWorldBorder().setSize(borderSize);

                    if (borderSize <= 0) {
                        declareWinnersAndCleanup(world);
                        cancel();
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 100);
    }

    private void declareWinnersAndCleanup(World world) {
        File configYML = getFile("config.yml");
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(configYML);
        String tpWorldName = configuration.getString("resurgence.tp-world");

        World tpWorld = Bukkit.getWorld(tpWorldName);
        if (tpWorld != null) {
            for (Player player : world.getPlayers()) {
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(tpWorld.getSpawnLocation());
                player.getInventory().clear();
            }

            Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "Resurgence:");
            announceWinners();
            Bukkit.broadcastMessage(ChatColor.GOLD + "The match has ended. Players have been teleported back.");

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (Bukkit.unloadWorld(world, false)) {
                    deleteWorld(world.getWorldFolder());
                }
            }, 100L);
        } else {
            plugin.getLogger().warning("TP World " + tpWorldName + " does not exist.");
        }
    }

    private void announceWinners() {
        List<Map.Entry<UUID, Integer>> sortedKills = new ArrayList<>(playerKills.entrySet());
        sortedKills.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int count = 0;
        for (Map.Entry<UUID, Integer> entry : sortedKills) {
            if (count >= 3) break;

            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                String position = (count == 0) ? "1st" : (count == 1) ? "2nd" : "3rd";
                Bukkit.broadcastMessage(ChatColor.GOLD + position + " place: " + player.getName() + " with " + entry.getValue() + " kills.");
                count++;
            }
        }
    }

    private void deleteWorld(File worldFolder) {
        if (worldFolder.exists()) {
            File[] files = worldFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteWorld(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        worldFolder.delete();
    }
}
