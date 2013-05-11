package me.jayfella.SafeCity.Runnables;

import java.util.Date;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCitySubZone;
import me.jayfella.SafeCity.SafeCityZone;

public class RentCycleThread implements Runnable
{
	private SafeCityContext context;


	public RentCycleThread(SafeCityContext context)
	{
		this.context = context;
	}

	@Override
	public void run()
	{
		Date dateNow;
		Date rentEnds;

		for (int i = 0; i < context.getZones().size(); i++)
		{
			SafeCityZone zone = context.getZones().get(i);
            if (!zone.isRented()) { continue; }

			dateNow = new Date();
			rentEnds = new Date(zone.getRentTimeEnds());

			if (dateNow.after(rentEnds))
			{
				zone.setRentDetails(true, false, zone.getSalePrice(), zone.getRentalLength(), 0L, "");

                if (zone.getInfoSignLocation() != null)
                {
                    if (zone.getInfoSign() != null)
                    {
                        context.getSignManager().setRentSignal(zone.getInfoSign(), zone.getSalePrice(), zone.getRentalLength());
                    }
                }


			}
		}

		for (int i = 0; i < context.getSubZones().size(); i++)
		{
			SafeCitySubZone subZone = context.getSubZones().get(i);
            if (!subZone.isRented()) { continue; }

			dateNow = new Date();
			rentEnds = new Date(subZone.getRentTimeEnds());

			if (dateNow.after(rentEnds))
			{
				subZone.setRentDetails(true, false, subZone.getSalePrice(), subZone.getRentalLength(), 0L, "");

                if (subZone.getInfoSignLocation() != null)
                {
                    if (subZone.getInfoSign() != null)
                    {
                        context.getSignManager().setRentSignal(subZone.getInfoSign(), subZone.getSalePrice(), subZone.getRentalLength());
                    }
                }
			}
		}
	}
}
