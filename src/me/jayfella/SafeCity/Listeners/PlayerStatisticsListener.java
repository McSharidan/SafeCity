package me.jayfella.SafeCity.Listeners;

import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCityPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerStatisticsListener implements Listener
{
    private final SafeCityContext context;

    public PlayerStatisticsListener(SafeCityContext context)
    {
        this.context = context;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        SafeCityPlayer scPlayer = context.getPlayer(event.getEntity());
        scPlayer.addTimeDied();
    }
}
