package net.Managers;

import net.Abilities.Model.Ability;
import net.Dimensions.Dimension;
import net.metaversePlugin.MetaversePlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AbilityManager {

    private static final HashMap<UUID, Ability> selectedAbility = new HashMap<>();
    private static final HashMap<UUID, Ability> secondAbility = new HashMap<>();

    public static List<Ability> getAbilities() {
        List<Ability> abilities = new ArrayList<>();

        for (Dimension dimension : DimensionManager.dimensions)
            abilities.addAll(dimension.getAbilities());

        return abilities;
    }

    public static HashMap<Dimension, List<Ability>> getAbilities(boolean dimensions) {
        if (!dimensions) return null;
        HashMap<Dimension, List<Ability>> abilities = new HashMap<>();

        for (Dimension dimension : DimensionManager.dimensions)
            abilities.getOrDefault(dimension, new ArrayList<>()).addAll(dimension.getAbilities());

        return abilities;
    }

    //expected String example "elysianwilds:bloomstep"
    public static @Nullable Ability getAbility(String string) {
        try {
            String[] strings = string.replace(" ", "").split(":",2);
            Dimension dimension = DimensionManager.getDimension(strings[0]);

            MetaversePlugin.logger.info(strings[0] + ", " + strings[1] + " from: " + string);
            if (dimension == null) {
                MetaversePlugin.logger.info("Could not find ability: " + "dimension = null");
                return null;
            }

            for (Ability ability : dimension.getAbilities())
                if (ability.getName().replace(" ", "").
                        equalsIgnoreCase(strings[1].replace(" ", "")))
                    return ability;
        } catch (ArrayIndexOutOfBoundsException ignored) {
            MetaversePlugin.logger.warning("Not enough arguments: AbilityManager.GetAbility(String)");
        }
        return null;
    }

    public static @Nullable Ability getAbility(ItemStack itemStack) {
        for (Ability currentAbility : getAbilities()) {
            if (currentAbility.getItemstack().isSimilar(itemStack))
                return currentAbility;
        }
        return null;
    }

    public static @Nullable Ability getSelectedAbility(Player player) {
        return selectedAbility.get(player.getUniqueId());
    }

    public static void setSelectedAbility(Player player, Ability ability) {
        selectedAbility.put(player.getUniqueId(), ability);
    }

    public static void setSelectedAbility(UUID uuid, Ability ability) {
        selectedAbility.put(uuid, ability);
    }

    public static Ability getSecondAbility(Player player) {
        return secondAbility.get(player.getUniqueId());
    }

    public static void setSecondAbility(Player player, Ability ability) {
        secondAbility.put(player.getUniqueId(), ability);
    }

    public static void setSecondAbility(UUID uuid, Ability ability) {
        secondAbility.put(uuid, ability);
    }
}
