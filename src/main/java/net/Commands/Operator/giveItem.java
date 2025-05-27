package net.Commands.Operator;

import net.Abilities.Model.Item;
import net.Managers.ItemManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.metaversePlugin.MetaversePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class giveItem implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) return true;
        if (args.length == 0) return false;

        ItemManager manager = ItemManager.getInstance();
        Item item = manager.getItem(args[0]);
        if (item == null) {
            sender.sendMessage(MetaversePlugin.prefix.append(Component.text(
                    "Invalid item: " + args[1]).color(NamedTextColor.RED)));
            return true;
        }

        if (args.length == 2) {
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                sender.sendMessage(MetaversePlugin.prefix.append(Component.text(
                        "Invalid player: " + args[1]).color(NamedTextColor.RED)));
                return true;
            }

            player.getInventory().addItem(item.getItemstack());
            player.sendMessage(MetaversePlugin.prefix.append(Component.text("You have been granted the: " +
                    item.getName() + "!").color(NamedTextColor.GREEN)));

            player.sendMessage(MetaversePlugin.prefix.append(Component.text("You have granted the: " +
                            item.getName() + " to: " + player.getName())
                    .color(NamedTextColor.GREEN)));
            return true;
        }

        if (sender instanceof Player player) {
            player.getInventory().addItem(item.getItemstack());
            player.sendMessage(MetaversePlugin.prefix.append(Component.text("You have been granted the: " +
                    item.getName() + "!").color(NamedTextColor.GREEN)));
            return true;
        }

        sender.sendMessage(MetaversePlugin.prefix.append(Component.text("Unable to give you the mace.").color(NamedTextColor.RED)));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender.isOp())) return null;
        if (args.length == 1) {
            List<String> items = new ArrayList<>();
            for (Item item : ItemManager.getInstance().getItems())
                items.add(item.getName().replace(" ", ""));

            return items;
        }
        if (args.length == 2) {
            List<String> players = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers())
                players.add(player.getName());
            return players;
        }
        return null;
    }
}
