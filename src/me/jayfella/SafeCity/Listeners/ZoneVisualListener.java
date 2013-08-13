package me.jayfella.SafeCity.Listeners;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import me.jayfella.SafeCity.Core.SafeCityTool;
import me.jayfella.SafeCity.Core.SettlementHierarchy;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCityPlayer;
import me.jayfella.SafeCity.SafeCitySubZone;
import me.jayfella.SafeCity.SafeCityZone;
import org.bukkit.ChatColor;
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

        if (!context.isValidWorld(event.getPlayer().getWorld().getName()))
            return;

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
            scPlayer.getBukkitPlayer().sendMessage(displayZoneData(zone));
        }
        else
        {
            scPlayer.getBukkitPlayer().sendMessage(displaySubZoneData(subZone));
        }
    }

    public String[] displayZoneData(SafeCityZone zone)
    {
        if (zone.isPublic())
        {
            return displayPublicZoneData(context, zone);
        }
        else
        {
            return displayPrivateZoneData(context, zone);
        }
    }

    private String[] displaySubZoneData(SafeCitySubZone subzone)
    {
        List<String> messages = new ArrayList<>();

        StringBuilder zoneTitle_Sb = new StringBuilder()
                .append(ChatColor.DARK_GRAY)
                .append("-----==] ")
                .append(ChatColor.WHITE)
                .append(subzone.getName())
                .append(ChatColor.DARK_GRAY)
                .append(" [==-----");

        StringBuilder zoneDimensions_Sb = new StringBuilder()
                .append(ChatColor.DARK_GREEN)
                .append("Length: ")
                .append(ChatColor.RED)
                .append(subzone.getLength())
                .append(ChatColor.GREEN)
                .append(" | ")
                .append(ChatColor.DARK_GREEN)
                .append("Width: ")
                .append(ChatColor.RED)
                .append(subzone.getWidth())
                .append(ChatColor.GREEN)
                .append(" | ")
                .append(ChatColor.DARK_GREEN)
                .append("Surface: ")
                .append(ChatColor.RED)
                .append(subzone.getSurfaceArea());

        if (subzone.getLesserCorner().getBlockY() != 1 && subzone.getGreaterCorner().getBlockY() != 255)
        {
                zoneDimensions_Sb
                        .append(ChatColor.GREEN)
                        .append(" | ")
                        .append(ChatColor.DARK_GREEN)
                        .append("Height: ")
                        .append(ChatColor.RED)
                        .append(subzone.getHeight());
        }

        messages.add(zoneTitle_Sb.toString());
        messages.add(zoneDimensions_Sb.toString());

        if (subzone.isForSale()) { messages.add(displaySaleData(null, subzone)); }
        else if (subzone.isForRent()) { messages.add(displayRentData(null, subzone)); }
        else if (subzone.isRented()) { messages.add(displayRentedData(null, subzone)); }

        return messages.toArray(new String[messages.size()]);
    }

    private String[] displayPublicZoneData(SafeCityContext context, SafeCityZone zone)
    {

        SettlementHierarchy settlementType = zone.getSettlementHierarchy();

        StringBuilder zoneTitle_Sb = new StringBuilder()
                .append(ChatColor.DARK_GRAY)
                .append("-----==] ")
                .append(ChatColor.WHITE)
                .append(zone.getName())
                .append(" (")
                .append(ChatColor.GOLD)
                .append(settlementType.getSettlementName())
                .append(ChatColor.WHITE)
                .append(")")
                .append(ChatColor.DARK_GRAY)
                .append(" [==-----");

        StringBuilder zoneDimensions_Sb = new StringBuilder()
                .append(ChatColor.DARK_GREEN)
                .append("Length: ")
                .append(ChatColor.RED)
                .append(zone.getLength())
                .append(ChatColor.GREEN)
                .append(" | ")
                .append(ChatColor.DARK_GREEN)
                .append("Width: ")
                .append(ChatColor.RED)
                .append(zone.getWidth())
                .append(ChatColor.GREEN)
                .append(" | ")
                .append(ChatColor.DARK_GREEN)
                .append("Total: ")
                .append(ChatColor.RED)
                .append(zone.getSurfaceArea());


        String[] population = zone.getPopulation();

        // plot count vs residents
        StringBuilder plotResCount_Sb = new StringBuilder()
                .append(ChatColor.DARK_GREEN)
                .append("Sub-Plots: ")
                .append(ChatColor.RED)
                .append(zone.getChildren().length)
                .append(ChatColor.GREEN)
                .append(" | ")
                .append(ChatColor.DARK_GREEN)
                .append("Residents: ")
                .append(ChatColor.RED)
                .append(population.length);

        StringBuilder publicSpawn_Sb = new StringBuilder()
                .append(ChatColor.DARK_GREEN)
                .append("Public Spawn Enabled: ")
                .append(ChatColor.GREEN)
                .append(zone.publicTeleportAllowed());

        StringBuilder ownerTitle_Sb = new StringBuilder()
                .append(ChatColor.DARK_GREEN)
                .append("Founder: ")
                .append(ChatColor.GREEN)
                .append(settlementType.getOwnerTitle())
                .append(" ")
                .append(zone.getFounder());

        StringBuilder population_Sb = new StringBuilder();

        if (population.length > 0)
        {
            population_Sb.append(ChatColor.DARK_GREEN).append("Residents : ");

            for (int i = 0; i < population.length; i++)
            {
                population_Sb.append(ChatColor.GRAY).append(population[i]);

                if (i != population.length -1)
                {
                    population_Sb.append(ChatColor.DARK_GREEN).append(", ");
                }
            }
        }
        else
        {
            population_Sb.append(ChatColor.DARK_GREEN)
                    .append("Residents")
                    .append(ChatColor.DARK_GREEN)
                    .append(" : ")
                    .append(ChatColor.GREEN)
                    .append("None");
        }

        List<String> messages = new ArrayList<>();

        messages.add(zoneTitle_Sb.toString());
        messages.add(zoneDimensions_Sb.toString());
        messages.add(plotResCount_Sb.toString());
        messages.add(publicSpawn_Sb.toString());
        messages.add(ownerTitle_Sb.toString());
        messages.add(population_Sb.toString());

        if (zone.isForSale()) { messages.add(displaySaleData(zone, null)); }
        else if (zone.isForRent()) { messages.add(displayRentData(zone, null)); }
        else if (zone.isRented()) { messages.add(displayRentedData(zone, null)); }

        return messages.toArray(new String[messages.size()]);
    }

    private String[] displayPrivateZoneData(SafeCityContext context, SafeCityZone zone)
    {
        StringBuilder zoneTitle_Sb = new StringBuilder()
                .append(ChatColor.DARK_GRAY)
                .append("-----==] ")
                .append(ChatColor.WHITE)
                .append(zone.getName())
                .append(" [==-----");

        StringBuilder zoneDimensions_Sb = new StringBuilder()
                .append(ChatColor.DARK_GREEN)
                .append("Length: ")
                .append(ChatColor.RED)
                .append(zone.getLength())
                .append(ChatColor.GREEN)
                .append(" | ")
                .append(ChatColor.DARK_GREEN)
                .append("Width: ")
                .append(ChatColor.RED)
                .append(zone.getWidth())
                .append(ChatColor.GREEN)
                .append(" | ")
                .append(ChatColor.DARK_GREEN)
                .append("Total: ")
                .append(ChatColor.RED)
                .append(zone.getSurfaceArea());

        StringBuilder ownerTitle_Sb = new StringBuilder()
                .append(ChatColor.DARK_GREEN)
                .append("Founder: ")
                .append(ChatColor.GREEN)
                .append(zone.getFounder());

        List<String> messages = new ArrayList<>();

        messages.add(zoneTitle_Sb.toString());
        messages.add(ownerTitle_Sb.toString());
        messages.add(zoneDimensions_Sb.toString());


        if (zone.isForSale()) { messages.add(displaySaleData(zone, null)); }
        else if (zone.isForRent()) { messages.add(displayRentData(zone, null)); }
        else if (zone.isRented()) { messages.add(displayRentedData(zone, null)); }

        return messages.toArray(new String[messages.size()]);
    }

    private String displaySaleData(SafeCityZone zone, SafeCitySubZone subZone)
    {
        return new StringBuilder()
                .append(ChatColor.GOLD)
                .append("For Sale: ")
                .append(ChatColor.RED)
                .append(context.currencySingular())
                .append((subZone == null) ? zone.getSalePrice() : subZone.getSalePrice())
                .toString();
    }

    private String displayRentData(SafeCityZone zone, SafeCitySubZone subZone)
    {
        return new StringBuilder()
                .append(ChatColor.GOLD)
                .append("For Rent: ")
                .append(ChatColor.RED)
                .append(context.currencySingular())
                .append((subZone == null) ? zone.getSalePrice() : subZone.getSalePrice())
                .append(ChatColor.GOLD)
                .append(" for ")
                .append(ChatColor.RED)
                .append((subZone == null) ? zone.getRentalLength() : subZone.getRentalLength())
                .append(ChatColor.GOLD)
                .append(" days")
                .toString();
    }

    private String displayRentedData(SafeCityZone zone, SafeCitySubZone subZone)
    {
        Date dateNow = new Date();
        Date rentEnds = new Date((subZone == null) ? zone.getRentTimeEnds() : subZone.getRentTimeEnds());

        long msDiff= rentEnds.getTime() - dateNow.getTime();

        long daysLeft = TimeUnit.MILLISECONDS.toDays(msDiff);
        long hoursLeft = TimeUnit.MILLISECONDS.toHours(msDiff);
        long minsLeft = TimeUnit.MILLISECONDS.toMinutes(msDiff);

        hoursLeft -= (24 * daysLeft);
        minsLeft -= (24 * daysLeft * 60);
        minsLeft -= (60 * hoursLeft);

        return new StringBuilder()
            .append(ChatColor.GOLD)
            .append(" [ R: ")
            .append((subZone == null) ? zone.getRenter() : subZone.getRenter())
            .append(" ] ")
            .append(" (")
            .append(ChatColor.RED)
            .append(daysLeft)
            .append(ChatColor.GOLD)
            .append(" days ")
            .append(ChatColor.RED)
            .append(hoursLeft)
            .append(ChatColor.GOLD)
            .append(" hours ")
            .append(ChatColor.RED)
            .append(minsLeft)
            .append(ChatColor.GOLD)
            .append(" mins left)")
            .toString();
    }
}
