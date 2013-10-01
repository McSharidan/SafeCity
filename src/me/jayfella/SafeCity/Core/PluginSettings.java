package me.jayfella.SafeCity.Core;

import java.util.List;
import me.jayfella.SafeCity.SafeCityContext;
import org.bukkit.configuration.file.FileConfiguration;


public final class PluginSettings
{
	private final SafeCityContext context;

    private volatile int zoneIdIncrement;

    // zone-related
    private final int minZoneDistance;
    private final int maxPlayerZones;
    private final boolean pvpInWilderness;
    private final String[] allowedWorlds;

    // --INDEV
    private final boolean allowCreeperExplosions;
    private final boolean allowTntExplosions;
    // private final boolean FounderLightPortalsOnly;

    private final double publicZoneCost;
    private final double publicSpawnCost;
    private final double pvpZoneCost;

    private final DatabaseDetails databaseDetails;

    public PluginSettings(SafeCityContext context)
    {
        this.context = context;

        context.getPlugin().saveDefaultConfig();

        FileConfiguration fileConfig = this.context.getPlugin().getConfig();

        this.databaseDetails = new DatabaseDetails(
                fileConfig.getString("database.type"),
                fileConfig.getString("database.url"),
                fileConfig.getInt("database.port"),
                fileConfig.getString("database.databasename"),
                fileConfig.getString("database.username"),
                fileConfig.getString("database.password"));

        this.minZoneDistance = fileConfig.getInt("general.min-zone-distance", 50);
        this.maxPlayerZones = fileConfig.getInt("general.max-zones-per-player", 1);
        this.pvpInWilderness = fileConfig.getBoolean("general.wilderness-pvp", false);

        List<?> worlds = fileConfig.getList("allowed-worlds");
        this.allowedWorlds = worlds.toArray(new String[worlds.size()]);

        this.publicZoneCost = fileConfig.getDouble("prices.public-zone", 15000);
        this.publicSpawnCost = fileConfig.getDouble("prices.public-spawn", 7500);
        this.pvpZoneCost = fileConfig.getDouble("prices.pvp-zone", 1000);
        
        this.allowCreeperExplosions = fileConfig.getBoolean("general.creeperexplosion", false);
        this.allowTntExplosions = fileConfig.getBoolean("general.tntexplosion", false);
    }

    public int getNewZoneId() { zoneIdIncrement++; return zoneIdIncrement; }
    public void setZoneIncrement(int value) { this.zoneIdIncrement = value; }
    public int getMinZoneDistance() { return minZoneDistance; }
    public int getMaxZonesPerPlayer() { return this.maxPlayerZones; }
    public boolean pvpInWilderness() { return this.pvpInWilderness; }

    public String[] getAllowedWorlds() { return this.allowedWorlds; }

    public double getPublicZoneCost() { return this.publicZoneCost; }
    public double getPublicSpawnCost() { return this.publicSpawnCost; }
    public double getPvpZoneCost() { return this.pvpZoneCost; }
    
    public boolean creeperExplosionAllowed() { return this.allowCreeperExplosions; }
    public boolean tntExplosionAllowed() { return this.allowTntExplosions; }

    public DatabaseDetails databaseDetails() { return this.databaseDetails; }

    public final class DatabaseDetails
    {
        private final String databaseType;
        private final String url;
        private final int port;
        private final String databaseName;
        private final String username;
        private final String password;

        public DatabaseDetails(String databaseType, String url, int port, String databaseName, String username, String password)
        {
            this.databaseType = databaseType;
            this.url = url;
            this.port = port;
            this.databaseName = databaseName;
            this.username = username;
            this.password = password;
        }

        public String databaseType() { return this.databaseType; }
        public String url() { return this.url; }
        public int port() { return this.port; }
        public String databaseName() { return this.databaseName; }
        public String username() { return this.username; }
        public String password() { return this.password; }
    }

}
