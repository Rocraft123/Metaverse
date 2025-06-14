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
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GiveItem extends CommandExtension {

    public GiveItem() {
        super("give", "gives you the selected item.");
        setRequiresOp(true);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) return true;
        if (args.length < 2) return true;

        ItemManager manager = ItemManager.getInstance();

        Item item = manager.getItem(args[1]);
        if (item == null) {
            sender.sendMessage(MetaversePlugin.prefix.append(
                    Component.text("Invalid item: " + args[1]).color(NamedTextColor.RED)));
            return true;
        }

        if (args.length == 3) {
            Player player = Bukkit.getPlayer(args[2]);
            if (player == null) {
                sender.sendMessage(MetaversePlugin.prefix.append(
                        Component.text("Invalid player: " + args[2]).color(NamedTextColor.RED)));
                return true;
            }

            Inventory inventory = player.getInventory();
            if (hasSpace(inventory, sender)) {
                player.getInventory().setItem(inventory.firstEmpty(), item.getItemstack());

                player.sendMessage(MetaversePlugin.prefix.append(
                        Component.text("you have been granted the: ").color(NamedTextColor.GREEN).append(
                                Component.text(item.getName() + " " + item.getIcon()).color(item.getColor()))));
                sender.sendMessage(MetaversePlugin.prefix.append(
                        Component.text("you have given: ").color(NamedTextColor.GREEN).append(
                                Component.text(item.getName() + " " + item.getIcon()).color(item.getColor())).append(
                                Component.text("to: " + player.getName()).color(NamedTextColor.GREEN))));
            }
            return true;
        }

        if (sender instanceof Player player) {
            Inventory inventory = player.getInventory();
            if (hasSpace(inventory, sender)) {
                player.getInventory().setItem(inventory.firstEmpty(), item.getItemstack());

                player.sendMessage(MetaversePlugin.prefix.append(
                        Component.text("you have been granted the: ").color(NamedTextColor.GREEN).append(
                                Component.text(item.getName() + " " + item.getIcon()).color(item.getColor()))));
            }
            return true;
        }

        sender.sendMessage(MetaversePlugin.prefix.append(
                Component.text("Unable to give any one the item!").color(NamedTextColor.RED)));
        return true;
    }

    private boolean hasSpace(Inventory inventory, CommandSender sender) {
        if (inventory.firstEmpty() == -1) {
            sender.sendMessage(MetaversePlugin.prefix.append(
                    Component.text("target does not have a free slot in their inventory!").color(NamedTextColor.RED)));
            return false;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender.isOp())) return new ArrayList<>();;
        if (args.length == 2) return Utils.getItemNameStrings();

        if (args.length == 3) {
            List<String> players = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers())
                players.add(player.getName().replace(" ", ""));
            return players;
        }
        return new ArrayList<>();
    }
}
