package me.jayfella.SafeCity.Runnables;

import me.jayfella.SafeCity.Core.ZoneDetails;
import me.jayfella.SafeCity.Core.ZonePermissionType;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCityZone;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class DisplayZoneData_Thread implements Runnable
{
    private final SafeCityContext context;
    private final Player player;
    private final String zoneName;
    
    public DisplayZoneData_Thread(SafeCityContext context, Player player, String zoneName)
    {
        this.context = context;
        this.player = player;
        this.zoneName = zoneName;
    }
    
    @Override
    public void run()
    {
        SafeCityZone zone = null;
        
        for (SafeCityZone z : context.getZones())
        {
            if (!zoneName.isEmpty())
            {
                if (z.getName().equalsIgnoreCase(zoneName))
                {
                    if (z.isPublic())
                    {
                        zone = z;
                        break;
                    }
                }
            }
            else
            {
                //if (z.isFounder(player.getName()))
                if (z.hasPermission(player.getName(), ZonePermissionType.Owner))
                {
                    zone = z;
                }
            }
        }
        
        if (zone == null)
        {
            player.sendMessage(ChatColor.RED + "No public zones have been found with that name.");
            return;
        }
        
        if (zone.isPublic())
        {
            // SafeCityPlayer scPlayer = context.getPlayer(player);
            player.sendMessage(ZoneDetails.displayZoneData(context, zone));
        }
        
    }

}
