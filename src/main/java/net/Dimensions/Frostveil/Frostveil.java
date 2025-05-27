package net.Dimensions.Frostveil;

import net.Dimensions.Dimension;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Frostveil extends Dimension {

    public Frostveil() {
        super("Frostveil");
    }

    @Override
    public @NotNull World generateWorld() {
        World world = new WorldCreator(getDisplayName())
                .generator("").biomeProvider("").generateStructures(true)
                .keepSpawnLoaded(TriState.TRUE)
                .createWorld();

        if (world == null)
            return Objects.requireNonNull(Bukkit.getWorld("world"));

        return world;
    }

    @Override
    public void registerAbilities(Plugin plugin) {

    }

    @Override
    public void registerItems(Plugin plugin) {

    }
}
