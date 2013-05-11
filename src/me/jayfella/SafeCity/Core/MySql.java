package me.jayfella.SafeCity.Core;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCityOfflinePlayer;
import me.jayfella.SafeCity.SafeCityPlayer;
import me.jayfella.SafeCity.SafeCitySubZone;
import me.jayfella.SafeCity.SafeCityZone;
import me.jayfella.c3p0extension.C3p0ExtensionPlugin;
import me.jayfella.c3p0extension.DatabaseConnection;
import me.jayfella.c3p0extension.DatabaseType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;


public final class MySql implements Closeable
{
	private final SafeCityContext context;
	
    /*private final String url;
	private final String username;
	private final String password;*/
    
    private final C3p0ExtensionPlugin c3p0;
    private final DatabaseConnection databaseConnection;

	private int increment = 0;

	//private final ComboPooledDataSource cpds;

	public MySql(SafeCityContext context)
	{
		this.context = context;
        
        this.c3p0 = (C3p0ExtensionPlugin)context.getPlugin().getServer().getPluginManager().getPlugin("c3p0Extension");
        
        this.databaseConnection = new DatabaseConnection(
                DatabaseType.valueOf(this.context.getPluginSettings().databaseDetails().databaseType()),
                this.context.getPluginSettings().databaseDetails().url(),
                this.context.getPluginSettings().databaseDetails().port(),
                this.context.getPluginSettings().databaseDetails().databaseName(),
                this.context.getPluginSettings().databaseDetails().username(),
                this.context.getPluginSettings().databaseDetails().password());

        if (this.c3p0 == null)
        {
            context.getLogger().log(Level.SEVERE, "Required Dependency c3p0Extension not found! Disabling plugin!");
            
            Bukkit.getPluginManager().disablePlugin(this.context.getPlugin());
            return;
        }
        
        if (!c3p0.createDataSource(databaseConnection))
        {
            context.getLogger().log(Level.SEVERE, "Unable to establish database connection! Disabling plugin!");
            
            Bukkit.getPluginManager().disablePlugin(this.context.getPlugin());
            return;
        }
        
		createTables();
	}

    public Connection getConnection() throws SQLException
    {
        return c3p0.getConnection(databaseConnection);
    }


	private void createTables()
	{
		Connection connection = null;
		Statement statement = null;

		try
		{
            connection = getConnection();
            statement = connection.createStatement();

			// zones table
            statement.executeUpdate(new StringBuilder()
                    .append("CREATE TABLE IF NOT EXISTS zones ( ")

                    .append("id INT PRIMARY KEY AUTO_INCREMENT,")
                    .append("owner VARCHAR(20),")
                    .append("zoneName VARCHAR(32),")
                    .append("isPublic BOOLEAN,")
                    .append("enterMessage VARCHAR(32),")
                    .append("exitMessage VARCHAR(32),")

                    .append("world VARCHAR(64),")
                    .append("lesserCornerX INT,")
                    .append("lesserCornerY INT,")
                    .append("lesserCornerZ INT,")
                    .append("greaterCornerX INT,")
                    .append("greaterCornerY INT,")
                    .append("greaterCornerZ INT,")

                    .append("spawnLocationX INT,")
                    .append("spawnLocationY INT,")
                    .append("spawnLocationZ INT,")
                    .append("spawnLocationPitch FLOAT,")
                    .append("spawnLocationYaw FLOAT,")
                    .append("publicTeleportAllowed BOOLEAN,")

                    .append("allowMobSpawning BOOLEAN,")
                    .append("allowPvp BOOLEAN,")
                    .append("allowFly BOOLEAN,")

                    .append("isForSale BOOLEAN,")
                    .append("salePrice INT,")

                    .append("isForRent BOOLEAN,")
                    .append("isRented BOOLEAN,")
                    .append("rentTimeEnds LONG,")
                    .append("rentalPeriod INT,")
                    .append("renter VARCHAR(20),")

                    .append("availableBlocks INT,")

                    .append("infoSignLocX INT,")
                    .append("infoSignLocY INT,")
                    .append("infoSignLocZ INT,")
                    .append("vineGrowth BOOLEAN")
                    .append(")")

                    .toString());


			// subZones table
            statement.executeUpdate(new StringBuilder()
                    .append("CREATE TABLE IF NOT EXISTS subzones (")

                    .append("id INT PRIMARY KEY AUTO_INCREMENT,")

                    .append("parentId INT,")
                    .append("owner VARCHAR(20),")
                    .append("zoneName VARCHAR(20),")
                    .append("enterMessage VARCHAR(32),")
                    .append("exitMessage VARCHAR(32),")

                    .append("world VARCHAR(20),")
                    .append("lesserCornerX INT,")
                    .append("lesserCornerY INT,")
                    .append("lesserCornerZ INT,")
                    .append("greaterCornerX INT,")
                    .append("greaterCornerY INT,")
                    .append("greaterCornerZ INT,")

                    .append("allowMobSpawning BOOLEAN,")
                    .append("allowPvp BOOLEAN,")
                    .append("allowFly BOOLEAN,")

                    .append("isForSale BOOLEAN,")
                    .append("salePrice INT,")
                    .append("isSold BOOLEAN,")

                    .append("isForRent BOOLEAN,")
                    .append("isRented BOOLEAN,")
                    .append("rentTimeEnds LONG,")
                    .append("rentalPeriod INT,")

                    .append("buyer VARCHAR(20),")
                    .append("renter VARCHAR(20),")

                    .append("infoSignLocX INT,")
                    .append("infoSignLocY INT,")
                    .append("infoSignLocZ INT,")
                    .append("vineGrowth BOOLEAN")

                    .append(")")
                    .toString());


			// players table
			statement.executeUpdate(new StringBuilder()
                    .append("CREATE TABLE IF NOT EXISTS players (")
                    .append("id INT PRIMARY KEY AUTO_INCREMENT,")
                    .append("playerName VARCHAR(20),")
                    
                    .append("registered BIGINT,")
                    .append("lastlogin BIGINT,")
                    .append("lastlogout BIGINT")
                    
                    .append(")")
                    .toString());


			// perm: access
			statement.executeUpdate(new StringBuilder()
                    .append("CREATE TABLE IF NOT EXISTS perm_access (")
                    .append("id INT PRIMARY KEY AUTO_INCREMENT,")
                    .append("playerName VARCHAR(20),")
                    .append("zoneId INT")
                    .append(")")
                    .toString());

			// perm: build
			statement.executeUpdate(new StringBuilder()
                    .append("CREATE TABLE IF NOT EXISTS perm_build (")
                    .append("id INT PRIMARY KEY AUTO_INCREMENT,")
                    .append("playerName VARCHAR(20),")
                    .append("zoneId INT")
                    .append(")")
                    .toString());

			// perm: farmer
			statement.executeUpdate(new StringBuilder()
                    .append("CREATE TABLE IF NOT EXISTS perm_farmer (")
                    .append("id INT PRIMARY KEY AUTO_INCREMENT,")
                    .append("playerName VARCHAR(20),")
                    .append("zoneId INT")
                    .append(")")
                    .toString());

			// perm: landlord
			statement.executeUpdate(new StringBuilder()
                    .append("CREATE TABLE IF NOT EXISTS perm_landlord (")
                    .append("id INT PRIMARY KEY AUTO_INCREMENT,")
                    .append("playerName VARCHAR(20),")
                    .append("zoneId INT")
                    .append(")")
                    .toString());

			// perm: owner
			statement.executeUpdate(new StringBuilder()
                    .append("CREATE TABLE IF NOT EXISTS perm_owner (")
                    .append("id INT PRIMARY KEY AUTO_INCREMENT,")
                    .append("playerName VARCHAR(20),")
                    .append("zoneId INT")
                    .append(")")
                    .toString());

			// perm: landlord
			statement.executeUpdate(new StringBuilder()
                    .append("CREATE TABLE IF NOT EXISTS perm_recruiter (")
                    .append("id INT PRIMARY KEY AUTO_INCREMENT,")
                    .append("playerName VARCHAR(20),")
                    .append("zoneId INT")
                    .append(")")
                    .toString());

			// perm: renter
			statement.executeUpdate(new StringBuilder()
                    .append("CREATE TABLE IF NOT EXISTS perm_renter (")
                    .append("id INT PRIMARY KEY AUTO_INCREMENT,")
                    .append("playerName VARCHAR(20),")
                    .append("zoneId INT")
                    .append(")")
                    .toString());

			// perm: zoner
			statement.executeUpdate(new StringBuilder()
                    .append("CREATE TABLE IF NOT EXISTS perm_zoner (")
                    .append("id INT PRIMARY KEY AUTO_INCREMENT,")
                    .append("playerName VARCHAR(20),")
                    .append("zoneId INT")
                    .append(")")
                    .toString());


		}
		catch (SQLException ex)
		{
			context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
		}
		finally
		{
			try
			{
			if (statement != null)
                {
                    statement.close();
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


    public SafeCityPlayer loadPlayer(String playerName)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try
        {
            connection = context.getMySql().getConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM players where playerName = ?");
            preparedStatement.setString(1, playerName);
            
            resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next())
            {
                long regTime = resultSet.getLong(3);
                long lastLogin = resultSet.getLong(4);
                long lastLogout = resultSet.getLong(5);
                
                Player player = context.getPlugin().getServer().getPlayer(playerName);
                
                return new SafeCityPlayer(context, player, regTime, lastLogin, lastLogout);
            }
            
            return null;
        }
        catch (SQLException ex)
        {
            context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
        finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }

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
                return null;
            }
        }
    }

    public SafeCityOfflinePlayer loadOfflinePlayer(String playerName)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try
        {
            connection = context.getMySql().getConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM players where playerName = ?");
            preparedStatement.setString(1, playerName);
            
            resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next())
            {
                long regTime = resultSet.getLong(3);
                long lastLogin = resultSet.getLong(4);
                long lastLogout = resultSet.getLong(5);
                
                // Player player = context.getPlugin().getServer().getPlayer(playerName);
                // OfflinePlayer offlinePlayer = context.getPlugin().getServer().getOfflinePlayer(playerName);
                
                // return new SafeCityPlayer(context, null, offlinePlayer, regTime, lastLogin, lastLogout);
                return new SafeCityOfflinePlayer(context, playerName, regTime, lastLogin, lastLogout);
            }
            
            return null;
        }
        catch (SQLException ex)
        {
            context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
        finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }

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
                return null;
            }
        }
    }
    
    public boolean playerExists(String playerName)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try
        {
            connection = context.getMySql().getConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM players where playerName = ?");
            preparedStatement.setString(1, playerName);
            
            resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next())
            {
                return true;
            }
            
            return false;
        }
        catch (SQLException ex)
        {
            context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            return false;
        }
        finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }

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
                return false;
            }
        }
    }
    
    public void loadZones()
    {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            int zoneCount = 0;

            try
            {
                    connection = context.getMySql().getConnection();

                    preparedStatement = connection.prepareStatement("SELECT * FROM zones");

                    resultSet = preparedStatement.executeQuery();

                    while (resultSet.next())
                    {
                            World world = context.getPlugin().getServer().getWorld(resultSet.getString(7));

                            ThinLocation lesserCorner = new ThinLocation(resultSet.getInt(8), resultSet.getInt(9), resultSet.getInt(10));
                            ThinLocation greaterCorner = new ThinLocation(resultSet.getInt(11), resultSet.getInt(12), resultSet.getInt(13));

                            Location spawnLocation = new Location(world, resultSet.getInt(14), resultSet.getInt(15), resultSet.getInt(16));
                            spawnLocation.setPitch(resultSet.getFloat(17));
                            spawnLocation.setYaw(resultSet.getFloat(18));

            ThinLocation infoSignLocation = null;
            Integer infoSignX = resultSet.getInt(31);
            Integer infoSignY = resultSet.getInt(32);
            Integer infoSignZ = resultSet.getInt(33);


            if (infoSignX != null && infoSignY != null && infoSignZ != null)
            {
                infoSignLocation = new ThinLocation(infoSignX, infoSignY, infoSignZ);
            }

                            SafeCityZone zone = new SafeCityZone(
                                            context,
                                            resultSet.getInt(1),
                                            resultSet.getString(2),
                                            resultSet.getString(3),
                                            resultSet.getBoolean(4),
                                            resultSet.getString(5),
                                            resultSet.getString(6),

                    world,
                                            lesserCorner,
                                            greaterCorner,

                                            spawnLocation,
                                            resultSet.getBoolean(19),

                                            resultSet.getBoolean(20),
                                            resultSet.getBoolean(21),
                                            resultSet.getBoolean(22),

                                            resultSet.getBoolean(23),
                                            resultSet.getInt(24),

                                            resultSet.getBoolean(25),
                                            resultSet.getBoolean(26),
                                            resultSet.getLong(27),
                                            resultSet.getInt(28),
                                            resultSet.getString(29),

                                            resultSet.getInt(30),

                    infoSignLocation,
                    resultSet.getBoolean(34));

                            // context.getZones().add(zone);
            context.addZone(zone);

                            zoneCount++;

                            if (resultSet.getInt(1) > increment)
            {
                increment = resultSet.getInt(1);
            }
                    }

            }
            catch (SQLException ex)
            {
                    context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                    return;
            }
            finally
            {
                    try
                    {
                            if (resultSet != null)
            {
                resultSet.close();
            }

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

            context.getConsole().sendMessage("[SafeCity] " + ChatColor.GOLD + "Retrieved " + ChatColor.RED +  zoneCount + ChatColor.GOLD + " zones.");
    }

    public void loadSubZones()
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        int subZoneCount = 0;

        try
        {
            connection = context.getMySql().getConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM subzones");

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                World world = context.getPlugin().getServer().getWorld(resultSet.getString(7));

                ThinLocation lesserCorner = new ThinLocation(resultSet.getInt(8), resultSet.getInt(9), resultSet.getInt(10));
                ThinLocation greaterCorner = new ThinLocation(resultSet.getInt(11), resultSet.getInt(12), resultSet.getInt(13));

                ThinLocation infoSignLocation = null;
                Integer infoSignX = resultSet.getInt(26);
                Integer infoSignY = resultSet.getInt(27);
                Integer infoSignZ = resultSet.getInt(28);


                if (infoSignX != null && infoSignY != null && infoSignZ != null)
                {
                    infoSignLocation = new ThinLocation(infoSignX, infoSignY, infoSignZ);
                }

                SafeCitySubZone subZone = new SafeCitySubZone(
                    context,
                    resultSet.getInt(1),
                    resultSet.getInt(2),
                    resultSet.getString(3),
                    resultSet.getString(4),

                    resultSet.getString(5),
                    resultSet.getString(6),

                    world,
                    lesserCorner,
                    greaterCorner,

                    resultSet.getBoolean(14),
                    resultSet.getBoolean(15),
                    resultSet.getBoolean(16),

                    resultSet.getBoolean(17),
                    resultSet.getInt(18),
                    resultSet.getBoolean(19),

                    resultSet.getBoolean(20),
                    resultSet.getBoolean(21),
                    resultSet.getLong(22),
                    resultSet.getInt(23),

                    resultSet.getString(24),
                    resultSet.getString(25),

                    infoSignLocation,
                    resultSet.getBoolean(29)
                                            );

                context.addSubZone(subZone);

                subZoneCount++;

                if (resultSet.getInt(1) > increment)
                {
                    increment = resultSet.getInt(1);
                }
            }


        }
        catch (SQLException ex)
        {
                context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                return;
        }
        finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }

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

            context.getConsole().sendMessage("[SafeCity] " + ChatColor.GOLD + "Retrieved " + ChatColor.RED +  subZoneCount + ChatColor.GOLD + " sub-zones.");
    }

	public int getLastLoadedIncrement()
	{
		return this.increment;
	}



	public void removeZone(int id)
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try
		{
			connection = context.getMySql().getConnection();
			preparedStatement = connection.prepareStatement("DELETE FROM zones WHERE id = ?");

			preparedStatement.setInt(1, id);
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

	public void removeSubZone(int id)
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try
		{
			connection = context.getMySql().getConnection();
			preparedStatement = connection.prepareStatement("DELETE FROM subzones WHERE id = ?");

			preparedStatement.setInt(1, id);
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

	public int removeSubZones(int parentId)
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		int subZoneCount = 0;

		try
		{
			connection = context.getMySql().getConnection();
			preparedStatement = connection.prepareStatement("DELETE FROM subzones WHERE parentId = ?");

			preparedStatement.setInt(1, parentId);
			subZoneCount = preparedStatement.executeUpdate();

		}
		catch (SQLException ex)
		{
			context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
			return 0;
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
				return 0;
			}
		}

		return subZoneCount;
	}

    public final StringHashSet getPermissionHolders(ZonePermissionType permission, int zoneId)
	{
		StringHashSet list = new StringHashSet();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try
		{
			connection = context.getMySql().getConnection();

			switch (permission)
			{
				case Access:
				{
					preparedStatement = connection.prepareStatement("SELECT * FROM perm_access WHERE zoneId = ?");
					break;
				}
				case Build:
				{
					preparedStatement = connection.prepareStatement("SELECT * FROM perm_build WHERE zoneId = ?");
					break;
				}
                case Buyer:
                {
                    preparedStatement = connection.prepareStatement("SELECT buyer WHERE zone_Id = ?");
                    break;
                }
				case Farmer:
				{
					preparedStatement = connection.prepareStatement("SELECT * FROM perm_farmer WHERE zoneId = ?");
					break;
				}
				case Landlord:
				{
					preparedStatement = connection.prepareStatement("SELECT * FROM perm_landlord WHERE zoneId = ?");
					break;
				}
				case Owner:
				{
					preparedStatement = connection.prepareStatement("SELECT * FROM perm_owner WHERE zoneId = ?");
					break;
				}
				case Recruiter:
				{
					preparedStatement = connection.prepareStatement("SELECT * FROM perm_recruiter WHERE zoneId = ?");
					break;
				}
				case Renter:
				{
					preparedStatement = connection.prepareStatement("SELECT * FROM perm_renter WHERE zoneId = ?");
					break;
				}
				case Zoner:
				{
					preparedStatement = connection.prepareStatement("SELECT * FROM perm_zoner WHERE zoneId = ?");
					break;
				}
			}

            preparedStatement.setInt(1, zoneId);

			resultSet = preparedStatement.executeQuery();

			while (resultSet.next())
			{
				if (permission == ZonePermissionType.Buyer)
                {
                list.add(resultSet.getString(1));
                }
                else
                {
                    list.add(resultSet.getString(2));
                }
			}

		}
		catch (SQLException ex)
		{
			context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
			return new StringHashSet();
		}
		finally
		{
			try
			{
				if (resultSet != null) { resultSet.close(); }
				if (preparedStatement != null) { preparedStatement.close(); }
				if (connection != null) { connection.close(); }
			}
			catch (SQLException ex)
			{
				context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
				return new StringHashSet();
			}
		}

		return list;
	}

    @Override
    public void close()
    {
       
    }

}
