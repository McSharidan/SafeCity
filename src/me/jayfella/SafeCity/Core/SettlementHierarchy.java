package me.jayfella.SafeCity.Core;

public enum SettlementHierarchy
{
    Dwelling(0, "Dwelling", "Spirit"),
    Settlement(1, "Settlement", "Hermit"),
    Estate(4, "Estate", "Landlord"),
    Hamlet(10, "Hamlet", "Chief"),
    Village(15, "Village", "Baron Von"),
    Town(20, "Town", "Viscount"),
    LargeTown(30, "Large Town", "Count Von"),
    City(45, "City", "Earl"),
    LargeCity(60, "Large City", "Duke"),
    Metropolis(80, "Metropolis", "Mayor"),
    Conurbation(120, "Conurbation", "Lord"),
    Megalopolis(150, "Megalopolis", "President"),
    Ecumenopolis(200, "Ecumenopolis", "King");

    private int minPopulation;
    private String settlementType;
    private String ownerTitle;

    private SettlementHierarchy(int minPopulation, String settlementType, String ownerTitle)
    {
        this.minPopulation = minPopulation;
        this.settlementType = settlementType;
        this.ownerTitle = ownerTitle;
    }

    public int getMinPopulation() { return minPopulation; }
    public String getSettlementName() {	return settlementType; }
    public String getOwnerTitle() { return ownerTitle; }

    @Override
    public String toString()
    {
        return settlementType;
    }

}
