package net.Managers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TrustManager {

    private static final HashMap<UUID, List<UUID>> trustedPlayers = new HashMap<>();

    public static boolean isTrusted(Player player, UUID trusted) {
        return getTrustedUUIDs(player).contains(trusted);
    }

    public static @NotNull List<UUID> getTrustedUUIDs(Player player) {
        return trustedPlayers.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }

    public static @NotNull List<UUID> getTrustedUUIDs(UUID uuid) {
        return trustedPlayers.getOrDefault(uuid, new ArrayList<>());
    }

    public static @NotNull List<OfflinePlayer> getTrustedOfflinePlayers(Player player) {
        List<OfflinePlayer> trustedPlayers = new ArrayList<>();
        for (UUID uuid : getTrustedUUIDs(player)) {
            OfflinePlayer trusted = Bukkit.getOfflinePlayer(uuid);

            if (trusted != null)
                trustedPlayers.add(trusted);
        }
        return trustedPlayers;
    }


    public static @NotNull List<Player> getTrustedPlayers(Player player) {
        List<Player> trustedPlayers = new ArrayList<>();
        for (UUID uuid : getTrustedUUIDs(player)) {
            Player trusted = Bukkit.getPlayer(uuid);

            if (trusted != null)
                trustedPlayers.add(trusted);
        }
        return trustedPlayers;
    }

    public static void addTrustedPlayer(Player player, Player trusted) {
        List<UUID> trustedUUIDs = getTrustedUUIDs(player);
        trustedUUIDs.add(trusted.getUniqueId());
        trustedPlayers.put(player.getUniqueId(), trustedUUIDs);
    }

    public static void addTrustedPlayer(UUID player, UUID trusted) {
        List<UUID> trustedUUIDs = getTrustedUUIDs(player);
        trustedUUIDs.add(trusted);
        trustedPlayers.put(player, trustedUUIDs);
    }

    public static void removeTrustedPlayer(Player player, Player trusted) {
        List<UUID> trustedUUIDs = getTrustedUUIDs(player);
        trustedUUIDs.remove(trusted.getUniqueId());
        trustedPlayers.put(player.getUniqueId(), trustedUUIDs);
    }

    public static void load(Player player, Plugin plugin) {

    }

    public static void save(Player player, Plugin plugin) {

    }
}
