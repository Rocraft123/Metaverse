package net.Abilities.Abilities.ElysianWilds;

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
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class LeafDrift extends Ability {

    private final Plugin plugin;

    public LeafDrift(Plugin plugin) {
        super("Leaf Drift", 120, TextColor.color(245, 113, 232), "\uE002");
        this.plugin = plugin;
        this.setKey("leaf_drift");
    }

    @Override
    public boolean onExecute(Player player) {
        @NotNull Collection<LivingEntity> targets = player.getLocation().getNearbyLivingEntities(15,15,15);
        spawnSmallLeafStorm(player.getLocation());

        for (LivingEntity target : targets) {
            if (TrustManager.isTrusted(player, target.getUniqueId()) || target.equals(player)) {
                givePositiveEffects(target);
            } else {
                giveNegativeEffects(target);
                if (target instanceof Player targetPlayer)
                    spawnLeafStorm(player.getLocation(), targetPlayer);
            }
        }
        return true;
    }

    private void spawnLeafStorm(Location center, Player target) {
        new BukkitRunnable() {
            int tick = 0;
            final int height = 15;
            final int duration = 20 * 10;
            final double radius = 15;

            @Override
            public void run() {
                if (tick > duration || !target.isOnline()) {
                    cancel();
                    return;
                }

                for (int i = 0; i < 300; i++) {
                    double angle = Math.random() * 2 * Math.PI;
                    double r = Math.random() * radius;
                    double x = Math.cos(angle) * r;
                    double z = Math.sin(angle) * r;
                    double y = Math.random() * height;

                    Location particleLoc = center.clone().add(x, y, z);
                    target.spawnParticle(Particle.PALE_OAK_LEAVES, particleLoc, 1, 0.5, 0.5, 0.5, 0.5);
                }

                tick++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }


    private void spawnSmallLeafStorm(Location center) {
        new BukkitRunnable() {
            int tick = 0;
            final int height = 15;
            final int duration = 20 * 10;
            final double radius = 15;

            @Override
            public void run() {
                if (tick > duration) {
                    cancel();
                    return;
                }

                for (int i = 0; i < 10; i++) {
                    double angle = Math.random() * 2 * Math.PI;
                    double r = Math.random() * radius;
                    double x = Math.cos(angle) * r;
                    double z = Math.sin(angle) * r;
                    double y = Math.random() * height;

                    Location particleLoc = center.clone().add(x, y, z);
                    center.getWorld().spawnParticle(Particle.PALE_OAK_LEAVES, particleLoc, 1, 0.5, 0.5, 0.5, 0.5);
                }

                tick++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }


    private void givePositiveEffects(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,20 * 30,2));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,20 * 90,1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,20 * 90,1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.HASTE,20 * 90,1));
    }

    private void giveNegativeEffects(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,20 * 60,1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,20 * 10,1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE,20 * 10,1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS,20 * 10,1));
    }
}
