package me.jayfella.SafeCity.Commands;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Set;
import me.jayfella.SafeCity.Core.BlockPriceMultiplier;
import me.jayfella.SafeCity.Core.PluginPermission;
import me.jayfella.SafeCity.Core.ThinLocation;
import me.jayfella.SafeCity.Core.ZonePermissionType;
import me.jayfella.SafeCity.Runnables.AutoResetDeleteConfirmationThread;
import me.jayfella.SafeCity.Runnables.DisplayZoneData_Thread;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCityPlayer;
import me.jayfella.SafeCity.SafeCitySubZone;
import me.jayfella.SafeCity.SafeCityZone;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ZoneCommands implements CommandExecutor
{

    private final SafeCityContext context;

    public ZoneCommands(SafeCityContext context)
    {
        this.context = context;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(context.getMessageHandler().Ingame_Only());
            return true;
        }

        if (args.length == 0)
        {
            displayHelp(sender, cmd, label, args);
            return true;
        }

        if (args.length > 0 && args[0].equals("help"))
        {
            displayHelp(sender, cmd, label, args);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("setname"))
        {
            nameZone(sender, cmd, label, args);
            return true;
        }

        // BLOCKS
        // ######

        if (args.length == 1 && args[0].equalsIgnoreCase("blockinfo"))
        {
            blocksLeft(sender, cmd, label, args);
            return true;
        }

        if (args[0].equalsIgnoreCase("blocks"))
        {
            if (args.length != 3)
            {
                sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("Useage: ").append(ChatColor.GOLD).append("/z blocks ").append(ChatColor.RED).append("<buy|quote> <amount>").toString());
                return true;
            }

            if (args[1].equalsIgnoreCase("buy"))
            {
                buyBlocks(sender, cmd, label, args);
                return true;
            }

            if (args[1].equalsIgnoreCase("quote"))
            {
                quoteBlocks(sender, cmd, label, args);
                return true;
            }
        }

        // END BLOCKS
        // ##########


        // PERMISSIONS
        // ###########

        for (ZonePermissionType p : ZonePermissionType.values())
        {
            if (args.length > 0 && args[0].equalsIgnoreCase(p.name()))
            {
                if (args.length < 2)
                {
                    sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("Useage: ").append(ChatColor.GOLD).append("/z ").append(p.name().toLowerCase(Locale.getDefault())).append(" ").append(ChatColor.RED).append("<add|remove|list> <player>").toString());
                    return true;
                }


                if (args[1].equalsIgnoreCase("add"))
                {
                    perm_add(sender, args, p);
                    return true;
                }

                if (args[1].equalsIgnoreCase("remove"))
                {
                    perm_remove(sender, args, p);
                    return true;
                }

                if (args[1].equalsIgnoreCase("list"))
                {
                    perm_list(sender, args, p);
                    return true;
                }

            }
        }

        // END PERMISSIONS
        // ###############


        // delete zone
        if (args.length == 1 && args[0].equalsIgnoreCase("delete"))
        {
            deleteZone(sender, cmd, label, args);
            return true;
        }


        // show public zone list
        if (args.length == 1 && args[0].equalsIgnoreCase("list"))
        {
            showList(sender, cmd, label, args);
            return true;
        }

        // sell / rent / unsell / unrent zone
        if (args.length > 1 && args[0].equalsIgnoreCase("set"))
        {
            if (args[1].equalsIgnoreCase("forsale"))
            {
                sellZone(sender, cmd, label, args);
                return true;
            }

            if (args[1].equalsIgnoreCase("notforsale"))
            {
                unSellZone(sender, cmd, label, args);
                return true;
            }

            if (args[1].equalsIgnoreCase("forrent"))
            {
                rentZone(sender, cmd, label, args);
                return true;
            }

            if (args[1].equalsIgnoreCase("notforrent"))
            {
                unrentZone(sender, cmd, label, args);
                return true;
            }

            if (args[1].equalsIgnoreCase("public"))
            {
                makePublic(sender, cmd, label, args);
                return true;
            }

            if (args[1].equalsIgnoreCase("private"))
            {
                makePrivate(sender, cmd, label, args);
                return true;
            }

            if (args[1].equalsIgnoreCase("vinegrowth"))
            {
                setVineGrowth(sender, cmd, label, args);
                return true;
            }

        }

        if (args.length == 1 && args[0].equalsIgnoreCase("buy"))
        {
            buyZone(sender, cmd, label, args);
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("rent"))
        {
            beginRentingZone(sender, cmd, label, args);
            return true;
        }


        if (args.length == 2 && args[0].equalsIgnoreCase("kick"))
        {
            kickPlayer(sender, cmd, label, args);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("entrymessage"))
        {
            setEntryMessage(sender, cmd, label, args);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("exitmessage"))
        {
            setEntryMessage(sender, cmd, label, args);
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("setspawn"))
        {
            setZoneSpawn(sender, cmd, label, args);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("spawn"))
        {
            zoneSpawn(sender, cmd, label, args);
            return true;
        }

        if (args.length > 0 && args[0].equals("publicspawn"))
        {
            if (args.length != 2)
            {
                sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("Useage: ").append("/z publicspawn ").append(ChatColor.RED).append("<enable|disable> ").toString());
                return true;
            }

            if (args[1].equalsIgnoreCase("enable"))
            {
                enablePublicSpawn(sender, cmd, label, args);
                return true;
            }

            if (args[1].equalsIgnoreCase("disable"))
            {
                disablePublicSpawn(sender, cmd, label, args);
                return true;
            }

        }

        if (args.length > 0 && args[0].equalsIgnoreCase("pvp"))
        {
            if (args.length != 2)
            {
                sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("Useage: ").append("/z pvp ").append(ChatColor.RED).append("<enable|disable> ").toString());
                return true;
            }

            if (args[1].equalsIgnoreCase("enable"))
            {
                pvpEnable(sender, cmd, label, args);
                return true;
            }

            if (args[1].equalsIgnoreCase("disable"))
            {
                pvpDisable(sender, cmd, label, args);
                return true;
            }

        }

        if (args.length > 0 && args[0].equalsIgnoreCase("mobs"))
        {
            if (args.length != 2)
            {
                sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("Useage: ").append("/z pvp ").append(ChatColor.RED).append("<enable|disable> ").toString());
                return true;
            }

            if (args[1].equalsIgnoreCase("enable"))
            {
                mobEnable(sender, cmd, label, args);
                return true;
            }

            if (args[1].equalsIgnoreCase("disable"))
            {
                mobDisable(sender, cmd, label, args);
                return true;
            }

        }

        if (args.length > 0 && args[0].equalsIgnoreCase("new"))
        {
            createNewZone(sender, cmd, label, args);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("info"))
        {
            displayZoneData(sender, cmd, label, args);
            return true;
        }
        
        return true;
    }

    private void nameZone(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (args.length == 1)
        {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("Useage: ").append(ChatColor.GOLD).append("/z setname ").append(ChatColor.RED).append("<name of town>").toString());
            return;
        }

        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);

        ThinLocation playerLocation = new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ());

        SafeCityZone zone = context.getZone(playerLocation, scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        SafeCitySubZone subZone = context.getSubZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld());

        if (subZone == null)
        {
            if (!zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
                return;
            }
        } 
        else
        {
            boolean canRename = false;

            for (ZonePermissionType p : ZonePermissionType.values())
            {
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                {
                    canRename = true;
                    break;
                } else if (subZone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
                {
                    if (p.getPermissions().canRenameSubZones())
                    {
                        canRename = true;
                    }
                }
            }

            if (!canRename)
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
                return;
            }
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < args.length; i++)
        {
            sb.append(args[i]).append(" ");
        }

        String newZoneName = sb.toString().trim();
        String oldZoneName;

        if (newZoneName.length() < 1)
        {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("Useage: ").append(ChatColor.GOLD).append("/z setname ").append(ChatColor.RED).append("<name of town>").toString());
            return;
        }

        if (newZoneName.length() > 32)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Exceeds_SizeLimit(newZoneName, 32));
            return;
        }

        if (subZone == null)
        {
            oldZoneName = zone.getName();

            if (zone.getName().equalsIgnoreCase("Protected Zone"))
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Named(newZoneName));
            } else
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Renamed(oldZoneName, newZoneName));
            }

            zone.setName(newZoneName);
        } else
        {
            oldZoneName = subZone.getName();

            if (subZone.getName().equalsIgnoreCase("Sub-Zone"))
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Named(newZoneName));
            } else
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Renamed(oldZoneName, newZoneName));
            }

            subZone.setName(newZoneName);
        }
    }

    private void deleteZone(CommandSender sender, Command cmd, String label, String[] args)
    {
        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);
        ThinLocation playerLocation = new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ());


        SafeCityZone zone = context.getZone(playerLocation, scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        if (!zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
            return;
        }

        SafeCitySubZone subZone = context.getSubZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld());

        if (subZone == null)
        {
            if (zone.isPublic() && zone.getPopulation().length > 0)
            {
                if (!scPlayer.getBukkitPlayer().hasPermission(PluginPermission.Delete_City.permissionNode()))
                {
                    scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + "You do not have permission to delete a public city with a population greater than zero.");
                    return;
                }
            }
        }

        // get confirmation
        if (!scPlayer.hasConfirmedDelete())
        {
            StringBuilder delConfirm = new StringBuilder()
                    .append(ChatColor.RED)
                    .append("Re-Type to confirm deleting the ")
                    .append(ChatColor.GOLD)
                    .append((subZone == null) ? "PRIMARY ZONE " : "SUB-ZONE ")
                    .append(ChatColor.GREEN)
                    .append((subZone == null) ? zone.getName() : subZone.getName());

            scPlayer.getBukkitPlayer().sendMessage(delConfirm.toString());

            // set a delayed task to reset after ten seconds.
            long resetConfirm = 200L;

            context.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(context.getPlugin(), new AutoResetDeleteConfirmationThread(scPlayer), resetConfirm);

            scPlayer.setDeleteConfirmation(true);

            return;
        }

        if (subZone == null)
        {
            // delete sub-zones
            SafeCitySubZone[] childZones = zone.getChildren();

            for (int i = 0; i < childZones.length; i++)
            {
                context.removeSubZone(childZones[i]);
                context.removeSubZoneFromMap(childZones[i]);
            }

            // remove children from database
            int subZoneCount = context.getMySql().removeSubZones(zone.getId());
            
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Deleted(subZoneCount));

            context.getMySql().removeZone(zone.getId());
            context.removeZone(zone);
            context.removeZoneFromMap(zone);
            
        } else
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().SubZone_Deleted(subZone.getName()));
            
            context.getMySql().removeSubZone(subZone.getId());
            context.removeSubZone(subZone);
            context.removeSubZoneFromMap(subZone);            
        }

        // refresh zone information
        scPlayer.getZoneManager().setCurrentZone(context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld()));
        scPlayer.setDeleteConfirmation(false);

    }

    // PERMISSIONS
    // ###########
    private boolean canRecruit(String playerName, SafeCityZone zone, SafeCitySubZone subZone)
    {
        if (subZone == null)
        {
            for (ZonePermissionType p : ZonePermissionType.values())
            {
                if (zone.hasPermission(playerName, p))
                {
                    if (p.getPermissions().canRecruit())
                    {
                        return true;
                    }
                }
            }
        } 
        else
        {
            for (ZonePermissionType p : ZonePermissionType.values())
            {
                if (zone.hasPermission(playerName, p))
                {
                    if (p.getPermissions().canRecruit())
                    {
                        return true;
                    }
                }
            }

            for (ZonePermissionType p : ZonePermissionType.values())
            {
                if (subZone.hasPermission(playerName, p))
                {
                    if (p.getPermissions().canRecruit())
                    {
                        return true;
                    }
                }
            }
            
            // buyer permission
            // buyer does not belong 
            
        }

        return false;
    }

    private void perm_add(CommandSender sender, String[] args, ZonePermissionType permission)
    {
        if (args.length != 3)
        {
            sender.sendMessage(new StringBuilder()
                    .append(ChatColor.RED)
                    .append("Useage: ")
                    .append(ChatColor.GOLD)
                    .append("/z ")
                    .append(permission.name())
                    .append(" ")
                    .append(ChatColor.RED)
                    .append("<add|remove|list> <player>")
                    .toString());
            return;
        }

        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);
        SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            sender.sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        SafeCitySubZone subZone = context.getSubZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld());

        if (subZone != null)
        {
            if (permission.getPermissions().primaryZoneOnly())
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Permission_PrimaryZone_Only(permission));
                return;
            }
        }

        if (subZone == null)
        {
            if (permission.getPermissions().requiresOwnerPermission())
            {
                boolean isAllowed = false;
                
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                {
                    isAllowed = true;
                }
                
                // staff override
                if (scPlayer.getBukkitPlayer().hasPermission(PluginPermission.Staff_Override.permissionNode()))
                {
                    scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + ">>** Staff Override - Owner Permission");
                    isAllowed = true;
                }
                
                if (!isAllowed)
                {
                    scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Permission_RequiresOwner(permission));
                    return;
                }
            }
        } 
        else
        {
            if (permission.getPermissions().requiresOwnerPermission())
            {
                if (!subZone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                {
                    scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Permission_RequiresOwner(permission));
                    return;
                }
            }
        }

        if (!canRecruit(scPlayer.getBukkitPlayer().getName(), zone, subZone))
        {
            if (scPlayer.getBukkitPlayer().hasPermission(PluginPermission.Staff_Override.permissionNode()))
            {
                scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + ">>** Staff Override - Recruit Permission");
            }
            else
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
                return;
            }
        }

        if (args[2].equals("*"))
        {
            if (!permission.getPermissions().canSetPublic())
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Permission_Cannot_SetPublic(permission));
                return;
            }
        }

        Player onPlayer = (!args[2].equals("*")) ? context.getPlugin().getServer().getPlayer(args[2]) : null;
        OfflinePlayer offPlayer = (!args[2].equals("*")) ? context.getPlugin().getServer().getOfflinePlayer(args[2]) : null;

        String newPlayerName;

        if (!args[2].equals("*"))
        {
            if ((onPlayer == null) && (offPlayer == null))
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Player_Not_Exist(args[2]));
                return;
            }

            newPlayerName = (onPlayer == null) ? offPlayer.getName() : onPlayer.getName();
        } 
        else
        {
            newPlayerName = args[2];
        }

        if (subZone == null)
        {
            if (zone.hasPermission(newPlayerName, permission))
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Permission_Exists(newPlayerName));
                return;
            }
        } 
        else
        {
            if (subZone.hasPermission(newPlayerName, permission))
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Permission_Exists(newPlayerName));
                return;
            }
        }

        if (subZone == null)
        {
            zone.addPermission(permission, newPlayerName);
        } 
        else
        {
            subZone.addPermission(permission, newPlayerName);
        }

        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Permission_Granted(permission.name(), args[2], (subZone == null) ? zone.getName() : subZone.getName()));

        if (onPlayer != null)
        {
            onPlayer.sendMessage(context.getMessageHandler().Permission_Granted_Player(permission.name(), (subZone == null) ? zone.getName() : subZone.getName(), scPlayer.getBukkitPlayer().getName()));
        }

        context.getConsole().sendMessage(new StringBuilder()
                .append(ChatColor.GOLD)
                .append("Player ")
                .append(ChatColor.RED)
                .append(scPlayer.getBukkitPlayer().getName())
                .append(ChatColor.GOLD)
                .append(" gave ")
                .append(ChatColor.RED)
                .append(permission.name())
                .append(ChatColor.GOLD)
                .append(" permission to player ")
                .append(newPlayerName)
                .toString());
    }

    private void perm_remove(CommandSender sender, String[] args, ZonePermissionType permission)
    {
        if (args.length != 3)
        {
            sender.sendMessage(new StringBuilder()
                    .append(ChatColor.RED)
                    .append("Useage: ")
                    .append(ChatColor.GOLD)
                    .append("/z ")
                    .append(permission.name())
                    .append(" ")
                    .append(ChatColor.RED)
                    .append("<add|remove|list> <player>")
                    .toString());

            return;
        }

        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);

        SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        SafeCitySubZone subZone = context.getSubZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld());

        if (subZone != null)
        {
            if (permission.getPermissions().primaryZoneOnly())
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Permission_PrimaryZone_Only(permission));
                return;
            }
        }

        if (subZone == null)
        {
            if (permission.getPermissions().requiresOwnerPermission())
            {
                boolean isAllowed = false;
                
                if (scPlayer.getBukkitPlayer().hasPermission(PluginPermission.Staff_Override.permissionNode()))
                {
                    scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + ">>** Staff Override - Owner Permission");
                    isAllowed = true;
                }
                
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                {
                    isAllowed = true;
                }
                
                if (!isAllowed)
                {
                    scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Permission_RequiresOwner(permission));
                    return;
                }
            }
        } 
        else
        {
            if (permission.getPermissions().requiresOwnerPermission())
            {
                if (!subZone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                {
                    scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Permission_RequiresOwner(permission));
                    return;
                }
            }
        }

        if (!canRecruit(scPlayer.getBukkitPlayer().getName(), zone, subZone))
        {
            if (scPlayer.getBukkitPlayer().hasPermission(PluginPermission.Staff_Override.permissionNode()))
            {
                scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + ">>** Staff Override - Recruit Permission");
            }
            else
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
                return;
            }
        }

        if (args[2].equals("*"))
        {
            if (!permission.getPermissions().canSetPublic())
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Permission_Cannot_SetPublic(permission));
                return;
            }
        }

        Player onPlayer = context.getPlugin().getServer().getPlayer(args[2]);
        OfflinePlayer offPlayer = context.getPlugin().getServer().getOfflinePlayer(args[2]);

        if ((onPlayer == null) && (offPlayer == null))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Player_Not_Exist(args[2]));
            return;
        }

        String newPlayerName;

        if (!args[2].equals("*"))
        {
            if ((onPlayer == null) && (offPlayer == null))
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Player_Not_Exist(args[2]));
                return;
            }

            newPlayerName = (onPlayer == null) ? offPlayer.getName() : onPlayer.getName();
        } 
        else
        {
            newPlayerName = args[2];
        }

        if (subZone == null)
        {
            // if (!zone.hasPermission(newPlayerName, permission))
            if (!zone.hasPermission(newPlayerName, permission))
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Permission_Not_Exists(newPlayerName));
                return;
            }
        } 
        else
        {
            if (!subZone.hasPermission(newPlayerName, permission))
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Permission_Not_Exists(newPlayerName));
                return;
            }
        }


        if (subZone == null)
        {
            zone.removePermission(permission, newPlayerName);
        } else
        {
            subZone.removePermission(permission, newPlayerName);
        }

        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Permission_Revoked(permission.name(), args[2], (subZone == null) ? zone.getName() : subZone.getName()));

        if (onPlayer != null)
        {
            onPlayer.sendMessage(context.getMessageHandler().Permission_Revoked_Player(permission.name(), (subZone == null) ? zone.getName() : subZone.getName(), scPlayer.getBukkitPlayer().getName()));
        }

        context.getConsole().sendMessage(new StringBuilder()
                .append(ChatColor.GOLD)
                .append("Player ")
                .append(ChatColor.RED)
                .append(scPlayer.getBukkitPlayer().getName())
                .append(ChatColor.GOLD)
                .append(" removed ")
                .append(ChatColor.RED)
                .append(permission.name())
                .append(ChatColor.GOLD)
                .append(" permission to player ")
                .append(newPlayerName)
                .toString());
    }

    private void perm_list(CommandSender sender, String[] args, ZonePermissionType permission)
    {

        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);

        SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        SafeCitySubZone subZone = context.getSubZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld());

        StringBuilder sb = new StringBuilder()
                .append(ChatColor.GREEN)
                .append(permission.name())
                .append(": ");

        String[] users;

        if (subZone == null)
        {
            Set<String> set = context.getMySql().getPermissionHolders(permission, zone.getId());
            users = (String[]) set.toArray(new String[set.size()]);
        } else
        {
            Set<String> set = context.getMySql().getPermissionHolders(permission, subZone.getId());
            users = (String[]) set.toArray(new String[set.size()]);
        }

        if (users.length < 1)
        {
            sb.append(ChatColor.RED).append("None");
        } else
        {
            for (int i = 0; i < users.length; i++)
            {
                sb.append(ChatColor.RED).append(users[i]);

                if (i != users.length - 1)
                {
                    sb.append(ChatColor.WHITE).append(", ");
                }
            }
        }

        scPlayer.getBukkitPlayer().sendMessage(sb.toString());
    }

    // END PERMISSIONS
    // ###############
    private void makePublic(CommandSender sender, Command cmd, String label, String[] args)
    {
        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);
        SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        if (!zone.isFounder(scPlayer.getBukkitPlayer().getName()))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
            return;
        }

        if (zone.isPublic())
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_AlreadyPublic(zone.getName()));
            return;
        }

        if (zone.isForRent())
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Error_Zone_Permission_MisMatch("for rent", "public"));
            return;
        }

        if (zone.isRented())
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Error_Zone_Permission_MisMatch("rented", "public"));
            return;
        }


        if (zone.isForSale())
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Error_Zone_Permission_MisMatch("for sale", "public"));
            return;
        }

        if (zone.getName().equalsIgnoreCase("protected zone"))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_NoName());
            return;
        }

        double cost = 15000;

        if (!context.getEconomy().has(scPlayer.getBukkitPlayer().getName(), cost))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Insufficient_Funds());
            return;
        }

        // withdraw money from buyer
        EconomyResponse ecoresp = context.getEconomy().withdrawPlayer(scPlayer.getBukkitPlayer().getName(), cost);
        if (!ecoresp.transactionSuccess())
        {
            scPlayer.getBukkitPlayer().sendMessage("Critical error withdrawing money.");
            return;
        }

        zone.setPublic(true);

        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_MakePublic(zone.getName()));
        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Item_Purchased((int) cost));

        // Tell entire server
        context.getPlugin().getServer().broadcastMessage(context.getMessageHandler().Zone_Public_Server_Announce(scPlayer.getBukkitPlayer().getName(), zone.getName()));
    }

    private void makePrivate(CommandSender sender, Command cmd, String label, String[] args)
    {
        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);
        SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        if (!zone.isFounder(scPlayer.getBukkitPlayer().getName()))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
            return;
        }

        if (!zone.isPublic())
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_AlreadyPrivate(zone.getName()));
            return;
        }

        zone.setPublic(false);

        if (zone.publicTeleportAllowed())
        {
            zone.setPublicTeleportAllowed(false);
        }

        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_MakePrivate(zone.getName()));

        context.getPlugin().getServer().broadcastMessage(context.getMessageHandler().Zone_Private_Server_Announce(zone));
    }

    private void showList(CommandSender sender, Command cmd, String label, String[] args)
    {
        sender.sendMessage(context.getMessageHandler().Display_Towns());
    }

    private void buyZone(CommandSender sender, Command cmd, String label, String[] args)
    {
        Player player = (Player) sender;
        context.getEstateManager().sellZone(player, player.getLocation());
    }

    private void sellZone(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (args.length != 3)
        {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("Useage: ").append(ChatColor.GOLD).append("/z set forsale ").append(ChatColor.RED).append("<price>").toString());
            return;
        }

        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);
        SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        /*if (!zone.hasPermission(scPlayer.getBukkitPlayer().getName()))
         {
         scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
         return;
         }*/

        boolean canDoThis = false;

        for (ZonePermissionType p : ZonePermissionType.values())
        {
            // if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
            if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
            {
                if (p.getPermissions().canSellZones())
                {
                    canDoThis = true;
                }
            }

            if (!canDoThis)
            {
                if (scPlayer.getBukkitPlayer().hasPermission(PluginPermission.Staff_Override.permissionNode()))
                {
                    scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + ">>** Staff Override");
                    canDoThis = true;
                }
            }
            
            if (canDoThis)
            {
                break;
            }
        }

        if (!canDoThis)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
            return;
        }

        SafeCitySubZone subZone = context.getSubZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld());

        if (subZone == null)
        {
            if (zone.isForRent())
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Error_Zone_Permission_MisMatch("for rent", "for sale"));
                return;
            }
        } else
        {
            if (subZone.isForRent())
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Error_Zone_Permission_MisMatch("for rent", "for sale"));
                return;
            }
        }

        int cost;

        try
        {
            cost = Integer.parseInt(args[2]);
        } catch (Exception ex)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Invalid_Amount(args[2]));
            return;
        }

        if (cost < 1)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Invalid_Amount(args[2]));
            return;
        }

        if (subZone == null)
        {
            zone.setSaleDetails(true, cost, zone.getFounder());

            if (zone.getInfoSignLocation() != null)
            {
                if (zone.getInfoSign() != null)
                {
                    context.getSignManager().setSaleSignal(zone.getInfoSign(), zone.getSalePrice());
                }
            }
        } else
        {
            subZone.setSaleDetails(true, false, cost, "");

            if (subZone.getInfoSignLocation() != null)
            {
                if (subZone.getInfoSign() != null)
                {
                    context.getSignManager().setSaleSignal(subZone.getInfoSign(), subZone.getSalePrice());
                }
            }
        }

        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Sale_Set(cost));
    }

    private void unSellZone(CommandSender sender, Command cmd, String label, String[] args)
    {
        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);
        SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        boolean canDoThis = false;

        for (ZonePermissionType p : ZonePermissionType.values())
        {
            // if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
            if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
            {
                if (p.getPermissions().canSellZones())
                {
                    canDoThis = true;
                }
            }
            
            if (!canDoThis)
            {
                if (scPlayer.getBukkitPlayer().hasPermission(PluginPermission.Staff_Override.permissionNode()))
                {
                    scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + ">>** Staff Override");
                    canDoThis = true;
                }
            }

            if (canDoThis)
            {
                break;
            }
        }

        if (!canDoThis)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
            return;
        }

        SafeCitySubZone subZone = context.getSubZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld());

        if (subZone == null)
        {
            zone.setSaleDetails(false, 0, zone.getFounder());

            if (zone.getInfoSignLocation() != null)
            {
                if (zone.getInfoSign() != null)
                {
                    context.getSignManager().displayOwnerSign(zone.getInfoSign(), zone.getFounder());
                }
            }
        } else
        {
            subZone.setSaleDetails(false, false, 0, "");

            if (subZone.getInfoSignLocation() != null)
            {
                if (subZone.getInfoSign() != null)
                {
                    context.getSignManager().displayOwnerSign(subZone.getInfoSign(), subZone.getFounder());
                }
            }
        }

        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Sale_Unset());
    }

    private void rentZone(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (args.length != 4)
        {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("Useage: ").append(ChatColor.GOLD).append("/z set forrent ").append(ChatColor.RED).append("<price> <days>").toString());
            return;
        }

        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);
        SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        boolean canDoThis = false;

        for (ZonePermissionType p : ZonePermissionType.values())
        {
            // if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
            if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
            {
                if (p.getPermissions().canRentZones())
                {
                    canDoThis = true;
                }
            }

            if (!canDoThis)
            {
                if (scPlayer.getBukkitPlayer().hasPermission(PluginPermission.Staff_Override.permissionNode()))
                {
                    scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + ">>** Staff Override");
                    canDoThis = true;
                }
            }
            
            if (canDoThis)
            {
                break;
            }
        }

        if (!canDoThis)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
            return;
        }

        SafeCitySubZone subZone = context.getSubZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld());

        if (subZone == null)
        {
            if (zone.isForSale())
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Error_Zone_Permission_MisMatch("for sale", "for rent"));
                return;
            }

            if (zone.isPublic())
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Error_Zone_Permission_MisMatch("public", "for rent"));
                return;
            }

        } else
        {
            if (subZone.isForSale())
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Error_Zone_Permission_MisMatch("for sale", "for rent"));
                return;
            }
        }

        int cost;

        try
        {
            cost = Integer.parseInt(args[2]);
        } catch (Exception ex)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Invalid_Amount(args[2]));
            return;
        }

        if (cost < 1)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Invalid_Amount(args[2]));
            return;
        }

        int days;

        try
        {
            days = Integer.parseInt(args[3]);
        } catch (Exception ex)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Invalid_Duration(args[3]));
            return;
        }

        if (days < 1)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Invalid_Duration(args[3]));
            return;
        }

        if (subZone == null)
        {
            zone.setRentDetails(true, false, cost, days, 0L, "");

            if (zone.getInfoSignLocation() != null)
            {
                if (zone.getInfoSign() != null)
                {
                    context.getSignManager().setRentSignal(zone.getInfoSign(), zone.getSalePrice(), zone.getRentalLength());
                }
            }
        } else
        {
            subZone.setRentDetails(true, false, cost, days, 0L, "");

            if (subZone.getInfoSignLocation() != null)
            {
                if (subZone.getInfoSign() != null)
                {
                    context.getSignManager().setRentSignal(subZone.getInfoSign(), subZone.getSalePrice(), subZone.getRentalLength());
                }
            }
        }

        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Rent_Set(cost, days));


    }

    private void unrentZone(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(context.getMessageHandler().Ingame_Only());
            return;
        }

        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);
        SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        boolean canDoThis = false;

        for (ZonePermissionType p : ZonePermissionType.values())
        {
            // if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
            if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
            {
                if (p.getPermissions().canSellZones())
                {
                    canDoThis = true;
                }
            }
            
            if (!canDoThis)
            {
                if (scPlayer.getBukkitPlayer().hasPermission(PluginPermission.Staff_Override.permissionNode()))
                {
                    scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + ">>** Staff Override");
                    canDoThis = true;
                }
            }

            if (canDoThis)
            {
                break;
            }
        }

        if (!canDoThis)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
            return;
        }

        SafeCitySubZone subZone = context.getSubZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld());

        if (subZone == null)
        {
            zone.setRentDetails(false, false, 0, 0, 0L, "");

            if (zone.getInfoSignLocation() != null)
            {
                if (zone.getInfoSign() != null)
                {
                    // context.getSignManager().clearRentSign(zone.getInfoSign());
                    context.getSignManager().displayOwnerSign(zone.getInfoSign(), zone.getFounder());
                }
            }

        } else
        {
            subZone.setRentDetails(false, false, 0, 0, 0L, "");

            if (subZone.getInfoSignLocation() != null)
            {
                if (subZone.getInfoSign() != null)
                {
                    // context.getSignManager().clearRentSign(subZone.getInfoSign());
                    context.getSignManager().displayOwnerSign(subZone.getInfoSign(), subZone.getFounder());
                }
            }
        }

        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Rent_Unset());
    }

    private void beginRentingZone(CommandSender sender, Command cmd, String label, String[] args)
    {
        Player player = (Player) sender;
        context.getEstateManager().rentZone(player, player.getLocation());
    }

    private void kickPlayer(CommandSender sender, Command cmd, String label, String[] args)
    {
        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);

        SafeCityZone zone = context.getZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        // if (!zone.isOwner(scPlayer.getBukkitPlayer().getName()))
        if (!zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
            return;
        }

        Player onPlayer = context.getPlugin().getServer().getPlayer(args[1]);
        OfflinePlayer offPlayer = context.getPlugin().getServer().getOfflinePlayer(args[1]);

        if ((onPlayer == null) && (offPlayer.getName().isEmpty()))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Player_Not_Exist(args[1]));
            return;
        }

        String playerName = (onPlayer == null) ? offPlayer.getName() : onPlayer.getName();

        zone.kickPlayer(playerName);
        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Resident_Kicked(playerName));

        if (onPlayer != null)
        {
            onPlayer.sendMessage(context.getMessageHandler().Resident_Kicked_Player(zone.getName(), zone.getFounder()));
        }
    }

    private void setEntryMessage(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(context.getMessageHandler().Ingame_Only());
            return;
        }

        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);

        if (!context.getBukkitPermissions().has(scPlayer.getBukkitPlayer(), "safecity.namezone"))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
            return;
        }

        SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        SafeCitySubZone subZone = context.getSubZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld());

        if (subZone == null)
        {
            // if (!zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
            if (!zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
                return;
            }
        } else
        {
            if (!subZone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
                return;
            }
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < args.length; i++)
        {
            sb.append(args[i]);

            if (i != (args.length - 1))
            {
                sb.append(" ");
            }
        }

        String newMessage = sb.toString();

        if (newMessage.length() > 20)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Exceeds_SizeLimit(newMessage, 20));
            return;
        }

        if (subZone == null)
        {
            zone.setEntryMessage(newMessage);
        } else
        {
            subZone.setEntryMessage(newMessage);
        }

        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().EntryMessage_Set());

    }

    private void setZoneSpawn(CommandSender sender, Command cmd, String label, String[] args)
    {
        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);

        // SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());
        SafeCityZone zone = context.getZone(context.toThinLocation(scPlayer.getBukkitPlayer().getLocation()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        if (!zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
            return;
        }

        zone.setSpawnPoint(scPlayer.getBukkitPlayer().getLocation());
        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Spawn_Set());
    }

    private void zoneSpawn(CommandSender sender, Command cmd, String label, String[] args)
    {
        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);

        if (args.length < 2)
        {
            return;
        }

        int zoneId = 0;
        String zoneName = "";

        try
        {
            zoneId = Integer.parseInt(args[1]);
        } catch (Exception ex)
        {
        }

        SafeCityZone zone = null;

        if (zoneId > 0)
        {
            for (int i = 0; i < context.getZones().size(); i++)
            {
                SafeCityZone z = context.getZones().get(i);

                if (z.getId() == zoneId)
                {
                    zone = z;
                    break;
                }
            }
        } else
        {
            StringBuilder sb = new StringBuilder();

            for (int i = 1; i < args.length; i++)
            {
                sb.append(args[i]).append(" ");
            }

            zoneName = sb.toString().trim();

            for (int i = 0; i < context.getZones().size(); i++)
            {
                SafeCityZone z = context.getZones().get(i);

                if (z.getName().equalsIgnoreCase(zoneName))
                {
                    zone = z;
                    break;
                }
            }
        }

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage((zoneId > 0)
                    ? context.getMessageHandler().Unknown_Zone(zoneId)
                    : context.getMessageHandler().Unknown_Zone(zoneName));
            return;
        }

        // only allow if public teleport, or player is a part of the town
        if (!zone.isPublic())
        {
            if (!zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Spawn_Not_Public_Town(zone.getName()));
                return;
            }
        } 
        else
        {
            boolean isAllowed = false;

            if (zone.isResident(scPlayer.getBukkitPlayer().getName()))
            {
                isAllowed = true;
            }

            if (zone.isFounder(scPlayer.getBukkitPlayer().getName()))
            {
                isAllowed = true;
            }

            if (zone.publicTeleportAllowed())
            {
                isAllowed = true;
            }

            if (!isAllowed)
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Spawn_Not_Public_Town(zone.getName()));
                return;
            }
        }

        Location spawnPoint = zone.getSpawnPoint();

        if (!zone.getWorld().getBlockAt(spawnPoint).getRelative(BlockFace.UP).isEmpty())
        {
            scPlayer.getBukkitPlayer().teleport(zone.getWorld().getHighestBlockAt(spawnPoint).getLocation());
        } else
        {
            scPlayer.getBukkitPlayer().teleport(zone.getSpawnPoint());
        }

    }

    private void enablePublicSpawn(CommandSender sender, Command cmd, String label, String[] args)
    {
        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);
        SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        // if (!zone.isFounder(scPlayer.getBukkitPlayer().getName()))
        if (!zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
            return;
        }

        if (!zone.isPublic())
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Spawn_Town_Not_Public(zone.getName()));
            return;
        }

        if (zone.publicTeleportAllowed())
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Spawn_Already_Public(zone.getName()));
            return;
        }

        double cost = 7500;

        if (!context.getEconomy().has(scPlayer.getBukkitPlayer().getName(), cost))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Insufficient_Funds());
            return;
        }

        // withdraw money from buyer
        EconomyResponse ecoresp = context.getEconomy().withdrawPlayer(scPlayer.getBukkitPlayer().getName(), cost);
        if (!ecoresp.transactionSuccess())
        {
            scPlayer.getBukkitPlayer().sendMessage("Critical error withdrawing money.");
            return;
        }

        zone.setPublicTeleportAllowed(true);

        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Spawn_Now_Public(zone.getName()));
        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Item_Purchased((int) cost));

    }

    public void disablePublicSpawn(CommandSender sender, Command cmd, String label, String[] args)
    {
        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);
        SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        if (!zone.publicTeleportAllowed())
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Spawn_Already_Private(zone.getName()));
            return;
        }
        
        if (!scPlayer.getBukkitPlayer().hasPermission(PluginPermission.Staff_Override.permissionNode()))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
            return;
        }

        zone.setPublicTeleportAllowed(false);
        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Spawn_Now_Private(zone.getName()));
    }

    private void blocksLeft(CommandSender sender, Command cmd, String label, String[] args)
    {
        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);

        SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        StringBuilder blocksLeft = new StringBuilder()
                .append(ChatColor.GREEN)
                .append("Blocks Remaining: ")
                .append(ChatColor.RED)
                .append(new DecimalFormat("#,###").format(zone.getAvailableBlocks()));

        StringBuilder totalBlocks = new StringBuilder()
                .append(ChatColor.GREEN)
                .append("Current Surface Area: ")
                .append(ChatColor.RED)
                .append(new DecimalFormat("#,###").format(zone.getWidth() * zone.getLength()));

        StringBuilder multiplier = new StringBuilder()
                .append(ChatColor.GREEN)
                .append("Purchase Multiplier: ")
                .append(ChatColor.RED)
                .append(zone.getBlockMultiplier());

        String[] message =
        {
            blocksLeft.toString(),
            totalBlocks.toString(),
            multiplier.toString(),
        };

        scPlayer.getBukkitPlayer().sendMessage(message);
    }

    private void buyBlocks(CommandSender sender, Command cmd, String label, String[] args)
    {
        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);

        SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        int blocks;

        try
        {
            blocks = Integer.parseInt(args[2]);
        } catch (Exception ex)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Invalid_Amount(args[2]));
            return;
        }

        if (blocks < 1)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Invalid_Amount(args[2]));
            return;
        }

        double cost = 0;
        int currentBlocks = zone.getSurfaceArea() + zone.getAvailableBlocks(); //scPlayer.getTotalBlocks();

        // work out price
        for (int i = 0; i < blocks; i++)
        {
            double multiplier = 1.0;

            for (BlockPriceMultiplier b : BlockPriceMultiplier.values())
            {
                if (currentBlocks >= b.getMinimumBlocks())
                {
                    multiplier = b.getMultiplier();
                }
            }

            cost += (1 * multiplier);
            currentBlocks++;
        }

        if (!context.getEconomy().has(scPlayer.getBukkitPlayer().getName(), cost))
        {
            scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + "Insufficient funds to complete the transaction.");
            return;
        }
        
        EconomyResponse ecoresp = context.getEconomy().withdrawPlayer(scPlayer.getBukkitPlayer().getName(), cost);
        if (!ecoresp.transactionSuccess())
        {
            scPlayer.getBukkitPlayer().sendMessage(ChatColor.DARK_RED + "Critical error withdrawing funds.");
            return;
        }

        int newBlocks = zone.getAvailableBlocks() + blocks;
        zone.setAvailableBlocks(newBlocks);

        // int newTotalBlocks = scPlayer.getTotalBlocks() + blocks;
        // scPlayer.setTotalBlocks(newTotalBlocks);


        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Block_Purchase_Success(blocks, cost));
        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Item_Purchased(cost));

    }

    private void quoteBlocks(CommandSender sender, Command cmd, String label, String[] args)
    {
        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);

        SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        int blocks;

        try
        {
            blocks = Integer.parseInt(args[2]);
        } catch (Exception ex)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Invalid_Amount(args[2]));
            return;
        }

        if (blocks < 1)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Invalid_Amount(args[2]));
            return;
        }

        double cost = 0;
        int currentBlocks = zone.getSurfaceArea() + zone.getAvailableBlocks();

        // work out price
        for (int i = 0; i < blocks; i++)
        {
            double multiplier = 1.0;

            for (BlockPriceMultiplier b : BlockPriceMultiplier.values())
            {
                if (currentBlocks >= b.getMinimumBlocks())
                {
                    multiplier = b.getMultiplier();
                }
            }

            cost += (1 * multiplier);
            currentBlocks++;
        }

        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Block_Purchase_Quote(blocks, cost));
        
        if (context.getEconomy().has(scPlayer.getBukkitPlayer().getName(), cost))
        {
            scPlayer.getBukkitPlayer().sendMessage(ChatColor.GREEN + "You have sufficient money to carry out this transaction.");
        }
        else
        {
            double playerBalance = context.getEconomy().getBalance(scPlayer.getBukkitPlayer().getName());
            double requiredMoney = cost - playerBalance;
            
            scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + "You require " + ChatColor.GOLD + context.currencySingular() + requiredMoney + ChatColor.RED + " to carry out this transaction.");
        }

    }

    private void pvpEnable(CommandSender sender, Command cmd, String label, String[] args)
    {
        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);

        SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        boolean isAllowed = false;
        
        if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
        {
            isAllowed = true;
            
        }
        
        if (scPlayer.getBukkitPlayer().hasPermission(PluginPermission.Staff_Override.permissionNode()))
        {
            isAllowed = true;
        }
        
        if (!isAllowed)
        {
            if (!zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
                return;
            }
            
            if (!zone.isPublic())
            {
                scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + "Only public towns are allowed to enable PvP zones.");
                return;
            }
        }

        SafeCitySubZone subZone = context.getSubZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld());

        if (subZone == null)
        {
            if (zone.isPvpEnabled())
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Pvp_Already_Enabled(zone.getName()));
                return;
            }

            zone.setPvpAllowed(true);
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Pvp_Enabled(zone.getName()));
        } 
        else
        {
            if (subZone.isPvpEnabled())
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Pvp_Already_Enabled(subZone.getName()));
                return;
            }

            subZone.setPvpAllowed(true);
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Pvp_Enabled(subZone.getName()));
        }

    }

    private void pvpDisable(CommandSender sender, Command cmd, String label, String[] args)
    {
        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);

        SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        // if (!zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
        if (!zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
            return;
        }

        SafeCitySubZone subZone = context.getSubZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld());

        if (subZone == null)
        {
            if (!zone.isPvpEnabled())
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Pvp_Already_Disabled(zone.getName()));
                return;
            }

            zone.setPvpAllowed(false);
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Pvp_Disabled(zone.getName()));
        } else
        {
            if (!subZone.isPvpEnabled())
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Pvp_Already_Disabled(subZone.getName()));
                return;
            }

            subZone.setPvpAllowed(false);
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Pvp_Disabled(subZone.getName()));
        }

    }

    private void mobEnable(CommandSender sender, Command cmd, String label, String[] args)
    {
        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);

        SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        // if (!zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
        if (!zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
            return;
        }

        SafeCitySubZone subZone = context.getSubZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld());

        if (subZone == null)
        {
            if (zone.allowsMobSpawning())
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Mob_Already_Enabled(zone.getName()));
                return;
            }

            zone.setMobSpawning(true);
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Mob_Enabled(zone.getName()));
        } else
        {
            if (subZone.allowsMobSpawning())
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Mob_Already_Enabled(subZone.getName()));
                return;
            }

            subZone.setMobSpawning(true);
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Mob_Enabled(subZone.getName()));
        }
    }

    private void mobDisable(CommandSender sender, Command cmd, String label, String[] args)
    {
        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);

        SafeCityZone zone = context.getZone(new ThinLocation(scPlayer.getBukkitPlayer().getLocation().getBlockX(), scPlayer.getBukkitPlayer().getLocation().getBlockY(), scPlayer.getBukkitPlayer().getLocation().getBlockZ()), scPlayer.getBukkitPlayer().getWorld());

        if (zone == null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        // if (!zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
        if (!zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
            return;
        }

        SafeCitySubZone subZone = context.getSubZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld());

        if (subZone == null)
        {
            if (!zone.allowsMobSpawning())
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Mob_Already_Disabled(zone.getName()));
                return;
            }

            zone.setMobSpawning(false);
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Mob_Disabled(zone.getName()));
        } else
        {
            if (!subZone.allowsMobSpawning())
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Mob_Already_Disabled(subZone.getName()));
                return;
            }

            subZone.setMobSpawning(false);
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Mob_Disabled(subZone.getName()));
        }
    }

    private void displayHelp(CommandSender sender, Command cmd, String label, String[] args)
    {
        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);

        int totalPages = 1;

        String[] perms = new String[ZonePermissionType.values().length];

        for (int i = 0; i < ZonePermissionType.values().length; i++)
        {
            perms[i] = ChatColor.GOLD + "/z " + ZonePermissionType.values()[i].name().toLowerCase(Locale.getDefault()) + ChatColor.RED + " <add|remove|list> <player> " + ChatColor.WHITE + ChatColor.ITALIC + "Add/Remove " + ZonePermissionType.values()[i].name().toLowerCase() + " trust.";
        }

        scPlayer.getBukkitPlayer().sendMessage(new String[]
        {
            ChatColor.YELLOW + "-----==] " + ChatColor.GREEN + "SafeCity Help" + ChatColor.YELLOW + " [==-----",
            ChatColor.GOLD + "/z list " + ChatColor.WHITE + ChatColor.ITALIC + "List all public zones.",
            ChatColor.GOLD + "/z delete " + ChatColor.WHITE + ChatColor.ITALIC + "Delete a zone or sub-zone permanently.",
            ChatColor.GOLD + "/z setname " + ChatColor.RED + "<name of town> " + ChatColor.WHITE + ChatColor.ITALIC + "Name or rename a town.",
            "",
            ChatColor.GOLD + "/z buy " + ChatColor.WHITE + ChatColor.ITALIC + "Purchase a plot marked 'For Sale'",
            ChatColor.GOLD + "/z rent " + ChatColor.WHITE + ChatColor.ITALIC + "Rent a plot marked 'For Rent'",
            ChatColor.GOLD + "/z kick " + ChatColor.RED + "<player> " + ChatColor.WHITE + ChatColor.ITALIC + "Kick a player from town.",
            "",
            ChatColor.GOLD + "/z set " + ChatColor.RED + "<public|private> " + ChatColor.WHITE + ChatColor.ITALIC + "Make zone a public town or private zone.",
            ChatColor.GOLD + "/z publicspawn " + ChatColor.RED + "<enable|disable> " + ChatColor.WHITE + ChatColor.ITALIC + "Allow everybody to warp to your public town",
            "",
            ChatColor.GOLD + "/z set forsale " + ChatColor.RED + "<price> " + ChatColor.WHITE + ChatColor.ITALIC + "Mark a plot as 'For Sale'.",
            ChatColor.GOLD + "/z set notforsale " + ChatColor.WHITE + ChatColor.ITALIC + "Stop displaying zone as 'For Sale'",
            "",
            ChatColor.GOLD + "/z set forrent " + ChatColor.RED + "<price> <days> " + ChatColor.WHITE + ChatColor.ITALIC + "Mark a plot as 'For Rent'.",
            ChatColor.GOLD + "/z set notforrent " + ChatColor.WHITE + ChatColor.ITALIC + "Stop displaying zone as 'For Rent'",
            "",
            ChatColor.GOLD + "/z setspawn " + ChatColor.WHITE + ChatColor.ITALIC + "Set the spawn point of your public town",
            "",
        });

        scPlayer.getBukkitPlayer().sendMessage(perms);
    }

    private void createNewZone(CommandSender sender, Command cmd, String label, String[] args)
    {
        SafeCityPlayer scPlayer = context.getPlayer((Player) sender);

        // check if zone already exists
        if (context.getZone(context.toThinLocation(scPlayer.getBukkitPlayer().getLocation()), scPlayer.getBukkitPlayer().getWorld()) != null)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Already_Exists());
            return;
        }

        // parse zone name
        StringBuilder zoneName_Sb = new StringBuilder();

        for (int i = 1; i < args.length; i++)
        {
            zoneName_Sb.append(args[i]).append(" ");
        }

        String zoneName = zoneName_Sb.toString().trim();

        if (zoneName.length() < 1)
        {
            return;
        }

        if (zoneName.length() > 32)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Exceeds_SizeLimit(zoneName, 32));
            return;
        }

        Location playerLocation = scPlayer.getBukkitPlayer().getLocation();

        // check if too close
        SafeCityZone nearZone = context.isTownTooClose(scPlayer, playerLocation);
        if (nearZone != null)
        {
            scPlayer.getZoneManager().clearCreationData();
            scPlayer.startCoolDown(2L);

            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Too_Close(nearZone));
            return;
        }

        int radius = 3;  // new zone size

        ThinLocation lesserCorner = context.toThinLocation(playerLocation);
        lesserCorner = new ThinLocation((lesserCorner.getBlockX() - radius), 1, (lesserCorner.getBlockZ() - radius));

        ThinLocation greaterCorner = context.toThinLocation(playerLocation);
        greaterCorner = new ThinLocation((greaterCorner.getBlockX() + radius), (scPlayer.getBukkitPlayer().getWorld().getMaxHeight() - 1), (greaterCorner.getBlockZ() + radius));


        int zoneCount = 0;

        for (SafeCityZone zone : context.getZones())
        {
            // check zone name uniquety
            if (zone.getName().equalsIgnoreCase(zoneName))
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Name_Already_Exists(zoneName));
                return;
            }

            // check zone overlap
            if (context.isOverlap(lesserCorner, greaterCorner, zone.getLesserCorner(), zone.getGreaterCorner()))
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Overlap());
                return;
            }

            // check if max-zones-per-player exceeded
            if (zone.isFounder(scPlayer.getBukkitPlayer().getName()))
            {
                if (!scPlayer.getBukkitPlayer().hasPermission(PluginPermission.Staff_Override.permissionNode()))
                {
                    zoneCount++;
                }
            }
        }

        if (zoneCount >= context.getPluginSettings().getMaxZonesPerPlayer())
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Too_Many_Zones());
            return;
        }


        SafeCityZone newZone = new SafeCityZone(context, scPlayer.getBukkitPlayer(), zoneName, lesserCorner, greaterCorner);

        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Created(newZone.getSurfaceArea()));

        // console output
        context.getConsole().sendMessage(new StringBuilder()
                .append(ChatColor.GOLD)
                .append("Player ")
                .append(ChatColor.RED)
                .append(scPlayer.getBukkitPlayer().getName())
                .append(ChatColor.GOLD)
                .append(" created a new zone. W:")
                .append(ChatColor.RED)
                .append(newZone.getWidth())
                .append(ChatColor.GOLD)
                .append(" L:")
                .append(ChatColor.RED)
                .append(newZone.getLength())
                .toString());

        context.addZone(newZone);
        context.addNewZoneToMap(newZone);

    }

    public void setVineGrowth(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (args.length != 3)
        {
            return;
        }

        boolean toggle = false;

        if (args[2].equalsIgnoreCase("on"))
        {
            toggle = true;
        } else if (args[2].equalsIgnoreCase("off"))
        {
            toggle = false;
        } else
        {
            return;
        }

        Player player = (Player) sender;

        Location loc = player.getLocation();

        SafeCityZone zone = context.getZone(context.toThinLocation(loc), loc.getWorld());

        if (zone == null)
        {
            player.sendMessage(context.getMessageHandler().Zone_Not_Found());
            return;
        }

        SafeCitySubZone subZone = context.getSubZone(context.toThinLocation(loc), loc.getWorld());

        if (subZone == null)
        {
            zone.setVineGrowth(toggle);

            player.sendMessage(context.getMessageHandler().vineGrowth(zone.getName(), toggle));
        } else
        {
            zone.setVineGrowth(toggle);
            player.sendMessage(context.getMessageHandler().vineGrowth(subZone.getName(), toggle));
        }

    }

    private void displayZoneData(CommandSender sender, Command cmd, String label, String[] args)
    {
        context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(), new DisplayZoneData_Thread(context, (Player)sender, (args.length > 1) ? args[1] : ""));
    }
}
