package net.Utils.Listeners;


import net.Dimensions.Dimension;
import net.Managers.DimensionManager;
import net.Managers.FileManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.metaversePlugin.MetaversePlugin;
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
import java.util.Random;


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
            setDimension(player);

        fileManager.loadPlayerData(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        FileManager fileManager = new FileManager(plugin);
        fileManager.savePlayerData(player);
    }

    public void setDimension(Player player) {
        Dimension dimension = DimensionManager.getDimension(new Random());
        DimensionManager.setDimension(player, dimension);
        player.sendMessage(MetaversePlugin.prefix.append(Component.text(
                "Your ability has been set to: " + dimension.getDisplayName() + "!").color(NamedTextColor.AQUA)));
        player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW,1,1);
    }
}