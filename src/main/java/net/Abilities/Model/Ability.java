package net.Abilities.Model;

import net.Events.PlayerActivateAbilityEvent;
import net.Managers.ItemManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import net.metaversePlugin.MetaversePlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.*;

@SuppressWarnings("deprecation")
public abstract class Ability {

    private final String name;
    private final int cooldownTime;
    private final String icon;
    private Sound executeSound = Sound.ENTITY_EVOKER_CAST_SPELL;
    private TextColor color;
    private String key = "";

    private final HashMap<UUID, Cooldown> playerCooldown = new HashMap<>();
    private final HashMap<UUID, Boolean> disabled = new HashMap<>();

    public Ability(@NotNull String name, int cooldownTime, @NotNull TextColor color, @NotNull String icon) {
        this.name = name;
        this.cooldownTime = cooldownTime;
        this.icon = icon;
        this.color = color;
    }

    public abstract boolean onExecute(Player player);

    public void execute(Player player) {
        PlayerActivateAbilityEvent event = new PlayerActivateAbilityEvent(player, this);
        player.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled())
            return;

        if (getCooldown(player) == null || Objects.requireNonNull(getCooldown(player)).hasFinished()) {
            if (onExecute(player)) {
                ItemManager manager = ItemManager.getInstance();
                if (this instanceof Item item)
                    manager.setLastUsedItem(player, item);
                setCooldown(player, new Cooldown(cooldownTime));

                if (executeSound != null)
                    player.getWorld().playSound(player.getLocation(), executeSound, 1,1);
            }
        } else if (getCooldown(player) != null) {
            player.sendMessage(MetaversePlugin.prefix.
                    append(Component.text(name + " is still on cooldown: " + Objects.requireNonNull(getCooldown(player)).getTimeLeft())
                            .color(NamedTextColor.RED)));
        }
    }

    public String getName() {
        return name;
    }

    public @NotNull String getIcon() {
        return icon;
    }

    public void setExecuteSound(Sound executeSound) {
        this.executeSound = executeSound;
    }
    public @NotNull TextColor getColor() {
        return color;
    }
    public void setColor(NamedTextColor color) {
        this.color = color;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public @Nullable Cooldown getCooldown(Player player) {
        return playerCooldown.get(player.getUniqueId());
    }

    public void setCooldown(Player player, @NotNull Cooldown cooldown) {
        playerCooldown.put(player.getUniqueId(), cooldown);
    }

    public boolean isDisabled(Player player) {
        return disabled.getOrDefault(player.getUniqueId(), false);
    }

    public void setDisabled(Player player, boolean b) {
        disabled.put(player.getUniqueId(), b);
    }

    public @NotNull ItemStack getItemstack() {
        ItemStack itemStack = new ItemStack(Material.ECHO_SHARD);
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null)
            return itemStack;

        meta.setDisplayName(ChatColor.of(new Color(204, 153, 255)) + "§l" + getName());
        meta.setRarity(ItemRarity.EPIC);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.of(new Color(177, 107, 255)) + "A mysterious fragment, pulsing with");
        lore.add(ChatColor.of(new Color(177, 107, 255)) + "arcane energy that bends to will.");
        lore.add("");
        lore.add(ChatColor.of(new Color(255, 128, 191)) + "§l" + "Right-click to awaken its gift.");
        lore.add(ChatColor.of(new Color(170, 85, 255)) + "Each shard carries a different essence.");
        lore.add("");
        lore.add(ChatColor.of(new Color(120, 60, 150)) + "Remnant of something ancient and unseen...");

        meta.setLore(lore);
        meta.setItemModel(new NamespacedKey("metaverse", key));

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
