package me.jayfella.SafeCity.Listeners;

import me.jayfella.SafeCity.Core.SafeCityTool;
import me.jayfella.SafeCity.Core.ZoneDetails;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCityPlayer;
import me.jayfella.SafeCity.SafeCitySubZone;
import me.jayfella.SafeCity.SafeCityZone;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class ZoneVisualListener implements Listener
{
    private final SafeCityContext context;

    public ZoneVisualListener(SafeCityContext context)
    {
        this.context = context;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) { return; }
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() == Material.AIR) { return; }

        SafeCityPlayer scPlayer = context.getPlayer(event.getPlayer());
        if (scPlayer.getBukkitPlayer().getItemInHand().getType() != SafeCityTool.ZoneInfoTool.material()) { return; }

        SafeCityZone zone = context.getZone(context.toThinLocation(event.getClickedBlock().getLocation()), event.getClickedBlock().getWorld()) ;

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Displaying_Visual());
            
            scPlayer.getVisualManager().clearVisuals(false);
            scPlayer.getVisualManager().getZoneVisualizer().visualize();
            
            return;
        }

        SafeCitySubZone subZone = context.getSubZone(context.toThinLocation(event.getClickedBlock().getLocation()), event.getClickedBlock().getWorld());

        if (subZone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(ZoneDetails.displayZoneData(context, zone));
        }
        else
        {
            displaySubZoneData(subZone);
        }
    }

    

    

    private void displaySubZoneData(SafeCitySubZone subZone)
    {

    }

    

    

    

}
