package net.Utils.Ritual;

import net.Dimensions.Dimension;
import net.Items.AbilityBeacon.AbilityBeacon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.metaversePlugin.MetaversePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public record Ritual(Dimension dimension, Location location, Player player) {

    public void start(Plugin plugin) {
        startBroadcasting(plugin);
        for (Player player : Bukkit.getOnlinePlayers())
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE,1,1);

    }

    public void stop(RitualEnding ending, Plugin plugin) {
        if (ending == RitualEnding.FAIL) {
            for (Player player : Bukkit.getOnlinePlayers())
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE,1,1);

            Item item = (Item) location.getWorld().spawnEntity(location, EntityType.ITEM);
            item.setItemStack(new AbilityBeacon(plugin).getItemstack());
        }
    }

    public void startBroadcasting(Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isValid()) {
                    cancel();
                    return;
                }

                plugin.getServer().broadcast(broadcastMessage());
            }
        }.runTaskTimer(plugin, 0, 20 * 60 * 5);
    }

    public Component broadcastMessage() {
        return MetaversePlugin.prefix.append(Component.text()
                .append(Component.text(player.getName()).color(getMessageColors().get(0)).decorate(TextDecoration.BOLD))
                .append(Component.text(" has begun a ritual at ").color(getMessageColors().get(1)))
                .append(Component.text(locToString(location)).color(getMessageColors().get(2)).decorate(TextDecoration.BOLD))
                .append(Component.text(" in ").color(getMessageColors().get(3)))
                .append(Component.text(dimension.getDisplayName()).color(getMessageColors().get(4)).decorate(TextDecoration.BOLD))
                .build());
    }

    public boolean isValid() {
        return true;
    }

    private String locToString(Location loc) {
        long x = Math.round(loc.getX());
        long y = Math.round(loc.getY());
        long z = Math.round(loc.getZ());

        return x + "X, " + y + "Y, " + z + "Z";
    }

    private List<TextColor> getMessageColors() {
        List<TextColor> colors = new ArrayList<>();
        TextColor base = dimension.getColor();

        Color bukkitColor = Color.fromRGB(base.red(), base.green(), base.blue());
        float[] hsb = java.awt.Color.RGBtoHSB(bukkitColor.getRed(), bukkitColor.getGreen(), bukkitColor.getBlue(), null);

        colors.add(HSBToTextColor(hsb[0], hsb[1], Math.min(1f, hsb[2] + 0.2f)));
        colors.add(HSBToTextColor((hsb[0] + 0.05f) % 1f, hsb[1], hsb[2]));
        colors.add(HSBToTextColor((hsb[0] + 0.1f) % 1f, hsb[1], hsb[2] - 0.1f));
        colors.add(HSBToTextColor(hsb[0], hsb[1] * 0.8f, hsb[2] * 0.8f));
        colors.add(base);

        return colors;
    }

    private TextColor HSBToTextColor(float hue, float saturation, float brightness) {
        int rgb = java.awt.Color.HSBtoRGB(hue, saturation, brightness);
        return TextColor.color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }

}
