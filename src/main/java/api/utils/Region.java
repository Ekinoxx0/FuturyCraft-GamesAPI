package api.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loucass003 on 26/11/16.
 */
public class Region
{
    private Vector min, max, size;
    private World w;
    private List<Block> blocks;

    public Region(Location a, Location b)
    {
        this(a.toVector(), b.toVector(), a.getWorld());
    }

    public Region(Location a, Location b, World w)
    {
        this(a.toVector(), b.toVector(), w);
    }

    public Region(Vector a, Vector b, World w)
    {
        min = Vector.getMinimum(a, b);
        max = Vector.getMaximum(a, b);
        size = max.clone().subtract(min);
        this.w = w;
    }

    public List<Block> getBlocks()
    {
        if(blocks != null)
            return blocks;
        blocks = new ArrayList<>();
        for (int x = 0; x <= size.getBlockX(); x++)
        {
            for (int y = 0; y <= size.getBlockY(); y++)
            {
                for (int z = 0; z <= size.getBlockZ(); z++)
                    blocks.add(min.toLocation(w).add(x, y, z).getBlock());
            }
        }
        return blocks;
    }

    public boolean isIn(Location loc) {
        return !(w != null && w != loc.getWorld()) && loc.toVector().isInAABB(min, max);
    }

    public boolean enter(Location from, Location to) {
        return !(w != null && w != to.getWorld()) && !isIn(from) && isIn(to);
    }

    public boolean leave(Location from, Location to) {
        return !(w != null && w != from.getWorld()) && isIn(from) && !isIn(to);
    }

    public Vector getMax() {
        return max;
    }

    public Vector getMin() {
        return min;
    }

    public Vector getSize() {
        return size;
    }

    public World getWorld() {
        return w;
    }
}