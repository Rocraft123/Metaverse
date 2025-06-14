package net.Commands.Ability;

import net.Abilities.Model.Ability;
import net.Commands.CommandExtension;
import net.Managers.AbilityManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.metaversePlugin.MetaversePlugin;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WithdrawAbility extends CommandExtension {

    public WithdrawAbility() {
        super("withdraw", "will put the ability item in your inventory");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length < 2) return true;
        String slot = args[1];

        return withdrawAbility(slot, player);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 2)
            return List.of("main", "inventory");
        return null;
    }

    public static boolean withdrawAbility(String slot, Player player) {
        Ability ability = slot.equalsIgnoreCase("main") ? AbilityManager.getSelectedAbility(player) :
                AbilityManager.getSecondAbility(player);

        if (ability == null) {
            player.sendMessage(MetaversePlugin.prefix.append(
                    Component.text("You do not have an ability in that slot.").color(NamedTextColor.RED)));
            return false;
        }

        Inventory inventory = player.getInventory();
        if (inventory.firstEmpty() == -1) {
            player.sendMessage(MetaversePlugin.prefix.append(
                    Component.text("Make sure to have at least 1 free slot.").color(NamedTextColor.RED)));
            return false;
        }
        inventory.setItem(inventory.firstEmpty(), ability.getItemstack());
        if (slot.equalsIgnoreCase("main"))
            AbilityManager.setSelectedAbility(player, null);
        else
            AbilityManager.setSecondAbility(player, null);

        player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE,1,1);
        return true;
    }

    public static void withdrawAbility(String slot, Player player, boolean messages) {
        Ability ability = slot.equalsIgnoreCase("main") ? AbilityManager.getSelectedAbility(player) :
                AbilityManager.getSecondAbility(player);

        if (ability == null)
            return;

        Inventory inventory = player.getInventory();
        if (inventory.firstEmpty() == -1)
            return;

        inventory.setItem(inventory.firstEmpty(), ability.getItemstack());
        if (slot.equalsIgnoreCase("main"))
            AbilityManager.setSelectedAbility(player, null);
        else
            AbilityManager.setSecondAbility(player, null);

        player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE,1,1);
    }
}
