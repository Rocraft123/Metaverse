package net.Abilities.Items.ShatteredRealm;

import net.Abilities.Model.Ability;
import net.Abilities.Model.Item;
import net.Managers.AbilityManager;
import net.Managers.ItemManager;
import net.Managers.TrustManager;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.Color;
import java.util.*;

@SuppressWarnings("deprecation")
public class ShatteredSword extends Item implements Listener {

    private final Plugin plugin;

    public ShatteredSword(Plugin plugin) {
        super("Shattered Sword", 210, TextColor.color(179, 85, 237), "", 500);
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private final Set<UUID> noFall = new HashSet<>();

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
    public boolean onExecute(Player player) {
        if (!player.isSneaking()) return false;

        Vector vector = new Vector(0,10,0);
        player.setVelocity(vector);
        noFall.add(player.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                Vector vector = new Vector(0,-5,0);
                player.setVelocity(vector);
                spawnSword(player);
            }
        }.runTaskLater(plugin, 40);
        return true;
    }

    private void spawnSword(Player player) {
        World world = player.getWorld();

        Location startLoc = player.getLocation().clone();
        startLoc.setYaw(0);
        startLoc.setPitch(0);

        ItemDisplay sword = (ItemDisplay) world.spawnEntity(startLoc, EntityType.ITEM_DISPLAY);
        sword.setBillboard(Display.Billboard.FIXED);
        sword.setItemStack(getItemstack());

        Quaternionf rotation = new Quaternionf().rotateZ((float) Math.toRadians(135));
        sword.setTransformation(new Transformation(
                new Vector3f(0, 0, 0),
                rotation,
                new Vector3f(20F, 20F, 20F),
                new Quaternionf()
        ));

        teleportSword(player, sword);
    }

    private void teleportSword(Player player, ItemDisplay sword) {
        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (tick >= 16) {
                    onImpact(player, sword);
                    cancel();
                    return;
                }

                Location location = player.getLocation();
                location.setYaw(0);
                location.setPitch(0);

                sword.teleport(location);
                tick++;
            }
        }.runTaskTimer(plugin, 0,1);
    }

    private void onImpact(Player player, ItemDisplay sword) {
        World world = sword.getWorld();
        Location impact = sword.getLocation();

        world.playSound(impact, Sound.ENTITY_GENERIC_EXPLODE, 2f, 0.5f);

        for (int i = 0; i < 360; i += 10) {
            double radians = Math.toRadians(i);
            double x = Math.cos(radians) * 3;
            double z = Math.sin(radians) * 3;
            Location particleLoc = impact.clone().add(x, 0.1, z);
            world.spawnParticle(Particle.EXPLOSION, particleLoc, 1, 0, 0, 0);
        }

        world.spawnParticle(Particle.FLASH, impact, 1);
        world.spawnParticle(Particle.CLOUD, impact, 150, 2, 0.2, 2, 0.05);
        world.spawnParticle(Particle.DUST, impact, 300, 3, 1, 3, new Particle.DustOptions(org.bukkit.Color.fromRGB(139, 26, 201), 2f));

        for (LivingEntity entity : impact.getNearbyLivingEntities(30, 15, 30)) {
            if (entity.equals(player) || entity.equals(sword) || TrustManager.isTrusted(player, entity.getUniqueId())) continue;
            entity.damage(70, player);
        }

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick >= 5) {
                    sword.remove();
                    noFall.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                for (LivingEntity entity : impact.getNearbyLivingEntities(20, 10, 20)) {
                    if (entity.equals(player) || entity.equals(sword) || TrustManager.isTrusted(player, entity.getUniqueId())) continue;
                    entity.damage(40, player);
                }

                world.spawnParticle(
                        Particle.DUST,
                        impact.clone().add(0, 1, 0),
                        400,
                        10, 5, 10,
                        new Particle.DustOptions(org.bukkit.Color.fromRGB(92, 8, 138), 2.5f)
                );

                tick++;
            }
        }.runTaskTimer(plugin, 0, 100);
    }

    @Override
    public void onDamage(Player player, LivingEntity target) {
        if (new Random().nextInt(100) > 10 || TrustManager.isTrusted(player, target.getUniqueId())) return;
        playSweepAnimation(player.getLocation());
        Collection<LivingEntity> entities = target.getLocation().getNearbyLivingEntities(3,2,3);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_AXE_SCRAPE,1,1);

        for (LivingEntity currentTarget : entities) {
            if (currentTarget.equals(player) || TrustManager.isTrusted(player, currentTarget.getUniqueId())) continue;
            currentTarget.damage(15);

            if (!(currentTarget instanceof Player targetPlayer)) continue;

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
            }.runTaskLater(plugin, 100);
        }
    }

    private void playSweepAnimation(Location loc) {

    }

    @Override
    public @NotNull ItemStack getItemstack() {
        ItemStack itemStack = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null)
            return itemStack;

        meta.setDisplayName(ChatColor.of(new Color(179, 85, 237)) + getName());
        meta.setLore(lore());

        meta.setUnbreakable(true);
        meta.setFireResistant(true);
        meta.setRarity(ItemRarity.EPIC);

        //meta.setItemModel(new NamespacedKey("metaverse", ""));

        meta.setCustomModelData(getItemID());
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private @NotNull java.util.List<String> lore() {
        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.of(new Color(196, 97, 255)) + "a powerful sword used to-");
        lore.add(ChatColor.of(new Color(196, 97, 255)) + "shatter your enemies!");
        lore.add(" ");

        lore.add(ChatColor.of(new Color(78, 14, 117)) + "On Hit:");
        lore.add(ChatColor.of(new Color(145, 47, 204)) + "Has a 5% change of doing a sweep attack");
        lore.add(ChatColor.of(new Color(145, 47, 204)) + "This will double the damage, damage nearby-");
        lore.add(ChatColor.of(new Color(145, 47, 204)) + "players and disable their abilities.");
        lore.add(" ");

        lore.add(ChatColor.of(new Color(78, 14, 117)) + "on shift + right click:");
        lore.add(ChatColor.of(new Color(145, 47, 204)) + "Will spawn a massive sword damaging nearby players!");

        return lore;
    }
}
