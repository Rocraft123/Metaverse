package net.Abilities.Abilities.ElysianWilds;

import net.Abilities.Model.Ability;
import net.Abilities.Model.Cooldown;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Wildstride extends Ability implements Listener {

    private final Plugin plugin;

    public Wildstride(Plugin plugin) {
        super("Wildstride", 120, TextColor.color(94, 242, 122), "\uE004");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        this.setKey("wildstride");
    }

    private final List<Player> users = new ArrayList<>();
    private final HashMap<UUID, Cooldown> doubleJumpCooldown = new HashMap<>();
    private final HashMap<UUID, Cooldown> grapplingCooldown = new HashMap<>();

    @Override
    public boolean onExecute(Player player) {
        users.add(player);
        player.setAllowFlight(true);

        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (!player.isValid() || player.isDead() || tick >= 20 * 30) {
                    cancel();
                    return;
                }

                Location loc = player.getLocation().add(0,0.5,0);
                loc.getWorld().spawnParticle(Particle.PALE_OAK_LEAVES,loc,2,0.2,0.2,0.2,1);
                tick++;
            }
        }.runTaskTimer(plugin,0,1);

        new BukkitRunnable() {
            @Override
            public void run() {
                users.remove(player);
                player.setAllowFlight(false);
                player.setWalkSpeed(0.2F);
            }
        }.runTaskLater(plugin, 20 * 30);
        return true;
    }

    @EventHandler
    public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
        Player player = event.getPlayer();
        if (!users.contains(player)) return;

        player.setWalkSpeed(0.2F);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!users.contains(player)) return;

        if (!player.isSprinting()) return;
        if (player.getWalkSpeed() + 0.001F > 1F) return;
        player.setWalkSpeed(player.getWalkSpeed() + 0.001F);
    }

    @EventHandler
    public void doubleJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (!users.contains(player)) return;
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;

        event.setCancelled(true);
        Cooldown cooldown = doubleJumpCooldown.get(player.getUniqueId());
        if (cooldown == null || cooldown.hasFinished()) {

            Vector jump = player.getLocation().getDirection().multiply(0.8).setY(1);
            player.setVelocity(jump);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WIND_CHARGE_WIND_BURST, 1, 1);

            doubleJumpCooldown.put(player.getUniqueId(), new Cooldown(5));
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!users.contains(player)) return;
        if (!event.isSneaking()) return;

        Cooldown cooldown = grapplingCooldown.get(player.getUniqueId());
        if (cooldown == null || cooldown.hasFinished()) {
            World world = player.getWorld();
            Location start = player.getEyeLocation();
            Vector direction = start.getDirection().normalize();

            BlockIterator iterator = new BlockIterator(world, start.toVector(), direction, 0, 20);
            Location end = start.clone().add(direction.clone().multiply(20));
            while (iterator.hasNext()) {
                Block block = iterator.next();
                if (block.getType().isSolid()) {
                    end = block.getLocation().add(0.5, 1, 0.5);
                    break;
                }
            }

            Particle.DustOptions brown = new Particle.DustOptions(org.bukkit.Color.fromRGB(101, 67, 33), 1.2F);
            Particle.DustOptions green = new Particle.DustOptions(org.bukkit.Color.fromRGB(34, 139, 34), 1.2F);

            Location finalEnd = end.clone();

            new BukkitRunnable() {
                double tick = 0;
                @Override
                public void run() {
                    if (tick > 1.0) {
                        Vector pull = finalEnd.clone().subtract(player.getLocation()).toVector().multiply(0.3);
                        player.setVelocity(pull);
                        this.cancel();
                        return;
                    }

                    Location point = start.clone().add(finalEnd.clone().subtract(start).multiply(tick));
                    world.spawnParticle(Particle.DUST, point, 0, 0, 0, 0, brown);

                    if ((int) (tick * 100) % 7 == 0) {
                        world.spawnParticle(Particle.DUST, point.clone().add(0, 0.2, 0), 0, 0, 0, 0, green);
                    }

                    tick += 0.05;
                }
            }.runTaskTimer(plugin, 0, 1);

            grapplingCooldown.put(player.getUniqueId(), new Cooldown(15));
        }
    }
}
