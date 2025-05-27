package net.Commands.Ability;

import net.Abilities.Model.Ability;
import net.Commands.CommandExtension;
import net.Dimensions.Dimension;
import net.Managers.AbilityManager;
import net.Managers.DimensionManager;
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

public class SetAbility extends CommandExtension {

    public SetAbility() {
        super("set", "Will set the selected ability.");
        setRequiresOp(true);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) return true;
        if (args.length < 3) return true;

        Ability ability = AbilityManager.getAbility(args[1]);
        String slot = args[2];
        if (ability == null) {
            sender.sendMessage(MetaversePlugin.prefix.append(
                    Component.text("Invalid ability: " + args[1]).color(NamedTextColor.RED)));
            return true;
        }

        if (args.length == 4) {
            Player player = Bukkit.getPlayer(args[3]);
            if (player == null) {
                sender.sendMessage(MetaversePlugin.prefix.append(
                        Component.text("Invalid player: " + args[3]).color(NamedTextColor.RED)));
                return true;
            }
            if (slot.equalsIgnoreCase("main"))
                AbilityManager.setSelectedAbility(player, ability);
            else if (slot.equalsIgnoreCase("inventory"))
                AbilityManager.setSecondAbility(player, ability);
            else {
                sender.sendMessage(MetaversePlugin.prefix.append(
                        Component.text("Invalid slot: " + args[2]).color(NamedTextColor.RED)));
                return true;
            }
            sender.sendMessage(MetaversePlugin.prefix.append(
                    Component.text("Set the main ability of: " + args[3] + " to ").color(NamedTextColor.GREEN).append(
                            Component.text(ability.getName() + " " + ability.getIcon()).color(ability.getColor()))));
            return true;
        }



        if (sender instanceof Player player) {
            if (slot.equalsIgnoreCase("main"))
                AbilityManager.setSelectedAbility(player, ability);
            else if (slot.equalsIgnoreCase("inventory"))
                AbilityManager.setSecondAbility(player, ability);
            else {
                sender.sendMessage(MetaversePlugin.prefix.append(
                        Component.text("Invalid slot: " + args[2]).color(NamedTextColor.RED)));
                return true;
            }
            sender.sendMessage(MetaversePlugin.prefix.append(
                    Component.text("Set your main ability to: ").color(NamedTextColor.GREEN).append(
                            Component.text(ability.getName() + " " + ability.getIcon()).color(ability.getColor()))));
            return true;
        }

        sender.sendMessage(MetaversePlugin.prefix.append(
                Component.text("Unable to set the ability").color(NamedTextColor.RED)));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender.isOp())) return null;
        if (args.length == 2) {
            List<String> abilities = new ArrayList<>();

            for (Dimension dimension : DimensionManager.dimensions) {
                for (Ability ability : dimension.getAbilities()) {
                    abilities.add(dimension.getDisplayName().toLowerCase().replace(" ", "") + ":" +
                            ability.getName().toLowerCase().replace(" ", ""));
                }
            }
            return abilities;
        }

        if (args.length == 3)
            return List.of("main", "inventory");

        if (args.length == 4) {
            List<String> players = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers())
                players.add(player.getName().replace(" ", ""));
            return players;
        }
        return new ArrayList<>();
    }
}
