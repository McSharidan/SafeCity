package me.jayfella.SafeCity.Runnables.MySql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCitySubZone;

public final class MySql_NewSubZone implements Runnable
{
	private final SafeCityContext context;
	private final SafeCitySubZone subZone;

	public MySql_NewSubZone(SafeCityContext context, SafeCitySubZone zone)
	{
		this.context = context;
		this.subZone = zone;
	}

	@Override
	public void run()
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try
		{
			StringBuilder sb = new StringBuilder()
                    .append("INSERT INTO subzones")
                    .append("(id, parentId, owner, zoneName, enterMessage, exitMessage, ")
                    .append("world, lesserCornerX, lesserCornerY, lesserCornerZ, greaterCornerX, greaterCornerY, greaterCornerZ, ")
                    .append("allowMobSpawning, allowPvp, allowFly, ")
                    .append("isForSale, salePrice, isSold, ")
                    .append("isForRent, isRented, rentTimeEnds, rentalPeriod, ")
                    .append("buyer, renter) ")
                    .append("VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

			connection = context.getMySql().getConnection();
			preparedStatement = connection.prepareStatement(sb.toString());

			preparedStatement.setInt(1, subZone.getId());
			preparedStatement.setInt(2, subZone.getParentId());
			preparedStatement.setString(3, subZone.getFounder());
			preparedStatement.setString(4, subZone.getName());
			preparedStatement.setString(5, subZone.getEntryMessage());
			preparedStatement.setString(6, subZone.getExitMessage());

            preparedStatement.setString(7, subZone.getParentZone().getWorld().getName());

			preparedStatement.setInt(8, subZone.getLesserCorner().getBlockX());
			preparedStatement.setInt(9, subZone.getLesserCorner().getBlockY());
			preparedStatement.setInt(10, subZone.getLesserCorner().getBlockZ());

			preparedStatement.setInt(11, subZone.getGreaterCorner().getBlockX());
			preparedStatement.setInt(12, subZone.getGreaterCorner().getBlockY());
			preparedStatement.setInt(13, subZone.getGreaterCorner().getBlockZ());

			preparedStatement.setBoolean(14, subZone.allowsMobSpawning());
			preparedStatement.setBoolean(15, subZone.isPvpEnabled());
			preparedStatement.setBoolean(16, subZone.isFlyAllowed());

			preparedStatement.setBoolean(17, subZone.isForSale());
			preparedStatement.setInt(18, subZone.getSalePrice());
			preparedStatement.setBoolean(19, subZone.isSold());

			preparedStatement.setBoolean(20, subZone.isForRent());
			preparedStatement.setBoolean(21, subZone.isRented());
			preparedStatement.setLong(22, subZone.getRentTimeEnds());
			preparedStatement.setInt(23, subZone.getRentalLength());

			preparedStatement.setString(24, subZone.getBuyer());
			preparedStatement.setString(25, subZone.getRenter());

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
