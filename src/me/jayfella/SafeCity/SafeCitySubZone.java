package me.jayfella.SafeCity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.jayfella.SafeCity.Core.GenericZone;
import me.jayfella.SafeCity.Core.StringHashSet;
import me.jayfella.SafeCity.Core.ThinLocation;
import me.jayfella.SafeCity.Core.ZonePermissionType;
import me.jayfella.SafeCity.Runnables.MySql.MySql_NewSubZone;
import org.bukkit.World;

public final class SafeCitySubZone extends GenericZone
{
	private int parentId;
	private boolean isSold;
	private String buyer;
    private SafeCityZone parentZone;

	// actual NEW zone
	public SafeCitySubZone(SafeCityContext context, int parentId, String owner, World world, ThinLocation corner1, ThinLocation corner2)
	{
        super(context,

                owner,
                "",
                "",
                "",
                "Sub-Zone",

                context.getPluginSettings().getNewZoneId(),
                0,
                0,

                0,

                world,

                new ThinLocation[] { corner1, corner2 },
                null,

                false,
                false,
                false,
                true,
                false,
                false,
                false,
                true,

                new StringHashSet(),
                new StringHashSet(),
                new StringHashSet(),
                new StringHashSet(),
                new StringHashSet(),
                new StringHashSet(),
                new StringHashSet(),
                new StringHashSet()

                );

        this.parentId = parentId;

        for (int i = 0; i < context.getZones().size(); i++)
        {
            SafeCityZone zone = context.getZones().get(i);

            if (zone.getId() == this.parentId)
            {
                this.parentZone = zone;
                break;
            }
        }

        this.isSold = false;
        this.buyer = "";

        this.getContext().getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(),	new MySql_NewSubZone(context, this));
	}

	// loading saved zone
	public SafeCitySubZone(
			SafeCityContext context, int id, int parentId, String owner, String zoneName,
			String enterMessage, String exitMessage,
            World world,
			ThinLocation lesserCorner, ThinLocation greaterCorner,
			boolean allowMobSpawning, boolean allowPvp, boolean allowFly,
			boolean isForSale, int salePrice, boolean isSold,
			boolean isForRent, boolean isRented, long rentTimeEnds, int rentalLength,
			String buyer, String renter,
            ThinLocation infoSignLocation,
            boolean vineGrowth
            )
	{
        super(context,

                owner,
                renter,
                enterMessage,
                exitMessage,
                zoneName,

                id,
                salePrice,
                rentalLength,

                rentTimeEnds,

                world,

                new ThinLocation[] { lesserCorner, greaterCorner },
                infoSignLocation,

                false,
                allowMobSpawning,
                allowPvp,
                allowFly,
                isForSale,
                isForRent,
                isRented,
                vineGrowth,

                context.getMySql().getPermissionHolders(ZonePermissionType.Access, id),
                context.getMySql().getPermissionHolders(ZonePermissionType.Build, id),
                context.getMySql().getPermissionHolders(ZonePermissionType.Farmer, id),
                context.getMySql().getPermissionHolders(ZonePermissionType.Landlord, id),
                context.getMySql().getPermissionHolders(ZonePermissionType.Owner, id),
                context.getMySql().getPermissionHolders(ZonePermissionType.Recruiter, id),
                context.getMySql().getPermissionHolders(ZonePermissionType.Renter, id),
                context.getMySql().getPermissionHolders(ZonePermissionType.Zoner, id)

                );

        this.parentId = parentId;

        for (int i = 0; i < context.getZones().size(); i++)
        {
            SafeCityZone zone = context.getZones().get(i);

            if (zone.getId() == this.parentId)
            {
                this.parentZone = zone;
                break;
            }
        }

        this.isSold = isSold;
        this.buyer = buyer;
	}

	public int getParentId() { return parentId; }
    public SafeCityZone getParentZone() { return this.parentZone; }
    public boolean isBuyer(String playerName) { return this.buyer.equals(playerName); }
	public boolean isSold() { return isSold; }
	public String getBuyer() { return buyer; }

	public void setSaleDetails(boolean isForSale, boolean isSold, int salePrice, String buyer)
	{
		this.setForSale(isForSale);
		this.setSalePrice(salePrice);

        this.isSold = isSold;
		this.buyer = buyer;

		/*StringBuilder statement = new StringBuilder()
                .append("UPDATE subzones SET ")
                .append("isForSale = '").append(booleanToBinary(this.isForSale())).append("', ")
                .append("isSold = '").append(booleanToBinary(this.isSold())).append("', ")
                .append("salePrice = '").append(this.getSalePrice()).append("', ")
                .append("buyer = '").append(this.getBuyer()).append("' ")
                .append("WHERE id = '").append(this.getId()).append("'");

		this.getContext().getPlugin().getServer().getScheduler().runTaskAsynchronously(this.getContext().getPlugin(), new MySql_SetValues(this.getContext(), statement.toString()));*/
        
        getContext().getPlugin().getServer().getScheduler().runTaskAsynchronously(getContext().getPlugin(), new Runnable() 
        {
            @Override
            public void run()
            {
                Connection connection = null;
                PreparedStatement ps = null;
                
                try
                {
                    StringBuilder statement = new StringBuilder()
                            .append("UPDATE subzones SET ")
                            .append("isForSale = ?, ")
                            .append("isSold = ?, ")
                            .append("salePrice = ?, ")
                            .append("buyer = ? ")
                            .append("WHERE id = ?");
                    
                    connection = getContext().getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());
                    
                    ps.setInt(1, booleanToBinary(isForSale()));
                    ps.setInt(2, booleanToBinary(isSold()));
                    ps.setInt(3, getSalePrice());
                    ps.setString(4, getBuyer());
                    ps.setInt(5, getId());
                    
                    ps.executeUpdate();
                }
                catch (SQLException ex)
                {
                    Logger.getLogger(GenericZone.class.getName()).log(Level.SEVERE, null, ex);
                }
                finally
                {
                    try
                    {
                        if (ps != null) { ps.close(); }
                        if (connection != null) { connection.close(); }
                    }
                    catch (SQLException ex)
                    {
                        getContext().getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                        return;
                    }
                }
            }
        });
	}

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof SafeCitySubZone)) { return false; }

        SafeCitySubZone otherSafeCitySubZone = (SafeCitySubZone)other;
        return (this.getId() == otherSafeCitySubZone.getId());
    }

    @Override
    public int hashCode()
    {
        return 83 * 5 + this.getId();
    }

}
