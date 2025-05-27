package net.Managers;

import net.Abilities.Model.Ability;
import net.Abilities.Model.Cooldown;
import net.Abilities.Model.Item;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActionBarManager extends BukkitRunnable {

    private static final List<UUID> disableActionBar = new ArrayList<>();

    public static void hideActionBar(Player player) {
        disableActionBar.add(player.getUniqueId());
    }

    public static void showActionBar(Player player) {
        disableActionBar.remove(player.getUniqueId());
    }

    public Component message(Player player) {
        Ability ability = AbilityManager.getSelectedAbility(player);
        Ability second = AbilityManager.getSecondAbility(player);

        ItemManager manager = ItemManager.getInstance();
        Item item = manager.getLastUsedItem(player);

        String abilityIcon = ability != null ? ability.getIcon() : nullIcon();
        String secondIcon = second != null ? second.getIcon() : nullIcon();
        String itemIcon = item != null ? item.getIcon() : nullIcon();

        Cooldown abilityCooldown = ability != null ? ability.getCooldown(player) : null;
        Cooldown secondCooldown = second != null ? second.getCooldown(player) : null;
        Cooldown itemCooldown = item != null ? item.getCooldown(player) : null;

        return Component.text()
                .append(compactBlock(abilityCooldown, abilityIcon))
                .append(Component.text(" | ").color(NamedTextColor.DARK_GRAY))
                .append(compactBlock(secondCooldown, secondIcon))
                .append(Component.text("     "))
                .append(compactBlock(itemCooldown, itemIcon))
                .build();
    }


    private String nullIcon() {
        return "\uE404";
    }

    private Component compactBlock(Cooldown cooldown, String icon) {
        long time = cooldown != null ? cooldown.getTimeLeft() : 0;
        long minutes = time / 60;
        long seconds = time % 60;

        String timeStr = time <= 0 ? " Ready" : String.format(" %dm %ds", minutes, seconds);
        return Component.text(icon + timeStr).color(NamedTextColor.WHITE);
    }


    public void start(Plugin plugin) {
        this.runTaskTimer(plugin,0,1);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (disableActionBar.contains(player.getUniqueId())) return;
            player.sendActionBar(message(player));
        }
    }
}
