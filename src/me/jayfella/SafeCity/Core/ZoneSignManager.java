package me.jayfella.SafeCity.Core;

import me.jayfella.SafeCity.SafeCityContext;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public final class ZoneSignManager
{
    private final SafeCityContext context;

    // before
    private final String signActivator = "[details]";

    private final String signOwner = ChatColor.DARK_BLUE + "[OWNED BY]";

    // after
    private final String rentSignal = ChatColor.DARK_BLUE + "[FOR RENT]";
    private final String saleSignal = ChatColor.DARK_BLUE + "[FOR SALE]";

    // used
    private final String rentedSignal = ChatColor.GOLD + "[RENTED]";
    private final String soldSignal = ChatColor.GOLD + "[SOLD]";

    public ZoneSignManager (SafeCityContext context)
    {
        this.context = context;
    }

    public void setRentSignal(SignChangeEvent event, int price, int length)
    {
        event.setLine(0, rentSignal);
        event.setLine(1, new StringBuilder().append(context.currencySingular()).append(ChatColor.GREEN).append(price).toString());
        event.setLine(2, "for");
        event.setLine(3, new StringBuilder().append(ChatColor.GREEN).append(length).append(ChatColor.BLACK).append(" days").toString());
    }

    public void setRentSignal(Sign sign, int price, int length)
    {
        sign.setLine(0, rentSignal);
        sign.setLine(1, new StringBuilder().append(context.currencySingular()).append(ChatColor.GREEN).append(price).toString());
        sign.setLine(2, "for");
        sign.setLine(3, new StringBuilder().append(ChatColor.GREEN).append(length).append(ChatColor.BLACK).append(" days").toString());

        sign.update();
    }


    public void setRentedSign(SignChangeEvent event, String renterName)
    {
        event.setLine(0, rentedSignal);
        event.setLine(1, "");
        event.setLine(2, renterName);
        event.setLine(3, "");
    }

    public void setRentedSign(Sign sign, String renterName)
    {
        sign.setLine(0, rentedSignal);
        sign.setLine(1, "");
        sign.setLine(2, renterName);
        sign.setLine(3, "");

        sign.update();
    }



    public void displayOwnerSign(SignChangeEvent event, String ownerName)
    {
        event.setLine(0, signOwner);
        event.setLine(1, "");
        event.setLine(2, ownerName);
        event.setLine(3, "");
    }

    public void displayOwnerSign(Sign sign, String ownerName)
    {
        sign.setLine(0, signOwner);
        sign.setLine(1, "");
        sign.setLine(2, ownerName);
        sign.setLine(3, "");

        sign.update();
    }



    public void setSaleSignal(SignChangeEvent event, int price)
    {
        event.setLine(0, saleSignal);
        event.setLine(1, "");
        event.setLine(2, new StringBuilder().append(context.currencySingular()).append(ChatColor.GREEN).append(price).toString());
        event.setLine(3, "");
    }

    public void setSaleSignal(Sign sign, int price)
    {
        sign.setLine(0, saleSignal);
        sign.setLine(1, "");
        sign.setLine(2, new StringBuilder().append(context.currencySingular()).append(ChatColor.GREEN).append(price).toString());
        sign.setLine(3, "");

        sign.update();
    }



    public void setSoldSignal(SignChangeEvent event, String ownerName)
    {
        event.setLine(0, soldSignal);
        event.setLine(1, "");
        event.setLine(2, ownerName);
        event.setLine(3, "");
    }

    public void setSoldSignal(Sign sign,  String ownerName)
    {
        sign.setLine(0, soldSignal);
        sign.setLine(1, "");
        sign.setLine(2, ownerName);
        sign.setLine(3, "");

        sign.update();
    }

    public String getSignActivator()
    {
        return this.signActivator;
    }

    public String getRentSignal()
    {
        return this.rentSignal;
    }

    public String getSaleSignal()
    {
        return this.saleSignal;
    }

    public String getRentedSignal()
    {
        return this.rentedSignal;
    }

    public String getSoldSignal()
    {
        return this.soldSignal;
    }

}
