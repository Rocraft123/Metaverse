package net.Utils.Listeners;

import net.Abilities.Model.Ability;
import net.Commands.Ability.WithdrawAbility;
import net.Dimensions.Dimension;
import net.Managers.AbilityManager;
import net.Managers.DimensionManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DragonEggListener extends BukkitRunnable {

    public DragonEggListener(Plugin plugin) {
        this.runTaskTimer(plugin, 0,5);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (hasDragonEgg(player) || player.isOp()) return;
            Dimension dimension = DimensionManager.getDimension(player);
            if (dimension == null) return;

            Ability ability = AbilityManager.getSelectedAbility(player);
            Dimension abilityDimension = ability != null ? DimensionManager.getDimension(ability) : null;
            if (abilityDimension != null) {
                if (!dimension.equals(abilityDimension)) {
                    WithdrawAbility.withdrawAbility("main", player, false);
                }
            }

            Ability inv = AbilityManager.getSecondAbility(player);
            Dimension invDimension = inv != null ? DimensionManager.getDimension(inv) : null;
            if (invDimension != null) {
                if (!dimension.equals(invDimension)) {
                    WithdrawAbility.withdrawAbility("inventory", player, false);
                }
            }
        }
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
