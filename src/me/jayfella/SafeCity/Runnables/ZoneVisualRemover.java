package me.jayfella.SafeCity.Runnables;

import me.jayfella.SafeCity.Core.ChunkBlockPair;
import me.jayfella.SafeCity.Core.VisualManager;

public class ZoneVisualRemover implements Runnable
{
    private VisualManager visualManager;

    // private boolean isBusy;

    public ZoneVisualRemover(VisualManager visualManager)
    {
	this.visualManager = visualManager;
    }

    public void removeVisuals(boolean runAsync)
    {
        run();
    }

    @Override
    public void run()
    {
        for (ChunkBlockPair cbp : visualManager.getActiveVisuals())
        {
            cbp.revertAllBlockChanges();
        }
    }

}
