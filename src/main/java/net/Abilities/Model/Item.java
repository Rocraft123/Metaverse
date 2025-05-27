package net.Abilities.Model;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class Item extends Ability {

    private final int itemID;

    public Item(String name, int cooldownTime, @NotNull TextColor color, String icon, int itemID) {
        super(name, cooldownTime, color, icon);
        this.itemID = itemID;
    }

    public abstract void onDamage(Player player, LivingEntity target);
    public int getItemID() {
        return itemID;
    }

}
