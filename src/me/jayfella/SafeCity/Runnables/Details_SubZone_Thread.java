package me.jayfella.SafeCity.Runnables;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCityPlayer;
import me.jayfella.SafeCity.SafeCitySubZone;
import org.bukkit.ChatColor;

public class Details_SubZone_Thread implements Runnable
{
	private SafeCityContext context;
	private SafeCityPlayer scPlayer;
	private SafeCitySubZone subZone;

	public Details_SubZone_Thread(SafeCityContext context, SafeCityPlayer scPlayer, SafeCitySubZone subZone)
	{
		this.context = context;
		this.scPlayer = scPlayer;
		this.subZone = subZone;
	}


	@Override
	public void run()
	{
		// Title
		StringBuilder zoneTitle_Sb = new StringBuilder();

		      zoneTitle_Sb.append(ChatColor.DARK_GRAY)
                        .append("-----==] ")
            .append(ChatColor.WHITE)
            .append(subZone.getName())
			.append(ChatColor.DARK_GRAY)
			.append(" [==-----");

		// Dimensions
		StringBuilder zoneDimensions_Sb = new StringBuilder()
			.append(ChatColor.DARK_GREEN)
			.append("Length: ")
			.append(ChatColor.RED)
			.append(subZone.getLength())
			.append(ChatColor.GREEN)
			.append(" | ")
			.append(ChatColor.DARK_GREEN)
			.append("Width: ")
			.append(ChatColor.RED)
			.append(subZone.getWidth())
			.append(ChatColor.GREEN)
			.append(" | ")
			.append(ChatColor.DARK_GREEN)
			.append("Total: ")
			.append(ChatColor.RED)
			.append(subZone.getSurfaceArea());

		if (subZone.getLesserCorner().getBlockY() != 1 && subZone.getGreaterCorner().getBlockY() != 255)
		{
			zoneDimensions_Sb
				.append(ChatColor.GREEN)
				.append(" | ")
				.append(ChatColor.DARK_GREEN)
				.append("Height: ")
				.append(ChatColor.RED)
				.append(subZone.getHeight());
		}


		if (subZone.isForSale())
		{
			StringBuilder sb = new StringBuilder()
				.append(ChatColor.GOLD)
				.append("For Sale: ")
				.append(ChatColor.RED)
				.append(context.currencySingular())
				.append(subZone.getSalePrice());

			String[] message = new String[]
				{
					zoneTitle_Sb.toString(),
					zoneDimensions_Sb.toString(),
					sb.toString(),
				};

			scPlayer.getBukkitPlayer().sendMessage(message);
		}
		else if (subZone.isSold())
		{
			StringBuilder sb = new StringBuilder()
				.append(ChatColor.GOLD)
				.append("Purchased By: ")
				.append(ChatColor.RED)
				.append(subZone.getBuyer());

			String[] message = new String[]
				{
					zoneTitle_Sb.toString(),
					zoneDimensions_Sb.toString(),
					sb.toString(),
				};

		scPlayer.getBukkitPlayer().sendMessage(message);
		}

		else if (subZone.isForRent())
		{
			StringBuilder sb = new StringBuilder()
				.append(ChatColor.GOLD)
				.append("For Rent: ")
				.append(ChatColor.RED)
				.append(context.currencySingular())
				.append(subZone.getSalePrice())
				.append(ChatColor.GOLD)
				.append(" for ")
				.append(ChatColor.RED)
				.append(subZone.getRentalLength())
				.append(ChatColor.GOLD)
				.append(" days");


			String[] message = new String[]
				{
					zoneTitle_Sb.toString(),
					zoneDimensions_Sb.toString(),
					sb.toString(),
				};

		scPlayer.getBukkitPlayer().sendMessage(message);
		}
		else if (subZone.isRented())
		{
			StringBuilder sb = new StringBuilder();

			sb.append(ChatColor.GOLD)
			.append(" [R: ")
			.append(subZone.getRenter())
			.append("] ");

			Date dateNow = new Date();
			Date rentEnds = new Date(subZone.getRentTimeEnds());

			long msDiff= rentEnds.getTime() - dateNow.getTime();

			long daysLeft = TimeUnit.MILLISECONDS.toDays(msDiff);
			long hoursLeft = TimeUnit.MILLISECONDS.toHours(msDiff);
			long minsLeft = TimeUnit.MILLISECONDS.toMinutes(msDiff);

			hoursLeft -= (24 * daysLeft);
			minsLeft -= (24 * daysLeft * 60);
			minsLeft -= (60 * hoursLeft);

			sb.append(ChatColor.GOLD)
				.append(" (")
				.append(ChatColor.RED)
				.append(daysLeft)
				.append(ChatColor.GOLD)
				.append(" days ")
				.append(ChatColor.RED)
				.append(hoursLeft)
				.append(ChatColor.GOLD)
				.append(" hours ")
				.append(ChatColor.RED)
				.append(minsLeft)
				.append(ChatColor.GOLD)
				.append(" mins left)");

			String[] message = new String[]
				{
					zoneTitle_Sb.toString(),
					zoneDimensions_Sb.toString(),
					sb.toString(),
				};

			scPlayer.getBukkitPlayer().sendMessage(message);
		}

		else // just a regular subZone
		{
			String owner = ChatColor.DARK_GREEN + "Owner: " + ChatColor.RED + subZone.getFounder();

			String[] message = new String[]
				{
					"",
					zoneTitle_Sb.toString(),
					owner,
					zoneDimensions_Sb.toString(),
				};

			scPlayer.getBukkitPlayer().sendMessage(message);
		}



	}

}
