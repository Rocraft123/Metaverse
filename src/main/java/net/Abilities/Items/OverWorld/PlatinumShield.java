package net.Abilities.Items.OverWorld;

import net.Abilities.Model.Item;
import net.Managers.TrustManager;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
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

import java.awt.*;
import java.util.*;
import java.util.List;

@SuppressWarnings("deprecation")
public class PlatinumShield extends Item {

    private final Plugin plugin;

    public PlatinumShield(Plugin plugin) {
        super("Platinum Shield", 120, TextColor.color(132, 138, 133), "", 200);
        this.plugin = plugin;
    }

    @Override
    public boolean onExecute(Player player) {
        if (!player.isSneaking()) return false;

        World world = player.getWorld();
        Vector forward = player.getLocation().getDirection().clone().setY(0).normalize();
        Vector right = forward.clone().crossProduct(new Vector(0, 1, 0)).normalize();

        Location initialSpawn = player.getEyeLocation()
                .add(forward.multiply(4.5))
                .add(right.multiply(2.5))
                .add(0, -0.5, 0);

        ItemDisplay shield = (ItemDisplay) world.spawnEntity(initialSpawn, EntityType.ITEM_DISPLAY);
        shield.setItemStack(getItemstack());

        Quaternionf rotation = new Quaternionf().rotateX((float) Math.toRadians(180));
        shield.setTransformation(new Transformation(
                new Vector3f(0, 0, 0),
                rotation,
                new Vector3f(5F, 5F, 5F),
                new Quaternionf()
        ));

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (!player.isOnline() || !shield.isValid() || tick >= 20 * 10) {
                    cancel();
                    return;
                }

                Vector forward = player.getLocation().getDirection().clone().setY(0).normalize();
                Vector right = forward.clone().crossProduct(new Vector(0, 1, 0)).normalize();

                Location updatedLoc = player.getEyeLocation()
                        .add(forward.multiply(4.5))
                        .add(right.multiply(2.5))
                        .add(0, -0.5, 0);

                shield.teleport(updatedLoc);
                shield.setRotation(player.getLocation().getYaw(), 0);

                for (LivingEntity entity : getEntitiesFrontShield(player)) {
                    if (entity.equals(player) || TrustManager.isTrusted(player, entity.getUniqueId())) return;
                    entity.damage(20);
                    entity.setVelocity(forward.multiply(0.15));
                }

                tick++;
            }
        }.runTaskTimer(plugin, 0, 1);
        new BukkitRunnable() {
            @Override
            public void run() {
                pushShield(player, shield);
            }
        }.runTaskLater(plugin, 20 * 10);
        new BukkitRunnable() {
            @Override
            public void run() {
                shield.remove();
            }
        }.runTaskLater(plugin, 20 * 12);
        return true;
    }

    private void pushShield(Player player, ItemDisplay shield) {
        Vector direction = shield.getLocation().getDirection().normalize();
        new BukkitRunnable() {
            double speed = 1.0;
            @Override
            public void run() {
                if (!shield.isValid()) {
                    cancel();
                    return;
                }

                Location currentLoc = shield.getLocation();
                Vector move = direction.clone().multiply(1.0);
                currentLoc.add(move);
                shield.teleport(currentLoc);

                for (Entity entity : shield.getNearbyEntities(4,10,4)) {
                    if (TrustManager.isTrusted(player, entity.getUniqueId())) continue;
                    if (entity instanceof LivingEntity living) {
                        living.damage(10);
                        living.setVelocity(direction.clone().multiply(1.5));
                    }
                }

                speed++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private List<LivingEntity> getEntitiesFrontShield(Player player) {
        double range = 3.0;
        double angle = 60.0;

        List<LivingEntity> inFront = new ArrayList<>();
        Location origin = player.getEyeLocation().add(0,-0.5,0);
        origin.add(origin.getDirection().multiply(1.5));

        Vector forward = origin.getDirection().normalize();

        for (Entity entity : player.getWorld().getNearbyEntities(origin, range, range, range)) {
            if (!(entity instanceof LivingEntity) || entity.equals(player) || TrustManager.isTrusted(player, entity.getUniqueId()))
                continue;

            Vector toEntity = entity.getLocation().toVector().subtract(origin.toVector());
            double distance = toEntity.length();
            if (distance > range) continue;

            double angleBetween = Math.toDegrees(forward.angle(toEntity.normalize()));
            if (angleBetween <= angle / 2)
                inFront.add((LivingEntity) entity);
        }
        return inFront;
    }

    @Override
    public void onDamage(Player player, LivingEntity target) {}

    @Override
    public @NotNull ItemStack getItemstack() {
        ItemStack itemStack = new ItemStack(Material.SHIELD);
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null)
            return itemStack;

        meta.setDisplayName(ChatColor.of(new Color(132, 138, 133)) + getName());
        meta.setLore(lore());

        meta.setUnbreakable(true);
        meta.setFireResistant(true);
        meta.setRarity(ItemRarity.EPIC);

        meta.setCustomModelData(getItemID());
        //meta.setItemModel(new NamespacedKey("metaverse", "momentum_mace"));

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private @NotNull java.util.List<String> lore() {
        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.of(new Color(201, 201, 201)) + "A shield used to protect you!");
        lore.add(" ");
        lore.add(ChatColor.of(new Color(77, 79, 78)) + "on shift + right click:");
        lore.add(ChatColor.of(new Color(153, 153, 153)) + "Will spawn a massive shield-");
        lore.add(ChatColor.of(new Color(153, 153, 153)) + "knocking back enemies and damaging them");
        return lore;
    }
}
