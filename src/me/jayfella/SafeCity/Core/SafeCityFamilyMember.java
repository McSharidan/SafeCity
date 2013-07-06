package me.jayfella.SafeCity.Core;

public interface SafeCityFamilyMember
{
    int getReputation();
    int giveReputation(int amount);
    int takeReputation(int amount);
    void setReputation(int amount);

}
