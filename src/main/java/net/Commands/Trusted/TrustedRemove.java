package net.Commands.Trusted;

import net.Commands.CommandExtension;
import net.Managers.TrustManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.metaversePlugin.MetaversePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TrustedRemove extends CommandExtension {

    public TrustedRemove() {
        super("remove", "will remove the trusted player");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MetaversePlugin.prefix.append(
                    Component.text("Only players can use this command.").color(NamedTextColor.RED)));
            return true;
        }

        Player trusted = Bukkit.getPlayer(args[1]);
        if (trusted == null) {
            sender.sendMessage(MetaversePlugin.prefix.append(
                    Component.text("Invalid player: " + args[1]).color(NamedTextColor.RED)));
            return true;
        }

        if (trusted == player) {
            sender.sendMessage(MetaversePlugin.prefix.append(
                    Component.text("what are you doing do you not trust yourself?").color(NamedTextColor.RED)));
            return true;
        }

        if (!TrustManager.isTrusted(player, trusted.getUniqueId())) {
            sender.sendMessage(MetaversePlugin.prefix.append(
                    Component.text(args[1] + " is not trusted.").color(NamedTextColor.AQUA)));
            return true;
        }

        TrustManager.removeTrustedPlayer(player, trusted);
        sender.sendMessage(MetaversePlugin.prefix.append(
                Component.text("not trusting: " + args[1] + "anymore").color(NamedTextColor.GREEN)));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return null;
    }
}