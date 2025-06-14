package net.Abilities.Items.ShatteredRealm;

import net.Abilities.Model.Ability;
import net.Abilities.Model.Item;
import net.Managers.AbilityManager;
import net.Managers.ItemManager;
import net.Managers.TrustManager;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ShatteredMace extends Item {

    private final Plugin plugin;

    public ShatteredMace(Plugin plugin) {
        super("Shattered Mace", 120, TextColor.color(168, 26, 196), "", 103);
        this.plugin = plugin;
        setExecuteSound(Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE);
    }

    @Override
    public boolean onExecute(Player player) {
        return true;
    }

    @Override
    public void onDamage(Player player, LivingEntity target) {
        if (!(player.getFallDistance() > 2)) return;
        Location center = target.getLocation().clone();

        spawnParticleRing(center);

        for (LivingEntity entity : center.getNearbyEntitiesByType(LivingEntity.class, 10,10,10)) {
            if (entity.equals(player) || TrustManager.isTrusted(player, entity.getUniqueId())) continue;

            if (!(entity instanceof Player targetPlayer)) continue;
            Ability ability = AbilityManager.getSelectedAbility(targetPlayer);
            if (ability == null) ability = ItemManager.getInstance().getLastUsedItem(targetPlayer);
            if (ability == null) return;

            Ability finalAbility = ability;
            finalAbility.setDisabled(targetPlayer, true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    finalAbility.setDisabled(targetPlayer, false);
                }
            }.runTaskLater(plugin, 200);
        }
    }

    private void spawnParticleRing(Location location) {
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
                    location.getWorld().spawnParticle(Particle.DUST, particleLoc, 2, 0.05, 0.05, 0.05, 0,
                            new Particle.DustOptions(org.bukkit.Color.fromRGB(188, 87, 235), 1));
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

        meta.setDisplayName(ChatColor.of(new Color(168, 26, 196)) + getName());
        meta.setLore(lore());

        meta.setUnbreakable(true);
        meta.setFireResistant(true);
        meta.setRarity(ItemRarity.EPIC);

        meta.setCustomModelData(getItemID());
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private @NotNull java.util.List<String> lore() {
        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.of(new Color(247, 224, 223)) + "a powerful mace used to-");
        lore.add(ChatColor.of(new Color(247, 224, 223)) + "upgrade your movement");
        lore.add(" ");

        lore.add(ChatColor.of(new Color(117, 224, 205)) + "On Hit:");
        lore.add(ChatColor.of(new Color(117, 224, 205)) + "it will shatter all the abilities of all the players-");
        lore.add(ChatColor.of(new Color(117, 224, 205)) + "in a 10 block radius and damage them.");
        lore.add(" ");

        lore.add(ChatColor.of(new Color(117, 224, 205)) + "on right click:");
       // lore.add(ChatColor.of(new Color(117, 224, 205)) + "a dash that will boost you 10 blocks");

        return lore;
    }
}
