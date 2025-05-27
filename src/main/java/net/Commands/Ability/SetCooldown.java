package net.Commands.Ability;

import net.Abilities.Model.Ability;
import net.Abilities.Model.Cooldown;
import net.Abilities.Model.Item;
import net.Commands.CommandExtension;
import net.Dimensions.Dimension;
import net.Managers.AbilityManager;
import net.Managers.DimensionManager;
import net.Managers.ItemManager;
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

public class SetCooldown extends CommandExtension {

    public SetCooldown() {
        super("setCooldown", "sets the cooldown of the selected ability.");
        setRequiresOp(true);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) return true;
        if (args.length == 1) return false;

        Ability ability = AbilityManager.getAbility(args[1]);
        int time;

        if (ability == null) {
            sender.sendMessage(MetaversePlugin.prefix.append(Component.text(
                    "Invalid ability: " + args[1]).color(NamedTextColor.RED)));
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

            ability.setCooldown(player, new Cooldown(time));
            player.sendMessage(MetaversePlugin.prefix.append(Component.text("Your cooldown for: " + ability.getName() + " to: " +
                    minutes + " minutes " + seconds + " seconds.").color(NamedTextColor.GREEN)));
            return true;
        }

        if (sender instanceof Player player) {
            ability.setCooldown(player, new Cooldown(time));
            player.sendMessage(MetaversePlugin.prefix.append(Component.text("Your cooldown for: " + ability.getName() + " to: " +
                    minutes + " minutes " + seconds + " seconds.").color(NamedTextColor.GREEN)));
            return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender.isOp())) return new ArrayList<>();
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
        if (args.length == 4) {
            List<String> players = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers())
                players.add(player.getName());
            return players;
        }
        return new ArrayList<>();
    }
}
