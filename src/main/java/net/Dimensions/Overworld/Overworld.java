package net.Dimensions.Overworld;

import net.Abilities.Items.OverWorld.MomentumMace;
import net.Abilities.Items.OverWorld.PlatinumShield;
import net.Dimensions.Dimension;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Overworld extends Dimension {

    public Overworld() {
        super("Overworld", TextColor.color(117, 128, 126));
    }

    @Override
    public @NotNull World generateWorld() {
        return Objects.requireNonNull(Bukkit.getWorld("world"));
    }

    @Override
    public void registerAbilities(Plugin plugin) {

    }

    @Override
    public void registerItems(Plugin plugin) {
        addItem(new PlatinumShield(plugin));
        addItem(new MomentumMace(plugin));
    }
}
