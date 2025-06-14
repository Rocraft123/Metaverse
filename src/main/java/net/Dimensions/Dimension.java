package net.Dimensions;

import net.Abilities.Model.Ability;
import net.Abilities.Model.Item;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Dimension {

    private final String displayName;
    private final World world;
    private final TextColor color;

    private final List<Ability> abilities = new ArrayList<>();
    private final List<Item> items = new ArrayList<>();

    protected Dimension(@NotNull String displayName, TextColor color) {
        this.displayName = displayName;
        this.color = color;

        World world = Bukkit.getWorld(displayName);
        this.world = world != null ? world : generateWorld();
    }

    public @NotNull String getDisplayName() {
        return displayName;
    }

    public @NotNull World getWorld() {
        return world;
    }
    public TextColor getColor() {
        return color;
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    public void addAbility(Ability ability) {
        abilities.add(ability);
    }

    public void removeAbility(Ability ability) {
        abilities.remove(ability);
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public abstract @NotNull World generateWorld();
    public abstract void registerAbilities(Plugin plugin);
    public abstract void registerItems(Plugin plugin);
}