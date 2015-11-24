package fi.dy.masa.paintedbiomes.event;

import net.minecraftforge.event.terraingen.WorldTypeEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import fi.dy.masa.paintedbiomes.PaintedBiomes;
import fi.dy.masa.paintedbiomes.config.Configs;
import fi.dy.masa.paintedbiomes.image.ImageHandler;
import fi.dy.masa.paintedbiomes.world.GenLayerBiomeGeneration;
import fi.dy.masa.paintedbiomes.world.GenLayerBiomeIndex;
import fi.dy.masa.paintedbiomes.world.WorldChunkManagerPaintedBiomes;

public class PaintedBiomesEventHandler
{
    @SubscribeEvent
    public void onServerTick(ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            ImageHandler.tickTimeouts();
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if (event.world.isRemote == true || Configs.getInstance().useGenLayer == true)
        {
            return;
        }

        int dim = event.world.provider.dimensionId;

        for (int i : Configs.getInstance().enabledInDimensions)
        {
            if (dim == i)
            {
                PaintedBiomes.logger.info("Wrapping the WorldChunkManager of dimension " + dim + " with WorldChunkManagerPaintedBiomes...");

                // Re-initialize the ImageHandler after a world loads, to update configs etc.
                ImageHandler imageHandler = ImageHandler.getImageHandler(dim);
                imageHandler.init();

                event.world.provider.worldChunkMgr = new WorldChunkManagerPaintedBiomes(event.world, event.world.provider.worldChunkMgr, imageHandler);

                break;
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event)
    {
        if (event.world.isRemote == true || Configs.getInstance().useGenLayer == true)
        {
            return;
        }

        ImageHandler.removeImageHandler(event.world.provider.dimensionId);
    }

    @SubscribeEvent
    public void onInitBiomeGen(WorldTypeEvent.InitBiomeGens event)
    {
        if (Configs.getInstance().useGenLayer == false)
        {
            return;
        }

        PaintedBiomes.logger.info("Registering Painted Biomes biome GenLayers...");
        ImageHandler.getImageHandler(0).init();
        event.newBiomeGens[0] = new GenLayerBiomeGeneration(event.seed, event.originalBiomeGens[0], event.worldType);
        event.newBiomeGens[1] = new GenLayerBiomeIndex(event.seed, event.originalBiomeGens[1], event.worldType);
        event.newBiomeGens[2] = event.newBiomeGens[0];
    }
}
