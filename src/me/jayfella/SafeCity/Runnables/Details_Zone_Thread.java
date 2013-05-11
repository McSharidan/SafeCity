package me.jayfella.SafeCity.Runnables;

import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCityPlayer;
import me.jayfella.SafeCity.SafeCityZone;

public class Details_Zone_Thread implements Runnable
{
	private SafeCityContext context;
	private SafeCityPlayer scPlayer;
	private SafeCityZone zone;

	public Details_Zone_Thread(SafeCityContext context, SafeCityPlayer scPlayer, SafeCityZone zone)
	{
		this.context = context;
		this.scPlayer = scPlayer;
		this.zone = zone;
	}

	@Override
	public void run()
	{
		// Title
		/*StringBuilder zoneTitle_Sb = new StringBuilder();
		SettlementHierarchy settlementType = zone.getSettlementHierarchy();

		zoneTitle_Sb
			.append(ChatColor.DARK_GRAY)
			.append("-----==] ")
			.append(ChatColor.WHITE)
			.append(zone.getName());

		if (zone.isPublic())
		{
			zoneTitle_Sb.append(" (")
				.append(ChatColor.GOLD)
				.append(settlementType.getSettlementName())
				.append(ChatColor.WHITE)
				.append(")");
		}

		zoneTitle_Sb.append(ChatColor.DARK_GRAY).append(" [==-----");

		// Dimensions
		StringBuilder zoneDimensions_Sb = new StringBuilder()
			.append(ChatColor.DARK_GREEN)
			.append("Length: ")
			.append(ChatColor.RED)
			.append(zone.getLength())
			.append(ChatColor.GREEN)
			.append(" | ")
			.append(ChatColor.DARK_GREEN)
			.append("Width: ")
			.append(ChatColor.RED)
			.append(zone.getWidth())
			.append(ChatColor.GREEN)
			.append(" | ")
			.append(ChatColor.DARK_GREEN)
			.append("Total: ")
			.append(ChatColor.RED)
			.append(zone.getSize());

		if (zone.isPublic())
		{
			ArrayList<String> population = zone.getPopulation();

			// plot count vs residents
			StringBuilder plotResCount_Sb = new StringBuilder()
				.append(ChatColor.DARK_GREEN)
				.append("Sub-Plots: ")
				.append(ChatColor.RED)
				.append(zone.getChildren().size())
				.append(ChatColor.GREEN)
				.append(" | ")
				.append(ChatColor.DARK_GREEN)
				.append("Residents: ")
				.append(ChatColor.RED)
				.append(population.size());

			// Owner
			StringBuilder ownerTitle_Sb = new StringBuilder()
				.append(ChatColor.DARK_GREEN)
				.append("Owner: ")
				.append(ChatColor.GREEN);

			if (zone.isPublic())
            {
                ownerTitle_Sb.append(settlementType.getOwnerTitle())
                .append(" ")
                .append(zone.getOwner());
            }
			else
            {
                ownerTitle_Sb.append(zone.getOwner());
            }

			// Assistants
			StringBuilder assistants_Sb = new StringBuilder();
			/*String[] assistants = zone.getAssistants();

			if (assistants.length > 0)
			{
				assistants_Sb.append(ChatColor.DARK_GREEN)
					.append("Assistants ")
					.append(ChatColor.GREEN)
					.append("[")
					.append(assistants.length)
					.append("] ")
					.append(ChatColor.DARK_GREEN)
					.append(" : ");

				for (int i = 0; i < assistants.length; i++)
				{
					assistants_Sb.append(ChatColor.GRAY).append(assistants[i]);

					if (i != zone.getAssistants().length -1)
                    {
                        assistants_Sb.append(ChatColor.DARK_GREEN).append(", ");
                    }
				}
			}
			else
			{
				assistants_Sb.append(ChatColor.DARK_GREEN)
					.append("Assistants")
					.append(ChatColor.DARK_GREEN)
					.append(" : ")
					.append(ChatColor.GREEN)
					.append("None");
			}

			// residents

			StringBuilder population_Sb = new StringBuilder();

			if (population.size() > 0)
			{
				population_Sb.append(ChatColor.DARK_GREEN).append("Residents : ");

				for (int i = 0; i < population.size(); i++)
				{
					population_Sb.append(ChatColor.GRAY).append(population.get(i));

					if (i != population.size() -1)
                    {
                        population_Sb.append(ChatColor.DARK_GREEN).append(", ");
                    }
				}
			}
			else
			{
				population_Sb.append(ChatColor.DARK_GREEN)
					.append("Residents")
					.append(ChatColor.DARK_GREEN)
					.append(" : ")
					.append(ChatColor.GREEN)
					.append("None");
			}

			String[] message = new String[]
					{
						"",
						zoneTitle_Sb.toString(),
						zoneDimensions_Sb.toString(),
						plotResCount_Sb.toString(),
						ownerTitle_Sb.toString(),
						assistants_Sb.toString(),
						population_Sb.toString(),
					};

			scPlayer.getBukkitPlayer().sendMessage(message);
		}

		if (zone.isForSale())
		{
			StringBuilder sb = new StringBuilder()
				.append(ChatColor.GOLD)
				.append("For Sale: ")
				.append(ChatColor.RED)
				.append(context.currencySingular())
				.append(zone.getSalePrice());

			String[] message = new String[]
				{
					zoneTitle_Sb.toString(),
					zoneDimensions_Sb.toString(),
					sb.toString(),
				};

			scPlayer.getBukkitPlayer().sendMessage(message);
		}

		else if (zone.isForRent())
		{
			StringBuilder sb = new StringBuilder()
			.append(ChatColor.GOLD)
			.append("For Rent: ")
			.append(ChatColor.RED)
			.append(context.currencySingular())
			.append(zone.getSalePrice())
			.append(ChatColor.GOLD)
			.append(" for ")
			.append(ChatColor.RED)
			.append(zone.getRentalPeriod())
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
		else if (zone.isRented())
		{
			StringBuilder sb = new StringBuilder();

			sb.append(ChatColor.GOLD)
				.append(" [ R: ")
				.append(zone.getRenter())
				.append(" ] ");

			Date dateNow = new Date();
			Date rentEnds = new Date(zone.getRentTimeEnds());

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
					"",
					zoneTitle_Sb.toString(),
					zoneDimensions_Sb.toString(),
					sb.toString(),
				};

			scPlayer.getBukkitPlayer().sendMessage(message);
		}

		else if (zone.isForMortgage())
		{
			StringBuilder sb = new StringBuilder()
			.append(ChatColor.GOLD)
			.append("For Mortgage: ")
			.append(ChatColor.RED)
			.append(context.currencySingular())
			.append(zone.getSalePrice())
			.append(ChatColor.GOLD)
			.append(" every week, for ")
			.append(ChatColor.RED)
			.append(zone.getMortgageLength())
			.append(ChatColor.GOLD)
			.append(" weeks");


			String[] message = new String[]
				{
					zoneTitle_Sb.toString(),
					zoneDimensions_Sb.toString(),
					sb.toString(),
				};

			scPlayer.getBukkitPlayer().sendMessage(message);
		}

		else // just a regular zone.
		{
			String owner = ChatColor.DARK_GREEN + "Owner: " + ChatColor.RED + zone.getOwner();

			String[] message = new String[]
					{
						"",
						zoneTitle_Sb.toString(),
						owner,
						zoneDimensions_Sb.toString(),
					};

				scPlayer.getBukkitPlayer().sendMessage(message);
		}*/

	}

}
