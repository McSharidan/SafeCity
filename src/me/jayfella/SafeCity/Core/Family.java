package me.jayfella.SafeCity.Core;

import java.util.Set;

public class Family implements SafeCityFamily
{
    private final String familyHead;
    private final Set<String> familyMembers;

    private int familyReputation;
    private double familyBankBalance;

    public Family(String familyHead, Set<String> familyMembers, int reputation)
    {
        this.familyHead = familyHead;
        this.familyMembers = familyMembers;
    }

    @Override public String getFamilyHead() { return familyHead; }
    @Override public String[] getFamilyMembers() { return familyMembers.toArray(new String[familyMembers.size()]); }

    @Override public boolean isFamilyMember(String playerName) { return familyMembers.contains(playerName); }
    @Override public boolean addFamilyMember(String playerName) { return this.familyMembers.add(playerName); }
    @Override public boolean removeFamilyMember(String playerName) { return this.familyMembers.remove(playerName); }

    @Override public int getReputation() { return this.familyReputation; }
    @Override public int giveReputation(int amount) { this.familyReputation += amount; return this.familyReputation; }
    @Override public int takeReputation(int amount) { this.familyReputation -= amount; return this.familyReputation; }
    @Override public void setReputation(int amount) { this.familyReputation = amount; }

    @Override public double getFamilyBankBalance() { return this.familyBankBalance; }
    @Override public double addMoneyToFamilyBank(double amount) { this.familyBankBalance += amount; return this.familyBankBalance; }
    @Override public double takeMoneyFromFamilyBank(double amount) { this.familyBankBalance -= amount; return this.familyBankBalance; }
}
