package net.Utils.Listeners;


import net.Managers.DimensionManager;
import net.Managers.FileManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;


public class OnPlayerJoin implements Listener {

    private final Plugin plugin;

    public OnPlayerJoin(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FileManager fileManager = new FileManager(plugin);

        if (!fileManager.hasPlayerData(player.getUniqueId()))
            onFirstJoin(player);

        fileManager.loadPlayerData(player.getUniqueId());
    }

    public void onFirstJoin(Player player) {
        World world = Objects.requireNonNull(DimensionManager.getDimension("void")).getWorld();
        player.teleport(new Location(world, 0, 170, 0));
        player.playSound(player.getLocation(), Sound.MUSIC_DISC_13,1,1);

        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    double tick = 0;
                    @Override
                    public void run() {
                        player.teleport(new Location(world, 0, 170 + tick / 100, 0));
                        tick++;
                    }
                }.runTaskTimer(plugin, 0,1);

            }
        }.runTaskLater(plugin, 20 * 10);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        FileManager fileManager = new FileManager(plugin);
        fileManager.savePlayerData(player);
    }
}