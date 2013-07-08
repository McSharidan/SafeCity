package me.jayfella.SafeCity.Core;

public class PlayerStatistics
{
    private int transactionsIn;
    private int transactionsOut;
    private double moneyIn;
    private double moneyOut;

    private int playersKilled;
    private int entitiesKilled;

    private long damageTaken;
    private long damageGiven;

    private int timesDied;

    public int getTransactionsIn() { return this.transactionsIn; }
    public int addTransactionIn(int amount) { this.transactionsIn += amount; return this.transactionsIn; }
    public int getTransactionsOut() { return this.transactionsOut; }
    public int addTransactionOut(int amount) { this.transactionsOut += amount; return this.transactionsOut; }

    public double getMoneyIn() { return this.moneyIn; }

    public double getMoneyOut() { return this.moneyOut; }

    public int getPlayersKilled() { return this.playersKilled; }
    public int addPlayerKilled() { this.playersKilled += 1; return this.playersKilled; }

    public int getEntitiesKilled() { return this.entitiesKilled; }
    public int addEntityKilled() { this.entitiesKilled += 1; return this.entitiesKilled; }

    public long getDamageTaken() { return this.damageTaken; }
    public long addDamageTaken(long amount) { this.damageTaken += amount; return this.damageTaken; }

    public long getDamageGiven() { return this.damageGiven; }
    public long addDamageGiven(long amount) { this.damageGiven += amount; return this.damageGiven; }


    public int getTimesDied() { return this.timesDied; }
    public int addTimeDied() { this.timesDied += 1; return this.timesDied; }
}
