package net.Abilities.Items.ElysianWilds;

import net.Abilities.Model.Item;
import net.Managers.TrustManager;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class NaturesMace extends Item implements Listener {

    private final Plugin plugin;

    public NaturesMace(Plugin plugin) {
        super("Natures Mace", 90, TextColor.color(14, 176, 73), "", 102);
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private final List<UUID> activePlayers = new ArrayList<>();

    @Override
    public boolean onExecute(Player player) {
        UUID uuid = player.getUniqueId();
        activePlayers.add(uuid);

        setGlowing(player);
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!activePlayers.contains(uuid) || !player.isOnline()) {
                    player.setGlowing(false);
                    cancel();
                    return;
                }

                player.getWorld().spawnParticle(Particle.DUST, player.getLocation().add(0, 1, 0), 8, 0.3, 0.5, 0.3, 0,
                        new Particle.DustOptions(org.bukkit.Color.fromRGB(50, 205, 50), 1));

                ticks++;
                if (ticks >= 40) {
                    activePlayers.remove(uuid);
                    stopGlowing(player);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 5L);
        return true;
    }


    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        Random random = new Random();
        boolean shouldBlockAll = random.nextInt(100) < 20;

        if (!(event.getEntity() instanceof Player player)) return;
        if (!activePlayers.contains(player.getUniqueId())) return;

        if (!(event.getDamager() instanceof LivingEntity entity)) {
            if (shouldBlockAll)
                blockAll(event, player);
            else
                event.setDamage(event.getDamage() / 2);
            return;
        }

        if (shouldBlockAll) {
            blockAll(event, player);
            entity.damage(event.getDamage(), player);
        } else {
            entity.damage(event.getDamage() / 2, player);
            event.setDamage(event.getDamage() / 2);
        }
    }

    private void blockAll(EntityDamageByEntityEvent event, Player player) {
        event.setCancelled(true);

        Location loc = player.getLocation();
        World world = loc.getWorld();

        world.playSound(loc, Sound.ENTITY_IRON_GOLEM_DAMAGE, 1f, 1f);

        world.spawnParticle(Particle.BLOCK, loc, 30, 1, 1.5, 1, Material.MOSS_BLOCK.createBlockData());
        world.spawnParticle(Particle.HAPPY_VILLAGER, loc.add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);
    }

    private void setGlowing(Player player) {
        player.setGlowing(true);

        Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        player.setScoreboard(mainScoreboard);
        Team team = mainScoreboard.registerNewTeam(ChatColor.GREEN + player.getUniqueId().toString());
        team.setColor(org.bukkit.ChatColor.GREEN);

        team.addEntry(player.getName());
    }

    private void stopGlowing(Player player) {
        player.setGlowing(false);

        Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = mainScoreboard.getTeam(ChatColor.GREEN + player.getUniqueId().toString());

        if (team != null) {
            team.removeEntry(player.getName());
            team.unregister();
        }
    }

    @Override
    public void onDamage(Player player, LivingEntity target) {
        if (!(player.getFallDistance() > 2)) return;
        Location center = target.getLocation().clone();

        spawnGreenRing(center);

        for (LivingEntity entity : center.getNearbyEntitiesByType(LivingEntity.class, 5,5,5)) {
            if (entity.equals(player)) continue;
            if (TrustManager.isTrusted(player, entity.getUniqueId())) continue;

            entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON,20 * 10,3));
        }
    }

    private void spawnGreenRing(Location location) {
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
                            new Particle.DustOptions(org.bukkit.Color.fromRGB(50, 205, 50),2));
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

        meta.setDisplayName(ChatColor.of(new Color(14, 176, 73)) + getName());
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

        lore.add(ChatColor.of(new Color(14, 176, 73)) + "a powerful mace used to-");
        lore.add(ChatColor.of(new Color(14, 176, 73)) + "upgrade your movement");
        lore.add(" ");

        lore.add(ChatColor.of(new Color(1, 102, 31)) + "On Hit:");
        lore.add(ChatColor.of(new Color(51, 214, 100)) + "it will knock back all the players in a-");
        lore.add(ChatColor.of(new Color(51, 214, 100)) + "10 block radius and damage them.");
        lore.add(" ");

        lore.add(ChatColor.of(new Color(1, 102, 31)) + "on right click:");
        lore.add(ChatColor.of(new Color(51, 214, 100)) + "a dash that will boost you 10 blocks");
        return lore;
    }
}
