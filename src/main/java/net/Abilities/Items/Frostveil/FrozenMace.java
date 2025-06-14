package net.Abilities.Items.Frostveil;

import net.Abilities.Model.Cooldown;
import net.Abilities.Model.Item;
import net.Managers.ActionBarManager;
import net.Managers.TrustManager;
import net.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import net.metaversePlugin.MetaversePlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.List;
import java.util.*;

@SuppressWarnings("deprecation")
public class FrozenMace extends Item {

    private final Plugin plugin;

    public FrozenMace(Plugin plugin) {
        super("Frozen Mace", 90, TextColor.color(117, 224, 205), "", 101);
        this.plugin = plugin;
    }

    private final HashMap<UUID, Double> snowballPower = new HashMap<>();
    private BukkitTask chargeTask;

    @Override
    public boolean onExecute(Player player) {
        displayActionBar(player);
        chargeSnowBall(player);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            chargeTask.cancel();
            fireSnowBall(player);
            ActionBarManager.showActionBar(player);
        }, 200);
        return true;
    }

    private void fireSnowBall(Player player) {
        double power = getSnowBallPower(player);
        if (power <= 0) return;

        Vector direction = player.getLocation().getDirection().normalize();
        double velocityMultiplier = Math.min(2.0, 0.5 + power / 20.0);

        Snowball snowball = player.launchProjectile(Snowball.class);
        snowball.setVelocity(direction.multiply(velocityMultiplier));
        snowball.setMetadata("snowballPower", new FixedMetadataValue(plugin, power));
        snowball.setCustomNameVisible(false);
        snowball.setGravity(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!snowball.isValid() || snowball.isOnGround()) {
                    this.cancel();
                    return;
                }

                snowball.getWorld().spawnParticle(
                        Particle.SNOWFLAKE,
                        snowball.getLocation(),
                        2, 0, 0, 0, 0.01
                );
            }
        }.runTaskTimer(plugin, 0L, 1L);
        snowballPower.put(player.getUniqueId(), 0.0);
    }

    private void chargeSnowBall(Player player) {
        chargeTask = new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (getSnowBallPower(player) >= 100) {
                    cancel();
                    return;
                }

                if (tick % 4 == 0) {
                    Block snowBlock = getNearbySnow(player.getLocation());
                    if (snowBlock != null) {
                        snowballPower.put(player.getUniqueId(), getSnowBallPower(player) + 2);
                        animateSnow(snowBlock.getLocation(), player);
                        snowBlock.setType(Material.AIR);
                    }
                }

                tick++;
                snowballPower.put(player.getUniqueId(), getSnowBallPower(player) + 0.05);
            }
        }.runTaskTimer(plugin, 0, 1L);
    }

    private void animateSnow(Location start, Player player) {
        FallingBlock snow = player.getWorld().spawnFallingBlock(start, Material.SNOW_BLOCK.createBlockData());
        snow.setGravity(false);
        snow.setDropItem(false);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (snow.isDead() || !player.isOnline() || chargeTask == null || chargeTask.isCancelled()) {
                    snow.remove();
                    cancel();
                    return;
                }

                Vector toPlayer = player.getLocation().add(0, 1.5, 0).toVector().subtract(snow.getLocation().toVector());
                double distance = toPlayer.length();

                if (distance < 1) {
                    snow.remove();
                    cancel();
                    return;
                }

                snow.setVelocity(toPlayer.normalize().multiply(1));
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private @Nullable Block getNearbySnow(Location center) {
        Set<Material> snow = Set.of(Material.SNOW, Material.SNOW_BLOCK);
        List<Block> blocks = Utils.getNearbyBlocksByTypes(snow, center, 10,10,10);
        return blocks.get(new Random().nextInt(blocks.size()));
    }

    private void displayActionBar(Player player) {
        ActionBarManager.hideActionBar(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isDead() || chargeTask == null || chargeTask.isCancelled())
                    cancel();

                double power = getSnowBallPower(player);
                String formatted = String.format("%.2f", power);
                player.sendActionBar(Component.text("Snowball power: " + formatted + "%").color(getColor()));
            }
        }.runTaskTimer(plugin, 0,1);
    }

    private double getSnowBallPower(Player player) {
        return snowballPower.getOrDefault(player.getUniqueId(), 0.0);
    }

    private Cooldown hitCooldown = new Cooldown(0);

    @Override
    public void onDamage(Player player, LivingEntity target) {
        if (!(player.getFallDistance() > 2)) return;
        if (!hitCooldown.hasFinished()) {
            player.sendMessage(MetaversePlugin.prefix.
                    append(Component.text(getName() + " is still on cooldown: " + hitCooldown.getTimeLeft())
                            .color(NamedTextColor.RED)));
            return;
        }

        Location center = target.getLocation().clone();
        player.getWorld().playSound(center, Sound.ENTITY_SNOW_GOLEM_HURT,1,1);

        for (LivingEntity entity : center.getNearbyEntitiesByType(LivingEntity.class, 5,5,5)) {
            if (entity.equals(player)) continue;
            if (TrustManager.isTrusted(player, entity.getUniqueId())) continue;

            new BukkitRunnable() {
                final Location center = entity.getLocation().clone();
                int tick = 0;
                @Override
                public void run() {
                    if (entity.isDead() || tick > 30) {
                        cancel();
                        return;
                    }

                    entity.setFreezeTicks(tick * 3);
                    entity.teleport(center);
                    tick++;
                }
            }.runTaskTimer(plugin,0,1);
        }
        hitCooldown = new Cooldown(15);
    }

    @Override
    public @NotNull ItemStack getItemstack() {
        ItemStack itemStack = new ItemStack(Material.MACE);
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null)
            return itemStack;

        meta.setDisplayName(ChatColor.of(new Color(117, 224, 205)) + getName());
        meta.setLore(lore());

        meta.setUnbreakable(true);
        meta.setFireResistant(true);
        meta.setRarity(ItemRarity.EPIC);

        meta.setCustomModelData(getItemID());
        meta.setItemModel(new NamespacedKey("metaverse", "frozen_mace"));

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private @NotNull List<String> lore() {
        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.of(new Color(117, 224, 205)) + "A frozen mace used to-");
        lore.add(ChatColor.of(new Color(117, 224, 205)) + "freeze your enemies.");
        lore.add(" ");

        lore.add(ChatColor.of(new Color(200, 240, 255)) + "On Hit:");
        lore.add(ChatColor.of(new Color(170, 220, 240)) + "will freeze all the players-");
        lore.add(ChatColor.of(new Color(150, 210, 230)) + "in a 10 block radius.");
        lore.add(" ");

        lore.add(ChatColor.of(new Color(200, 240, 255)) + "On Right Click:");
        lore.add(ChatColor.of(new Color(170, 220, 240)) + "Will charge up a snowball!");
        return lore;
    }
}
