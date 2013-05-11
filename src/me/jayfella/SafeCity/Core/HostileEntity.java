
package me.jayfella.SafeCity.Core;

import org.bukkit.entity.EntityType;

public enum HostileEntity
{
    Blaze(EntityType.BLAZE),
    CaveSpider(EntityType.CAVE_SPIDER),
    Creeper(EntityType.CREEPER),
    Enderman(EntityType.ENDERMAN),
    EnderDragon(EntityType.ENDER_DRAGON),
    Ghast(EntityType.GHAST),
    Giant(EntityType.GIANT),
    MagmaCube(EntityType.MAGMA_CUBE),
    PigZombie(EntityType.PIG_ZOMBIE),
    SilverFish(EntityType.SILVERFISH),
    Skeleton(EntityType.SKELETON),
    Slime(EntityType.SLIME),
    Spider(EntityType.SPIDER),
    Witch(EntityType.WITCH),
    Wither(EntityType.WITHER),
    Zombie(EntityType.ZOMBIE);

    private EntityType type;

    private HostileEntity(EntityType type)
    {
        this.type = type;
    }

    public EntityType getType()
    {
        return this.type;
    }

}
