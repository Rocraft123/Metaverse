package net.Utils.Listeners;

import net.Abilities.Model.Ability;
import net.Dimensions.Dimension;
import net.Events.PlayerActivateAbilityEvent;
import net.Managers.DimensionManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CustomEventListener implements Listener {

    @EventHandler
    public void onAbilityExecute(PlayerActivateAbilityEvent event) {
        Ability ability = event.getAbility();
        Player player = event.getPlayer();

        if (ability.isDisabled(player)) {
            player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE,1,1);
            event.setCancelled(true);
            return;
        }

        Dimension abilityDimension = DimensionManager.getDimension(ability);
        Dimension playerDimension = DimensionManager.getDimension(player);

        if (abilityDimension == null || playerDimension == null) return;
        if (!abilityDimension.equals(playerDimension) && !hasDragonEgg(player) && !player.isOp())
            event.setCancelled(true);
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
