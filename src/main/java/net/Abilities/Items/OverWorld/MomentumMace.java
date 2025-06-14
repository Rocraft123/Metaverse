package net.Abilities.Items.OverWorld;

import net.Abilities.Model.Item;
import net.Managers.TrustManager;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.List;
import java.util.*;

@SuppressWarnings("deprecation")
public class MomentumMace extends Item implements Listener {

    private final Plugin plugin;

    public MomentumMace(Plugin plugin) {
        super("Momentum Mace", 30, TextColor.color(247, 224, 223), "", 100);
        setExecuteSound(Sound.ENTITY_BREEZE_WIND_BURST);
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private final Set<UUID> noFall = new HashSet<>();

    @Override
    public boolean onExecute(Player player) {
        Vector dir = player.getEyeLocation().getDirection().normalize().multiply(3);
        player.setVelocity(dir);

        noFall.add(player.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                noFall.remove(player.getUniqueId());
            }
        }.runTaskLater(plugin, 100L);

        return true;
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL && noFall.contains(player.getUniqueId())) {
                event.setCancelled(true);
                noFall.remove(player.getUniqueId());
            }
        }
    }


    @Override
    public void onDamage(Player player, LivingEntity target) {
        if (!(player.getFallDistance() > 2)) return;
        Location center = target.getLocation().clone();

        spawnCloudRing(center);

        for (LivingEntity entity : center.getNearbyEntitiesByType(LivingEntity.class, 5,5,5)) {
            if (entity.equals(player)) continue;
            if (entity.equals(target)) continue;
            if (TrustManager.isTrusted(player, entity.getUniqueId())) continue;

            entity.damage(25);
            entity.knockback(0.2,0.2,0.2);
        }
    }

    private void spawnCloudRing(Location location) {
        new BukkitRunnable() {
            double radius = 1.0;
            final double maxRadius = 5.0;
            final double step = 0.5;

            @Override
            public void run() {
                if (radius > maxRadius) {
                    cancel();
                    return;
                }

                int particles = 20;
                for (int i = 0; i < particles; i++) {
                    double angle = 2 * Math.PI * i / particles;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);

                    Location particleLoc = location.clone().add(x, 0.1, z);
                    location.getWorld().spawnParticle(Particle.CLOUD, particleLoc, 2, 0.05, 0.05, 0.05, 0);
                }

                radius += step;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    @Override
    public @NotNull ItemStack getItemstack() {
        ItemStack itemStack = new ItemStack(Material.MACE);
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null)
            return itemStack;

        meta.setDisplayName(ChatColor.of(new Color(247, 224, 223)) + getName());
        meta.setLore(lore());

        meta.setUnbreakable(true);
        meta.setFireResistant(true);
        meta.setRarity(ItemRarity.EPIC);

        meta.setCustomModelData(getItemID());
        meta.setItemModel(new NamespacedKey("metaverse", "momentum_mace"));

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private @NotNull List<String> lore() {
        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.of(new Color(247, 224, 223)) + "a powerful mace used to-");
        lore.add(ChatColor.of(new Color(247, 224, 223)) + "upgrade your movement");
        lore.add(" ");

        lore.add(ChatColor.of(new Color(117, 224, 205)) + "On Hit:");
        lore.add(ChatColor.of(new Color(117, 224, 205)) + "it will knock back all the players in a-");
        lore.add(ChatColor.of(new Color(117, 224, 205)) + "10 block radius and damage them.");
        lore.add(" ");

        lore.add(ChatColor.of(new Color(117, 224, 205)) + "on right click:");
        lore.add(ChatColor.of(new Color(117, 224, 205)) + "a dash that will boost you 10 blocks");
        return lore;
    }
}
