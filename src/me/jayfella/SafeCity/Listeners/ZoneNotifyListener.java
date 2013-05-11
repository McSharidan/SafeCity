package me.jayfella.SafeCity.Listeners;

import me.jayfella.SafeCity.Core.ThinLocation;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCityPlayer;
import me.jayfella.SafeCity.SafeCitySubZone;
import me.jayfella.SafeCity.SafeCitySubZoneCollection;
import me.jayfella.SafeCity.SafeCityZone;
import me.jayfella.SafeCity.SafeCityZoneCollection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public final class ZoneNotifyListener implements Listener
{
    private final SafeCityContext context;

    public ZoneNotifyListener(SafeCityContext context)
    {
	this.context = context;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        SafeCityPlayer scPlayer = context.getPlayer(event.getPlayer());
        if (scPlayer.getCoolDownState()) { return; }

        // ensure the player isn't just moving their head
        if (scPlayer.getBukkitPlayer().getLocation().getBlockX() == scPlayer.getLocation().getBlockX()
            && (scPlayer.getBukkitPlayer().getLocation().getBlockY() == scPlayer.getLocation().getBlockY())
            && (scPlayer.getBukkitPlayer().getLocation().getBlockZ() == scPlayer.getLocation().getBlockZ()))
		{ return; }

        Location playerLocation = scPlayer.getBukkitPlayer().getLocation();
        scPlayer.setLocation(new ThinLocation(playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ()));

        int chunkX = (playerLocation.getBlockX() >> 4);
        int chunkZ = (playerLocation.getBlockZ() >> 4);

        SafeCityZoneCollection zoneCollection = context.getZonesInChunk(chunkX, chunkZ);

        if (zoneCollection == null)
        {
            if (scPlayer.getZoneManager().getCurrentZone() != null)
            {
                scPlayer.getBukkitPlayer().sendMessage(ChatColor.GOLD + " ~ " + ChatColor.GREEN + "Wilderness");
                scPlayer.getZoneManager().setCurrentZone(null);

                return;
            }

            return;
        }

        SafeCityZone currentZone = zoneCollection.getExactZone(playerLocation);
        if (currentZone == null)
        {
           if (scPlayer.getZoneManager().getCurrentZone() != null)
            {
                scPlayer.getBukkitPlayer().sendMessage(ChatColor.GOLD + " ~ " + ChatColor.GREEN + "Wilderness");
                scPlayer.getZoneManager().setCurrentZone(null);

                return;
            }

            return;
        }

        SafeCitySubZoneCollection subZoneCollection = context.getSubZonesInChunk(chunkX, chunkZ);
        SafeCitySubZone currentSubZone = null;

        if (subZoneCollection != null)
        {
            currentSubZone = subZoneCollection.getExactSubZone(playerLocation);
        }

        if (currentSubZone == null)
        {
            if (currentZone.equals(scPlayer.getZoneManager().getCurrentZone()))
            {
                if (scPlayer.getZoneManager().getCurrentSubZone() == null)
                {
                    return;
                }
            }
        }
        else
        {
            if (currentSubZone.equals(scPlayer.getZoneManager().getCurrentSubZone()))
            {
                return;
            }
        }

        scPlayer.getZoneManager().setCurrentZone(currentZone);
        scPlayer.getZoneManager().setCurrentSubZone(currentSubZone);

        StringBuilder sb = new StringBuilder()
			.append(ChatColor.GOLD)
			.append(" ~ ");

        if (currentZone.isPvpEnabled())
        {
            sb.append(ChatColor.RED).append("(PvP) ");
        }

        if (currentZone.isPublic())
        {
            sb.append(ChatColor.AQUA)
                .append("[")
                .append(scPlayer.getZoneManager().getCurrentZone().getSettlementHierarchy().getSettlementName())
                .append("] ");
        }

        if (currentZone.isForSale())
        {
            sb.append(ChatColor.GOLD)
                .append("[ B: ")
                .append(ChatColor.GREEN)
                .append(context.currencySingular())
                .append(scPlayer.getZoneManager().getCurrentZone().getSalePrice())
                .append(ChatColor.GOLD)
                .append(" ] ");
        }

        if (currentZone.isForRent())
        {
            sb.append(ChatColor.GOLD)
                .append("[ R: ")
                .append(ChatColor.GREEN)
                .append(context.currencySingular())
                .append(currentZone.getSalePrice())
                .append(ChatColor.GOLD)
                .append(" for ")
                .append(ChatColor.GREEN)
                .append(currentZone.getRentalLength())
                .append(ChatColor.GOLD).append(" days ] ");
        }
        else if (scPlayer.getZoneManager().getCurrentZone().isRented())
        {
            sb.append(ChatColor.GOLD)
                .append("[ R: ")
                .append(ChatColor.GREEN)
                .append(currentZone.getRenter())
                .append(ChatColor.GOLD)
                .append(" ] ");
        }

        sb.append(ChatColor.YELLOW)
            .append(currentZone.getName());

        if (currentSubZone != null)
        {
            sb.append(ChatColor.GREEN).append(" -> ");

            if (currentSubZone.isPvpEnabled())
            {
                sb.append(ChatColor.RED).append("(PvP) ");
            }

            if (currentSubZone.isForSale())
            {
                sb.append(ChatColor.GOLD)
                        .append("[ B: ").append(ChatColor.GREEN)
                        .append(context.currencySingular())
                        .append(currentSubZone.getSalePrice())
                        .append(ChatColor.GOLD)
                        .append(" ] ");
            }

            else if (currentSubZone.isForRent())
            {
                sb.append(ChatColor.GOLD)
                        .append("[ R: ")
                        .append(ChatColor.GREEN)
                        .append(context.currencySingular())
                        .append(currentSubZone.getSalePrice())
                        .append(ChatColor.GOLD)
                        .append(" for ")
                        .append(ChatColor.GREEN)
                        .append(currentSubZone.getRentalLength())
                        .append(ChatColor.GOLD)
                        .append(" days ] ");
            }
            else if (currentSubZone.isRented())
            {
                sb.append(ChatColor.GOLD)
                        .append("[ R: ")
                        .append(ChatColor.GREEN)
                        .append(currentSubZone.getRenter())
                        .append(ChatColor.GOLD)
                        .append(" ] ");
            }

            if (!currentSubZone.getBuyer().isEmpty())
            {
                sb.append(ChatColor.GOLD)
                        .append("[ B: ")
                        .append(ChatColor.GREEN)
                        .append(currentSubZone.getBuyer())
                        .append(ChatColor.GOLD)
                        .append(" ] ");
            }

            sb.append(ChatColor.YELLOW).append(currentSubZone.getName());
        }

        scPlayer.getBukkitPlayer().sendMessage(sb.toString());

        if (currentSubZone == null)
        {
            if (!currentZone.getEntryMessage().isEmpty())
            {
                scPlayer.getBukkitPlayer().sendMessage(currentZone.getEntryMessage());
            }
        }
        else
        {
            if (!currentSubZone.getEntryMessage().isEmpty())
            {
                scPlayer.getBukkitPlayer().sendMessage(currentSubZone.getEntryMessage());
            }
        }

        scPlayer.startCoolDown(5L);

    }

}
