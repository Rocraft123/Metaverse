package net.Abilities.Model;

import net.Events.PlayerActivateAbilityEvent;
import net.Managers.ItemManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.metaversePlugin.MetaversePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public abstract class Ability {

    private final String name;
    private int cooldownTime;
    private final String icon;
    private Sound executeSound = Sound.ENTITY_EVOKER_CAST_SPELL;
    private TextColor color;

    private final HashMap<UUID, Cooldown> playerCooldown = new HashMap<>();

    public Ability(@NotNull String name, int cooldownTime, @NotNull TextColor color, @NotNull String icon) {
        this.name = name;
        this.cooldownTime = cooldownTime;
        this.icon = icon;
        this.color = color;
    }

    public abstract boolean onExecute(Player player);
    public abstract @NotNull ItemStack getItemstack();

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
    public int getCooldownTime() {
        return cooldownTime;
    }
    public void setCooldownTime(int cooldownTime) {
        this.cooldownTime = cooldownTime;
    }
    public @NotNull String getIcon() {
        return icon;
    }
    public @NotNull Sound getExecuteSound() {
        return executeSound;
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

    public @Nullable Cooldown getCooldown(Player player) {
        return playerCooldown.get(player.getUniqueId());
    }

    public void setCooldown(Player player, @NotNull Cooldown cooldown) {
        playerCooldown.put(player.getUniqueId(), cooldown);
    }
}
