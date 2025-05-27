package net.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Utils {

    public static @NotNull List<Block> getNearbyBlocksByTypes(Set<Material> types, Location center, int radiusX, int radiusY, int radiusZ) {
        List<Block> blocks = new ArrayList<>();
        World world = center.getWorld();

        int baseX = center.getBlockX();
        int baseY = center.getBlockY();
        int baseZ = center.getBlockZ();

        for (int x = -radiusX; x <= radiusX; x++) {
            for (int y = -radiusY; y <= radiusY; y++) {
                for (int z = -radiusZ; z <= radiusZ; z++) {
                    Block block = world.getBlockAt(baseX + x, baseY + y, baseZ + z);
                    if (types.contains(block.getType())) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }


    public static @NotNull List<Block> getNearbyBlocksByType(Material type, Location center, int radiusX, int radiusY, int radiusZ) {
        World world = center.getWorld();

        int baseX = center.getBlockX();
        int baseY = center.getBlockY();
        int baseZ = center.getBlockZ();

        List<Location> searchLocations = new ArrayList<>();

        for (int x = -radiusX; x <= radiusX; x++) {
            for (int y = -radiusY; y <= radiusY; y++) {
                for (int z = -radiusZ; z <= radiusZ; z++) {
                    searchLocations.add(new Location(world, baseX + x, baseY + y, baseZ + z));
                }
            }
        }

        List<Block> blocks = new ArrayList<>();

        Collections.shuffle(searchLocations);
        for (Location loc : searchLocations) {
            Block block = world.getBlockAt(loc);
            Material blockType = block.getType();
            if (blockType == type) {
                blocks.add(block);
            }
        }
        return blocks;
    }

    public static void spawnParticleCircle(Particle particle, Location center, double radius, double count, @Nullable Particle.DustOptions dustOptions) {
        World world = center.getWorld();
        if (world == null) return;

        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            double y = center.getY();

            Location particleLoc = new Location(world, x, y, z);

            if (particle == Particle.DUST && dustOptions != null) {
                world.spawnParticle(particle, particleLoc, 0, 0, 0, 0, dustOptions);
            } else {
                world.spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
            }
        }
    }


    public static void disableRecipe(@NotNull Material result, @NotNull Plugin plugin) {
        Iterator<Recipe> it = plugin.getServer().recipeIterator();
        while (it.hasNext()) {
            Recipe recipe = it.next();
            if (recipe != null && recipe.getResult().getType() == result) {
                it.remove();
            }
        }
    }
}
