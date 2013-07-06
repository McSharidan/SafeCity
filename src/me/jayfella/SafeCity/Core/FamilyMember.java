package me.jayfella.SafeCity.Core;

public class FamilyMember implements SafeCityFamilyMember
{
    private int reputation;

    @Override public int getReputation() { return this.reputation; }
    @Override public int giveReputation(int amount) { this.reputation += amount; return this.reputation; }
    @Override public int takeReputation(int amount) { this.reputation -= amount; return this.reputation; }
    @Override public void setReputation(int amount) { this.reputation = amount; }
    
}
