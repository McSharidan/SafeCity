package me.jayfella.SafeCity.Listeners;

import me.jayfella.SafeCity.Core.SafeCityTool;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCityPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public final class ToolInHandListener implements Listener
{
    private final SafeCityContext context;

    public ToolInHandListener(SafeCityContext context)
    {
        this.context = context;
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event)
    {
        if (!context.isValidWorld(event.getPlayer().getWorld().getName()))
            return;

    	SafeCityPlayer scPlayer = context.getPlayer(event.getPlayer());

        if ((scPlayer.hasToolInHand(event, SafeCityTool.ZoneInfoTool.material())) && (!(scPlayer.getZoneManager().isResizing() | scPlayer.getZoneManager().isCreatingZone() | scPlayer.getZoneManager().isCreatingSubZone())))
        {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Info_Notify());
                scPlayer.getVisualManager().getZoneVisualizer().visualize();
        }
        else
        {
            scPlayer.getVisualManager().clearVisuals(true);

            if (scPlayer.hadToolInHand(event, new Material[] { SafeCityTool.ZoneTool.material(), SafeCityTool.ZoneTool3d.material() }))
            {
                if (scPlayer.getZoneManager().isResizing())
                {
                    scPlayer.getZoneManager().clearResizeData();
                    scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Resize_Cancel());
                }
                else if (scPlayer.getZoneManager().isCreatingZone() | scPlayer.getZoneManager().isCreatingSubZone())
                {
                    scPlayer.getZoneManager().clearCreationData();
                    scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Create_Cancel());
                }
            }
        }

        if (scPlayer.hasToolInHand(event, SafeCityTool.ZoneTool.material()))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Create_Notify());
        }
        else if (scPlayer.hasToolInHand(event, SafeCityTool.ZoneTool3d.material()))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Create3d_Notify());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        if (!context.isValidWorld(event.getPlayer().getWorld().getName()))
            return;

        SafeCityPlayer scPlayer = context.getPlayer((Player)event.getPlayer());

        ItemStack itemInHandStack = scPlayer.getBukkitPlayer().getItemInHand();

        if (itemInHandStack.getType() != SafeCityTool.ZoneInfoTool.material())
        {
            scPlayer.getVisualManager().clearVisuals(true);
        }
        else
        {
            scPlayer.getVisualManager().clearVisuals(false);
            scPlayer.getVisualManager().getZoneVisualizer().visualize();
        }

    }

    @EventHandler
    public void onPlayerPickupItem(final PlayerPickupItemEvent event)
    {
        if (event.getItem().getItemStack().getType() == SafeCityTool.ZoneInfoTool.material())
        {
            // check the contents of the players hand on the next tick
            context.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(context.getPlugin(),

                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (event.getPlayer().getItemInHand().getType() == SafeCityTool.ZoneInfoTool.material())
                            {
                                SafeCityPlayer scPlayer = context.getPlayer(event.getPlayer());

                                scPlayer.getVisualManager().clearVisuals(false);
                                scPlayer.getVisualManager().getZoneVisualizer().visualize();
                            }
                        }
                    }

                    , 1L);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        SafeCityPlayer scPlayer = context.getPlayer(event.getPlayer());

        // throwing a stick
        if (event.getItemDrop().getItemStack().getType() == SafeCityTool.ZoneInfoTool.material() && event.getPlayer().getItemInHand().getType() == Material.AIR)
        {
            scPlayer.getVisualManager().clearVisuals(false);
        }

        // throwing a wood shovel
        if (event.getItemDrop().getItemStack().getType() == SafeCityTool.ZoneTool.material() && event.getPlayer().getItemInHand().getType() == Material.AIR)
        {
            if (scPlayer.getZoneManager().isResizing())
            {
                scPlayer.getZoneManager().clearResizeData();
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Resize_Cancel());
            }
            else if (scPlayer.getZoneManager().isCreatingZone() | scPlayer.getZoneManager().isCreatingSubZone())
            {
                scPlayer.getZoneManager().clearCreationData();
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Create_Cancel());
            }
        }

        // throwing a gold shovel
        if (event.getItemDrop().getItemStack().getType() == SafeCityTool.ZoneTool3d.material() && event.getPlayer().getItemInHand().getType() == Material.AIR)
        {
            if (scPlayer.getZoneManager().isResizing())
            {
                scPlayer.getZoneManager().clearResizeData();
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Resize_Cancel());
            }
            else if (scPlayer.getZoneManager().isCreatingSubZone())
            {
                scPlayer.getZoneManager().clearCreationData();
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Create_Cancel());
            }
        }
    }

}
