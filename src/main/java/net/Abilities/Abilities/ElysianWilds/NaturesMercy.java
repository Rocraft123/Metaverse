package net.Abilities.Abilities.ElysianWilds;

import net.Abilities.Model.Ability;
import net.Managers.TrustManager;
import net.Utils.Utils;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class NaturesMercy extends Ability implements Listener {

    private final Plugin plugin;

    public NaturesMercy(Plugin plugin) {
        super("Nature's Mercy", 120, TextColor.color(255, 231, 97), "\uE003");
        this.plugin = plugin;
        this.setKey("natures_mercy");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private final List<Player> shouldHeal = new ArrayList<>();

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!shouldHeal.contains(player)) return;

        List<Block> plants = getNearbyPlants(player);
        if (event.getFinalDamage() < player.getHealth()) return;

        if (plants.size() > 15) {
            event.setCancelled(true);
            player.heal(5, EntityRegainHealthEvent.RegainReason.ENDER_CRYSTAL);
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN,1,1);
            shouldHeal.remove(player);
        }
    }

    @Override
    public boolean onExecute(Player player) {
        shouldHeal.add(player);
        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (!player.isValid() || tick > 20) {
                    cancel();
                    shouldHeal.remove(player);
                    return;
                }

                tick++;
                List<Block> cachedPlants = getNearbyPlants(player);
                if (cachedPlants.isEmpty()) return;


                List<Entity> nearby = player.getNearbyEntities(15, 15, 15);
                if (nearby.isEmpty()) return;

                Entity target = nearby.get(new Random().nextInt(nearby.size()));
                if (!(target instanceof LivingEntity entity)) return;

                Block plant = cachedPlants.get(new Random().nextInt(cachedPlants.size()));

                if (TrustManager.isTrusted(player, target.getUniqueId()) || entity.equals(player)) {
                    spawnFlowParticles(plant.getLocation(), entity.getLocation(),
                            new Particle.DustTransition(Color.fromRGB(50, 205, 50),
                                    Color.fromRGB(255, 255, 150), 1.3f));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            entity.heal(3);
                            spawnHealParticles(target.getLocation());
                        }
                    }.runTaskLater(plugin,20);
                } else {
                    spawnFlowParticles(plant.getLocation(), entity.getLocation(),
                            new Particle.DustOptions(Color.fromRGB(255, 50, 50), 1.3f));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            entity.damage(7 + nearby.size());
                            spawnDamageParticles(target.getLocation());
                        }
                    }.runTaskLater(plugin,20);
                }
            }
        }.runTaskTimer(plugin, 0, 10L);
        return true;
    }

    private void spawnHealParticles(Location target) {
        Particle.DustTransition transition = new Particle.DustTransition(
                Color.fromRGB(50, 205, 50),
                Color.fromRGB(255, 255, 150),
                1.0f
        );
        target.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, target, 10, 0.2, 0.2, 0.2, 0.01, transition);

    }

    private void spawnDamageParticles(Location target) {
        Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 50, 50), 1.2f);
        target.getWorld().spawnParticle(Particle.DUST, target, 10, 0.2, 0.2, 0.2, 0.01, dust);
    }

    private void spawnFlowParticles(Location from, Location to, Particle.DustOptions dust) {
        new BukkitRunnable() {
            final Location start = from.clone().add(0.5, 0.7, 0.5);
            final Location end = to.clone().add(0, 1.2, 0);
            final int steps = 20;
            int currentStep = 0;

            @Override
            public void run() {
                if (currentStep > steps) {
                    cancel();
                    return;
                }

                double t = currentStep / (double) steps;
                Location point = start.clone().add(end.clone().subtract(start).toVector().multiply(t));

                double offsetX = (Math.random() - 0.5) * 0.05;
                double offsetY = (Math.random() - 0.5) * 0.05;
                double offsetZ = (Math.random() - 0.5) * 0.05;

                point.getWorld().spawnParticle(
                        Particle.DUST,
                        point.getX() + offsetX,
                        point.getY() + offsetY,
                        point.getZ() + offsetZ,
                        3, 0.1, 0.1, 0.1, 0.1,
                        dust
                );

                currentStep++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }


    private List<Block> getNearbyPlants(Player player) {
        Set<Material> plantTypes = Set.of(
                Material.FLOWERING_AZALEA_LEAVES, Material.MANGROVE_PROPAGULE, Material.FLOWERING_AZALEA,
                Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID, Material.ALLIUM, Material.AZURE_BLUET, Material.RED_TULIP,
                Material.ORANGE_TULIP, Material.WHITE_TULIP, Material.PINK_TULIP, Material.OXEYE_DAISY, Material.CORNFLOWER, Material.LILY_OF_THE_VALLEY,
                Material.TORCHFLOWER, Material.CLOSED_EYEBLOSSOM, Material.OPEN_EYEBLOSSOM, Material.WITHER_ROSE, Material.PINK_PETALS,
                Material.SPORE_BLOSSOM, Material.SUNFLOWER, Material.LILAC, Material.ROSE_BUSH, Material.PEONY, Material.PITCHER_PLANT,
                Material.CHORUS_FLOWER, Material.WEEPING_VINES, Material.TWISTING_VINES, Material.VINE, Material.LILY_PAD
        );
        return Utils.getNearbyBlocksByTypes(plantTypes, player.getLocation(), 20,6,20);
    }
}
