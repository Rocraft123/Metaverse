package net.Commands.Item;

import net.Abilities.Model.Cooldown;
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

public class SetCooldownItem extends CommandExtension {

    public SetCooldownItem() {
        super("setCooldown", "sets the cooldown of the selected item.");
        setRequiresOp(true);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) return true;
        if (args.length == 1) return false;

        ItemManager manager = ItemManager.getInstance();

        Item item = manager.getItem(args[1]);
        int time;

        if (item == null) {
            sender.sendMessage(MetaversePlugin.prefix.append(Component.text(
                    "Invalid item: " + args[1]).color(NamedTextColor.RED)));
            return true;
        }

        try {
            time = Integer.parseInt(args[2]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            sender.sendMessage(MetaversePlugin.prefix.append(Component.text(
                    "Invalid integer: " + args[2]).color(NamedTextColor.RED)));
            return true;
        }

        long minutes = time / 60;
        long seconds = time % 60;

        if (args.length == 4) {
            Player player = Bukkit.getPlayer(args[3]);
            if (player == null) {
                sender.sendMessage(MetaversePlugin.prefix.append(Component.text(
                        "Invalid player: " + args[3]).color(NamedTextColor.RED)));
                return true;
            }

            item.setCooldown(player, new Cooldown(time));
            player.sendMessage(MetaversePlugin.prefix.append(Component.text("Your cooldown for: " + item.getName() + " to: " +
                    minutes + " minutes " + seconds + " seconds.").color(NamedTextColor.GREEN)));
            sender.sendMessage(MetaversePlugin.prefix.append(Component.text("Your cooldown for: " + item.getName() + " to: " +
                    minutes + " minutes " + seconds + " seconds, for: " + player.getName()).color(NamedTextColor.GREEN)));
            return true;
        }

        if (sender instanceof Player player) {
            item.setCooldown(player, new Cooldown(time));
            player.sendMessage(MetaversePlugin.prefix.append(Component.text("Your cooldown for: " + item.getName() + " to: " +
                    minutes + " minutes " + seconds + " seconds.").color(NamedTextColor.GREEN)));
            return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender.isOp())) return new ArrayList<>();
        if (args.length == 2) return Utils.getItemNameStrings();

        if (args.length == 4) {
            List<String> players = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers())
                players.add(player.getName());
            return players;
        }
        return new ArrayList<>();
    }
}
