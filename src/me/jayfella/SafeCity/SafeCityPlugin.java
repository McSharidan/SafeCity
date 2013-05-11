package me.jayfella.SafeCity;

import org.bukkit.plugin.java.JavaPlugin;

public final class SafeCityPlugin extends JavaPlugin
{
	private SafeCityContext context;

    @Override
	public void onEnable()
	{
        context = new SafeCityContext(this);
	}

    @Override
	public void onDisable()
	{
        if (this.context != null)
        {
            this.context.getMySql().close();
        }
	}

    public SafeCityContext getContext()
    {
        return this.context;
    }
    
}
