package net.Managers;

import net.Abilities.Model.Ability;
import net.Dimensions.Dimension;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DimensionManager {

    public static List<Dimension> dimensions = new ArrayList<>();

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

    public static void registerDimension(Dimension dimension) {
        dimensions.add(dimension);
    }

    public static void unRegisterDimension(Dimension dimension) {
        dimensions.add(dimension);
    }
}
