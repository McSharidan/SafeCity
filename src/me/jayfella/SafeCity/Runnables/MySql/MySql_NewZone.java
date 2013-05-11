package me.jayfella.SafeCity.Runnables.MySql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCityZone;

public final class MySql_NewZone implements Runnable
{
	private final SafeCityContext context;
	private final SafeCityZone zone;

	public MySql_NewZone(SafeCityContext context, SafeCityZone zone)
	{
		this.context = context;
		this.zone = zone;
	}

	@Override
	public void run()
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try
		{
			StringBuilder sb = new StringBuilder()
                    .append("INSERT INTO zones")
                    .append("(id, owner, zoneName, isPublic, enterMessage, exitMessage, ")
                    .append("world, lesserCornerX, lesserCornerY, lesserCornerZ, greaterCornerX, greaterCornerY, greaterCornerZ, ")
                    .append("spawnLocationX, spawnLocationY, spawnLocationZ, spawnLocationPitch, spawnLocationYaw, publicTeleportAllowed, ")
                    .append("allowMobSpawning, allowPvp, allowFly, ")
                    .append("isForSale, salePrice, ")
                    .append("isForrent, isRented, rentTimeEnds, rentalPeriod, renter, ")
                    .append("availableBlocks) ")
                    .append("VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

			connection = context.getMySql().getConnection();
			preparedStatement = connection.prepareStatement(sb.toString());

			preparedStatement.setInt(1, zone.getId());
			preparedStatement.setString(2, zone.getFounder());
			preparedStatement.setString(3, zone.getName());
			preparedStatement.setBoolean(4, zone.isPublic());
			preparedStatement.setString(5, zone.getEntryMessage());
			preparedStatement.setString(6, zone.getExitMessage());

			preparedStatement.setString(7, context.getPlugin().getServer().getPlayer(zone.getFounder()).getWorld().getName());
			preparedStatement.setInt(8, zone.getLesserCorner().getBlockX());
			preparedStatement.setInt(9, 1);
			preparedStatement.setInt(10, zone.getLesserCorner().getBlockZ());

			preparedStatement.setInt(11, zone.getGreaterCorner().getBlockX());
			preparedStatement.setFloat(12, context.getPlugin().getServer().getPlayer(zone.getFounder()).getWorld().getMaxHeight() -1);
			preparedStatement.setInt(13, zone.getGreaterCorner().getBlockZ());

			preparedStatement.setInt(14, zone.getSpawnPoint().getBlockX());
			preparedStatement.setInt(15, zone.getSpawnPoint().getBlockY());
			preparedStatement.setInt(16, zone.getSpawnPoint().getBlockZ());
			preparedStatement.setFloat(17, zone.getSpawnPoint().getPitch());
			preparedStatement.setFloat(18, zone.getSpawnPoint().getYaw());
			preparedStatement.setBoolean(19, zone.publicTeleportAllowed());

			preparedStatement.setBoolean(20, zone.allowsMobSpawning());
			preparedStatement.setBoolean(21, zone.isPvpEnabled());
			preparedStatement.setBoolean(22, zone.isFlyAllowed());

			preparedStatement.setBoolean(23, zone.isForSale());
			preparedStatement.setInt(24, zone.getSalePrice());

			preparedStatement.setBoolean(25, zone.isForRent());
			preparedStatement.setBoolean(26, zone.isRented());
			preparedStatement.setLong(27, zone.getRentTimeEnds());
			preparedStatement.setInt(28, zone.getRentalLength());
			preparedStatement.setString(29, zone.getRenter());

			preparedStatement.setInt(30, zone.getAvailableBlocks());

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
