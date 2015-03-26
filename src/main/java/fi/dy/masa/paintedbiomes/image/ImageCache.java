package fi.dy.masa.paintedbiomes.image;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import fi.dy.masa.paintedbiomes.util.RegionCoords;

public class ImageCache
{
    private Map<RegionCoords, IImageReader> imageRegions;
    private Map<RegionCoords, Long> timeouts;

    public ImageCache()
    {
        this.imageRegions = new HashMap<RegionCoords, IImageReader>();
        this.timeouts = new HashMap<RegionCoords, Long>();
    }

    public boolean contains(int blockX, int blockZ)
    {
        return this.imageRegions.containsKey(RegionCoords.fromBlockCoords(blockX, blockZ));
    }

    public IImageReader getRegionImage(int blockX, int blockZ, String path)
    {
        RegionCoords regionCoords = RegionCoords.fromBlockCoords(blockX, blockZ);
        IImageReader imageRegion = this.imageRegions.get(regionCoords);

        if (imageRegion == null)
        {
            imageRegion = new ImageRegion(regionCoords.regionX, regionCoords.regionZ, path);
            this.imageRegions.put(regionCoords, imageRegion);
        }

        this.timeouts.put(regionCoords, Long.valueOf(System.currentTimeMillis()));

        return imageRegion;
    }

    public void removeOld(int thresholdSeconds)
    {
        long currentTime = System.currentTimeMillis();
        Iterator<Entry<RegionCoords, Long>> iter = this.timeouts.entrySet().iterator();

        while (iter.hasNext() == true)
        {
            Entry<RegionCoords, Long> entry = iter.next();

            if (currentTime - entry.getValue().longValue() >= (thresholdSeconds * 1000))
            {
                this.imageRegions.remove(entry.getKey());
                iter.remove();
            }
        }
    }
}