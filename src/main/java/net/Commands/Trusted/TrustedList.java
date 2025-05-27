package net.Commands.Trusted;

import net.Commands.CommandExtension;
import net.Managers.TrustManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.metaversePlugin.MetaversePlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class TrustedList extends CommandExtension {

    public TrustedList() {
        super("list", "will send you the list of all the players you trust");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return true;

        List<OfflinePlayer> trustedList = TrustManager.getTrustedOfflinePlayers(player);

        player.sendMessage(MetaversePlugin.prefix.append(Component.text(
                "Trusted Players: ").color(NamedTextColor.GREEN)));
        player.sendMessage(Component.text("-----------------------------------------").color(NamedTextColor.GRAY));
        for (OfflinePlayer trusted : trustedList) {
            player.sendMessage(Component.text("Trusted: ").color(NamedTextColor.GREEN).append(Component.text(
                    Objects.requireNonNull(trusted.getName())).color(NamedTextColor.GRAY)));
        }
        player.sendMessage(Component.text("-----------------------------------------").color(NamedTextColor.GRAY));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return null;
    }
}
