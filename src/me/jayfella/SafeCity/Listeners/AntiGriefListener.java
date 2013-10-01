package me.jayfella.SafeCity.Listeners;

import java.util.Iterator;
import java.util.List;
import me.jayfella.SafeCity.Core.ThinLocation;
import me.jayfella.SafeCity.Core.ZonePermissionType;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCityPlayer;
import me.jayfella.SafeCity.SafeCitySubZone;
import me.jayfella.SafeCity.SafeCityZone;
import me.jayfella.SafeCity.SafeCityZoneCollection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.PortalCreateEvent.CreateReason;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;

public final class AntiGriefListener implements Listener
{
    private final SafeCityContext context;

    public AntiGriefListener(SafeCityContext context)
    {
        this.context = context;
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event)
    {
        // avoid destination portals being created in a zone
        if (event.getReason() == CreateReason.OBC_DESTINATION)
        {
            // ignore other world types
            if (event.getWorld().getEnvironment() != Environment.NORMAL)
            {
                return;
            }

            List<Block> portalBlocks = event.getBlocks();

            for (Block block : portalBlocks)
            {
                int chunkX = block.getLocation().getBlockX() >> 4;
                int chunkZ = block.getLocation().getBlockZ() >> 4;

                SafeCityZoneCollection zoneCollection = context.getZonesInChunk(block.getWorld(), chunkX, chunkZ);

                for (SafeCityZone zone : zoneCollection.getAllZones())
                {
                    if (zone.isInsideZone(context.toThinLocation(block.getLocation()), block.getWorld(), false))
                    {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onTreeGrow(StructureGrowEvent event)
    {
        if (event.getPlayer() == null)
            return;

        if (!context.isValidWorld(event.getPlayer().getWorld().getName()))
            return;

        SafeCityZone rootZone = context.getZone(context.toThinLocation(event.getLocation()), event.getLocation().getWorld());
        SafeCitySubZone rootSubZone = context.getSubZone(context.toThinLocation(event.getLocation()), event.getWorld());

        for (int i = 0; i < event.getBlocks().size(); i++)
        {
            BlockState block = (BlockState)event.getBlocks().get(i);

            SafeCityZone blockZone = context.getZone(context.toThinLocation(block.getLocation()), block.getLocation().getWorld());
            SafeCitySubZone blockSubZone = context.getSubZone(context.toThinLocation(block.getLocation()), block.getWorld());

            if (rootZone != blockZone | rootSubZone != blockSubZone)
            {
                event.getBlocks().remove(i--);
            }
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
    public void onBlockPistonExtend(BlockPistonExtendEvent event)
    {
        if (!context.isValidWorld(event.getBlock().getWorld().getName()))
            return;

        Block pistonBlock = event.getBlock();
        Block invadedBlock = pistonBlock.getRelative(event.getDirection());

        SafeCityZone pistonZone = context.getZone(context.toThinLocation(event.getBlock().getLocation()), event.getBlock().getWorld());
        SafeCityZone invBlockZone = context.getZone(context.toThinLocation(invadedBlock.getLocation()), invadedBlock.getWorld());

        SafeCitySubZone pistonSubZone = context.getSubZone(context.toThinLocation(pistonBlock.getLocation()), pistonBlock.getWorld());
        SafeCitySubZone invBlockSubZone = context.getSubZone(context.toThinLocation(invadedBlock.getLocation()), invadedBlock.getWorld());

        List<Block> blocks = event.getBlocks();

        // if no blocks are being moved
        if (blocks.isEmpty())
        {
            if (pistonZone != invBlockZone || pistonSubZone != invBlockSubZone)
            {
                event.setCancelled(true);
                event.getBlock().getWorld().createExplosion(event.getBlock().getLocation(), 0.0F);
                event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(event.getBlock().getType()));
                event.getBlock().setType(Material.AIR);
            }

            return;
        }

        for (Block block : blocks)
        {
            SafeCityZone blockZone = context.getZone(context.toThinLocation(block.getLocation()), block.getWorld());
            SafeCitySubZone blockSubZone = context.getSubZone(context.toThinLocation(block.getLocation()), block.getWorld());

            if (blockZone != pistonZone || blockSubZone != pistonSubZone)
            {
                event.setCancelled(true);
                event.getBlock().getWorld().createExplosion(event.getBlock().getLocation(), 0.0F);
                event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(event.getBlock().getType()));
                event.getBlock().setType(Material.AIR);
                return;
            }
        }


        int xChange = 0;
        int zChange = 0;

        Block piston = event.getBlock();
        Block firstBlock = (Block)blocks.get(0);

        if (firstBlock.getX() > piston.getX())
        {
            xChange = 1;
        }
        else if (firstBlock.getX() < piston.getX())
        {
            xChange = -1;
        }
        else if (firstBlock.getZ() > piston.getZ())
        {
            zChange = 1;
        }
        else if (firstBlock.getZ() < piston.getZ())
        {
            zChange = -1;
        }

        if ((xChange != 0) || (zChange != 0))
        {
            for (Block block : blocks)
            {
                SafeCityZone newZone = context.getZone(context.toThinLocation(block.getLocation().add(xChange, 0.0D, zChange)), block.getWorld());
                SafeCitySubZone newSubZone = context.getSubZone(context.toThinLocation(block.getLocation().add(xChange, 0.0D, zChange)), block.getWorld());

                // if ((originalZone != null) && (newZone != null) && (newZone.isOwner(originalZone.getOwner())))
                if (pistonZone != newZone || pistonSubZone != newSubZone)
                {
                    event.setCancelled(true);
                    event.getBlock().getWorld().createExplosion(event.getBlock().getLocation(), 0.0F);
                    event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(event.getBlock().getType()));
                    event.getBlock().setType(Material.AIR);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event)
    {
        if (!context.isValidWorld(event.getBlock().getWorld().getName()))
            return;

        if (event.isSticky())
        {
           if (event.getRetractLocation().getBlock().getType() == Material.AIR)
            {
                return;
            }

            SafeCityZone movingBlockZone = context.getZone(context.toThinLocation(event.getRetractLocation()), event.getRetractLocation().getWorld());
            SafeCityZone pistonZone = context.getZone(context.toThinLocation(event.getBlock().getLocation()), event.getRetractLocation().getWorld());

            SafeCityZone movingBlockSubZone = context.getZone(context.toThinLocation(event.getRetractLocation()), event.getRetractLocation().getWorld());
            SafeCityZone pistonSubZone = context.getZone(context.toThinLocation(event.getBlock().getLocation()), event.getRetractLocation().getWorld());

            if (movingBlockZone != pistonZone || movingBlockSubZone != pistonSubZone)
            {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockIgnite(BlockIgniteEvent event)
    {
        if (!context.isValidWorld(event.getBlock().getWorld().getName()))
            return;

        if (event.getCause() == IgniteCause.SPREAD) { event.setCancelled(true); }

        // limit portal creation to owners only
        if (event.getBlock().getType() == Material.OBSIDIAN)
        {
            if (event.getCause() == IgniteCause.FLINT_AND_STEEL)
            {
                SafeCityZone zone = context.getZone(context.toThinLocation(event.getBlock().getLocation()), event.getBlock().getLocation().getWorld());

                if (zone == null) { return; }

                Player player = event.getPlayer();

                if (player != null)
                {
                    if (!zone.hasPermission(player.getName(), ZonePermissionType.Owner))
                    {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event)
    {
        if (!context.isValidWorld(event.getBlock().getWorld().getName()))
            return;

        Block fromBlock = event.getBlock();
        SafeCityZone fromZone = context.getZone(context.toThinLocation(fromBlock.getLocation()), fromBlock.getWorld());
        SafeCitySubZone fromSubZone = context.getSubZone(context.toThinLocation(fromBlock.getLocation()), fromBlock.getWorld());

        Block toBlock = event.getToBlock();
        SafeCityZone toZone = context.getZone(context.toThinLocation(toBlock.getLocation()), toBlock.getWorld());
        SafeCitySubZone toSubZone = context.getSubZone(context.toThinLocation(toBlock.getLocation()), toBlock.getWorld());

	if (fromZone != toZone | fromSubZone != toSubZone)
        {
            event.setCancelled(true);
        }

    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
    public void onEntityChangeBlock(EntityChangeBlockEvent event)
    {
        if (!context.isValidWorld(event.getEntity().getWorld().getName()))
            return;

        if (event.getEntityType() == EntityType.ENDERMAN)
        {
            event.setCancelled(true);
        }
        else if (event.getEntityType() == EntityType.SILVERFISH)
        {
            event.setCancelled(true);
        }
        else if (event.getEntityType() == EntityType.WITHER)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
    public void onEntityExplode(EntityExplodeEvent event)
    {
        if (!context.isValidWorld(event.getEntity().getWorld().getName()))
            return;

        if (context.getPluginSettings().creeperExplosionAllowed() == false && context.getPluginSettings().tntExplosionAllowed() == false)
        {
            event.blockList().clear();
            return;
        }
        
        Iterator<Block> iterator = event.blockList().iterator();
        
        if (event.getEntity().getType() == EntityType.CREEPER)
        {
            if (context.getPluginSettings().creeperExplosionAllowed())
            {
                while (iterator.hasNext())
                {
                    Block block = iterator.next();

                    Location loc = block.getLocation();
                    ThinLocation tLoc = context.toThinLocation(loc);
                    SafeCityZone zone = context.getZone(tLoc, loc.getWorld());

                    if (zone != null)
                        iterator.remove();
                }
            }
            else
            {
                event.blockList().clear();
            }
            
        }
        
        if (event.getEntity().getType() == EntityType.PRIMED_TNT)
        {
            if (context.getPluginSettings().tntExplosionAllowed())
            {
                while (iterator.hasNext())
                {
                    Block block = iterator.next();

                    Location loc = block.getLocation();
                    ThinLocation tLoc = context.toThinLocation(loc);
                    SafeCityZone zone = context.getZone(tLoc, loc.getWorld());

                    if (zone != null)
                        iterator.remove();
                }
            }
        }
        
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onEntityPickup(EntityChangeBlockEvent event)
    {
        if (!context.isValidWorld(event.getEntity().getWorld().getName()))
            return;

        if ((event.getEntity() instanceof Enderman))
        {
            event.setCancelled(true);
        }
    }

    // TNT
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if (!context.isValidWorld(event.getPlayer().getWorld().getName()))
            return;

        // if (event.getBlock().getType() == Material.TNT && context.getPluginSettings().tntExplosionAllowed() == false)
        // {
            // event.setCancelled(true);
        // }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onTntPrime(ExplosionPrimeEvent event)
    {
        // maybe we should leave worldguard to deal with block placement instad.
        
        // if (!context.isValidWorld(event.getEntity().getWorld().getName()))
            // return;

        // if (event.getEntity() instanceof TNTPrimed)
        // {
            // event.setCancelled(true);
        // }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onBlockBurn(BlockBurnEvent event)
    {
        if (!context.isValidWorld(event.getBlock().getWorld().getName()))
            return;

        event.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onCropTrample(PlayerInteractEvent event)
    {
        if (!context.isValidWorld(event.getPlayer().getWorld().getName()))
            return;

        if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL)
        {
            SafeCityPlayer scPlayer = context.getPlayer(event.getPlayer());

            SafeCityZone zone = context.getZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld());

            if (zone == null)
            {
                return;
            }

            if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
            {
               return;
            }

            SafeCitySubZone subZone = context.getSubZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld());

            if (subZone != null)
            {
                if (subZone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Buyer))
                {
                    return;
                }

                if (subZone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Farmer))
                {
                    return;
                }

                if (subZone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Build))
                {
                    return;
                }
            }
            else
            {
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Buyer))
                {
                    return;
                }

                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Farmer))
                {
                    return;
                }

                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Build))
                {
                    return;
                }
            }

            event.setCancelled(true);
         }
    }
    
}
