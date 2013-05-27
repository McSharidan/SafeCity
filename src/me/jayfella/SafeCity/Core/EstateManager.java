package me.jayfella.SafeCity.Core;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCityPlayer;
import me.jayfella.SafeCity.SafeCitySubZone;
import me.jayfella.SafeCity.SafeCityZone;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class EstateManager
{
    private final SafeCityContext context;

    public EstateManager(SafeCityContext context)
    {
        this.context = context;
    }

    public boolean rentZone(Player buyer, Location zoneLocation)
    {
        SafeCityPlayer scPlayer = context.getPlayer(buyer);

        SafeCityZone zone = context.getZone(context.toThinLocation(zoneLocation), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return false;
        }

        if (!zone.isPublic())
        {
            scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + "Only public zones can rent areas of land.");
            return false;
        }

        SafeCitySubZone subZone = context.getSubZone(context.toThinLocation(zoneLocation), scPlayer.getBukkitPlayer().getWorld());

        if (subZone == null)
        {
            if (zone.isFounder(scPlayer.getBukkitPlayer().getName()))
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Already_Own());
                return false;
            }

            if (!zone.isForRent() && !zone.isRenter(scPlayer.getBukkitPlayer().getName()))
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Not_For_Rent());
                return false;
            }
        }
        else
        {
            if (subZone.isFounder(scPlayer.getBukkitPlayer().getName()) || subZone.isBuyer(scPlayer.getBukkitPlayer().getName()))
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Already_Own());
                // return false;
            }

            if (!subZone.isForRent() && !subZone.isRenter(scPlayer.getBukkitPlayer().getName()))
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Not_For_Rent());
                return false;
            }
        }

        if (!context.getEconomy().has(scPlayer.getBukkitPlayer().getName(), zone.getSalePrice()))
        {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Insufficient_Funds());
                return false;
        }

        // withdraw money from buyer
        EconomyResponse ecoresp = context.getEconomy().withdrawPlayer(scPlayer.getBukkitPlayer().getName(), (subZone == null) ? zone.getSalePrice() : subZone.getSalePrice());
        if (!ecoresp.transactionSuccess())
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Insufficient_Funds());
            return false;
        }

        // give money to seller
        // TODO: Give money back to buyer if rent fails
        ecoresp = context.getEconomy().depositPlayer(scPlayer.getBukkitPlayer().getName(), (subZone == null) ? zone.getSalePrice() : subZone.getSalePrice());
        if (!ecoresp.transactionSuccess())
        {
            scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + "Critical error depositing money. Transaction failed!");
            return false;
        }

        int days = (subZone == null) ? zone.getRentalLength() : subZone.getRentalLength();

        Calendar calendar = new GregorianCalendar();

        if (subZone == null)
        {
            if (zone.isRenter(scPlayer.getBukkitPlayer().getName()))
            {
                calendar.setTime(new Date(zone.getRentTimeEnds()));
            }

            calendar.add(Calendar.DAY_OF_MONTH, days);
            Date future = calendar.getTime();

            int rentalPrice = zone.getSalePrice();
            zone.setRentDetails(false, true, rentalPrice, days, future.getTime(), scPlayer.getBukkitPlayer().getName());

            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Rent_Success(zone.getSalePrice()));

            // set info sign
            if (zone.getInfoSignLocation() != null)
            {
                if (zone.getInfoSign() != null)
                {
                    context.getSignManager().setRentedSign(zone.getInfoSign(), buyer.getName());
                }
            }
        }
        else
        {
            if (subZone.isRenter(scPlayer.getBukkitPlayer().getName()))
            {
                calendar.setTime(new Date(subZone.getRentTimeEnds()));
            }

            calendar.add(Calendar.DAY_OF_MONTH, days);
            Date future = calendar.getTime();

            int rentalPrice = subZone.getSalePrice();
            subZone.setRentDetails(false, true, rentalPrice, days, future.getTime(), scPlayer.getBukkitPlayer().getName());

            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Rent_Success(subZone.getSalePrice()));

            // set info sign
            if (subZone.getInfoSignLocation() != null)
            {
                if (subZone.getInfoSign() != null)
                {
                    context.getSignManager().setRentedSign(subZone.getInfoSign(), buyer.getName());
                }
            }
        }

        Player onlineOwner = context.getPlugin().getServer().getPlayer(zone.getFounder());

        if (onlineOwner != null)
        {
            onlineOwner.sendMessage(context.getMessageHandler().Rent_Success_Owner(scPlayer.getBukkitPlayer().getName(), (subZone == null) ? zone.getSalePrice() : subZone.getSalePrice(), days));
        }

        return true;
    }

    public boolean sellZone(Player buyer, Location zoneLocation)
    {
        SafeCityPlayer scPlayer = context.getPlayer(buyer);

        SafeCityZone zone = context.getZone(context.toThinLocation(zoneLocation), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return false;
        }

        SafeCitySubZone subZone = context.getSubZone(context.toThinLocation(zoneLocation), scPlayer.getBukkitPlayer().getWorld());

        if (subZone != null)
        {
            if (!zone.isPublic())
            {
                scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + "Only public zones can sell areas of land.");
                return false;
            }
        }

        if (subZone == null)
        {
            if (zone.isFounder(scPlayer.getBukkitPlayer().getName()))
            {
                    scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Already_Own());
                    return false;
            }

            if (!zone.isForSale())
            {
                    scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Not_For_Sale());
                    return false;
            }
        }
        else
        {
            if (subZone.isFounder(scPlayer.getBukkitPlayer().getName()))
            {
                    scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Already_Own());
                    return false;
            }

            if (subZone.isBuyer(scPlayer.getBukkitPlayer().getName()))
            {
                    scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Already_Own());
                    return false;
            }

            if (!subZone.isForSale())
            {
                    scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Not_For_Sale());
                    return false;
            }
        }

        // check buyers zoneCount (primary zone only)
        if (subZone == null)
        {
            int zoneCount = 0;

            for (SafeCityZone z : context.getZones())
            {
                if (z.isFounder(scPlayer.getBukkitPlayer().getName()))
                {
                    zoneCount++;
                }
            }

            if (zoneCount >= context.getPluginSettings().getMaxZonesPerPlayer())
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Too_Many_Zones());
                return false;
            }
        }


        if (!context.getEconomy().has(scPlayer.getBukkitPlayer().getName(), zone.getSalePrice()))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Insufficient_Funds());
            return false;
        }

        int price = (subZone == null) ? zone.getSalePrice() : subZone.getSalePrice();

        EconomyResponse ecoresp = context.getEconomy().withdrawPlayer(scPlayer.getBukkitPlayer().getName(), price);
        if (!ecoresp.transactionSuccess())
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Insufficient_Funds());
            return false;
        }

        String ownerName = (subZone == null) ? zone.getFounder() : subZone.getFounder();

        ecoresp = context.getEconomy().depositPlayer(ownerName, price);
        if (!ecoresp.transactionSuccess())
        {
            scPlayer.getBukkitPlayer().sendMessage("Critical error depositing money.");
            return false;
        }

        // notify the seller of the good news
        Player sellerPlayer = context.getPlugin().getServer().getPlayer(ownerName);

        // if player is online
        if (sellerPlayer != null)
        {
            sellerPlayer.sendMessage(context.getMessageHandler().Sale_Success_Owner(scPlayer.getBukkitPlayer().getName(), price));
        }

        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Sale_Success(price));
        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Item_Purchased(price));

        if (subZone == null)
        {
            zone.setSaleDetails(false, 0, scPlayer.getBukkitPlayer().getName());

            if (zone.getInfoSignLocation() != null)
            {
                if (zone.getInfoSign() != null)
                {
                    context.getSignManager().setSoldSignal(zone.getInfoSign(), scPlayer.getBukkitPlayer().getName());
                }
            }

            for (SafeCitySubZone child : zone.getChildren())
            {
                child.setOwner(scPlayer.getBukkitPlayer().getName());
            }
        }
        else
        {
            subZone.setSaleDetails(false, true, 0, scPlayer.getBukkitPlayer().getName());

            if (subZone.getInfoSignLocation() != null)
            {
                if (subZone.getInfoSign() != null)
                {
                    context.getSignManager().setSoldSignal(subZone.getInfoSign(), scPlayer.getBukkitPlayer().getName());
                }
            }
        }

        return true;
    }

}
