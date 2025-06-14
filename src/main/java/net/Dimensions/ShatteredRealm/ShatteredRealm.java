package net.Dimensions.ShatteredRealm;

import net.Abilities.Abilities.ShatteredRealm.VoidEcho;
import net.Abilities.Items.ShatteredRealm.ShatteredMace;
import net.Abilities.Items.ShatteredRealm.ShatteredSword;
import net.Dimensions.Dimension;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ShatteredRealm extends Dimension {

    public ShatteredRealm() {
        super("Shattered Realm", TextColor.color(145, 62, 184));
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
        addAbility(new VoidEcho(plugin));
    }

    @Override
    public void registerItems(Plugin plugin) {
        addItem(new ShatteredMace(plugin));
        addItem(new ShatteredSword(plugin));
    }
}
