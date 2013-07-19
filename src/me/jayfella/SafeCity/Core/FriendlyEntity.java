package me.jayfella.SafeCity.Core;

import org.bukkit.entity.EntityType;

public enum FriendlyEntity
{
    Bat(EntityType.BAT),
    Chicken(EntityType.CHICKEN),
    Cow(EntityType.COW),
    Horse(EntityType.HORSE),
    IronGolem(EntityType.IRON_GOLEM),
    MushroomCow(EntityType.MUSHROOM_COW),
    Ocelot(EntityType.OCELOT),
    Pig(EntityType.PIG),
    Sheep(EntityType.SHEEP),
    Snowman(EntityType.SNOWMAN),
    Squid(EntityType.SQUID),
    Villager(EntityType.VILLAGER),
    Wolf(EntityType.WOLF);

    private EntityType type;

    private FriendlyEntity(EntityType type)
    {
        this.type = type;
    }

    public EntityType getType() { return this.type; }
}
