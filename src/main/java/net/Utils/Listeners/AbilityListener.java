package net.Utils.Listeners;

import net.Abilities.Model.Ability;
import net.Dimensions.Dimension;
import net.Managers.AbilityManager;
import net.Managers.DimensionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.metaversePlugin.MetaversePlugin;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AbilityListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if (itemStack == null || !event.getAction().isRightClick()) return;

        Ability ability = AbilityManager.getAbility(itemStack);
        if (ability == null) return;

        Dimension abilityDimension = DimensionManager.getDimension(ability);
        Dimension playerDimension = DimensionManager.getDimension(player);

        if (abilityDimension == null || playerDimension == null) return;
        if (!abilityDimension.equals(playerDimension) && !hasDragonEgg(player) && !player.isOp()) {
            player.sendMessage(MetaversePlugin.prefix.append(Component.text(
                    "The powers of this ability is out of your world!").color(TextColor.color(138, 95, 232))));
            return;
        }

        if (AbilityManager.getSelectedAbility(player) == null)
            AbilityManager.setSelectedAbility(player, ability);
        else if (AbilityManager.getSecondAbility(player) == null)
            AbilityManager.setSecondAbility(player, ability);
        else {
            player.sendMessage(MetaversePlugin.prefix.append(
                    Component.text("No available slot.").color(NamedTextColor.RED)));
            return;
        }

        player.getInventory().removeItem(itemStack);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1);

        Location loc = player.getLocation();
        World world = player.getWorld();

        new BukkitRunnable() {
            double tick = 0;
            @Override
            public void run() {
                tick += Math.PI / 8;
                if (tick > 2 * Math.PI) {
                    cancel();
                    return;
                }

                for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 4) {
                    double x = Math.cos(angle + tick) * 0.5;
                    double z = Math.sin(angle + tick) * 0.5;
                    world.spawnParticle(Particle.WITCH, loc.clone().add(x, tick, z), 1, 0, 0, 0, 0);
                }
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(MetaversePlugin.class), 0, 1L);
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
