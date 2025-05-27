package net.Commands.Ability;

import net.Abilities.Model.Ability;
import net.Commands.CommandExtension;
import net.Managers.AbilityManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.metaversePlugin.MetaversePlugin;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SwitchAbility extends CommandExtension {

    public SwitchAbility() {
        super("switch", "will switch the inventory Ability to the main");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return true;
        if (!hasDragonEgg(player)) {
            player.sendMessage(MetaversePlugin.prefix.append(Component.text(
                    "You do not have the power to use this command").color(NamedTextColor.DARK_PURPLE)));
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_HIT,1,1);
            return true;
        }

        Ability main = AbilityManager.getSelectedAbility(player);
        Ability inv = AbilityManager.getSecondAbility(player);


        AbilityManager.setSecondAbility(player, main);
        AbilityManager.setSelectedAbility(player, inv);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ALLAY_AMBIENT_WITH_ITEM,1,1);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return null;
    }

    private boolean hasDragonEgg(Player player) {
        Inventory inventory = player.getInventory();
        for (ItemStack item : inventory.getContents()) {
            if (item == null) continue;
            if (item.getType() == Material.DRAGON_EGG) return true;
        }
        return false;
    }
}
