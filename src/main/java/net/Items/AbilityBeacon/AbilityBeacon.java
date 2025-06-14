package net.Items.AbilityBeacon;

import net.Abilities.Model.Item;
import net.Dimensions.Dimension;
import net.Managers.DimensionManager;
import net.Utils.Ritual.Ritual;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class AbilityBeacon extends Item implements Listener {

    private final Plugin plugin;

    public AbilityBeacon(Plugin plugin) {
        super("AbilityBeacon", 0, TextColor.color(255, 248, 115), "", 1000);
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setExecuteSound(null);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();

        if (!isAbilityBeacon(item)) return;

        Dimension dimension = DimensionManager.getDimension(player.getWorld());
        if (dimension == null || dimension.equals(DimensionManager.getDimension("void"))) {
            event.setCancelled(true);
            return;
        }

        Ritual ritual = new Ritual(dimension, player.getLocation(), player);
        ritual.start(plugin);
    }


    @Override
    public boolean onExecute(Player player) {return false;}

    @Override
    public void onDamage(Player player, LivingEntity target) {}

    private boolean isAbilityBeacon(ItemStack item) {
        if (item == null || item.getType() != Material.BEACON) return false;
        if (!item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && ChatColor.stripColor(meta.getDisplayName()).equalsIgnoreCase(getName())
                && meta.hasCustomModelData() && meta.getCustomModelData() == getItemID();
    }


    @Override
    public @NotNull ItemStack getItemstack() {
        ItemStack item = new ItemStack(Material.BEACON);
        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            return item;

        meta.setDisplayName(ChatColor.of(new Color(255, 248, 115)) + getName());
        meta.setLore(lore());

        meta.setUnbreakable(true);
        meta.setFireResistant(true);
        meta.setRarity(ItemRarity.EPIC);

        meta.setMaxStackSize(1);
        meta.setCustomModelData(getItemID());

        //meta.setItemModel(new NamespacedKey("metaverse", ""));
        item.setItemMeta(meta);
        return item;
    }

    private @NotNull List<String> lore() {
        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.of(new Color(255, 248, 115)) + "" + ChatColor.ITALIC + "Place this to begin a mysterious ritual...");
        lore.add(ChatColor.of(new Color(237, 217, 33)) + "It will grant you a random ability");
        lore.add(ChatColor.of(new Color(237, 217, 33)) + "based on your current dimension.");

        lore.add("");

        lore.add(ChatColor.of(new Color(156, 34, 12)) + "" + ChatColor.BOLD + "⚠ Beware!");
        lore.add(ChatColor.of(new Color(199, 91, 72)) + "Once placed, its presence will be known.");
        lore.add(ChatColor.of(new Color(199, 91, 72)) + "Others may find you...");
        lore.add(ChatColor.of(new Color(199, 91, 72)) + "" + ChatColor.ITALIC + "and try to claim your reward.");

        lore.add("");

        lore.add(ChatColor.of(new Color(123, 0, 237)) + "" + ChatColor.BOLD + "Use this only if you're ready for the risk.");
        return lore;
    }

}
