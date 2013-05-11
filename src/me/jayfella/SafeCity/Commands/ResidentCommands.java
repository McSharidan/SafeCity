package me.jayfella.SafeCity.Commands;

import me.jayfella.SafeCity.Runnables.PlayerInfo_Thread;
import me.jayfella.SafeCity.SafeCityContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResidentCommands implements CommandExecutor
{
    private final SafeCityContext context;

	public ResidentCommands(SafeCityContext context)
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
        
        // details about self
        if (args.length == 0)
		{
			displayPlayerData((Player)sender, sender.getName());
		}
        
        // details about a specific player
        if (args.length == 1)
        {
            if (!context.playerExists(args[0]))
            {
                ((Player)sender).sendMessage(context.getMessageHandler().Player_Not_Exist(args[0]));
                return true;
            }
            
            displayPlayerData((Player)sender, args[0]);
        }
        
        return false;
    }
    
    private void displayPlayerData(Player player, String queryPlayer)
    {
        context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(), new PlayerInfo_Thread(context, player, queryPlayer));
    }
    
}
