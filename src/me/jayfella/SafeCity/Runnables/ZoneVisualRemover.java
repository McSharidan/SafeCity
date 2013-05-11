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
        // if (this.isBusy) { return; }

        /*if (runAsync)
        {
            visualManager.getContext().getPlugin().getServer().getScheduler().runTaskAsynchronously(visualManager.getContext().getPlugin(), this);
        }
        else
        {
            run();
        }*/

        run();
    }

    @Override
    public void run()
    {
        // isBusy = true;

        for (ChunkBlockPair cbp : visualManager.getActiveVisuals())
        {
            cbp.revertAllBlockChanges();
        }

        // isBusy = false;
    }

}
