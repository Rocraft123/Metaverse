package net.Utils.Listeners;

import io.papermc.paper.event.player.PlayerShieldDisableEvent;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ShieldSound implements Listener {

    @EventHandler
    public void shieldDisable(PlayerShieldDisableEvent event) {
        Entity entity = event.getDamager();
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK,1,1);

        if (entity instanceof Player damager)
            damager.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK,1,1);
    }
}
