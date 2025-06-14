package net.Dimensions.Frostveil;

import net.Abilities.Abilities.Frostveil.Snowstorm;
import net.Abilities.Items.ElysianWilds.NaturesMace;
import net.Abilities.Items.Frostveil.FrozenMace;
import net.Dimensions.Dimension;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Frostveil extends Dimension {

    public Frostveil() {
        super("Frostveil", TextColor.color(70, 207, 219));
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
        addAbility(new Snowstorm(plugin));
    }

    @Override
    public void registerItems(Plugin plugin) {
        addItem(new FrozenMace(plugin));
    }
}
