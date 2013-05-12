package me.jayfella.SafeCity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.jayfella.SafeCity.Core.BlockPriceMultiplier;
import me.jayfella.SafeCity.Core.GenericZone;
import me.jayfella.SafeCity.Core.SettlementHierarchy;
import me.jayfella.SafeCity.Core.StringHashSet;
import me.jayfella.SafeCity.Core.ThinLocation;
import me.jayfella.SafeCity.Core.ZonePermissionType;
import me.jayfella.SafeCity.Runnables.MySql.MySql_NewZone;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class SafeCityZone extends GenericZone
{
    private int availableBlocks;
    private boolean isPublic;
    private boolean publicTeleportEnabled;
    private Location spawnLocation;

	// actual NEW zone
	public SafeCityZone(SafeCityContext context, Player owner, String zoneName, ThinLocation corner1, ThinLocation corner2)
	{
        super(context,

                owner.getName(),
                "",
                "",
                "",
                zoneName,

                context.getPluginSettings().getNewZoneId(),
                0,
                0,

                0,

                owner.getWorld(),

                context.sortCorners(corner1, corner2),
                null,

                true,
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

        this.availableBlocks = 0;
        this.isPublic = false;
        this.publicTeleportEnabled = false;
        this.spawnLocation = new Location(this.getWorld(), this.getLesserCorner().getBlockX(), this.getLesserCorner().getBlockY(), this.getLesserCorner().getBlockZ());

        this.getContext().getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(),	new MySql_NewZone(context, this));
	}

	// loading saved zone
	public SafeCityZone(
			SafeCityContext context,
			int id, String owner, String zoneName, boolean isPublic, String enterMessage, String exitMessage,
			World world,
            ThinLocation lesserCorner, ThinLocation greaterCorner,
			Location spawnLocation,
            boolean publicTeleportEnabled,
			boolean allowMobSpawning, boolean allowPvp, boolean allowFly,
			boolean isForSale, int salePrice,
			boolean isForRent, boolean isRented, long rentTimeEnds, int rentalPeriod, String renter,
			int totalBlocks,
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
                rentalPeriod,

                rentTimeEnds,

                world,

                new ThinLocation[] { lesserCorner, greaterCorner },
                infoSignLocation,

                true,
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

        this.availableBlocks = totalBlocks;
        this.isPublic = isPublic;
        this.publicTeleportEnabled = publicTeleportEnabled;
        this.spawnLocation = spawnLocation;

	}


	public void setSaleDetails(boolean isForSale, int salePrice, String owner)
	{
		this.setForSale(isForSale);
		this.setSalePrice(salePrice);
		this.setOwner(owner);

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
                            .append("UPDATE zones SET ")
                            .append("isForSale = ?, ")
                            .append("salePrice = ?, ")
                            .append("owner = ? ")
                            .append("WHERE id = ?");
                    
                    connection = getContext().getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());
                    
                    ps.setInt(1, booleanToBinary(isForSale()));
                    ps.setInt(2, getSalePrice());
                    ps.setString(3, getFounder());
                    ps.setInt(4, getId());
                    
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

	public boolean isPublic() {	return isPublic; }
	public void setPublic(boolean value)
	{
		isPublic = value;

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
                            .append("UPDATE zones SET ")
                            .append("isPublic = ? ")
                            .append("WHERE id = ?");
                    
                    connection = getContext().getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());
                    
                    ps.setInt(1, booleanToBinary(isPublic()));
                    ps.setInt(2, getId());
                    
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

    public SettlementHierarchy getSettlementHierarchy()
    {
        int population = this.getPopulation().length;

        // get the first one (for cities with zero population
        SettlementHierarchy currentType = SettlementHierarchy.values()[0];
        
        for (SettlementHierarchy type : SettlementHierarchy.values())
        {
            if (type.getMinPopulation() < population) 
            { 
                currentType = type;
            }
            else
            {
                return currentType;
            }
        }

        return currentType;
    }

	public boolean isResident(String playerName)
	{
        for (int i = 0; i < this.getPopulation().length; i++)
        {
            if (this.getPopulation()[i].equalsIgnoreCase(playerName))
            {
                return true;
            }
        }

        return false;
	}

	public SafeCitySubZone[] getChildren()
	{
		ArrayList<SafeCitySubZone> zoneChildren = new ArrayList<>();

		for (int i = 0; i < this.getContext().getSubZones().size(); i++)
		{
			SafeCitySubZone child = this.getContext().getSubZones().get(i);

			if (child.getParentId() == this.getId())
            {
                zoneChildren.add(child);
            }
		}

		return zoneChildren.toArray(new SafeCitySubZone[zoneChildren.size()]);
	}

	public void kickPlayer(String playerName)
	{
        for (ZonePermissionType p : ZonePermissionType.values())
		{
            if (this.hasPermission(playerName, p))
			{
				this.removePermission(p, playerName);
			}
		}

		if (this.isRenter(playerName))
		{
			this.setRentDetails(false, false, this.getSalePrice(), this.getRentalLength(), 0L, "");
		}

		// remove all child permissions...
		SafeCitySubZone[] children = getChildren();

		for (int i = 0; i < children.length; i++)
		{
			SafeCitySubZone child = children[i];

			for (ZonePermissionType p : ZonePermissionType.values())
			{
				if (child.hasPermission(playerName, p))
				{
					child.removePermission(p, playerName);
				}
			}

			if (child.isBuyer(playerName))
			{
				child.setSaleDetails(false, false, child.getSalePrice(), "");
			}

			if (child.isRenter(playerName))
			{
				child.setRentDetails(true, false, child.getSalePrice(), child.getRentalLength(), 0L, "");
			}

		}
	}

	public String[] getPopulation()
	{
		List<String> population = new ArrayList<>();

		for (int i = 0; i < this.getContext().getSubZones().size(); i++)
		{
			SafeCitySubZone subZone = this.getContext().getSubZones().get(i);

			if (subZone.getParentId() != this.getId())
            {
                continue;
            }

			if (subZone.isSold())
            {
				if (!population.contains(subZone.getBuyer()))
				{
					population.add(subZone.getBuyer());
					continue;
				}
            }

			if (subZone.isRented())
            {
				if (!population.contains(subZone.getRenter()))
				{
					population.add(subZone.getRenter());
					continue;
				}
            }
		}

		return population.toArray(new String[population.size()]);
	}

	public void setSpawnPoint(Location location)
	{
		this.spawnLocation = location;
        
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
                            .append("UPDATE zones SET ")
                            .append("spawnLocationX = ?, ")
                            .append("spawnLocationY = ?, ")
                            .append("spawnLocationZ = ?, ")
                            .append("spawnLocationPitch = ?, ")
                            .append("spawnLocationYaw = ? ")
                            .append("WHERE id = ?");
                    
                    connection = getContext().getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());
                    
                    ps.setInt(1, getSpawnPoint().getBlockX());
                    ps.setInt(2, getSpawnPoint().getBlockY());
                    ps.setInt(3, getSpawnPoint().getBlockZ());
                    ps.setFloat(4, getSpawnPoint().getPitch());
                    ps.setFloat(5, getSpawnPoint().getYaw());
                    ps.setInt(6, getId());
                    
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

	public Location getSpawnPoint()
	{
		return this.spawnLocation;
	}

	public boolean publicTeleportAllowed()
	{
		return publicTeleportEnabled;
	}

	public void setPublicTeleportAllowed(boolean value)
	{
		publicTeleportEnabled = value;
        
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
                            .append("UPDATE zones SET ")
                            .append("publicTeleportAllowed = ? ")
                            .append("WHERE id = ?");
                    
                    connection = getContext().getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());
                    
                    ps.setInt(1, booleanToBinary(publicTeleportAllowed()));
                    ps.setInt(2, getId());
                    
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

	public int getAvailableBlocks()
	{
		return this.availableBlocks;
	}

	public void setAvailableBlocks(int blockCount)
	{
		this.availableBlocks = blockCount;
        
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
                            .append("UPDATE zones SET ")
                            .append("availableBlocks = ? ")
                            .append("WHERE id = ?");
                    
                    connection = getContext().getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());
                    
                    ps.setInt(1, getAvailableBlocks());
                    ps.setInt(2, getId());
                    
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

	public BlockPriceMultiplier getBlockMultiplier()
	{
		BlockPriceMultiplier multi = null;
		int totalBlocks = (this.getWidth() * this.getLength());

		totalBlocks += this.getAvailableBlocks();

		for (BlockPriceMultiplier b : BlockPriceMultiplier.values())
		{
			if (totalBlocks >= b.getMinimumBlocks())
            {
                multi = b;
            }
		}

		return multi;
	}

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof SafeCityZone)) { return false; }

        SafeCityZone otherSafeCityZone = (SafeCityZone)other;
        return (this.getId() == otherSafeCityZone.getId());
    }

    @Override
    public int hashCode()
    {
        return 17 * 7 + this.getId();
    }

}
