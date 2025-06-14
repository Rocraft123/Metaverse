package net.Abilities.Abilities.ElysianWilds;

import net.Abilities.Model.Ability;
import net.Managers.ActionBarManager;
import net.Managers.TrustManager;
import net.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.metaversePlugin.MetaversePlugin;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class VerdantPulse extends Ability {

    private final Plugin plugin;
    public VerdantPulse(Plugin plugin) {
        super("Verdant Pulse", 90, TextColor.color(83, 207, 122), "\uE001");
        this.plugin = plugin;
        this.setKey("verdant_pulse");
    }

    private final HashMap<UUID, Double> burstEnergy = new HashMap<>();
    private final HashMap<UUID, BukkitTask> chargeTask = new HashMap<>();

    @Override
    public boolean onExecute(Player player) {
        setEnergy(player, 0);
        chargeBurst(player);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            ActionBarManager.showActionBar(player);
            chargeTask.get(player.getUniqueId()).cancel();
            releaseBurst(player);
        }, 20 * 5);
        return true;
    }

    private void releaseBurst(Player player) {
        final double energy = getEnergy(player);
        final Location origin = player.getLocation().clone().add(0, 1, 0);
        final World world = player.getWorld();
        final double maxRadius = 5 + (energy / 10);
        final Set<UUID> trusted = new HashSet<>();

        for (LivingEntity entity : origin.getNearbyLivingEntities(maxRadius)) {
            if (entity instanceof Player otherPlayer && TrustManager.isTrusted(player, otherPlayer.getUniqueId()))
                trusted.add(otherPlayer.getUniqueId());
        }

        player.getWorld().playSound(origin, Sound.BLOCK_BEACON_POWER_SELECT, 2f, 0.8f);

        new BukkitRunnable() {
            double radius = 1;
            @Override
            public void run() {
                if (radius > maxRadius) {
                    this.cancel();
                    return;
                }

                for (LivingEntity entity : origin.getNearbyLivingEntities(radius)) {
                    if (entity.getUniqueId().equals(player.getUniqueId())) continue;

                    if (trusted.contains(entity.getUniqueId())) {
                        entity.setHealth(Math.min(entity.getMaxHealth(), entity.getHealth() + getEnergy(player) / 50));
                        world.spawnParticle(Particle.HEART, entity.getLocation().add(0, 1, 0), 1);
                    } else {
                        entity.damage(getEnergy(player) / 8);
                        world.spawnParticle(Particle.TRIAL_SPAWNER_DETECTION_OMINOUS, entity.getLocation().add(0, 1, 0), 2);
                    }
                }

                radius += 1.0;
            }
        }.runTaskTimer(plugin, 0, 4L);
        new BukkitRunnable() {
            double radius = 1;
            int tick = 0;
            @Override
            public void run() {
                if (radius > maxRadius) {
                    this.cancel();
                    return;
                }
                Particle.DustOptions dustOptions = tick % 2 == 0 ? new Particle.DustOptions(Color.fromRGB(4, 10, 0),1)
                        : new Particle.DustOptions(Color.fromRGB(44, 92, 13),1);
                Utils.spawnParticleCircle(Particle.DUST, origin, radius,50 * radius / 2, dustOptions);

                radius += 0.2;

                Particle.DustOptions dustOption = tick % 2 == 0 ? new Particle.DustOptions(Color.fromRGB(4, 10, 0),1)
                        : new Particle.DustOptions(Color.fromRGB(44, 92, 13),1);
                Utils.spawnParticleCircle(Particle.DUST, origin, radius,50 * radius / 2, dustOption);
                radius += 0.2;
                tick++;
            }
        }.runTaskTimer(plugin,0,1);
    }

    private void chargeBurst(Player player) {
        BukkitTask task = new BukkitRunnable() {
            private Location oldLocation;
            @Override
            public void run() {
                if (!player.isValid() || getEnergy(player) > 100) {
                    cancel();
                    return;
                }

                Location newLocation = player.getLocation().clone();
            if (oldLocation == null || (newLocation.getX() == oldLocation.getX() && newLocation.getY() == oldLocation.getY() &&
                    newLocation.getZ() == oldLocation.getZ()))
                    addEnergy(player, 2);

                addEnergy(player, 1);
                oldLocation = newLocation;
                spawnChargingParticles(player);
            }
        }.runTaskTimer(plugin, 0,3L);
        chargeTask.put(player.getUniqueId(), task);
        displayActionBar(player);
        player.sendMessage(MetaversePlugin.prefix.append(
                Component.text("Stay calm to to charge up your powers.").color(NamedTextColor.GREEN)));
    }

    private void spawnChargingParticles(Player player) {
        Location center = player.getLocation().add(0, 1, 0);
        World world = center.getWorld();
        int count = 12;

        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count;
            double radius = 1.2;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            Location spawnLoc = center.clone().add(x, Math.random() * 0.5 - 0.25, z);
            @NotNull Vector direction = center.toVector().subtract(spawnLoc.toVector()).normalize().multiply(0.1);

            Particle.DustOptions greenDust = new Particle.DustOptions(Color.fromRGB(30, 180, 30), 1f);

            world.spawnParticle(Particle.DUST, spawnLoc, 0, direction.getX(), direction.getY(), direction.getZ(), greenDust);
            world.spawnParticle(Particle.HAPPY_VILLAGER, spawnLoc, 0, direction.getX(), direction.getY(), direction.getZ());
        }
    }

    private void displayActionBar(Player player) {
        ActionBarManager.hideActionBar(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isValid() || chargeTask.get(player.getUniqueId()) == null ||
                        chargeTask.get(player.getUniqueId()).isCancelled())
                    cancel();

                double power = getEnergy(player);
                String formatted = String.format("%.2f", power);
                player.sendActionBar(Component.text("Green energy: " + formatted + "%").color(getColor()));
            }
        }.runTaskTimer(plugin, 0,1);
    }

    private double getEnergy(Player player) {
        return burstEnergy.getOrDefault(player.getUniqueId(), 0.0);
    }

    private void setEnergy(Player player, double energy) {
        burstEnergy.put(player.getUniqueId(), energy);
    }

    private void addEnergy(Player player, double energy) {
        setEnergy(player, getEnergy(player) + energy);
    }
}
