package me.jayfella.SafeCity.Commands;

import me.jayfella.SafeCity.SafeCityContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FamilyCommands implements CommandExecutor
{
    private final SafeCityContext context;

    public FamilyCommands(SafeCityContext context)
    {
        this.context = context;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings)
    {
        return false;
    }

}
