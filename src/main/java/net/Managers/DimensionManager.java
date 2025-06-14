package net.Managers;

import net.Abilities.Model.Ability;
import net.Dimensions.Dimension;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DimensionManager {

    public static List<Dimension> dimensions = new ArrayList<>();
    private static final HashMap<UUID, Dimension> playerDimension = new HashMap<>();

    public static @Nullable Dimension getDimension(Player player) {
        return playerDimension.get(player.getUniqueId());
    }

    public static void setDimension(Player player, Dimension dimension) {
        playerDimension.put(player.getUniqueId(), dimension);
    }

    public static @Nullable Dimension getDimension(UUID uuid) {
        return playerDimension.get(uuid);
    }

    public static void setDimension(UUID uuid, Dimension dimension) {
        playerDimension.put(uuid, dimension);
    }

    public static @Nullable Dimension getDimension(String name) {
        for (Dimension dimension : dimensions) {
            if (dimension.getDisplayName().replace(" ", "").
                    equalsIgnoreCase(name.replace(" ", "")))
                return dimension;
        }
        return null;
    }

    public static @Nullable Dimension getDimension(Ability ability) {
        for (Dimension dimension : dimensions) {
            if (dimension.getAbilities().contains(ability))
                return dimension;
        }
        return null;
    }

    public static @Nullable Dimension getDimension(World world) {
        for (Dimension dimension : dimensions) {
            if (dimension.getWorld().equals(world))
                return dimension;
        }
        return null;
    }

    public static Dimension getDimension(Random random) {
        Dimension dimension = null;

        for (int i = 0; i <= 100; i++) {
            dimension = dimensions.get(random.nextInt(dimensions.size()));
            if (dimension != null || dimension != getDimension("void")) break;
        }

        return dimension;
    }

    public static void registerDimension(Dimension dimension) {
        dimensions.add(dimension);
    }

    public static void unRegisterDimension(Dimension dimension) {
        dimensions.add(dimension);
    }
}
