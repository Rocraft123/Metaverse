package net.Managers;

import net.Abilities.Model.Item;
import net.Dimensions.Dimension;
import net.metaversePlugin.MetaversePlugin;
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

    private final HashMap<UUID, Item> lastUsed = new HashMap<>();
    private static ItemManager instance;
    private final List<Item> items = new ArrayList<>();


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
        for (Item currentItem : getAllItems()) {
            if (currentItem.getItemID() == itemID) return currentItem;
        }
        return null;
    }

    public void registerItem(Item item) {
        items.add(item);
    }

    public void unRegisterItem(Item item) {
        items.add(item);
    }


    public @Nullable Item getItem(String string) {
        try {
            String[] strings = string.replace(" ", "").split(":",2);
            Dimension dimension = DimensionManager.getDimension(strings[0]);

            if (dimension == null && strings[0].equalsIgnoreCase("metaverse")) {
                for (Item item : items)
                    if (item.getName().replace(" ", "").
                            equalsIgnoreCase(strings[1].replace(" ", "")))
                        return item;
                return null;
            }

            if (dimension == null) return null;

            for (Item item : dimension.getItems())
                if (item.getName().replace(" ", "").
                        equalsIgnoreCase(strings[1].replace(" ", "")))
                    return item;
        } catch (ArrayIndexOutOfBoundsException ignored) {
            MetaversePlugin.logger.warning("Not enough arguments: ItemManager.getItem(String)");
        }
        return null;
    }

    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();

        for (Dimension dimension : DimensionManager.dimensions)
            items.addAll(dimension.getItems());

        return items;
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();

        for (Dimension dimension : DimensionManager.dimensions)
            items.addAll(dimension.getItems());

        items.addAll(this.items);
        return items;
    }

    public List<Item> getUtilItems() {
        return items;
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
