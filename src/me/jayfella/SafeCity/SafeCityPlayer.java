package me.jayfella.SafeCity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.jayfella.SafeCity.Core.FamilyMember;
import me.jayfella.SafeCity.Core.ThinLocation;
import me.jayfella.SafeCity.Core.VisualManager;
import me.jayfella.SafeCity.Core.ZoneManager;
import me.jayfella.SafeCity.Runnables.MySql.MySql_NewPlayer;
import me.jayfella.SafeCity.Runnables.PlayerCoolDownThread;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public final class SafeCityPlayer extends FamilyMember
{
    private final SafeCityContext context;
    private final Player player;

    private ThinLocation currentLocation = null;
    private boolean isCoolingDown = false;

    // extras
    private final VisualManager visualManager;
    private final ZoneManager zoneManager;

    // delete zone confirmation
    private boolean deleteConfirmation;

    private long registeredTime;
    private long lastLoginTime;
    private long lastLogoutTime;

    // new player
    public SafeCityPlayer(SafeCityContext context, Player player)
    {
        this.context = context;
        this.player = player;

        currentLocation = new ThinLocation(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());

        visualManager = new VisualManager(context, this);
        zoneManager = new ZoneManager(this);

        context.getPlugin().getServer().getScheduler().runTaskAsynchronously(context.getPlugin(), new MySql_NewPlayer(context, player));
    }

    // load player
    public SafeCityPlayer(SafeCityContext context, Player player, long regMillis, long loginMillis, long logoutMillis)
    {
        this.context = context;

        this.player = player;

        currentLocation = new ThinLocation(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());

        visualManager = new VisualManager(context, this);
        zoneManager = new ZoneManager(this);

        this.setRegisterTime(regMillis);
        this.setLastLoginTime(loginMillis);
        this.setLogoutTime(logoutMillis);
    }

    public long getRegisterTime() { return this.registeredTime; }
    public void setRegisterTime(long time)
    {
        this.registeredTime = time;

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
                            .append("UPDATE players SET ")
                            .append("registered = ? ")
                            .append("WHERE playerName = ?");

                    connection = context.getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());

                    ps.setLong(1, getRegisterTime());
                    ps.setString(2, getBukkitPlayer().getName());

                    ps.executeUpdate();
                }
                catch (SQLException ex)
                {
                    Logger.getLogger(SafeCityPlayer.class.getName()).log(Level.SEVERE, null, ex);
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

    public long getLastLoginTime() { return this.lastLoginTime; }
    public void setLastLoginTime(long time)
    {
        this.lastLoginTime = time;

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
                            .append("UPDATE players SET ")
                            .append("lastlogin = ? ")
                            .append("WHERE playerName = ?");

                    connection = context.getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());

                    ps.setLong(1, getLastLoginTime());
                    ps.setString(2, getBukkitPlayer().getName());

                    ps.executeUpdate();
                }
                catch (SQLException ex)
                {
                    Logger.getLogger(SafeCityPlayer.class.getName()).log(Level.SEVERE, null, ex);
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

    public long getLastLogoutTime() { return this.lastLogoutTime; }
    public void setLogoutTime(long time)
    {
        this.lastLogoutTime = time;

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
                            .append("UPDATE players SET ")
                            .append("lastlogout = ? ")
                            .append("WHERE playerName = ?");

                    connection = context.getMySql().getConnection();
                    ps = connection.prepareStatement(statement.toString());

                    ps.setLong(1, getLastLogoutTime());
                    ps.setString(2, getBukkitPlayer().getName());

                    ps.executeUpdate();
                }
                catch (SQLException ex)
                {
                    Logger.getLogger(SafeCityPlayer.class.getName()).log(Level.SEVERE, null, ex);
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

    public boolean isOnline() { return (this.player == null); }

    public Player getBukkitPlayer() { return this.player; }
    public ThinLocation getLocation() { return currentLocation; }
    public void setLocation(ThinLocation location) { currentLocation = location; }

    public boolean hasConfirmedDelete() { return this.deleteConfirmation; }
    public void setDeleteConfirmation(boolean value) { this.deleteConfirmation = value; }

    public void startCoolDown(long ticks)
    {
        isCoolingDown = true;
        this.context.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(context.getPlugin(), new PlayerCoolDownThread(this), ticks);
    }

    public boolean getCoolDownState() {	return isCoolingDown; }
    public void stopCoolDown() { isCoolingDown = false; }

    public boolean hasToolInHand(PlayerItemHeldEvent event, Material material)
    {
        ItemStack newItemStack = this.getBukkitPlayer().getInventory().getItem(event.getNewSlot());
	return (newItemStack != null && newItemStack.getType() == material);
    }

    public boolean hasToolInHand(PlayerItemHeldEvent event, Material[] materials)
    {
        ItemStack newItemStack = this.getBukkitPlayer().getInventory().getItem(event.getNewSlot());

        if (newItemStack == null)
        {
            return false;
        }

        Material itemInHandMaterial = newItemStack.getType();
        return Arrays.asList(materials).contains(itemInHandMaterial);
    }

    public boolean hadToolInHand(PlayerItemHeldEvent event, Material material)
    {
        ItemStack oldItemStack = this.getBukkitPlayer().getInventory().getItem(event.getPreviousSlot());
	return (oldItemStack != null && oldItemStack.getType() == material);
    }

    public boolean hadToolInHand(PlayerItemHeldEvent event, Material[] materials)
    {
        ItemStack oldItemStack = this.getBukkitPlayer().getInventory().getItem(event.getPreviousSlot());

        if (oldItemStack == null) { return false; }

        Material itemInHandMaterial = oldItemStack.getType();
        return Arrays.asList(materials).contains(itemInHandMaterial);
    }

    public VisualManager getVisualManager() { return this.visualManager; }
    public ZoneManager getZoneManager() { return this.zoneManager; }
}
