package me.jayfella.SafeCity;

public final class SafeCityOfflinePlayer 
{
    private final SafeCityContext context;
    
    private final String playerName;
    
    private long registeredTime;
    private long lastLoginTime;
    private long lastLogoutTime;
    
    public SafeCityOfflinePlayer(SafeCityContext context, String playerName, long regMillis, long loginMillis, long logoutMillis)
    {
        this.context = context;
        
        
        if (!context.playerExists(playerName))
        {
            this.playerName = "";
            return;
        }
        this.playerName = playerName;
        this.registeredTime = regMillis;
        this.lastLoginTime = loginMillis;
        this.lastLogoutTime = logoutMillis;
    }
    
    public String getName() { return this.playerName; }
    
    public long getRegisterTime() { return this.registeredTime; }
    public long getLastLoginTime() { return this.lastLoginTime; }
    public long getLastLogoutTime() { return this.lastLogoutTime; }
    
}
