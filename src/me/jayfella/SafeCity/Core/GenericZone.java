package me.jayfella.SafeCity.Core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCitySubZone;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public abstract class GenericZone
{
    private final SafeCityContext context;

    private final String databaseName;
    private String owner = "";
    private String renter = "";
    private String enterMessage = "";
	private String exitMessage = "";
    private String zoneName;

    private int id;
    private int salePrice = 0;
	private int rentalLength = 0;

    private long rentTimeEnds = 0;

    private World world;

    private ThinLocation lesserCorner;
	private ThinLocation greaterCorner;
    private ThinLocation infoSignLocation = null;

    private final boolean isPrimaryZone;
    private boolean allowMobSpawning = false;
    private boolean allowPvp = false;
	private boolean allowFly = true;
    private boolean isForSale = false;
	private boolean isForRent = false;
	private boolean isRented = false;
    private boolean vineGrowth = false;

    private final StringHashSet perm_access;
	private final StringHashSet perm_build;
	private final StringHashSet perm_farmer;
	private final StringHashSet perm_landlord;
	private final StringHashSet perm_owner;
	private final StringHashSet perm_recruiter;
	private final StringHashSet perm_renter;
	private final StringHashSet perm_zoner;

    public GenericZone(
            SafeCityContext context,

            String owner,
            String renter,
            String enterMessage,
            String exitMessage,
            String zoneName,

            int id,
            int salePrice,
            int rentalLength,

            long rentTimeEnds,

            World world,

            ThinLocation[] cornerLocations,
            ThinLocation infoSignLocation,

            boolean isPrimaryZone,
            boolean allowMobSpawning,
            boolean allowPvp,
            boolean allowFly,
            boolean isForSale,
            boolean isForRent,
            boolean isRented,
            boolean vineGrowth,

            StringHashSet perm_access,
            StringHashSet perm_build,
            StringHashSet perm_farmer,
            StringHashSet perm_landlord,
            StringHashSet perm_owner,
            StringHashSet perm_recruiter,
            StringHashSet perm_renter,
            StringHashSet perm_zoner
            )
    {
        this.context = context;

        this.owner = owner;
        this.renter = renter;
        this.enterMessage = enterMessage;
        this.exitMessage = exitMessage;
        this.zoneName = zoneName;

        this.id = id;
        this.salePrice = salePrice;
        this.rentalLength = rentalLength;

        this.rentTimeEnds = rentTimeEnds;

        this.world = world;

        this.lesserCorner = cornerLocations[0];
        this.greaterCorner = cornerLocations[1];
        this.infoSignLocation = infoSignLocation;

        this.isPrimaryZone = isPrimaryZone;
        this.allowMobSpawning = allowMobSpawning;
        this.allowPvp = allowPvp;
        this.allowFly = allowFly;
        this.isForSale = isForSale;
        this.isForRent = isForRent;
        this.isRented = isRented;
        this.vineGrowth = vineGrowth;

        this.perm_access = perm_access;
        this.perm_build = perm_build;
        this.perm_farmer = perm_farmer;
        this.perm_landlord = perm_landlord;
        this.perm_owner = perm_owner;
        this.perm_recruiter = perm_recruiter;
        this.perm_renter = perm_renter;
        this.perm_zoner = perm_zoner;

        this.databaseName = (isPrimaryZone) ? "zones" : "subzones";
    }

    public SafeCityContext getContext() { return this.context; }

    public boolean isPrimaryZone() { return this.isPrimaryZone; }

    public int getId() { return this.id; }
	public void setId(int value) { this.id = value; }

    public String getFounder() { return owner; }
    public boolean isFounder(String playerName) {	return owner.equals(playerName); }

    public void setOwner(String playerName)
	{
		owner = playerName;

        context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                Connection connection = null;
                PreparedStatement ps = null;

                try
                {
                    StringBuilder statement = new StringBuilder()
                            .append("UPDATE ")
                            .append(databaseName)
                            .append(" SET ")
                            .append("owner = ? ")
                            .append("WHERE id = ?");

                    connection = context.getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());

                    ps.setString(1, getFounder());
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
                        context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                        return;
                    }
                }
            }
        });
	}

    public String getName() { return zoneName; }
    public void setName(String name)
	{
		zoneName = name;

		/*StringBuilder statement = new StringBuilder()
                .append("UPDATE ")
                .append(this.databaseName)
                .append(" SET ")
                .append("zoneName = '").append(this.getName()).append("' ")
        		.append("WHERE id = '").append(this.getId()).append("'");

		context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(), new MySql_SetValues(context, statement.toString()));*/

        context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                Connection connection = null;
                PreparedStatement ps = null;

                try
                {
                    StringBuilder statement = new StringBuilder()
                            .append("UPDATE ")
                            .append(databaseName)
                            .append(" SET ")
                            .append("zoneName = ? ")
                            .append("WHERE id = ?");

                    connection = context.getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());

                    ps.setString(1, getName());
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
                        context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                        return;
                    }
                }
            }
        });

	}

    public World getWorld() { return this.world; }

    public ThinLocation getLesserCorner() { return this.lesserCorner; }
	public ThinLocation getGreaterCorner() { return this.greaterCorner; }
    public ThinLocation[] getCorners() { return new ThinLocation[] { lesserCorner, greaterCorner }; }
    public void defineCorners(ThinLocation corner1, ThinLocation corner2)
    {
        int smallestX = Math.min(corner1.getBlockX(), corner2.getBlockX());
		int largestX = Math.max(corner1.getBlockX(), corner2.getBlockX());

		int smallestZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
		int largestZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

		int smallestY = (isPrimaryZone) ? 1 : Math.min(corner1.getBlockY(), corner2.getBlockY());
		int largestY = (isPrimaryZone) ? this.getWorld().getMaxHeight() - 1 : Math.max(corner1.getBlockY(), corner2.getBlockY());

		lesserCorner = new ThinLocation(smallestX, smallestY, smallestZ);
		greaterCorner = new ThinLocation(largestX, largestY, largestZ);

		/*StringBuilder statement = new StringBuilder()
                .append("UPDATE ")
                .append(this.databaseName)
                .append(" SET ")
                .append("lesserCornerX = '").append(this.getLesserCorner().getBlockX()).append("', ")
                .append("lesserCornerZ = '").append(this.getLesserCorner().getBlockZ()).append("', ")
                .append("greaterCornerX = '").append(this.getGreaterCorner().getBlockX()).append("', ")
                .append("greaterCornerZ = '").append(this.getGreaterCorner().getBlockZ()).append("' ")
                .append("WHERE id = '").append(this.getId()).append("'");

        context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(),	new MySql_SetValues(context, statement.toString()));*/

        context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                Connection connection = null;
                PreparedStatement ps = null;

                try
                {
                    StringBuilder statement = new StringBuilder()
                            .append("UPDATE ")
                            .append(databaseName)
                            .append(" SET ")
                            .append("lesserCornerX = ?, ")
                            .append("lesserCornerZ = ?, ")
                            .append("greaterCornerX = ?, ")
                            .append("greaterCornerZ = ? ")
                            .append("WHERE id = ?");

                    connection = context.getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());

                    ps.setInt(1, getLesserCorner().getBlockX());
                    ps.setInt(2, getLesserCorner().getBlockZ());
                    ps.setInt(3, getGreaterCorner().getBlockX());
                    ps.setInt(4, getGreaterCorner().getBlockZ());
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
                        context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                        return;
                    }
                }
            }
        });

    }

    public int getLength() { return (greaterCorner.getBlockX() - lesserCorner.getBlockX()) + 1;	}
    public int getWidth() {	return (greaterCorner.getBlockZ() - lesserCorner.getBlockZ()) + 1; }
    public int getHeight() { return (greaterCorner.getBlockY() - lesserCorner.getBlockY()) + 1;	}

    public int getSurfaceArea() { return (getWidth() * getLength()); }

    public boolean allowsMobSpawning() { return allowMobSpawning; }
    public void setMobSpawning(boolean value)
	{
		allowMobSpawning = value;

		/*StringBuilder statement = new StringBuilder()
                .append("UPDATE ")
                .append(this.databaseName)
                .append( " SET ")
                .append("allowMobSpawning = '").append(booleanToBinary(this.allowsMobSpawning())).append("' ")
                .append("WHERE id = '").append(this.getId()).append("'");

		context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(),	new MySql_SetValues(context, statement.toString()));*/

        context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                Connection connection = null;
                PreparedStatement ps = null;

                try
                {
                    StringBuilder statement = new StringBuilder()
                            .append("UPDATE ")
                            .append(databaseName)
                            .append(" SET ")
                            .append("allowMobSpawning = ? ")
                            .append("WHERE id = ?");

                    connection = context.getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());

                    ps.setInt(1, booleanToBinary(allowsMobSpawning()));
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
                        context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                        return;
                    }
                }
            }
        });

	}

    public boolean isForSale() { return isForSale; }
	public void setForSale(boolean value) { this.isForSale = value; }

    public int getSalePrice() {	return salePrice; }
    public void setSalePrice(int value) { this.salePrice = value; }

    public boolean isForRent() { return isForRent; }
    public void setForRent(boolean value) { this.isForRent = value; }

    public boolean isRented() { return isRented; }
    public String getRenter() { return renter; }
    public boolean isRenter(String playerName) { return renter.equals(playerName); }

    public int getRentalLength() { return rentalLength; }
    public long getRentTimeEnds() { return rentTimeEnds; }

    public void setRentDetails(boolean isForRent, boolean isRented, int rentalPrice, int rentalPeriod, long rentTimeEnds, String renter)
	{
		this.isForRent = isForRent;
		this.isRented = isRented;
		this.salePrice = rentalPrice;
		this.rentalLength = rentalPeriod;
		this.rentTimeEnds = rentTimeEnds;
		this.renter = renter;

		/*StringBuilder statement;
        statement = new StringBuilder()
                .append("UPDATE ")
                .append(this.databaseName)
                .append(" SET ")
                .append("isForRent = '").append(booleanToBinary(this.isForRent())).append("', ")
                .append("isRented = '").append(booleanToBinary(this.isRented())).append("', ")
                .append("salePrice = '").append(this.getSalePrice()).append("', ")
                .append("rentalPeriod = '").append(this.getRentalLength()).append("', ")
                .append("rentTimeEnds = '").append(this.getRentTimeEnds()).append("', ")
                .append("renter = '").append(this.getRenter()).append("' ")
                .append("WHERE id = '").append(this.getId()).append("'");

		context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(),	new MySql_SetValues(context, statement.toString()));*/

        context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                Connection connection = null;
                PreparedStatement ps = null;

                try
                {
                    StringBuilder statement = new StringBuilder()
                            .append("UPDATE ")
                            .append(databaseName)
                            .append(" SET ")
                            .append("isForRent = ?, ")
                            .append("isRented = ?, ")
                            .append("salePrice = ?, ")
                            .append("rentalPeriod = ?, ")
                            .append("rentTimeEnds = ?, ")
                            .append("renter = ? ")
                            .append("WHERE id = ?");

                    connection = context.getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());

                    ps.setInt(1, booleanToBinary(isForRent()));
                    ps.setInt(2, booleanToBinary(isRented()));
                    ps.setInt(3, getSalePrice());
                    ps.setInt(4, getRentalLength());
                    ps.setLong(5, getRentTimeEnds());
                    ps.setString(6, getRenter());
                    ps.setInt(7, getId());

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
                        context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                        return;
                    }
                }
            }
        });

	}

    public String getEntryMessage() { return this.enterMessage; }
    public void setEntryMessage(String message)
	{
		this.enterMessage = message;

		/*StringBuilder statement = new StringBuilder()
                .append("UPDATE ")
                .append(this.databaseName)
                .append(" SET ")
                .append("enterMessage = '").append(this.getEntryMessage()).append("' ")
                .append("WHERE id = '").append(this.getId()).append("'");

		context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(),	new MySql_SetValues(context, statement.toString()));*/

        context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                Connection connection = null;
                PreparedStatement ps = null;

                try
                {
                    StringBuilder statement = new StringBuilder()
                            .append("UPDATE ")
                            .append(databaseName)
                            .append(" SET ")
                            .append("enterMessage = ? ")
                            .append("WHERE id = ?");

                    connection = context.getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());

                    ps.setString(1, getEntryMessage());
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
                        context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                        return;
                    }
                }
            }
        });
	}

    public String getExitMessage() { return this.exitMessage; }
    public void setExitMessage(String message)
	{
		this.exitMessage = message;

		/*StringBuilder statement = new StringBuilder()
                .append("UPDATE ")
                .append(this.databaseName)
                .append(" SET ")
                .append("exitMessage = '").append(this.getExitMessage()).append("' ")
                .append("WHERE id = '").append(this.getId()).append("'");

		context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(),	new MySql_SetValues(context, statement.toString()));*/

        context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                Connection connection = null;
                PreparedStatement ps = null;

                try
                {
                    StringBuilder statement = new StringBuilder()
                            .append("UPDATE ")
                            .append(databaseName)
                            .append(" SET ")
                            .append("exitMessage = ? ")
                            .append("WHERE id = ?");

                    connection = context.getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());

                    ps.setString(1, getExitMessage());
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
                        context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                        return;
                    }
                }
            }
        });
	}

    public boolean isPvpEnabled() {	return this.allowPvp; }
    public void setPvpAllowed(boolean value)
	{
		this.allowPvp = value;

		/*StringBuilder statement = new StringBuilder()
                .append("UPDATE ")
                .append(this.databaseName)
                .append(" SET ")
                .append("allowPvp = '").append(booleanToBinary(this.isPvpEnabled())).append("' ")
                .append("WHERE id = '").append(this.getId()).append("'");

		context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(),	new MySql_SetValues(context, statement.toString()));*/

        context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                Connection connection = null;
                PreparedStatement ps = null;

                try
                {
                    StringBuilder statement = new StringBuilder()
                            .append("UPDATE ")
                            .append(databaseName)
                            .append(" SET ")
                            .append("allowPvp = ? ")
                            .append("WHERE id = ?");

                    connection = context.getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());

                    ps.setInt(1, booleanToBinary(isPvpEnabled()));
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
                        context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                        return;
                    }
                }
            }
        });
	}

    public boolean isFlyAllowed() {	return this.allowFly; }
    public void setFlyAllowed(boolean value)
	{
		this.allowFly = value;

		/*StringBuilder statement = new StringBuilder()
                .append("UPDATE ")
                .append(this.databaseName)
                .append(" SET ")
                .append("allowFly = '").append(booleanToBinary(this.isFlyAllowed())).append("' ")
                .append("WHERE id = '").append(this.getId()).append("'");

		context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(),	new MySql_SetValues(context, statement.toString()));*/

        context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                Connection connection = null;
                PreparedStatement ps = null;

                try
                {
                    StringBuilder statement = new StringBuilder()
                            .append("UPDATE ")
                            .append(databaseName)
                            .append(" SET ")
                            .append("allowFly = ? ")
                            .append("WHERE id = ?");

                    connection = context.getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());

                    ps.setInt(1, booleanToBinary(isFlyAllowed()));
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
                        context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                        return;
                    }
                }
            }
        });
	}

    public ThinLocation getInfoSignLocation() { return this.infoSignLocation; }
    public void setInfoSignLocation(ThinLocation value)
    {
        if (this.infoSignLocation != null)
        {
            boolean xIsSame = (value.getBlockX() == this.infoSignLocation.getBlockX());
            boolean yIsSame = (value.getBlockY() == this.infoSignLocation.getBlockY());
            boolean zIsSame = (value.getBlockZ() == this.infoSignLocation.getBlockZ());

            if (xIsSame & yIsSame & zIsSame)
            {
                return;
            }
        }

        this.infoSignLocation = value;

        /*StringBuilder statement = new StringBuilder()
                .append("UPDATE ")
                .append(this.databaseName)
                .append(" SET ")
                .append("infoSignLocX = '").append(value.getBlockX()).append("', ")
                .append("infoSignLocY = '").append(value.getBlockY()).append("', ")
                .append("infoSignLocZ = '").append(value.getBlockZ()).append("' ")
                .append("WHERE id = '").append(this.getId()).append("'");

		context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(),	new MySql_SetValues(context, statement.toString()));*/

        context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                Connection connection = null;
                PreparedStatement ps = null;

                try
                {
                    StringBuilder statement = new StringBuilder()
                            .append("UPDATE ")
                            .append(databaseName)
                            .append(" SET ")
                            .append("infoSignLocX = ?, ")
                            .append("infoSignLocY = ?, ")
                            .append("infoSignLocZ = ? ")
                            .append("WHERE id = ?");

                    connection = context.getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());

                    ps.setInt(1, getInfoSignLocation().getBlockX());
                    ps.setInt(2, getInfoSignLocation().getBlockY());
                    ps.setInt(3, getInfoSignLocation().getBlockZ());
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
                        context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                        return;
                    }
                }
            }
        });
    }

    public boolean getVineGrowth() { return this.vineGrowth; }
    public void setVineGrowth(boolean value)
    {
        this.vineGrowth = value;

        /*StringBuilder statement = new StringBuilder()
                .append("UPDATE ")
                .append(this.databaseName)
                .append(" SET ")
                .append("vineGrowth = '").append(booleanToBinary(this.getVineGrowth())).append("' ")
                .append("WHERE id = '").append(this.getId()).append("'");

		context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(),	new MySql_SetValues(context, statement.toString()));*/

        context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                Connection connection = null;
                PreparedStatement ps = null;

                try
                {
                    StringBuilder statement = new StringBuilder()
                            .append("UPDATE ")
                            .append(databaseName)
                            .append(" SET ")
                            .append("vineGrowth = ? ")
                            .append("WHERE id = ?");

                    connection = context.getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());

                    ps.setInt(1, booleanToBinary(getVineGrowth()));
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
                        context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                        return;
                    }
                }
            }
        });
    }

    public org.bukkit.block.Sign getInfoSign()
    {
        ThinLocation signLocation = this.getInfoSignLocation();
        if (signLocation == null) { return null; }

        Block signBlock = this.getWorld().getBlockAt(signLocation.getBlockX(), signLocation.getBlockY(), signLocation.getBlockZ());
        Material material = signBlock.getType();

        if (material == Material.SIGN_POST)
        {
            return (org.bukkit.block.Sign)signBlock.getState();
        }
        else if (material == Material.WALL_SIGN)
        {
            return (org.bukkit.block.Sign)signBlock.getState();
        }
        else
        {
            return null;
        }
    }

    public void addPermission(ZonePermissionType permission, String playerToAdd)
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;

        this.addPermission(playerToAdd, permission);

		try
		{
			connection = context.getMySql().getConnection();

			switch (permission)
			{
				case Access:
				{
					preparedStatement = connection.prepareStatement("INSERT INTO perm_access(playerName, zoneId) VALUES(?, ?)");
					break;
				}
				case Build:
				{
					preparedStatement = connection.prepareStatement("INSERT INTO perm_build(playerName, zoneId) VALUES(?, ?)");
					break;
				}
				case Farmer:
				{
					preparedStatement = connection.prepareStatement("INSERT INTO perm_farmer(playerName, zoneId) VALUES(?, ?)");
					break;
				}
				case Landlord:
				{
					preparedStatement = connection.prepareStatement("INSERT INTO perm_landlord(playerName, zoneId) VALUES(?, ?)");
					break;
				}
				case Owner:
				{
					preparedStatement = connection.prepareStatement("INSERT INTO perm_owner(playerName, zoneId) VALUES(?, ?)");
					break;
				}
				case Recruiter:
				{
					preparedStatement = connection.prepareStatement("INSERT INTO perm_recruiter(playerName, zoneId) VALUES(?, ?)");
					break;
				}
				case Renter:
				{
					preparedStatement = connection.prepareStatement("INSERT INTO perm_renter(playerName, zoneId) VALUES(?, ?)");
					break;
				}
				case Zoner:
				{
					preparedStatement = connection.prepareStatement("INSERT INTO perm_zoner(playerName, zoneId) VALUES(?, ?)");
					break;
				}
			}

			preparedStatement.setString(1, playerToAdd);
			preparedStatement.setInt(2, this.id);

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
                if (preparedStatement != null) { preparedStatement.close(); }
                if (connection != null) { connection.close(); }
			}
			catch (SQLException ex)
			{
				context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
				return;
			}

		}

	}

    private void addPermission(String playerName, ZonePermissionType perm)
    {
        switch (perm)
        {
            case Access:
            {
                perm_access.add(playerName);
                break;
            }
            case Build:
            {
                perm_build.add(playerName);
                break;
            }
            case Farmer:
            {
                perm_farmer.add(playerName);
                break;
            }

            case Landlord:
            {
                perm_landlord.add(playerName);
                break;
            }
            case Owner:
            {
                perm_owner.add(playerName);
                break;
            }
            case Recruiter:
            {
                perm_recruiter.add(playerName);
                break;
            }
            case Renter:
            {
                perm_renter.add(playerName);
                break;
            }
            case Zoner:
            {
                perm_zoner.add(playerName);
                break;
            }
        }
    }

    public void removePermission(ZonePermissionType permission, String playerToAdd)
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;

        this.removePermission(playerToAdd, permission);

		try
		{
			connection = context.getMySql().getConnection();

			switch(permission)
			{
				case Access:
				{
					preparedStatement = connection.prepareStatement("DELETE FROM perm_access WHERE playerName = ? AND zoneId = ?");
					break;
				}
				case Build:
				{
					preparedStatement = connection.prepareStatement("DELETE FROM perm_build WHERE playerName = ? AND zoneId = ?");
        			break;
				}
                case Buyer:
                {
                    // NOTE: primary zones dont have buyers, only owners.
                    // if you buy a primary zone, you become the owner.

                    // subzone buy permissions are handled with setSaleDetails()

                    // buy permissions are instead handled elsewhere

                    // hence, this is ignored.
                }
				case Farmer:
				{
					preparedStatement = connection.prepareStatement("DELETE FROM perm_farmer WHERE playerName = ? AND zoneId = ?");
					break;
				}
				case Landlord:
				{
					preparedStatement = connection.prepareStatement("DELETE FROM perm_landlord WHERE playerName = ? AND zoneId = ?");
					break;
				}
				case Owner:
				{
					preparedStatement = connection.prepareStatement("DELETE FROM perm_owner WHERE playerName = ? AND zoneId = ?");
					break;
				}
				case Recruiter:
				{
					preparedStatement = connection.prepareStatement("DELETE FROM perm_recruiter WHERE playerName = ? AND zoneId = ?");
					break;
				}
				case Renter:
				{
					preparedStatement = connection.prepareStatement("DELETE FROM perm_renter WHERE playerName = ? AND zoneId = ?");
					break;
				}
				case Zoner:
				{
					preparedStatement = connection.prepareStatement("DELETE FROM perm_zoner WHERE playerName = ? AND zoneId = ?");
					break;
				}
			}

			if (preparedStatement != null)
            {
                preparedStatement.setString(1, playerToAdd);
                preparedStatement.setInt(2, this.id);

                preparedStatement.executeUpdate();
            }
            else
            {
                context.getConsole().sendMessage(ChatColor.RED + "Error removing permission " + ChatColor.GOLD + permission.name() + ChatColor.RED + " from player " + ChatColor.GOLD + playerToAdd);
            }
		}
		catch (SQLException ex)
		{
			context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
		}
		finally
		{
			try
			{
                if (preparedStatement != null) { preparedStatement.close(); }
                if (connection != null) { connection.close(); }
			}
			catch (SQLException ex)
			{
				context.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
				return;
			}
		}
	}

    private void removePermission(String playerName, ZonePermissionType perm)
    {
        switch (perm)
        {
            case Access:
            {
                perm_access.remove(playerName);
                break;
            }
            case Build:
            {
                perm_build.remove(playerName);
                break;
            }
            case Farmer:
            {
                perm_farmer.remove(playerName);
                break;
            }

            case Landlord:
            {
                perm_landlord.remove(playerName);
                break;
            }
            case Owner:
            {
                perm_owner.remove(playerName);
                break;
            }
            case Recruiter:
            {
                perm_recruiter.remove(playerName);
                break;
            }
            case Renter:
            {
                perm_renter.remove(playerName);
                break;
            }
            case Zoner:
            {
                perm_zoner.remove(playerName);
                break;
            }
        }
    }

    public boolean hasPermission(String playerName, ZonePermissionType perm)
	{
        // not a great idea, since staff can't be added to any permission.
        /*if (context.getBukkitPermissions().has((World)null, playerName, PluginPermission.Staff_Override.permissionNode()))
        {
            return true;
        }*/

		switch (perm)
		{

            case Access:
			{
				if (this.perm_access.containsIgnoreCase(playerName)) { return true; }
				if (this.perm_access.contains("*")) { return true; }

                if (this instanceof SafeCitySubZone)
                {
                    SafeCitySubZone sz = (SafeCitySubZone)this;
                    if (sz.getBuyer().equalsIgnoreCase(playerName))
                    {
                        return true;
                    }

                }

				break;
			}

			case Build:
			{
				if (this.perm_build.containsIgnoreCase(playerName)) { return true; }
                if (this.perm_build.contains("*")) { return true; }

                if (this instanceof SafeCitySubZone)
                {
                    SafeCitySubZone sz = (SafeCitySubZone)this;
                    if (sz.getBuyer().equalsIgnoreCase(playerName))
                    {
                        return true;
                    }

                }

				break;
			}

            case Buyer:
            {
                if (this instanceof SafeCitySubZone)
                {
                    SafeCitySubZone sz = (SafeCitySubZone)this;
                    if (sz.getBuyer().equalsIgnoreCase(playerName))
                    {
                        return true;
                    }
                }

                break;
            }

			case Farmer:
			{
				if (this.perm_farmer.containsIgnoreCase(playerName)) { return true; }
				if (this.perm_farmer.contains("*")) { return true; }

                if (this instanceof SafeCitySubZone)
                {
                    SafeCitySubZone sz = (SafeCitySubZone)this;
                    if (sz.getBuyer().equalsIgnoreCase(playerName))
                    {
                        return true;
                    }

                }

				break;
			}

			case Landlord:
			{
				if (this.perm_landlord.containsIgnoreCase(playerName)) { return true; }
                if (this.perm_landlord.contains("*")) { return true; }

				break;
			}

			case Owner:
			{
				if (this.owner.equalsIgnoreCase(playerName)) { return true; }
                if (this.perm_owner.containsIgnoreCase(playerName)) { return true; }
				if (this.perm_owner.contains("*")) { return true; }

				break;
			}

			case Recruiter:
			{
				if (this.perm_recruiter.containsIgnoreCase(playerName)) { return true; }
				if (this.perm_recruiter.contains("*")) { return true; }

				break;
			}

			case Renter:
			{
                if (this.renter.equalsIgnoreCase(playerName)) { return true; }
                if (this.perm_renter.containsIgnoreCase(playerName)) { return true; }
				if (this.perm_renter.contains("*")) { return true; }

				break;
			}

			case Zoner:
			{
                if (this.perm_zoner.containsIgnoreCase(playerName)) { return true; }
				if (this.perm_zoner.contains("*")) { return true; }

				break;
			}
		}

		return false;
	}

    public boolean isInsideZone(ThinLocation location, World world, boolean includeVertical)
	{
		if (!this.world.getName().equals(world.getName()))
        {
            return false;
        }

		int smallestX = Math.min(this.lesserCorner.getBlockX(), this.greaterCorner.getBlockX());
		int largestX = Math.max(this.lesserCorner.getBlockX(), this.greaterCorner.getBlockX());

        int smallestY = Math.min(this.lesserCorner.getBlockY(), this.greaterCorner.getBlockY());
		int largestY = Math.max(this.lesserCorner.getBlockY(), this.greaterCorner.getBlockY());

		int smallestZ = Math.min(this.lesserCorner.getBlockZ(), this.greaterCorner.getBlockZ());
		int largestZ = Math.max(this.lesserCorner.getBlockZ(), this.greaterCorner.getBlockZ());

		int locationX = location.getBlockX();
        int locationY = location.getBlockY();
		int locationZ = location.getBlockZ();

		boolean xIsInside = (locationX >= smallestX && locationX <= largestX);
        boolean yIsInside = (locationY >= smallestY && locationY <= largestY);
		boolean zIsInside = (locationZ >= smallestZ && locationZ <= largestZ);

		if (includeVertical)
        {
            return (xIsInside && yIsInside && zIsInside);
        }
        else
        {
            return (xIsInside && zIsInside);
        }
	}

    public boolean isCornerLocation(ThinLocation location, World world, boolean includeVertical)
    {
        if (!this.world.getName().equals(world.getName()))
        {
            return false;
        }

		int smallestX = lesserCorner.getBlockX();
		int largestX = greaterCorner.getBlockX();

		int smallestY = lesserCorner.getBlockY();
		int largestY = greaterCorner.getBlockY();

		int smallestZ = lesserCorner.getBlockZ();
		int largestZ = greaterCorner.getBlockZ();

		boolean isX = (location.getBlockX() == smallestX || location.getBlockX() == largestX);
		boolean isY = (location.getBlockY() == smallestY || location.getBlockY() == largestY);
		boolean isZ = (location.getBlockZ() == smallestZ || location.getBlockZ() == largestZ);

        if (this.lesserCorner.getBlockY() == 1 && this.greaterCorner.getBlockY() == this.world.getMaxHeight() -1)
        {
            return (isX && isZ);
        }
        else
        {
            return (isX && isY && isZ);
        }

    }

    public boolean isEdgeLocation(Location location)
	{
		if (!this.world.getName().equals(location.getWorld().getName()))
        {
            return false;
        }

        if (location.getBlockX() == this.getLesserCorner().getBlockX())
        {
            return true;
        }

		if (location.getBlockX() == this.getGreaterCorner().getBlockX())
        {
            return true;
        }

		if (location.getBlockZ() == this.getLesserCorner().getBlockZ())
        {
            return true;
        }

		if (location.getBlockZ() == this.getGreaterCorner().getBlockZ())
        {
            return true;
        }

		return false;
	}


    public int booleanToBinary(boolean value) { return (value) ? 1 : 0; }

}
