package me.jayfella.SafeCity.Runnables.MySql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import me.jayfella.SafeCity.SafeCityContext;

public final class MySql_SetValues implements Runnable
{
	private final SafeCityContext context;
    
    private Connection connection;
    private PreparedStatement preparedStatement;

    public MySql_SetValues(SafeCityContext context, Connection connection, PreparedStatement preparedStatement)
	{
		this.context = context;
        this.preparedStatement = preparedStatement;
	}

	@Override
	public void run()
	{
		try
		{
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
