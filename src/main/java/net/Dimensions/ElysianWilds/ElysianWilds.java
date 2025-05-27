package net.Dimensions.ElysianWilds;

import net.Abilities.Abilities.ElysianWilds.*;
import net.Dimensions.Dimension;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ElysianWilds extends Dimension {

    public ElysianWilds() {
        super("Elysian Wilds");
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
        addAbility(new LeafDrift(plugin));
        addAbility(new NaturesMercy(plugin));
        addAbility(new RootSnare(plugin));
        addAbility(new VerdantPulse(plugin));
        addAbility(new Wildstride(plugin));
    }

    @Override
    public void registerItems(Plugin plugin) {

    }
}
