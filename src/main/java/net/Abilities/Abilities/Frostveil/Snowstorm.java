package net.Abilities.Abilities.Frostveil;

import net.Abilities.Model.Ability;
import net.Managers.TrustManager;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Snowstorm extends Ability {

    private final Plugin plugin;

    public Snowstorm(Plugin plugin) {
        super("Snowstorm", 150, TextColor.color(143, 242, 255), "\uE010");
        this.plugin = plugin;
        this.setKey("null");

    }

    @Override
    public boolean onExecute(Player player) {
        Location center = player.getLocation().clone();
        spawnSmallSnowStorm(center);

        for (LivingEntity entity : player.getLocation().getNearbyLivingEntities(30,30,30)) {
            giveEffects(player, entity);

            if (entity instanceof Player target && !TrustManager.isTrusted(player, entity.getUniqueId())) {
                spawnClones(target);
                spawnSnowStorm(center, target);
            }
        }

        return true;
    }

    private void spawnSnowStorm(Location center, Player target) {
        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (tick > 20 * 30 || !target.isOnline()) {
                    cancel();
                    return;
                }

                for (int i = 0; i < 1000; i++) {
                    double angle = Math.random() * 2 * Math.PI;
                    double r = Math.random() * 30;
                    double x = Math.cos(angle) * r;
                    double z = Math.sin(angle) * r;
                    double y = Math.random() * 20;

                    Location particleLoc = center.clone().add(x, y, z);
                    target.spawnParticle(Particle.SNOWFLAKE, particleLoc, 0, 0.1, 0.1, 0.1);
                }

                tick++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }
    private void spawnSmallSnowStorm(Location center) {
        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (tick > 20 * 30) {
                    cancel();
                    return;
                }

                for (int i = 0; i < 50; i++) {
                    double angle = Math.random() * 2 * Math.PI;
                    double r = Math.random() * 30;
                    double x = Math.cos(angle) * r;
                    double z = Math.sin(angle) * r;
                    double y = Math.random() * 20;

                    Location particleLoc = center.clone().add(x, y, z);
                    center.getWorld().spawnParticle(Particle.SNOWFLAKE, particleLoc, 0, 0.1, 0.1, 0.1);
                }

                tick++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private void giveEffects(Player user, LivingEntity entity) {
        if (TrustManager.isTrusted(user, entity.getUniqueId()) || user.equals(entity)) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,1200 * 30,0));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,20 * 60,2));
        } else {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,20 * 30,1));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,20 * 20,2));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,20 * 20,2));
        }
    }

    private void spawnClones(Player player) {

    }

    private void spawnClone(Location location) {

    }
}
