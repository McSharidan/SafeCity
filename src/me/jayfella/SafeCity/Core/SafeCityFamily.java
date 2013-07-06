package me.jayfella.SafeCity.Core;

public interface SafeCityFamily
{
    String getFamilyHead();
    String[] getFamilyMembers();

    double getFamilyBankBalance();
    double addMoneyToFamilyBank(double amount);
    double takeMoneyFromFamilyBank(double amount);

    boolean isFamilyMember(String playerName);
    boolean addFamilyMember(String playerName);
    boolean removeFamilyMember(String playerName);

    int getReputation();
    int giveReputation(int amount);
    int takeReputation(int amount);
    void setReputation(int amount);

}
