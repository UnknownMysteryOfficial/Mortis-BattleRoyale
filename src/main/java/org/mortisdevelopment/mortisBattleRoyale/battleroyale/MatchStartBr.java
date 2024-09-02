package org.mortisdevelopment.mortisBattleRoyale.battleroyale;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.mortisdevelopment.mortisBattleRoyale.MortisBattleRoyale;

import java.io.File;

public class MatchStartBr implements Listener {

    private final MortisBattleRoyale plugin;

    public MatchStartBr(MortisBattleRoyale plugin) {
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
        int playerSize = config.getInt("battleroyale.player-size");
        String lobbyWorldName = config.getString("battleroyale.world-name");

        Player player = event.getPlayer();
        World world = player.getWorld();

        if (world.getName().equalsIgnoreCase(lobbyWorldName)) {
            int playersInLobby = world.getPlayers().size();

            if (playersInLobby >= playerSize) {
                String matchWorldName = "battleroyale";
                File configYML = getFile("config.yml");
                FileConfiguration configuration = YamlConfiguration.loadConfiguration(configYML);
                int initialBorderSize = configuration.getInt("battleroyale.world-size");

                for (Player p : world.getPlayers()) {
                    p.sendMessage(ChatColor.GREEN + "Match starting! Preparing the battle royale world...");
                }
                worldCreationAsync(matchWorldName, lobbyWorldName, World.Environment.NORMAL, initialBorderSize);
            } else {
                File msgFile = getFile("messages.yml");
                FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
                String notEnoughPlayersMsg = ChatColor.translateAlternateColorCodes('&', msgConfig.getString("br-not-enough-players", "&cWaiting for enough players to start the match."));
                player.sendMessage(notEnoughPlayersMsg);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        File file = getFile("messages.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        String msg = ChatColor.translateAlternateColorCodes('&', config.getString("br-elimination") + " ");

        Player player = event.getEntity();
        World world = player.getWorld();

        if (world.getName().startsWith("battleroyale")) {
            Location deathLocation = player.getLocation();

            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null) {
                    world.dropItemNaturally(deathLocation, item);
                }
            }
            player.getInventory().clear();

            event.setKeepLevel(true);
            event.setDeathMessage(msg);
            world.strikeLightningEffect(deathLocation);

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setHealth(1.0);
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }.runTaskLater(plugin, 1L);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    public void worldCreationAsync(String baseWorldName, String lobbyWorldName, World.Environment environment, int initialBorderSize) {
        String worldName = generateUniqueWorldName(baseWorldName);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            WorldCreator worldCreator = new WorldCreator(worldName);
            worldCreator.environment(environment);
            worldCreator.generator("TerraformGenerator");

            Bukkit.getScheduler().runTask(plugin, () -> {
                World world = worldCreator.createWorld();

                if (world != null) {
                    loadWorldAndTeleportPlayers(world, lobbyWorldName, initialBorderSize);
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

    private void loadWorldAndTeleportPlayers(World world, String lobbyWorldName, int initialBorderSize) {
        Location spawnLocation = world.getSpawnLocation();
        world.getWorldBorder().setCenter(spawnLocation);
        world.getWorldBorder().setSize(initialBorderSize);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().getName().equalsIgnoreCase(lobbyWorldName)) {
                Location highLocation = new Location(world, spawnLocation.getX(), world.getMaxHeight(), spawnLocation.getZ());
                player.teleport(highLocation);

                ItemStack elytra = new ItemStack(Material.ELYTRA);
                player.getInventory().setChestplate(elytra);

                monitorPlayerFlight(player);
            }
        }

        world.setPVP(false);
        startPvPTimer(world);
        startBorderShrinkTask(world, initialBorderSize);
    }

    private void monitorPlayerFlight(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnGround()) {
                    if (player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType() == Material.ELYTRA) {
                        player.getInventory().setChestplate(new ItemStack(Material.AIR));
                    }
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 10);
    }

    private void startPvPTimer(World world) {
        File timerFile = getFile("config.yml");
        FileConfiguration configTimer = YamlConfiguration.loadConfiguration(timerFile);
        int timerPvP = configTimer.getInt("battleroyale.pvp-enabled-time");

        new BukkitRunnable() {
            int timer = timerPvP;
            boolean pvpEnabled = false;

            @Override
            public void run() {
                for (Player player : world.getPlayers()) {
                    if (timer > 0) {
                        player.sendActionBar(ChatColor.GREEN + "PVP: Disabled");
                        timer--;
                    } else {
                        if (!pvpEnabled) {
                            pvpEnabled = true;
                            world.setPVP(true);
                        }
                        player.sendActionBar(ChatColor.RED + "PVP: Enabled");
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private void startBorderShrinkTask(World world, int initialBorderSize) {
        new BukkitRunnable() {
            int borderSize = initialBorderSize;

            @Override
            public void run() {
                int survivalPlayersCount = getSurvivalPlayers(world);

                if (survivalPlayersCount == 1) {
                    Player winner = world.getPlayers().stream()
                            .filter(p -> p.getGameMode() == GameMode.SURVIVAL)
                            .findFirst().orElse(null);
                    if (winner != null) {
                        declareWinner(winner, world);
                    }
                    cancel();
                } else if (borderSize > 0) {
                    borderSize -= 5;
                    world.getWorldBorder().setSize(borderSize);
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 200);
    }

    private int getSurvivalPlayers(World world) {
        return (int) world.getPlayers().stream()
                .filter(p -> p.getGameMode() == GameMode.SURVIVAL)
                .count();
    }

    private void declareWinner(Player winner, World world) {
        File configYML = getFile("config.yml");
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(configYML);
        String tpWorldName = configuration.getString("battleroyale.tp-world");

        World tpWorld = Bukkit.getWorld(tpWorldName);
        if (tpWorld != null) {
            for (Player player : world.getPlayers()) {
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(tpWorld.getSpawnLocation());
            }
            Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "Battleroyale");
            Bukkit.broadcastMessage(ChatColor.GOLD + winner.getName() + " is the winner! Congratulations!");
        }

        Bukkit.unloadWorld(world, false);
        deleteWorld(world.getWorldFolder());
    }


    private void deleteWorld(File path) {
        if (path.exists()) {
            for (File file : path.listFiles()) {
                if (file.isDirectory()) {
                    deleteWorld(file);
                } else {
                    file.delete();
                }
            }
            path.delete();
        }
    }
}
