package me.jayfella.SafeCity.Runnables;

import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCityPlayer;
import me.jayfella.SafeCity.SafeCitySubZone;
import me.jayfella.SafeCity.SafeCityZone;
import org.bukkit.ChatColor;


public class ZoneInfoThread implements Runnable
{
	private SafeCityContext context;
	private SafeCityPlayer scPlayer;

	public ZoneInfoThread(SafeCityContext context, SafeCityPlayer scPlayer)
	{
		this.context = context;
		this.scPlayer = scPlayer;
	}

	@Override
	public void run()
	{
		// check if we are still in the same zone
		if (scPlayer.getZoneManager().getCurrentZone() != null)
		{
			// if in same zone
			if (scPlayer.getZoneManager().getCurrentZone().isInsideZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld(), false))
			{
				// check if we are still in the same sub-zone
				if (scPlayer.getZoneManager().getCurrentSubZone() != null)
				{
					// in the same zone & sub-zone, so return;
					if (scPlayer.getZoneManager().getCurrentSubZone().isInsideZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld(), true))
					{
						return;
					}
					else // different (or null) subZone.
					{
						scPlayer.getZoneManager().setCurrentSubZone(scPlayer.getZoneManager().getCurrentZoneChild(scPlayer.getLocation()));
					}
				}
				else // check if we are now in a zone
				{
					SafeCitySubZone subZone = scPlayer.getZoneManager().getCurrentZoneChild(scPlayer.getLocation());

					// current and new subZone == null, so leave
					if (subZone == null)
					{
						return;
					}

					scPlayer.getZoneManager().setCurrentSubZone(subZone);
				}
			}
			else // not in same zone
			{
				scPlayer.getZoneManager().setCurrentZone(context.getZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld()));
			}
		}
		else // if null (wilderness) check again
		{
			SafeCityZone zone = context.getZone(scPlayer.getLocation(), scPlayer.getBukkitPlayer().getWorld());

			// current and new zone == null, so leave.
			if (zone == null)
			{
				return;
			}

			scPlayer.getZoneManager().setCurrentZone(zone);
		}

		if (scPlayer.getZoneManager().getCurrentZone() == null)
		{
			scPlayer.getBukkitPlayer().sendMessage(ChatColor.GOLD + " ~ " + ChatColor.GREEN + "Wilderness");
			return;
		}


		StringBuilder sb = new StringBuilder()
			.append(ChatColor.GOLD)
			.append(" ~ ");

        if (scPlayer.getZoneManager().getCurrentZone().isPvpEnabled())
        {
            sb.append(ChatColor.RED).append("(PvP) ");
        }

		if (scPlayer.getZoneManager().getCurrentZone().isPublic())
        {
            sb.append(ChatColor.AQUA)
                .append("[")
                .append(scPlayer.getZoneManager().getCurrentZone().getSettlementHierarchy().getSettlementName())
                .append("]");
        }

		if (scPlayer.getZoneManager().getCurrentZone().isForSale())
        {
            sb.append(ChatColor.GOLD)
                .append("[ B: ")
                .append(ChatColor.GREEN)
                .append(context.currencySingular())
                .append(scPlayer.getZoneManager().getCurrentZone().getSalePrice())
                .append(ChatColor.GOLD)
                .append(" ] ");
        }

		if (scPlayer.getZoneManager().getCurrentZone().isForRent())
        {
            sb.append(ChatColor.GOLD)
                .append("[ R: ")
                .append(ChatColor.GREEN)
                .append(context.currencySingular())
                .append(scPlayer.getZoneManager().getCurrentZone().getSalePrice())
                .append(ChatColor.GOLD)
                .append(" for ")
                .append(ChatColor.GREEN)
                .append(scPlayer.getZoneManager().getCurrentZone().getRentalLength())
                .append(ChatColor.GOLD).append(" days ] ");
        }
		else if (scPlayer.getZoneManager().getCurrentZone().isRented())
        {
            sb.append(ChatColor.GOLD)
                .append("[ R: ")
                .append(ChatColor.GREEN)
                .append(scPlayer.getZoneManager().getCurrentZone().getRenter())
                .append(ChatColor.GOLD)
                .append(" ] ");
        }

		sb.append(ChatColor.YELLOW)
			// .append(" ")
			.append(scPlayer.getZoneManager().getCurrentZone().getName());

		if (scPlayer.getZoneManager().getCurrentSubZone() != null)
		{
			sb.append(ChatColor.GREEN).append(" -> ");

            if (scPlayer.getZoneManager().getCurrentSubZone().isPvpEnabled())
            {
                sb.append(ChatColor.RED).append("(PvP) ");
            }

			if (scPlayer.getZoneManager().getCurrentSubZone().isForSale())
			{
				sb.append(ChatColor.GOLD)
					.append("[ B: ").append(ChatColor.GREEN)
					.append(context.currencySingular())
					.append(scPlayer.getZoneManager().getCurrentSubZone().getSalePrice())
					.append(ChatColor.GOLD)
					.append(" ] ");
			}

			else if (scPlayer.getZoneManager().getCurrentSubZone().isForRent())
			{
				sb.append(ChatColor.GOLD)
					.append("[ R: ")
					.append(ChatColor.GREEN)
					.append(context.currencySingular())
					.append(scPlayer.getZoneManager().getCurrentSubZone().getSalePrice())
					.append(ChatColor.GOLD)
					.append(" for ")
					.append(ChatColor.GREEN)
					.append(scPlayer.getZoneManager().getCurrentSubZone().getRentalLength())
					.append(ChatColor.GOLD)
					.append(" days ] ");
			}
			else if (scPlayer.getZoneManager().getCurrentSubZone().isRented())
			{
				sb.append(ChatColor.GOLD)
					.append("[ R: ")
					.append(ChatColor.GREEN)
					.append(scPlayer.getZoneManager().getCurrentSubZone().getRenter())
					.append(ChatColor.GOLD)
					.append(" ] ");
			}

			if (!scPlayer.getZoneManager().getCurrentSubZone().getBuyer().isEmpty())
			{
				sb.append(ChatColor.GOLD)
					.append("[ B: ")
					.append(ChatColor.GREEN)
					.append(scPlayer.getZoneManager().getCurrentSubZone().getBuyer())
					.append(ChatColor.GOLD)
					.append(" ] ");
			}

			sb.append(ChatColor.YELLOW).append(scPlayer.getZoneManager().getCurrentSubZone().getName());
		}

		scPlayer.getBukkitPlayer().sendMessage(sb.toString());

		if (scPlayer.getZoneManager().getCurrentSubZone() == null)
		{
			if (!scPlayer.getZoneManager().getCurrentZone().getEntryMessage().isEmpty())
			{
				scPlayer.getBukkitPlayer().sendMessage(scPlayer.getZoneManager().getCurrentZone().getEntryMessage());
			}
		}
		else
		{
			if (!scPlayer.getZoneManager().getCurrentSubZone().getEntryMessage().isEmpty())
			{
				scPlayer.getBukkitPlayer().sendMessage(scPlayer.getZoneManager().getCurrentSubZone().getEntryMessage());
			}
		}

		scPlayer.startCoolDown(5L);
	}

}
