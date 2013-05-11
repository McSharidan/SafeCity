package me.jayfella.SafeCity.Runnables;

import me.jayfella.SafeCity.SafeCityPlayer;
import org.bukkit.ChatColor;

public class AutoResetDeleteConfirmationThread implements Runnable
{
    private final SafeCityPlayer scPlayer;
    
    public AutoResetDeleteConfirmationThread(SafeCityPlayer scPlayer)
    {
        this.scPlayer = scPlayer;
    }

    @Override
    public void run()
    {
        if (scPlayer.hasConfirmedDelete())
        {
            scPlayer.setDeleteConfirmation(false);
            
            StringBuilder cancelledMsg = new StringBuilder()
                    .append(ChatColor.RED)
                    .append("Delete confirmation expired.");
            
            scPlayer.getBukkitPlayer().sendMessage(cancelledMsg.toString());
            
        }
    }

}
