package me.jayfella.SafeCity.Runnables;

import me.jayfella.SafeCity.SafeCityPlayer;


public class PlayerCoolDownThread implements Runnable
{
	private SafeCityPlayer _scPlayer;
	
	public PlayerCoolDownThread(SafeCityPlayer scPlayer)
	{
		_scPlayer = scPlayer;
	}
	
	@Override
	public void run()
	{
		_scPlayer.stopCoolDown();
	}

}
