package net.Utils.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class SnowBallListener implements Listener {

    @EventHandler
    public void onSnowballHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Snowball snowball)) return;
        if (!(snowball.getShooter() instanceof Player shooter)) return;

        if (!snowball.hasMetadata("snowballPower")) return;

        double power = snowball.getMetadata("snowballPower").get(0).asDouble();
        event.setDamage(power);
    }

}
