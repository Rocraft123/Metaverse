package net.Commands.Item;

import net.Abilities.Model.Item;
import net.Commands.CommandExtension;
import net.Managers.ItemManager;
import net.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.metaversePlugin.MetaversePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetDisabledItem extends CommandExtension {

    public SetDisabledItem() {
        super("disable", "will disable/enable the selected item.");
        setRequiresOp(true);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) return false;
        if (args.length < 3) return true;

        Item item = ItemManager.getInstance().getItem(args[1]);
        boolean disabled = Boolean.parseBoolean(args[2]);
        String strDisabled = disabled ? "Disabled" : "Enabled";

        if (item == null) {
            sender.sendMessage(MetaversePlugin.prefix.append(
                    Component.text("Invalid item: " + args[1]).color(NamedTextColor.RED)));
            return true;
        }

        if (args.length == 4) {
            Player player = Bukkit.getPlayer(args[3]);
            if (player == null) {
                sender.sendMessage(MetaversePlugin.prefix.append(
                        Component.text("Invalid player: " + args[3]).color(NamedTextColor.RED)));
                return true;
            }

            item.setDisabled(player, disabled);
            sender.sendMessage(MetaversePlugin.prefix.
                    append(Component.text(strDisabled + ": ").color(NamedTextColor.GREEN)).
                    append(Component.text(item.getName() + " " + item.getIcon()).color(item.getColor())).
                    append((Component.text(" For: " + player.getName()).color(NamedTextColor.GREEN))));
            player.sendMessage(MetaversePlugin.prefix.
                    append(Component.text(item.getName() + " " + item.getIcon()).color(item.getColor())).
                    append(Component.text(" has been " + strDisabled + " by: " + sender.getName()).color(NamedTextColor.GREEN)));
            return true;
        }

        if (sender instanceof Player player) {
            item.setDisabled(player, disabled);

            sender.sendMessage(MetaversePlugin.prefix.
                    append(Component.text(strDisabled + ": ").color(NamedTextColor.GREEN)).
                    append(Component.text(item.getName() + " " + item.getIcon()).color(item.getColor())));
            return true;
        }

        sender.sendMessage(MetaversePlugin.prefix.append(
                Component.text("Unable to disable/enable a item").color(NamedTextColor.RED)));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) return new ArrayList<>();
        if (args.length == 3) return List.of("true", "false");
        if (args.length == 2) return Utils.getItemNameStrings();

        if (args.length == 4) {
            List<String> players = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers())
                players.add(player.getName().replace(" ", ""));
            return players;
        }
        return new ArrayList<>();
    }
}