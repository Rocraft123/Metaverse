package net.Abilities.Items.ShatteredRealm;

import net.Abilities.Model.Item;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ShatteredMace extends Item {

    public ShatteredMace() {
        super("Shattered Mace", 120, TextColor.color(168, 26, 196), "", 103);
    }

    @Override
    public boolean onExecute(Player player) {
        return true;
    }

    @Override
    public void onDamage(Player player, LivingEntity target) {
    }

    @Override
    public @NotNull ItemStack getItemstack() {
        ItemStack itemStack = new ItemStack(Material.MACE);
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null)
            return itemStack;

        meta.setDisplayName(ChatColor.of(new Color(247, 224, 223)) + getName());
        meta.setLore(lore());

        meta.setUnbreakable(true);
        meta.setFireResistant(true);
        meta.setRarity(ItemRarity.EPIC);

        meta.setCustomModelData(getItemID());
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private @NotNull java.util.List<String> lore() {
        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.of(new Color(247, 224, 223)) + "a powerful mace used to-");
        lore.add(ChatColor.of(new Color(247, 224, 223)) + "upgrade your movement");
        lore.add(" ");

        lore.add(ChatColor.of(new Color(117, 224, 205)) + "On Hit:");
        lore.add(ChatColor.of(new Color(117, 224, 205)) + "it will knock back all the players in a-");
        lore.add(ChatColor.of(new Color(117, 224, 205)) + "10 block radius and damage them.");
        lore.add(" ");

        lore.add(ChatColor.of(new Color(117, 224, 205)) + "on right click:");
        lore.add(ChatColor.of(new Color(117, 224, 205)) + "a dash that will boost you 10 blocks");

        return lore;
    }
}
