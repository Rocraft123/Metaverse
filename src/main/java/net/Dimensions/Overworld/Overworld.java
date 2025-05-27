package net.Dimensions.Overworld;

import net.Dimensions.Dimension;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Overworld extends Dimension {

    public Overworld() {
        super("Overworld");
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

    }
}
