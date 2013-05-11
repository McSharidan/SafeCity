package me.jayfella.SafeCity.Listeners;

import java.util.Calendar;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCityPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerPresenceListener implements Listener
{
	private final SafeCityContext context;

	public PlayerPresenceListener(SafeCityContext context)
	{
		this.context = context;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
        // force a lookup
        SafeCityPlayer scPlayer = context.getPlayer(event.getPlayer());
        scPlayer.setLastLoginTime(Calendar.getInstance().getTimeInMillis());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		SafeCityPlayer scPlayer = context.getPlayer(player);

		if (scPlayer == null)
        {
            return;
        }

        scPlayer.setLogoutTime(Calendar.getInstance().getTimeInMillis());
		context.removePlayer(player);
	}

}
