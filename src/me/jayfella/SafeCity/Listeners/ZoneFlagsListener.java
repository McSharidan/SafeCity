package me.jayfella.SafeCity.Listeners;

import me.jayfella.SafeCity.Core.FriendlyEntity;
import me.jayfella.SafeCity.Core.ThinLocation;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCitySubZone;
import me.jayfella.SafeCity.SafeCityZone;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;



public final class ZoneFlagsListener implements Listener
{
    private final SafeCityContext context;

	/*private final EntityType[] friendlyMobs =
		{
            EntityType.CHICKEN,
            EntityType.COW,
            EntityType.HORSE,
            EntityType.OCELOT,
            EntityType.IRON_GOLEM,
            EntityType.MUSHROOM_COW,
            EntityType.PIG,
            EntityType.SHEEP,
            EntityType.SNOWMAN,
            EntityType.VILLAGER,
            EntityType.WOLF,
		};*/


    public ZoneFlagsListener(SafeCityContext context)
    {
        this.context = context;
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        if (!context.isValidWorld(event.getLocation().getWorld().getName()))
            return;

        Location loc = event.getLocation();

        World entityWorld = loc.getWorld();
        ThinLocation entityLoc = context.toThinLocation(loc);

        SafeCityZone zone = context.getZone(entityLoc, entityWorld);
        if (zone == null) { return; }

        SafeCitySubZone subZone = context.getSubZone(entityLoc, entityWorld);

        if (subZone != null)
        {
            if (!subZone.allowsMobSpawning())
            {
                /*for(int f = 0; f < FriendlyEntity.values().length; f++)
                {
                    if(event.getEntityType() == FriendlyEntity.values()[f])
                    {
                        return;
                    }
                }*/
                
                for (FriendlyEntity f : FriendlyEntity.values())
                {
                    if (event.getEntityType() == f.getType())
                    {
                        return;
                    }
                }

                event.setCancelled(true);
            }
            else
            {
                if (entityLoc.getBlockY() <= subZone.getLesserCorner().getBlockY() && entityLoc.getBlockY() >= subZone.getGreaterCorner().getBlockY())
                {
                    event.setCancelled(true);
                }
            }
        }
        else
        {
            if (!zone.allowsMobSpawning())
            {
                /*for(int f = 0; f < friendlyMobs.length; f++)
                {
                    if(event.getEntityType() == friendlyMobs[f])
                    {
                        return;
                    }
                }*/
                
                for (FriendlyEntity f : FriendlyEntity.values())
                {
                    if (event.getEntityType() == f.getType())
                    {
                        return;
                    }
                }

                event.setCancelled(true);
            }
        }
    }

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
        if (!context.isValidWorld(event.getEntity().getWorld().getName()))
            return;

		if (!(event.getEntity() instanceof Player))
        {
            return;
        }

		Player defender = (Player)event.getEntity();

		Player attacker = null;
	    Entity damageSource = event.getDamager();

		if ((damageSource instanceof Player))
	    {
	    	attacker = (Player)damageSource;
	    }
	    else if ((damageSource instanceof Arrow))
	    {
	    	Arrow arrow = (Arrow)damageSource;

	    	if ((arrow.getShooter() instanceof Player))
	    	{
	    		attacker = (Player)arrow.getShooter();
	    	}
	    }
	    else if ((damageSource instanceof ThrownPotion))
	    {
	    	ThrownPotion potion = (ThrownPotion)damageSource;

	    	if ((potion.getShooter() instanceof Player))
	    	{
	    		attacker = (Player)potion.getShooter();
	    	}

	    }
	    else if ((damageSource instanceof Snowball))
	    {
	    	Snowball snowball = (Snowball)damageSource;

	    	if ((snowball.getShooter() instanceof Player))
	    	{
	    		attacker = (Player)snowball.getShooter();
	    	}
	    }
	    else if ((damageSource instanceof Egg))
	    {
	    	Egg egg = (Egg)damageSource;

	    	if ((egg.getShooter() instanceof Player))
	    	{
	    		attacker = (Player)egg.getShooter();
	    	}
	    }

	    if (attacker == null)
        {
            return;
        }

	    if (!(attacker instanceof Player))
        {
            return;
        }


		SafeCityZone attackerZone = context.getZone(context.toThinLocation(attacker.getLocation()), attacker.getWorld());
		SafeCityZone defenderZone = context.getZone(context.toThinLocation(defender.getLocation()), defender.getWorld());

		if (attackerZone == null || defenderZone == null)
		{
			event.setCancelled(true);
			return;
		}

		// both must be in the same place...
		if (attackerZone != defenderZone)
		{
			event.setCancelled(true);
			return;
		}

		SafeCitySubZone attackerSubZone = context.getSubZone(context.toThinLocation(attacker.getLocation()), attacker.getWorld());
		SafeCitySubZone defenderSubZone = context.getSubZone(context.toThinLocation(defender.getLocation()), defender.getWorld());

		// both must be in the same place...
		if (attackerSubZone != defenderSubZone)
		{
			event.setCancelled(true);
			return;
		}

		if (attackerSubZone == null)
		{
			if (!attackerZone.isPvpEnabled())
			{
				event.setCancelled(true);
				return;
			}
		}
		else
		{
			if (!attackerSubZone.isPvpEnabled())
			{
				event.setCancelled(true);
				return;
			}
		}

		event.setCancelled(false);
	}

}
