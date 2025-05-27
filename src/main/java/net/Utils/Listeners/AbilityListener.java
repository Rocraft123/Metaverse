package net.Utils.Listeners;

import net.Abilities.Model.Ability;
import net.Managers.AbilityManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.metaversePlugin.MetaversePlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
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

        if (AbilityManager.getSelectedAbility(player) == null)
            AbilityManager.setSelectedAbility(player, ability);
        else if (AbilityManager.getSecondAbility(player) == null)
            AbilityManager.setSecondAbility(player, ability);
        else {
            player.sendMessage(MetaversePlugin.prefix.append(
                    Component.text("No available slot.").color(NamedTextColor.RED)));
            return;
        }

        player.getInventory().remove(itemStack);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1);

        Location loc = player.getLocation().add(0, 1, 0);
        World world = player.getWorld();

        new BukkitRunnable() {
            double t = 0;
            @Override
            public void run() {
                t += Math.PI / 8;
                if (t > 2 * Math.PI) {
                    cancel();
                    return;
                }

                for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 4) {
                    double x = Math.cos(angle + t) * 0.5;
                    double z = Math.sin(angle + t) * 0.5;
                    world.spawnParticle(Particle.WITCH, loc.clone().add(x, t / 2, z), 1, 0, 0, 0, 0);
                }
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(MetaversePlugin.class), 0, 1L);
    }

}
