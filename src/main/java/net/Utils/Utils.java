package net.Utils;

import net.Abilities.Model.Item;
import net.Dimensions.Dimension;
import net.Managers.DimensionManager;
import net.Managers.ItemManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
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

    public static Location getRandomSaveLocation(Location base, World world, int range) {
        Random random = new Random();

        for (int attempts = 0; attempts < 20; attempts++) {
            int dx = random.nextInt(range) - 10;
            int dz = random.nextInt(range) - 10;
            int dy = random.nextInt(range) - 5;

            Location loc = base.clone().add(dx, dy, dz);

            int y = loc.getBlockY();
            for (int i = y + 5; i >= y - 5; i--) {
                Location check = new Location(world, loc.getX(), i, loc.getZ());
                Material below = check.getBlock().getType();
                Material above = check.clone().add(0, 1, 0).getBlock().getType();

                if (below.isSolid() && above.isAir()) {
                    return check.add(0, 1, 0);
                }
            }
        }
        return base;
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

    public static @NotNull List<String> getItemNameStrings() {
        List<String> items = new ArrayList<>();

        ItemManager manager = ItemManager.getInstance();

        for (Dimension dimension : DimensionManager.dimensions) {
            for (Item item : dimension.getItems()) {
                items.add(dimension.getDisplayName().toLowerCase().replace(" ", "") + ":" +
                        item.getName().toLowerCase().replace(" ", ""));
            }
            for (Item item : manager.getUtilItems()) {
                items.add("metaverse" + ":" +
                        item.getName().toLowerCase().replace(" ", ""));
            }
        }
        return items;
    }
}
