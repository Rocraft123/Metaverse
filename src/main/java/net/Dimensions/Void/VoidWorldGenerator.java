package net.Dimensions.Void;

import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;

public class VoidWorldGenerator extends ChunkGenerator implements Listener {


    @Override
    public boolean shouldGenerateCaves() {
        return false;
    }

    @Override
    public boolean shouldGenerateSurface() {
        return false;
    }
}
