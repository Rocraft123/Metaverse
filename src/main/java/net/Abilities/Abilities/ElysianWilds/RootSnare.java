package net.Abilities.Abilities.ElysianWilds;

import net.Abilities.Model.Ability;
import net.Managers.TrustManager;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("deprecation")
public class RootSnare extends Ability {

    private final Plugin plugin;

    public RootSnare(Plugin plugin) {
        super("Root Snare", 75, TextColor.color(168, 81, 47), "\uE000");
        this.plugin = plugin;
    }

    @Override
    public boolean onExecute(Player player) {
        Collection<LivingEntity> targets = player.getLocation().getNearbyLivingEntities(15, 15, 15);
        for (LivingEntity entity : targets) {
            if (entity.equals(player) || TrustManager.isTrusted(player, entity.getUniqueId())) continue;
            growRootsToTarget(player, entity);
        }
        return true;
    }

    private void growRootsToTarget(Player from, LivingEntity to) {
        Particle.DustOptions root = new Particle.DustOptions(org.bukkit.Color.fromRGB(120, 62, 0), 1);
        Particle.DustOptions rootLeaves = new Particle.DustOptions(org.bukkit.Color.fromRGB(38, 173, 0), 1);

        Location start = from.getEyeLocation().clone();
        int steps = 40;

        new BukkitRunnable() {
            int tick = 0;
            Location currentStart = start.clone();
            @Override
            public void run() {
                if (tick >= steps || to.isDead()) {
                    cancel();
                    freezeTarget(to);
                    return;
                }

                Location currentEnd = to.getLocation().clone().add(0, 0.5, 0);
                Vector direction = currentEnd.toVector().subtract(currentStart.toVector()).normalize();
                double stepLength = currentStart.distance(currentEnd) / (steps - tick);

                Location point = currentStart.clone().add(direction.multiply(stepLength));
                currentStart = point.clone();

                from.getWorld().spawnParticle(Particle.DUST, point, 3, 0, 0, 0, 0, root);
                if (tick % 3 == 0) {
                    from.getWorld().spawnParticle(Particle.DUST, point.clone().add(0, 0.1, 0), 1, 0, 0, 0, 0, rootLeaves);
                }

                tick++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private void freezeTarget(LivingEntity target) {
        Particle.DustOptions root = new Particle.DustOptions(org.bukkit.Color.fromRGB(120, 62, 0), 1);
        Particle.DustOptions rootLeaves = new Particle.DustOptions(org.bukkit.Color.fromRGB(38, 173, 0), 1);
        Location center = target.getLocation().clone();

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick >= 100 || target.isDead()) {
                    cancel();
                    if (target instanceof Player player) {
                        player.setAllowFlight(false);
                        player.setFlying(false);
                    }
                    return;
                }

                target.teleport(center);
                if (target instanceof Player player) {
                    player.setAllowFlight(true);
                    player.setFlying(false);
                }

                double height = (tick % 20) * 0.05;
                double radius = 0.7;
                int points = 20;

                for (int i = 0; i < points; i++) {
                    double angle = (Math.PI * 2 / points) * i + tick * 0.1;
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    Location particleLoc = center.clone().add(x, height, z);
                    target.getWorld().spawnParticle(Particle.DUST, particleLoc, 0, 0, 0, 0, root);
                    if (i % 5 == 0) {
                        target.getWorld().spawnParticle(Particle.DUST, particleLoc.clone().add(0, 0.05, 0), 0, 0, 0, 0, rootLeaves);
                    }
                }
                tick++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public @NotNull ItemStack getItemstack() {
        ItemStack itemStack = new ItemStack(Material.ECHO_SHARD);
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null)
            return itemStack;

        meta.setDisplayName(ChatColor.of(new Color(204, 153, 255)) + "§l" + getName());
        meta.setRarity(ItemRarity.EPIC);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.of(new Color(177, 107, 255)) + "A mysterious fragment, pulsing with");
        lore.add(ChatColor.of(new Color(177, 107, 255)) + "arcane energy that bends to will.");
        lore.add("");
        lore.add(ChatColor.of(new Color(255, 128, 191)) + "§l" + "Right-click to awaken its gift.");
        lore.add(ChatColor.of(new Color(170, 85, 255)) + "Each shard carries a different essence.");
        lore.add("");
        lore.add(ChatColor.of(new Color(120, 60, 150)) + "Remnant of something ancient and unseen...");

        meta.setLore(lore);
        meta.setItemModel(new NamespacedKey("metaverse", "root_snare"));

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
