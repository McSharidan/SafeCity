package me.jayfella.SafeCity.Core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCitySubZone;
import me.jayfella.SafeCity.SafeCityZone;
import org.bukkit.ChatColor;

public final class ZoneDetails
{
    
    public static String[] displayZoneData(SafeCityContext context, SafeCityZone zone)
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
    
    private static String[] displayPublicZoneData(SafeCityContext context, SafeCityZone zone)
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

        // scPlayer.getBukkitPlayer().sendMessage(message);

        if (zone.isForSale()) { messages.add(displaySaleData(context, zone, null)); }
        else if (zone.isForRent()) { messages.add(displayRentData(context, zone, null)); }
        else if (zone.isRented()) { messages.add(displayRentedData(zone, null)); }
        
        return messages.toArray(new String[messages.size()]);
    }
    
    private static String[] displayPrivateZoneData(SafeCityContext context, SafeCityZone zone)
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
        

        if (zone.isForSale()) { messages.add(displaySaleData(context, zone, null)); }
        else if (zone.isForRent()) { messages.add(displayRentData(context, zone, null)); }
        else if (zone.isRented()) { messages.add(displayRentedData(zone, null)); }
        
        return messages.toArray(new String[messages.size()]);
    }
    
    private static String displaySaleData(SafeCityContext context, SafeCityZone zone, SafeCitySubZone subZone)
    {
        return new StringBuilder()
            .append(ChatColor.GOLD)
            .append("For Sale: ")
            .append(ChatColor.RED)
            .append(context.currencySingular())
            .append((subZone == null) ? zone.getSalePrice() : subZone.getSalePrice())
            .toString();
    }
    
    private static String displayRentData(SafeCityContext context, SafeCityZone zone, SafeCitySubZone subZone)
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
    
    private static String displayRentedData(SafeCityZone zone, SafeCitySubZone subZone)
    {
        Date dateNow = new Date();
        Date rentEnds = new Date(zone.getRentTimeEnds());

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
            .append(zone.getRenter())
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
