package me.jayfella.SafeCity;

import java.awt.geom.Rectangle2D;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.jayfella.SafeCity.Commands.FamilyCommands;
import me.jayfella.SafeCity.Commands.ResidentCommands;
import me.jayfella.SafeCity.Commands.ZoneCommands;
import me.jayfella.SafeCity.Core.ChunkLocation;
import me.jayfella.SafeCity.Core.EstateManager;
import me.jayfella.SafeCity.Core.MessageHandler;
import me.jayfella.SafeCity.Core.MySql;
import me.jayfella.SafeCity.Core.PluginSettings;
import me.jayfella.SafeCity.Core.ThinLocation;
import me.jayfella.SafeCity.Core.ZonePermissionType;
import me.jayfella.SafeCity.Core.ZoneSignManager;
import me.jayfella.SafeCity.Listeners.AntiGriefListener;
import me.jayfella.SafeCity.Listeners.PermissionListener;
import me.jayfella.SafeCity.Listeners.PlayerPresenceListener;
import me.jayfella.SafeCity.Listeners.SignsListener;
import me.jayfella.SafeCity.Listeners.ToolInHandListener;
import me.jayfella.SafeCity.Listeners.ZoneCreationListener;
import me.jayfella.SafeCity.Listeners.ZoneFlagsListener;
import me.jayfella.SafeCity.Listeners.ZoneNotifyListener;
import me.jayfella.SafeCity.Listeners.ZoneResizeListener;
import me.jayfella.SafeCity.Listeners.ZoneVisualListener;
import me.jayfella.SafeCity.Runnables.RentCycleThread;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class SafeCityContext
{
    private final SafeCityPlugin plugin;
    private final PluginSettings pluginSettings;

    private final MySql mySql;

    private final Map<Player, SafeCityPlayer> safeCityPlayers = new HashMap<>();

    private final List<SafeCityZone> zones = new CopyOnWriteArrayList<>();
    private final List<SafeCitySubZone> subZones = new CopyOnWriteArrayList<>();

    private final Map<ChunkLocation, SafeCityZoneCollection> zoneMap = new HashMap<>();
    private final Map<ChunkLocation, SafeCitySubZoneCollection> subZoneMap = new HashMap<>();

    private final Economy economy;

    private final Permission bukkitPermission;

    private final MessageHandler messageHandler = new MessageHandler(this);
    private final ZoneSignManager signManager = new ZoneSignManager(this);
    private final EstateManager estateManager = new EstateManager(this);

	public SafeCityContext(SafeCityPlugin plugin)
	{
		this.plugin = plugin;

		boolean vaultPresent = initializeVault();

        this.economy = initializeEconomy();
		this.bukkitPermission = initializePermissions();

        this.pluginSettings = new PluginSettings(this);

        this.mySql = initializeDatabase();

        if (!vaultPresent | this.economy == null)
        {
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

		this.mySql.loadZones();
		this.mySql.loadSubZones();

        this.pluginSettings.setZoneIncrement(this.getMySql().getLastLoadedIncrement());

        addAllZonesToHashMap();
        addAllSubZonesToHashMap();

        initializeCommands();
		registerListeners();

		startRentCycleThread();
	}

	private void registerListeners()
	{
		plugin.getServer().getPluginManager().registerEvents(new PlayerPresenceListener(this), plugin);
		plugin.getServer().getPluginManager().registerEvents(new ZoneResizeListener(this), plugin);
		plugin.getServer().getPluginManager().registerEvents(new ZoneCreationListener(this), plugin);
		plugin.getServer().getPluginManager().registerEvents(new ZoneVisualListener(this), plugin);
		plugin.getServer().getPluginManager().registerEvents(new ZoneNotifyListener(this), plugin);
		plugin.getServer().getPluginManager().registerEvents(new ToolInHandListener(this), plugin);

		plugin.getServer().getPluginManager().registerEvents(new PermissionListener(this), plugin);
		plugin.getServer().getPluginManager().registerEvents(new ZoneFlagsListener(this), plugin);
		plugin.getServer().getPluginManager().registerEvents(new AntiGriefListener(this), plugin);
        plugin.getServer().getPluginManager().registerEvents(new SignsListener(this), plugin);
	}

	private void initializeCommands()
	{
		plugin.getCommand("zone").setExecutor(new ZoneCommands(this));
        plugin.getCommand("citizen").setExecutor(new ResidentCommands(this));
        plugin.getCommand("family").setExecutor(new FamilyCommands(this));
    }

	private boolean initializeVault()
	{
		boolean vaultPresent = (plugin.getServer().getPluginManager().getPlugin("Vault") != null);

		if (vaultPresent)
		{
			plugin.getLogger().info("Vault detected and enabled.");

            return true;
		}
		else
		{
			plugin.getLogger().info("Vault NOT PRESENT.");
			plugin.getLogger().warning("Disabling plugin.");

			plugin.getPluginLoader().disablePlugin(plugin);

            return false;
		}
	}

	private Economy initializeEconomy()
	{
		RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);

		if (rsp == null)
		{
			plugin.getLogger().info("Compatible Economy plugin NOT PRESENT.");
			plugin.getLogger().warning("Disabling plugin.");

			plugin.getPluginLoader().disablePlugin(plugin);
			return null;
		}


		Economy econ = (Economy)rsp.getProvider();

		if (econ != null)
		{
			plugin.getLogger().log(Level.INFO, "Using Economy: {0}", econ.getName());
		}
		else
		{
			plugin.getLogger().warning("No compatible economy plugin detected [Vault].");
			plugin.getLogger().warning("Disabling plugin.");
			plugin.getPluginLoader().disablePlugin(plugin);
		}

        return econ;
    }

	private Permission initializePermissions()
	{
		RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
		Permission bPermission = (Permission)rsp.getProvider();

		if (bPermission != null)
		{
			plugin.getLogger().log(Level.INFO, "Using Permissions: {0}", bPermission.getName());
		}
		else
		{
			plugin.getLogger().warning("No compatible permissions plugin detected [Vault].");
			plugin.getLogger().warning("Disabling plugin.");
			plugin.getPluginLoader().disablePlugin(plugin);

            return null;
		}

        return bPermission;
	}

    private void addAllZonesToHashMap()
    {
        this.getConsole().sendMessage("[SafeCity] " + ChatColor.GREEN + "Constructing ZoneMap...");

        for (int i = 0; i < zones.size(); i++)
        {
            SafeCityZone zone = zones.get(i);
            addNewZoneToMap(zone);
        }
    }

    private void addAllSubZonesToHashMap()
    {
        this.getConsole().sendMessage("[SafeCity] " + ChatColor.GREEN + "Constructing SubZoneMap...");

        for (int i = 0; i < subZones.size(); i++)
        {
            SafeCitySubZone subZone = subZones.get(i);
            addNewSubZoneToMap(subZone);
        }
    }

    private void startRentCycleThread()
	{
		this.getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(this.getPlugin(), new RentCycleThread(this), 60L, 200L);
	}

	private MySql initializeDatabase() { return new MySql(this); }

    public MySql getMySql() { return mySql; }
	public ConsoleCommandSender getConsole() { return plugin.getServer().getConsoleSender(); }
	public Logger getLogger() { return plugin.getLogger(); }
    public Permission getBukkitPermissions() { return this.bukkitPermission; }
    public ZoneSignManager getSignManager() { return this.signManager; }
    public EstateManager getEstateManager() { return this.estateManager; }
	public SafeCityPlugin getPlugin() { return plugin; }
	public Economy getEconomy() { return economy; }
	public PluginSettings getPluginSettings() { return pluginSettings; }
    public MessageHandler getMessageHandler() { return messageHandler; }
    public char currencySingular() { return '$'; }

	public List<SafeCityZone> getZones() { return zones; }
	public List<SafeCitySubZone> getSubZones() { return this.subZones; }

    public void addZone(SafeCityZone zone) { this.zones.add(zone);  }
    public void removeZone(SafeCityZone zone)
    {
        // remove the sign
            if (zone.getInfoSignLocation() != null)
            {
                if (zone.getInfoSign() != null)
                {
                    Location signLoc = new Location(zone.getWorld(), zone.getInfoSignLocation().getBlockX(), zone.getInfoSignLocation().getBlockY(), zone.getInfoSignLocation().getBlockZ());

                    zone.getWorld().getBlockAt(signLoc).setType(Material.AIR);
                    zone.getWorld().dropItem(signLoc, new ItemStack(Material.SIGN));
                }
            }

        this.zones.remove(zone);
    }

    public void addSubZone(SafeCitySubZone subZone) { this.subZones.add(subZone); }
    public void removeSubZone(SafeCitySubZone subZone)
    {
        // remove the [details] sign
        if (subZone.getInfoSignLocation() != null)
        {
            if (subZone.getInfoSign() != null)
            {
                Location signLoc = new Location(subZone.getWorld(), subZone.getInfoSignLocation().getBlockX(), subZone.getInfoSignLocation().getBlockY(), subZone.getInfoSignLocation().getBlockZ());

                subZone.getWorld().getBlockAt(signLoc).setType(Material.AIR);
                subZone.getWorld().dropItem(signLoc, new ItemStack(Material.SIGN));
            }
        }

        this.subZones.remove(subZone);
    }

    public SafeCityZone getZone(ThinLocation location, World world)
    {
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;

        ChunkLocation cl = new ChunkLocation(chunkX, chunkZ);

        SafeCityZoneCollection zoneColl = zoneMap.get(cl);

        if (zoneColl == null) { return null; }

        return zoneColl.getExactZone(location, world);
    }

    public SafeCityZoneCollection getZonesInChunk(int chunkX, int chunkZ)
    {
        ChunkLocation chunkLocation = new ChunkLocation(chunkX, chunkZ);
        return zoneMap.get(chunkLocation);
    }

    public SafeCitySubZone getSubZone(ThinLocation location, World world)
    {
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;

        ChunkLocation cl = new ChunkLocation(chunkX, chunkZ);

        SafeCitySubZoneCollection subZoneColl = subZoneMap.get(cl);

        if (subZoneColl == null) { return null; }

        return subZoneColl.getExactSubZone(location, world);
    }

    public SafeCitySubZoneCollection getSubZonesInChunk(int chunkX, int chunkZ)
    {
        ChunkLocation chunkLocation = new ChunkLocation(chunkX, chunkZ);
        return subZoneMap.get(chunkLocation);
    }

	public Map<Player, SafeCityPlayer> getPlayers()	{ return safeCityPlayers; }

    public SafeCityPlayer getPlayer(Player player)
	{
		// get player from map
        SafeCityPlayer scP = safeCityPlayers.get(player);
        if (scP != null) { return scP; }

        // if not exist, try loading the player from the database
        scP = this.getMySql().loadPlayer(player.getName());
        if (scP != null)
        {
            safeCityPlayers.put(player, scP);
            return scP;
        }

        // if not exist, create a new player, and add them to the map
		SafeCityPlayer p = new SafeCityPlayer(this, player);
        safeCityPlayers.put(player, p);
		return p;
	}


    public SafeCityOfflinePlayer getOfflinePlayer(String playerName)
    {
        return this.getMySql().loadOfflinePlayer(playerName);
    }

    public boolean playerExists(String playerName)
    {
        return this.getMySql().playerExists(playerName);
    }

    public String getTimeDiffString(Date earliest, Date latest)
    {
        long timeDiff = latest.getTime() - earliest.getTime();

        int weeks   = (int) (timeDiff / (1000*60*60*24*7));
		int days    = (int) ((timeDiff / (1000*60*60*24)) % 7);
		int hours   = (int) ((timeDiff / (1000*60*60)) % 24);
        int minutes = (int) ((timeDiff / (1000*60)) % 60);
        int seconds = (int) (timeDiff / 1000) % 60 ;

        StringBuilder difference = new StringBuilder();

        if (weeks > 0) { difference.append(ChatColor.RED).append(weeks).append(ChatColor.GOLD).append("weeks "); }
        if (days > 0) { difference.append(ChatColor.RED).append(days).append(ChatColor.GOLD).append("days "); }
        if (hours > 0) { difference.append(ChatColor.RED).append(hours).append(ChatColor.GOLD).append("hrs "); }
        if (minutes > 0) { difference.append(ChatColor.RED).append(minutes).append(ChatColor.GOLD).append("mins "); }


        difference.append(ChatColor.RED).append(seconds).append(ChatColor.GOLD).append("s");

        return difference.toString();
    }

	public void removePlayer(Player player) { safeCityPlayers.remove(player); }

	public ThinLocation[] sortCorners(ThinLocation lesserCorner, ThinLocation greaterCorner)
	{
		int smallestX = Math.min(lesserCorner.getBlockX(), greaterCorner.getBlockX());
		int largestX = Math.max(lesserCorner.getBlockX(), greaterCorner.getBlockX());

		int smallestY = Math.min(lesserCorner.getBlockY(), greaterCorner.getBlockY());
		int largestY = Math.max(lesserCorner.getBlockY(), greaterCorner.getBlockY());

		int smallestZ = Math.min(lesserCorner.getBlockZ(), greaterCorner.getBlockZ());
		int largestZ = Math.max(lesserCorner.getBlockZ(), greaterCorner.getBlockZ());

		ThinLocation bottomLeft = new ThinLocation(smallestX, smallestY, smallestZ);
		ThinLocation topRight = new ThinLocation(largestX, largestY, largestZ);

		return new ThinLocation[] { bottomLeft, topRight };
	}

	public boolean isInsideArea(ThinLocation point, ThinLocation lesserCorner, ThinLocation greaterCorner)
	{
		boolean xIsInside = (point.getBlockX() >= lesserCorner.getBlockX() && point.getBlockX() <= greaterCorner.getBlockX());
		boolean yIsInside = (point.getBlockY() >= lesserCorner.getBlockY() && point.getBlockY() <= greaterCorner.getBlockY());
		boolean zIsInside = (point.getBlockZ() >= lesserCorner.getBlockZ() && point.getBlockZ() <= greaterCorner.getBlockZ());

		return (xIsInside && yIsInside && zIsInside);
	}

    public SafeCityZone isTownTooClose(SafeCityPlayer scPlayer, Location location)
	{
		int minX = location.getBlockX() - getPluginSettings().getMinZoneDistance();
		int maxX = location.getBlockX() + getPluginSettings().getMinZoneDistance();

		int minZ = location.getBlockZ() - getPluginSettings().getMinZoneDistance();
		int maxZ = location.getBlockZ() + getPluginSettings().getMinZoneDistance();

        int y = location.getBlockY();

        World world = location.getWorld();

		for (int x = minX; x <= maxX; x++)
		{
			for (int z = minZ; z <= maxZ; z++)
			{
                ChunkLocation chunkLoc = new ChunkLocation(x >> 4, z >> 4);
                SafeCityZoneCollection zoneCollection = zoneMap.get(chunkLoc);

                if (zoneCollection == null) { continue; }

                ThinLocation loc = new ThinLocation(x, y, z);

                SafeCityZone zone = zoneCollection.getExactZone(loc, world);

                if (zone != null)
                {
                    if (!zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                    {
                        return zone;
                    }
                }
			}
		}

		return null;
	}

    public ThinLocation toThinLocation(Location location)
    {
        return new ThinLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public boolean isOverlap(ThinLocation rect1Lesser, ThinLocation rect1Greater, ThinLocation rect2Lesser, ThinLocation rect2Greater)
	{
		// 2 engulfed 1
        boolean engulfed1X = (rect2Lesser.getBlockX() >= rect1Lesser.getBlockX() && rect2Greater.getBlockX() <= rect1Greater.getBlockX());
        boolean engulfed1Y = (rect2Lesser.getBlockY() >= rect1Lesser.getBlockY() && rect2Greater.getBlockY() <= rect1Greater.getBlockY());
        boolean engulfed1Z = (rect2Lesser.getBlockZ() >= rect1Lesser.getBlockZ() && rect2Greater.getBlockZ() <= rect1Greater.getBlockZ());
        if (engulfed1X && engulfed1Y && engulfed1Z) { return true; }

        // 1 engulfed 2
        boolean engulfed2X = (rect1Lesser.getBlockX() >= rect2Lesser.getBlockX() && rect1Greater.getBlockX() <= rect2Greater.getBlockX());
        boolean engulfed2Y = (rect1Lesser.getBlockY() >= rect2Lesser.getBlockY() && rect1Greater.getBlockY() <= rect2Greater.getBlockY());
        boolean engulfed2Z = (rect1Lesser.getBlockZ() >= rect2Lesser.getBlockZ() && rect1Greater.getBlockZ() <= rect2Greater.getBlockZ());
        if (engulfed2X && engulfed2Y && engulfed2Z) { return true; }

        int rect1Width = Math.abs((rect1Greater.getBlockX() - rect1Lesser.getBlockX()));
        int rect1Length = Math.abs((rect1Greater.getBlockZ() - rect1Lesser.getBlockZ()));
        int rect2Width = Math.abs((rect2Greater.getBlockX() - rect2Lesser.getBlockX()));
        int rect2Length = Math.abs((rect2Greater.getBlockZ() - rect2Lesser.getBlockZ()));

        Rectangle2D rect2D = new Rectangle2D.Double(rect1Lesser.getBlockX(), rect1Lesser.getBlockZ(), rect1Width, rect1Length);

        boolean yTopIntersects = (rect1Lesser.getBlockY() >= rect2Lesser.getBlockY() & rect1Lesser.getBlockY() <= rect2Greater.getBlockY());
        boolean yBottomIntersects = (rect1Greater.getBlockY() >= rect2Lesser.getBlockY() & rect1Greater.getBlockY() <= rect2Greater.getBlockY());

        if (rect2D.intersects(rect2Lesser.getBlockX(), rect2Lesser.getBlockZ(), rect2Width, rect2Length))
        {
            return (yTopIntersects || yBottomIntersects);
        }

        return false;
	}

    public void addNewZoneToMap(SafeCityZone zone)
    {
        int minX = zone.getLesserCorner().getBlockX();
        int minZ = zone.getLesserCorner().getBlockZ();

        int maxX = zone.getGreaterCorner().getBlockX();
        int maxZ = zone.getGreaterCorner().getBlockZ();

        int minChunkX = (minX >> 4);
        int minChunkZ = (minZ >> 4);

        int maxChunkX = (maxX >> 4);
        int maxChunkZ = (maxZ >> 4);

        for (int x = minChunkX; x <= maxChunkX; x++)
        {
            for (int z = minChunkZ; z <= maxChunkZ; z++)
            {

                ChunkLocation chunkLoc = new ChunkLocation(x, z);

                // if one does not exist
                if (!zoneMap.containsKey(chunkLoc))
                {
                    SafeCityZoneCollection newColl = new SafeCityZoneCollection(chunkLoc);
                    newColl.addZone(zone);

                    zoneMap.put(chunkLoc, newColl);
                }
                else
                {
                    if (!zoneMap.get(chunkLoc).containsZone(zone))
                    {
                        zoneMap.get(chunkLoc).addZone(zone);
                    }
                }
            }
        }

    }

    public void addNewSubZoneToMap(SafeCitySubZone subZone)
    {
        int minX = subZone.getLesserCorner().getBlockX();
        int minZ = subZone.getLesserCorner().getBlockZ();

        int maxX = subZone.getGreaterCorner().getBlockX();
        int maxZ = subZone.getGreaterCorner().getBlockZ();

        int minChunkX = (minX >> 4);
        int minChunkZ = (minZ >> 4);

        int maxChunkX = (maxX >> 4);
        int maxChunkZ = (maxZ >> 4);

        for (int x = minChunkX; x <= maxChunkX; x++)
        {
            for (int z = minChunkZ; z <= maxChunkZ; z++)
            {
                ChunkLocation chunkLoc = new ChunkLocation(x, z);

                // if one does not exist
                if (!subZoneMap.containsKey(chunkLoc))
                {
                    SafeCitySubZoneCollection newColl = new SafeCitySubZoneCollection(chunkLoc);
                    newColl.addZone(subZone);

                    subZoneMap.put(chunkLoc, newColl);
                }
                else
                {
                    if (!subZoneMap.get(chunkLoc).containsSubZone(subZone))
                    {
                        subZoneMap.get(chunkLoc).addZone(subZone);
                    }
                }
            }
        }
    }

    public void removeZoneFromMap(SafeCityZone zone)
    {
        int minX = zone.getLesserCorner().getBlockX();
        int minZ = zone.getLesserCorner().getBlockZ();

        int maxX = zone.getGreaterCorner().getBlockX();
        int maxZ = zone.getGreaterCorner().getBlockZ();

        int minChunkX = (minX >> 4);
        int minChunkZ = (minZ >> 4);

        int maxChunkX = (maxX >> 4);
        int maxChunkZ = (maxZ >> 4);

        for (int x = minChunkX; x <= maxChunkX; x++)
        {
            for (int z = minChunkZ; z <= maxChunkZ; z++)
            {
                ChunkLocation chunkLoc = new ChunkLocation(x, z);

                if (zoneMap.get(chunkLoc).containsZone(zone))
                {
                    zoneMap.get(chunkLoc).removeZone(zone);
                }

            }
        }
    }

    public void removeSubZoneFromMap(SafeCitySubZone subZone)
    {
        int minX = subZone.getLesserCorner().getBlockX();
        int minZ = subZone.getLesserCorner().getBlockZ();

        int maxX = subZone.getGreaterCorner().getBlockX();
        int maxZ = subZone.getGreaterCorner().getBlockZ();

        int minChunkX = (minX >> 4);
        int minChunkZ = (minZ >> 4);

        int maxChunkX = (maxX >> 4);
        int maxChunkZ = (maxZ >> 4);

        for (int x = minChunkX; x <= maxChunkX; x++)
        {
            for (int z = minChunkZ; z <= maxChunkZ; z++)
            {
                ChunkLocation chunkLoc = new ChunkLocation(x, z);

                if (subZoneMap.get(chunkLoc).containsSubZone(subZone))
                {
                    subZoneMap.get(chunkLoc).removeSubZone(subZone);
                }

            }
        }
    }

    public boolean isValidWorld(String worldName)
    {
        boolean worldState = false;

        for (String world : this.getPluginSettings().getAllowedWorlds())
        {
            if (worldName.equalsIgnoreCase(world))
            {
                worldState = true;
                break;
            }
        }

        return worldState;
    }

}

