package me.jayfella.SafeCity.Runnables;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import me.jayfella.SafeCity.Core.SettlementHierarchy;
import me.jayfella.SafeCity.Core.ZonePermissionType;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCityOfflinePlayer;
import me.jayfella.SafeCity.SafeCityPlayer;
import me.jayfella.SafeCity.SafeCityZone;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerInfo_Thread implements Runnable
{
    private final SafeCityContext context;
    
    private final Player player;
    private final String queryPlayer;
    
    public PlayerInfo_Thread(SafeCityContext context, Player player, String queryPlayer)
    {
        this.context = context;
        
        this.player = player;
        this.queryPlayer = queryPlayer;
    }

    @Override
    public void run()
    {
        Player qPlayer = context.getPlugin().getServer().getPlayer(queryPlayer);
        
        SafeCityPlayer scPlayer = null;
        SafeCityOfflinePlayer scOfflinePlayer = null;
        
        player.sendMessage("==========");
        
        if (qPlayer != null)
        {
            scPlayer = context.getPlayer(qPlayer);
            player.sendMessage(ChatColor.GOLD + "First Joined: " + ChatColor.GREEN + new Date(scPlayer.getRegisterTime()).toString());
        }
        else
        {
            scOfflinePlayer = context.getOfflinePlayer(queryPlayer);
            
            player.sendMessage(ChatColor.GOLD + "First Joined: " + ChatColor.GREEN + new Date(scOfflinePlayer.getRegisterTime()).toString());
        }
        
        
        
        StringBuilder loginState = new StringBuilder()
                .append(ChatColor.GOLD)
                .append("Player ")
                .append(ChatColor.RED)
                .append(queryPlayer)
                .append(ChatColor.GOLD)
                .append(" has been ");
                
        loginState.append((qPlayer != null) 
                    ? ChatColor.GREEN + "online" 
                    : ChatColor.DARK_RED + "offline")
                .append(ChatColor.GOLD)
                .append(" since ")
                .append((qPlayer != null)
                    ? context.getTimeDiffString(new Date(scPlayer.getLastLoginTime()), Calendar.getInstance().getTime())
                    : context.getTimeDiffString(new Date(scOfflinePlayer.getLastLogoutTime()), new Date()));

        player.sendMessage(loginState.toString());
        
        SafeCityZone playersCityZone = null;
        List<String> playerResidences = new ArrayList<>();
        
        // does this person own a town?
        for (SafeCityZone zone : context.getZones())
        {
            if (zone.isPublic())
            {
                if (zone.isFounder(queryPlayer))
                {
                    playersCityZone = zone;
                }
                
                else if (zone.isResident(queryPlayer))
                {
                    playerResidences.add(zone.getName());
                }
            }
            else
            {
                // if the player is querying themselves,  display private zones too.
                if (player.getName().equalsIgnoreCase(queryPlayer))
                {
                    if (zone.hasPermission(player.getName(), ZonePermissionType.Owner))
                    playerResidences.add("[P]" + zone.getName());
                }
            }
        }
        
        if (playersCityZone != null)
        {
            SettlementHierarchy sh = playersCityZone.getSettlementHierarchy();

            player.sendMessage(ChatColor.GOLD + "Owner of: " + ChatColor.AQUA + "[" + sh.getSettlementName() + "] " +  playersCityZone.getName());
            player.sendMessage(ChatColor.GOLD + "Title: " + ChatColor.GREEN + sh.getOwnerTitle());
            player.sendMessage(ChatColor.GOLD + "Population: " + ChatColor.GREEN + playersCityZone.getPopulation().length);
        }
        else
        {
            player.sendMessage(ChatColor.GOLD + "This player does not own a city.");
        }
        
        if (!playerResidences.isEmpty())
        {
            StringBuilder residences_sb = new StringBuilder()
                    .append(ChatColor.GOLD)
                    .append("Resides in: ");
            
            for (int i = 0; i < playerResidences.size(); i++)
            {
                residences_sb.append(ChatColor.GREEN).append(playerResidences.get(i));
                
                if (i != playerResidences.size() -1)
                {
                    residences_sb.append(ChatColor.WHITE).append(", ");
                }
            }
            
            player.sendMessage(residences_sb.toString());
        }
    }
    
    
    

}
