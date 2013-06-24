package me.jayfella.SafeCity.Listeners;

import me.jayfella.SafeCity.Core.ThinLocation;
import me.jayfella.SafeCity.Core.ZonePermissionType;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCitySubZone;
import me.jayfella.SafeCity.SafeCitySubZoneCollection;
import me.jayfella.SafeCity.SafeCityZone;
import me.jayfella.SafeCity.SafeCityZoneCollection;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Sign;

public final class SignsListener implements Listener
{
    private final SafeCityContext context;

    public SignsListener(SafeCityContext context)
    {
        this.context = context;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event)
    {
        if (event.getLine(0).equalsIgnoreCase(context.getSignManager().getSignActivator()))
        {
            initSignActivator(event);
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event)
    {
        boolean isRequiredAction = false;

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) { isRequiredAction = true; }
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) { isRequiredAction = true; }

        if (!isRequiredAction) { return; }

        if (event.getPlayer().isSneaking())
        {
            return;
        }

        Block signBlock = event.getClickedBlock();
        if (signBlock == null) { return; }

        Material material = event.getClickedBlock().getType();

        boolean isValidSign = false;
        if (material == Material.SIGN_POST) { isValidSign = true; }
        if (material == Material.WALL_SIGN) { isValidSign = true; }

        if (!isValidSign)
        {
            return;
        }

        org.bukkit.block.Sign sign = (org.bukkit.block.Sign)event.getClickedBlock().getState();

        if (sign.getLine(0).equals(context.getSignManager().getRentSignal()))
        {
            // cancel the block appearing
            event.setCancelled(true);

            Block attatchedBlock = null;

            if (signBlock.getType() == Material.WALL_SIGN)
            {
                Sign materialSign = (Sign)signBlock.getState().getData();
                attatchedBlock = signBlock.getRelative(materialSign.getAttachedFace());
            }

            Location loc = (attatchedBlock == null) ? signBlock.getLocation() : attatchedBlock.getLocation();

            context.getEstateManager().rentZone(event.getPlayer(), loc);
        }
        else if (sign.getLine(0).equals(context.getSignManager().getSaleSignal()))
        {
            // cancel the block appearing
            event.setCancelled(true);

            Block attatchedBlock = null;

            if (signBlock.getType() == Material.WALL_SIGN)
            {
                Sign materialSign = (Sign)signBlock.getState().getData();
                attatchedBlock = signBlock.getRelative(materialSign.getAttachedFace());
            }

            Location loc = (attatchedBlock == null) ? signBlock.getLocation() : attatchedBlock.getLocation();

            context.getEstateManager().sellZone(event.getPlayer(), loc);
        }

    }


    private void initSignActivator(SignChangeEvent event)
    {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Block signBlock = event.getBlock();
        Block attatchedBlock = null;

        if (signBlock.getType() == Material.WALL_SIGN)
        {
            Sign sign = (Sign)signBlock.getState().getData();
            attatchedBlock = signBlock.getRelative(sign.getAttachedFace());
        }

        ThinLocation signZoneLocation = context.toThinLocation((attatchedBlock == null) ? signBlock.getLocation() : attatchedBlock.getLocation());

        SafeCityZone foundZone = context.getZone(signZoneLocation, world);
        if (foundZone == null) { return; }

        SafeCitySubZone foundSubZone = context.getSubZone(signZoneLocation, world);

        if (!foundZone.hasPermission(player.getName(), ZonePermissionType.Owner))
        {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Only owners can add details signs.");
            return;
        }

        // check if a sign already exists
        if (foundSubZone != null)
        {
            if (foundSubZone.getInfoSign() != null)
            {
                boolean xIsSame = (signBlock.getLocation().getBlockX() == foundSubZone.getInfoSignLocation().getBlockX());
                boolean yIsSame = (signBlock.getLocation().getBlockY() == foundSubZone.getInfoSignLocation().getBlockY());
                boolean zIsSame = (signBlock.getLocation().getBlockZ() == foundSubZone.getInfoSignLocation().getBlockZ());

                boolean isSameLocation = (xIsSame & yIsSame & zIsSame);

                if (!isSameLocation)
                {
                    world.dropItem(signBlock.getLocation(), new ItemStack(Material.SIGN));
                    world.getBlockAt(signBlock.getLocation()).setType(Material.AIR);

                    player.sendMessage(ChatColor.RED + "A " + context.getSignManager().getSignActivator() + " sign already exists for zone: " + ChatColor.GOLD + foundSubZone.getName());
                    player.sendMessage(ChatColor.RED + "At location: " + ChatColor.GREEN
                            + "X: " + foundSubZone.getInfoSignLocation().getBlockX()
                            + "Y: " + foundSubZone.getInfoSignLocation().getBlockY()
                            + "Z: " + foundSubZone.getInfoSignLocation().getBlockZ());
                    return;
                }
            }
        }
        else
        {
            if (foundZone.getInfoSign() != null)
            {
                boolean xIsSame = (signBlock.getLocation().getBlockX() == foundZone.getInfoSignLocation().getBlockX());
                boolean yIsSame = (signBlock.getLocation().getBlockY() == foundZone.getInfoSignLocation().getBlockY());
                boolean zIsSame = (signBlock.getLocation().getBlockZ() == foundZone.getInfoSignLocation().getBlockZ());

                boolean isSameLocation = (xIsSame & yIsSame & zIsSame);

                if (!isSameLocation)
                {
                    world.dropItem(signBlock.getLocation(), new ItemStack(Material.SIGN));
                    world.getBlockAt(signBlock.getLocation()).setType(Material.AIR);

                    player.sendMessage(ChatColor.RED + "A " + context.getSignManager().getSignActivator() + " sign already exists for zone: " + ChatColor.GOLD + foundZone.getName());
                    player.sendMessage(ChatColor.RED + "At location: " + ChatColor.GREEN
                            + "X: " + foundZone.getInfoSignLocation().getBlockX()
                            + "Y: " + foundZone.getInfoSignLocation().getBlockY()
                            + "Z: " + foundZone.getInfoSignLocation().getBlockZ());


                    return;
                }
            }
        }

        boolean isZoneForSale = (foundZone.isForSale());
        boolean isSubZoneForSale = (foundSubZone != null && foundSubZone.isForSale());

        boolean isSubZoneSold = (foundSubZone != null && foundSubZone.isSold());

        boolean isZoneForRent = (foundZone.isForRent());
        boolean isSubZoneForRent = (foundSubZone != null && foundSubZone.isForRent());

        boolean isZoneRented = (foundZone.isRented());
        boolean isSubZoneRented = (foundSubZone != null && foundSubZone.isRented());

        // SUB-ZONES first!

        // for sale
        if (isSubZoneForSale)
        {
            context.getSignManager().setSaleSignal(event, foundSubZone.getSalePrice());
            foundSubZone.setInfoSignLocation(context.toThinLocation(signBlock.getLocation()));
        }

        // sold
        else if (isSubZoneSold) // primary zones cannot be "sold" - only owned.
        {
            context.getSignManager().setSoldSignal(event, foundSubZone.getBuyer());
            foundSubZone.setInfoSignLocation(context.toThinLocation(signBlock.getLocation()));
        }

        // for rent
        else if (isSubZoneForRent)
        {
            context.getSignManager().setRentSignal(event, foundSubZone.getSalePrice(), foundSubZone.getRentalLength());
            foundSubZone.setInfoSignLocation(context.toThinLocation(signBlock.getLocation()));
        }

        // rented
        else if (isSubZoneRented)
        {
            context.getSignManager().setRentedSign(event, foundSubZone.getRenter());
            foundSubZone.setInfoSignLocation(context.toThinLocation(signBlock.getLocation()));
        }

        // PRIMARY ZONES

        // for sale
        else if (isZoneForSale)
        {
            context.getSignManager().setSaleSignal(event, foundZone.getSalePrice());
            foundZone.setInfoSignLocation(context.toThinLocation(signBlock.getLocation()));
        }

        // for rent
        else if (isZoneForRent)
        {
            context.getSignManager().setRentSignal(event, foundZone.getSalePrice(), foundZone.getRentalLength());
            foundZone.setInfoSignLocation(context.toThinLocation(signBlock.getLocation()));
        }

        // rented
        else if (isZoneRented)
        {
            context.getSignManager().setRentedSign(event, foundZone.getRenter());
            foundZone.setInfoSignLocation(context.toThinLocation(signBlock.getLocation()));
        }

        // nothing - regular zone
        else
        {
            if (foundSubZone != null)
            {
                context.getSignManager().displayOwnerSign(event, foundSubZone.getFounder());
                foundSubZone.setInfoSignLocation(context.toThinLocation(signBlock.getLocation()));
            }
            else
            {
                context.getSignManager().displayOwnerSign(event, foundZone.getFounder());
                foundZone.setInfoSignLocation(context.toThinLocation(signBlock.getLocation()));
            }
        }
    }

    // used for state changes when chunks are not loaded.
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event)
    {
        Chunk chunk = event.getChunk();

        SafeCityZoneCollection zoneCollection = context.getZonesInChunk(chunk.getX(), chunk.getZ());

        if (zoneCollection == null)
            return;

        int minX = chunk.getX() << 4;
        int maxX = minX + 16;

        int minZ = chunk.getZ() << 4;
        int maxZ = minZ + 16;

        for (SafeCityZone z : zoneCollection.getAllZones())
        {
            if (z.getInfoSign() == null)
                continue;

            // if sign isnt in this chunk, dont force load it.
            if (z.getInfoSignLocation().getBlockX() < minX || z.getInfoSignLocation().getBlockX() > maxX || z.getInfoSignLocation().getBlockZ() < minZ || z.getInfoSignLocation().getBlockZ() > maxZ)
                continue;

            if (z.isForSale())
            {
                context.getSignManager().setSaleSignal(z.getInfoSign(), z.getSalePrice());
            }
            else if (z.isForRent())
            {
                context.getSignManager().setRentSignal(z.getInfoSign(), z.getSalePrice(), z.getRentalLength());
            }
            else if (z.isRented())
            {
                context.getSignManager().setRentedSign(z.getInfoSign(), z.getRenter());
            }
            else
            {
                context.getSignManager().displayOwnerSign(z.getInfoSign(), z.getFounder());
            }
        }

        SafeCitySubZoneCollection subZoneCollection = context.getSubZonesInChunk(chunk.getX(), chunk.getZ());

        if (subZoneCollection == null)
            return;

        for (SafeCitySubZone sz : subZoneCollection.getAllSubZones())
        {
            if (sz.getInfoSign() == null)
                continue;

            // if sign isnt in this chunk, dont force load it.
            if (sz.getInfoSignLocation().getBlockX() < minX || sz.getInfoSignLocation().getBlockX() > maxX || sz.getInfoSignLocation().getBlockZ() < minZ || sz.getInfoSignLocation().getBlockZ() > maxZ)
                continue;

            if (sz.isForSale())
            {
                context.getSignManager().setSaleSignal(sz.getInfoSign(), sz.getSalePrice());
            }
            else if (sz.isSold())
            {
                context.getSignManager().setSoldSignal(sz.getInfoSign(), sz.getBuyer());
            }
            else if (sz.isForRent())
            {
                context.getSignManager().setRentSignal(sz.getInfoSign(), sz.getSalePrice(), sz.getRentalLength());
            }
            else if (sz.isRented())
            {
                context.getSignManager().setRentedSign(sz.getInfoSign(), sz.getRenter());
            }
            else
            {
                context.getSignManager().displayOwnerSign(sz.getInfoSign(), sz.getFounder());
            }
        }

    }

}
