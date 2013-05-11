package me.jayfella.SafeCity.Runnables.MySql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;
import me.jayfella.SafeCity.SafeCityContext;
import org.bukkit.entity.Player;

public final class MySql_NewPlayer implements Runnable
{
    private final SafeCityContext context;
    private final Player player;
	
    public MySql_NewPlayer(SafeCityContext context, Player player)
    {
        this.context = context;
        this.player = player;
    }

    @Override
    public void run()
    {
        Connection connection = null;
		PreparedStatement preparedStatement = null;
        
        try
        {
            StringBuilder sb = new StringBuilder()
				.append("INSERT INTO players(")
				.append("playerName, registered, lastlogin, lastlogout ")
				.append(") ")
				.append("VALUES(?, ?, ?, ?)");

			connection = context.getMySql().getConnection();
			preparedStatement = connection.prepareStatement(sb.toString());
            
            long currentTime = Calendar.getInstance().getTimeInMillis();
            
            preparedStatement.setString(1, player.getName());
            preparedStatement.setLong(2, currentTime);
            preparedStatement.setLong(3, currentTime);
            preparedStatement.setLong(4, 0);
            
            preparedStatement.executeUpdate();
        }
        catch (SQLException ex)
		{
			context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
		}
		finally
		{
			try
			{
				if (preparedStatement != null)
                {
                    preparedStatement.close();
                }

				if (connection != null)
                {
                    connection.close();
                }
			}
			catch (SQLException ex)
			{
				context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
				return;
			}
		}
    }

}
