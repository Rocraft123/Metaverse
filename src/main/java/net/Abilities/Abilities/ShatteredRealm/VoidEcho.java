package net.Abilities.Abilities.ShatteredRealm;

import net.Abilities.Model.Ability;
import net.Events.PlayerActivateAbilityEvent;
import net.Managers.AbilityManager;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class VoidEcho extends Ability implements Listener {

    private final Plugin plugin;

    public VoidEcho(Plugin plugin) {
        super("Void Echo", 0, TextColor.color(30, 7, 41), "");
        this.setKey("void_echo");
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void echoAbility(PlayerActivateAbilityEvent event) {
        Player player = event.getPlayer();
        Ability ability = event.getAbility();

        Ability main = AbilityManager.getSelectedAbility(player);
        Ability inv = AbilityManager.getSecondAbility(player);

        if ((main == null || main.getClass() != this.getClass()) &&
                (inv == null || inv.getClass() != this.getClass())) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                ability.onExecute(player);
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SCULK_SHRIEKER_SHRIEK,1,1);

                player.getWorld().spawnParticle(Particle.SHRIEK, player.getLocation().add(0, 1, 0),
                        20, 0.5, 1, 0.5, 0.01, 6);
            }
        }.runTaskLater(plugin, 200);
    }

    @Override
    public boolean onExecute(Player player) {
        return false;
    }
}
