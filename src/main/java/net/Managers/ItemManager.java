package net.Managers;

import net.Abilities.Model.Item;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemManager implements Listener {

    private final List<Item> items = new ArrayList<>();
    private final HashMap<UUID, Item> lastUsed = new HashMap<>();
    private static ItemManager instance;


    public Item getLastUsedItem(Player player) {
        return lastUsed.get(player.getUniqueId());
    }

    public void setLastUsedItem(Player player, Item item) {
        lastUsed.put(player.getUniqueId(), item);
    }

    public ItemManager(Plugin plugin) {
        instance = this;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public @Nullable Item getItem(int itemID) {
        for (Item currentItem : items) {
            if (currentItem.getItemID() == itemID) return currentItem;
        }
        return null;
    }

    public @Nullable Item getItem(String name) {
        for (Item currentItem : items) {
            if (currentItem.getName().replace(" ", "").equalsIgnoreCase(name)) return currentItem;
        }
        return null;
    }

    public List<Item> getItems() {
        return items;
    }

    public void registerItem(Item item) {
        items.add(item);
    }

    public void unRegisterItem(Item item) {
        items.remove(item);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        if (entity instanceof HumanEntity damaged && damaged.isBlocking()) return;

        ItemStack itemStack = player.getInventory().getItem(EquipmentSlot.HAND);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;
        if (!meta.hasCustomModelData()) return;

        Item item = getItem(meta.getCustomModelData());
        if (item == null) return;

        item.onDamage(player, entity);
    }

    @EventHandler
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if (itemStack == null || !event.getAction().isRightClick()) return;
        if (!itemStack.getItemMeta().hasCustomModelData()) return;

        Item item = getItem(itemStack.getItemMeta().getCustomModelData());
        if (item == null) return;

        item.execute(player);
    }

    public static ItemManager getInstance() {
        return instance;
    }
}
