package net.Commands.Dimension;

import net.Commands.CommandExtension;
import net.Dimensions.Dimension;
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

public class SetDimension extends CommandExtension {

    public SetDimension() {
        super("set", "sets the selected dimension as your main.");
        setRequiresOp(true);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) return true;
        if (args.length < 2) return true;

        Dimension dimension = DimensionManager.getDimension(args[1]);
        if (dimension == null) {
            sender.sendMessage(MetaversePlugin.prefix.
                    append(Component.text("Invalid dimension: " + args[1]).color(NamedTextColor.RED)));
            return true;
        }

        if (args.length == 3) {
            Player player = Bukkit.getPlayer(args[2]);
            if (player == null) {
                sender.sendMessage(MetaversePlugin.prefix.append(
                        Component.text("Invalid player: " + args[2]).color(NamedTextColor.RED)));
                return true;
            }

            DimensionManager.setDimension(player, dimension);
            sender.sendMessage(MetaversePlugin.prefix.
                    append(Component.text("Set the dimension of: " + player.getName() + " to: ").color(NamedTextColor.GREEN)).
                    append(Component.text(dimension.getDisplayName()).color(dimension.getColor())));
            player.sendMessage(MetaversePlugin.prefix.
                    append(Component.text("your dimension has been set to: ").color(NamedTextColor.GREEN)).
                    append(Component.text(dimension.getDisplayName()).color(dimension.getColor())));
            return true;
        }

        if (sender instanceof Player player) {
            DimensionManager.setDimension(player, dimension);
            player.sendMessage(MetaversePlugin.prefix.
                    append(Component.text("your dimension has been set to: ").color(NamedTextColor.GREEN)).
                    append(Component.text(dimension.getDisplayName()).color(dimension.getColor())));
            return true;
        }

        sender.sendMessage(MetaversePlugin.prefix.
                append(Component.text("Unable to set anyone's dimension.").color(NamedTextColor.RED)));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> collections = new ArrayList<>();
        if (!sender.isOp()) return collections;

        if (args.length == 2) {
            for (Dimension dimension : DimensionManager.dimensions)
                collections.add(dimension.getDisplayName().replace(" ", "").toLowerCase());

        }

        if (args.length == 3) {
            for (Player player : Bukkit.getOnlinePlayers())
                collections.add(player.getName());

        }
        return collections;
    }
}
