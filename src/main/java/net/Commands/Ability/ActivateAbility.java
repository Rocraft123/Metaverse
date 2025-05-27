package net.Commands.Ability;

import net.Abilities.Model.Ability;
import net.Commands.CommandExtension;
import net.Managers.AbilityManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.metaversePlugin.MetaversePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ActivateAbility extends CommandExtension {

    public ActivateAbility() {
        super("activate", "Will activate the selected ability");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MetaversePlugin.prefix.append(
                    Component.text("Only players can use this command.").color(NamedTextColor.RED)));
            return true;
        }

        Ability ability = AbilityManager.getSelectedAbility(player);
        if (ability == null) {
            sender.sendMessage(MetaversePlugin.prefix.append(
                    Component.text("Could not activate the ability. make sure to select one first.").color(NamedTextColor.RED)));
            return true;
        }

        ability.execute(player);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return new ArrayList<>();
    }
}
