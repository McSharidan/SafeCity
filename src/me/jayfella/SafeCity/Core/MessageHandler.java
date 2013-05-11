package me.jayfella.SafeCity.Core;

import java.util.ArrayList;
import java.util.Locale;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCitySubZone;
import me.jayfella.SafeCity.SafeCityZone;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public final class MessageHandler
{
	private final SafeCityContext context;

	private final String PluginPrefix = ChatColor.YELLOW + "[SafeCity] ";

	private final ChatColor error = ChatColor.DARK_RED;
	private final ChatColor info = ChatColor.GRAY;
	private final ChatColor highlight = ChatColor.RED;
	private final ChatColor success = ChatColor.GREEN;

	public MessageHandler(SafeCityContext context)
	{
		this.context = context;
	}

	public String Ingame_Only()
	{
		return new StringBuilder()
                .append(PluginPrefix)
                .append(error)
                .append("This command can only be run from in-game")
                .toString();
	}

	public String No_Permission()
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(error)
			.append("You do not have permission to do that.")
			.toString();
	}



	public String Exceeds_SizeLimit(String string, int maxChars)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(error)
			.append("Value: ")
			.append(highlight)
			.append(string)
			.append(error)
			.append(" exceeds ")
			.append(highlight)
			.append(maxChars)
			.append(error)
			.append(" in length.")
			.toString();
	}

	public String Player_Not_Exist(String playerName)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(info)
			.append("Unable to locate player: ")
			.append(highlight)
			.append(playerName)
			.toString();
	}

	public String Zone_Not_Found()
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(error)
			.append("You do not appear to be standing inside a zone.")
			.toString();
	}

    public String Unknown_Zone(String zoneName)
    {
        return new StringBuilder()
                .append(PluginPrefix)
                .append(error)
                .append("Cannot locate zone: ")
                .append(highlight)
                .append(zoneName)
                .toString();
    }

    public String Unknown_Zone(int zoneId)
    {
        return new StringBuilder()
                .append(PluginPrefix)
                .append(error)
                .append("Cannot locate zone ID: ")
                .append(highlight)
                .append(zoneId)
                .toString();
    }

    public String Zone_Already_Exists()
    {
        return new StringBuilder()
                .append(PluginPrefix)
                .append(error)
                .append("A primary zone already exists here.")
                .toString();
    }

    public String Zone_Name_Already_Exists(String zoneName)
    {
        return new StringBuilder()
                .append(PluginPrefix)
                .append(error)
                .append("A zone already exists with the name: ")
                .append(highlight)
                .append(zoneName)
                .toString();
    }

    public String Too_Many_Zones()
    {
        return new StringBuilder()
                .append(PluginPrefix)
                .append(error)
                .append("You already have the maximum amount of zones you are allowed to own.")
                .toString();
    }

    public String Zone_Name_Too_Long()
    {
        return new StringBuilder()
                .append(PluginPrefix)
                .append(error)
                .append("Zone name exceeds 32 characters")
                .toString();
    }

	public String Zone_Named(String zoneName)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("Zone successfully named to ")
			.append(highlight)
			.append(zoneName)
			.append(success)
			.append(".")
			.toString();
	}

	public String Zone_Renamed(String oldName, String newName)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("Zone successfully renamed from ")
			.append(highlight)
			.append(oldName)
			.append(success)
			.append(" to ")
			.append(highlight)
			.append(newName)
			.append(success)
			.append(".")
			.toString();
	}

	public String Zone_Deleted(int childCount)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("Zone deleted, along with ")
			.append(highlight)
			.append(Integer.toString(childCount))
			.append(success)
			.append(" sub-zones.")
			.toString();
	}

	public String SubZone_Deleted(String subZone)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(highlight)
			.append(subZone)
			.append(success)
			.append(" sucessfully deleted.")
			.toString();
	}

	public String Zone_MakePublic(String zoneName)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("Zone ")
			.append(highlight)
			.append(zoneName)
			.append(success)
			.append(" is now ")
			.append(highlight)
			.append("public")
			.append(success)
			.append(".")
			.toString();
	}

	public String Zone_AlreadyPublic(String zoneName)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(info)
			.append("Zone ")
			.append(highlight)
			.append(zoneName)
			.append(info)
			.append(" is already ")
			.append(highlight)
			.append("public")
			.append(info)
			.append(".")
			.toString();
	}

	public String Zone_MakePrivate(String zoneName)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("Zone ")
			.append(highlight)
			.append(zoneName)
			.append(success)
			.append(" is now ")
			.append(highlight)
			.append("private")
			.append(success)
			.append(".")
			.toString();
	}

	public String Zone_AlreadyPrivate(String zoneName)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(info)
			.append("Zone ")
			.append(highlight)
			.append(zoneName)
			.append(info)
			.append(" is already ")
			.append(highlight)
			.append("private")
			.append(info)
			.append(".")
			.toString();
	}

	public String Zone_Public_Server_Announce(String playerName, String zoneName)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(highlight)
			.append(playerName)
			.append(success)
			.append(" has established the town ")
			.append(highlight)
			.append(zoneName)
			.append(success)
			.append("!")
			.toString();
	}

	public String Zone_Private_Server_Announce(SafeCityZone zone)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("The ")
			.append(zone.getSettlementHierarchy())
			.append(" ")
			.append(highlight)
			.append(zone.getName())
			.append(success)
			.append(" has fallen into ruin!")
			.toString();
	}

	public String Zone_NoName()
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(info)
			.append("You must give this zone a name before you can declare it as ")
			.append(highlight)
			.append("public")
			.append(info)
			.append(".")
			.toString();
	}

	public String Permission_Granted(String permission, String playerName, String zoneName)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("Permission ")
			.append(highlight)
			.append(permission)
			.append(success)
			.append(" granted to player ")
			.append(highlight)
			.append(playerName)
			.append(success)
			.append(" in ")
			.append(highlight)
			.append(zoneName)
			.append(success)
			.append(".")
			.toString();
	}

	public String Permission_Granted_Player(String permission, String zoneName, String zoneOwner)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("You have been granted ")
			.append(highlight)
			.append(permission)
			.append(success)
			.append(" permission on zone ")
			.append(highlight)
			.append(zoneName)
			.append(success)
			.append(", owned by ")
			.append(highlight)
			.append(zoneOwner)
			.append(success)
			.append(".")
			.toString();
	}

	public String Permission_Revoked(String permission, String playerName, String zoneName)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("Permission ")
			.append(highlight)
			.append(permission)
			.append(success)
			.append(" revoked from player ")
			.append(highlight)
			.append(playerName)
			.append(success)
			.append(" in ")
			.append(highlight)
			.append(zoneName)
			.append(success)
			.append(".")
			.toString();
	}

	public String Permission_Revoked_Player(String permission, String zoneName, String zoneOwner)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("Permission ")
			.append(highlight)
			.append(permission)
			.append(success)
			.append(" has been revoked on zone ")
			.append(highlight)
			.append(zoneName)
			.append(success)
			.append(", owned by ")
			.append(highlight)
			.append(zoneOwner)
			.append(success)
			.append(".")
			.toString();
	}

	public String Permission_Exists(String playerName)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(info)
			.append("That permission already exists for ")
			.append(highlight)
			.append(playerName)
			.toString();
	}

	public String Permission_Not_Exists(String playerName)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(info)
			.append("Cannot revoke permission from ")
			.append(highlight)
			.append(playerName)
			.append(info)
			.append(" because the player does not possess it.")
			.toString();
	}

	public String Blocks_Remaining(int blocksRemaining)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(info)
			.append("You have ")
			.append(highlight)
			.append(Integer.toString(blocksRemaining))
			.append(info)
			.append(" blocks remaining.")
			.toString();
	}

	public String Blocks_Needed(int blocksRequired)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(info);
		message.append("You have exceeded your available blocks by ");
		message.append(highlight);
		message.append(Integer.toString(blocksRequired));
		message.append(info);
		message.append(".");

		return message.toString();
	}

	public String Insufficient_Funds()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(error);
		message.append("Insufficient funds to carry out this transaction.");

		return message.toString();
	}

	public String[] Display_Towns()
	{
		ArrayList<SafeCityZone> publicZones = new ArrayList<>();

		for (SafeCityZone z : context.getZones())
		{
			if (z.isPublic())
			{
				publicZones.add(z);
			}
		}

		if (publicZones.isEmpty())
		{
			return new String[] { PluginPrefix + info + "No public towns exist." };
		}

		StringBuilder sb = new StringBuilder();

		int publicZoneCount = publicZones.size();

        ChatColor publicWarp = ChatColor.GREEN;
        ChatColor privateWarp = ChatColor.AQUA;
        
		// for (SafeCityZone z : publicZones)
		for (int i = 0; i < publicZoneCount; i++)
		{
            
			SafeCityZone z = publicZones.get(i);
			sb.append(z.publicTeleportAllowed() ? publicWarp : privateWarp)
                    .append(z.getName())
                    .append(ChatColor.DARK_AQUA).append("[").append(highlight).append(z.getPopulation().length).append(ChatColor.DARK_AQUA).append("]");

			if (i != (publicZoneCount -1))
            {
                sb.append(ChatColor.RED).append(", ");
            }
		}

        String privateKey = ChatColor.WHITE + "[" + privateWarp + "#" + ChatColor.WHITE + "] Private Warp     ";
        String publicKey = ChatColor.WHITE + "[" + publicWarp + "#" + ChatColor.WHITE + "] Public Warp";
        
		String title = (ChatColor.GOLD + "_________.o0[" + ChatColor.YELLOW + "Towns" + ChatColor.GOLD + "]0o.__________");
		String towns = sb.toString();

		return new String[] { title, privateKey + publicKey, towns };
	}

	public String Too_Many_Children()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(info);
		message.append("You cannot create a zone within a sub-zone.");

		return message.toString();
	}



	public String Zone_Resizing(String zoneName)
	{
		return new StringBuilder()
            .append(PluginPrefix)
            .append(success)
            .append("Resizing zone ")
            .append(highlight)
            .append(zoneName)
            .append(success)
            .append("...")
            .toString();
	}

    public String Zone_Resized(String zoneName)
    {
        return new StringBuilder()
            .append(PluginPrefix)
            .append(success)
            .append("Zone ")
            .append(highlight)
            .append(zoneName)
            .append(success)
            .append(" resized successfully.")
            .toString();
    }



    public String SubZone_Creating()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(success);
		message.append("Creating new sub-zone...");

		return message.toString();
	}

	public String SubZone_Resizing(String subZoneName)
	{
		return new StringBuilder()
            .append(PluginPrefix)
            .append(success)
            .append("Resizing sub-zone ")
            .append(highlight)
            .append(subZoneName)
            .append(success)
            .append("...")
            .toString();
	}

    public String SubZone_Resized(String subZoneName)
    {
        return new StringBuilder()
            .append(PluginPrefix)
            .append(success)
            .append("Sub-zone ")
            .append(highlight)
            .append(subZoneName)
            .append(success)
            .append(" resized successfully.")
            .toString();
    }



	public String Zone_Resized_Larger(int blockDiff)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(success);
		message.append("Zone resized successfully, using ");
		message.append(highlight);
		message.append(Integer.toString(blockDiff));
		message.append(success);
		message.append(" additional blocks.");

		return message.toString();
	}

	public String Zone_Resized_Smaller(int blockDiff)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(success);
		message.append("Zone resized successfully, using ");
		message.append(highlight);
		message.append(Integer.toString(blockDiff));
		message.append(success);
		message.append(" less blocks.");

		return message.toString();
	}

	public String Zone_Resize_Cancel()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(error);
		message.append("Zone resize cancelled.");

		return message.toString();
	}



	public String Zone_Created(int size)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(success);
		message.append("Zone created successfully using ");
		message.append(highlight);
		message.append(Integer.toString(size));
		message.append(success);
		message.append(" blocks.");

		return message.toString();
	}

	public String Zone_Sub_Creating()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(success);
		message.append("Creating new sub-zone..");

		return message.toString();
	}

	public String Zone_Sub_Created()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(success);
		message.append("Sub-zone created successfully.");

		return message.toString();
	}



	public String Zone_Overlap()
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(error)
			.append("Zone overlaps an existing zone.")
			.toString();
	}

	public String Zone_Mismatch()
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(error)
			.append("Zone mismatch. Sub-zone exceeds primary zone.")
			.toString();
	}

	public String Zone_Resize_SubZones_Outside()
	{
		return new StringBuilder()
		.append(PluginPrefix)
		.append(error)
		.append("One or more sub-zones are outside primary zone.")
		.toString();
	}

	public String Zone_Too_Close(SafeCityZone zone)
	{
		StringBuilder message = new StringBuilder()
                .append(PluginPrefix)
                .append(error)
                .append("Zone is too close to zone ")
                .append(highlight)
                .append(zone.getName())
                .append(error)
                .append(" owned by ")
                .append(highlight)
                .append(zone.getFounder());

		return message.toString();
	}

	public String Zone_Create_Cancel()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(error);
		message.append("Zone creation cancelled.");

		return message.toString();
	}

	public String Zone_Create_Notify()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(info);
		message.append("Right-click a block to begin creating a sub-zone.");

		return message.toString();
	}

	public String Zone_Create3d_Notify()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(info);
		message.append("Right-click a block to begin creating a 3D sub-zone.");

		return message.toString();
	}

	public String Zone_Info_NotDefined()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(info);
		message.append("No zone has been defined here.");

		return message.toString();
	}

	public String Zone_Info_Notify()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(info);
		message.append("Right-click a block to view zone information.");

		return message.toString();
	}

	public String Invalid_Amount(String amount)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(error);
		message.append("Invalid amount specified: ");
		message.append(highlight);
		message.append(amount);

		return message.toString();
	}



	public String Sale_Set(int amount)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("Zone is now for sale for: ")
			.append(highlight)
			.append(context.currencySingular())
			.append(amount)
			.toString();
	}

	public String Sale_Unset()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(success);
		message.append("Zone is no longer for sale.");

		return message.toString();
	}

	public String Sale_Success(int price)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(success);
		message.append("You have successfully purchased the zone for ");
		message.append(highlight);
		message.append(context.currencySingular()).append(price);
		message.append(success);
		message.append(".");

		return message.toString();
	}

	public String Sale_Success_Owner(String buyer, int price)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("Player ")
			.append(highlight)
			.append(buyer)
            .append(success)
			.append(" has purchased a zone for ")
			.append(highlight)
			.append(context.currencySingular())
			.append(price)
			.append(success)
			.append(".")
			.toString();
	}



	public String Rent_Set(int amount, int duration)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("Zone is now for rent: ")
			.append(highlight)
			.append(context.currencySingular())
			.append(amount)
			.append(success)
			.append(" for ")
			.append(highlight)
			.append(duration)
			.append(success)
			.append(" days.")
			.toString();
	}

	public String Rent_Unset()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(success);
		message.append("Zone is no longer for rent.");

		return message.toString();
	}

	public String Rent_Success(int price)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("You have successfully rented the zone for ")
			.append(highlight)
			.append(context.currencySingular())
			.append(price)
			.append(success)
			.append(".")
			.toString();
	}

	public String Rent_Success_Owner(String buyer, int price, int duration)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("Player ")
			.append(highlight)
			.append(buyer)
			.append(" has rented a zone for ")
			.append(highlight)
			.append(context.currencySingular())
			.append(price)
			.append(success)
			.append(" for ")
			.append(highlight)
			.append(duration)
			.append(success)
			.append(" days.")
			.toString();
	}



	public String Already_Own()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(info);
		message.append("You already own this zone.");

		return message.toString();
	}

	public String Not_For_Sale()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(info);
		message.append("This zone is not for sale.");

		return message.toString();
	}

	public String Not_For_Rent()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(info);
		message.append("This zone is not for rent.");

		return message.toString();
	}

	public String Invalid_Duration(String duration)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(error);
		message.append("Invalid duration specified: ");
		message.append(highlight);
		message.append(duration);

		return message.toString();
	}

	public String Resident_Not_Found(String playerName)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(info);
		message.append("Unable to kick ");
		message.append(highlight);
		message.append(playerName);
		message.append(info);
		message.append(" because player is not a resident.");

		return message.toString();
	}

	public String Resident_Kicked(String playerName)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(success);
		message.append("All permissions and purchased zones for ");
		message.append(highlight);
		message.append(playerName);
		message.append(success);
		message.append(" have been revoked.");

		return message.toString();
	}

	public String Resident_Kicked_Player(String zoneName, String owner)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(info);
		message.append("You have been kicked from zone ");
		message.append(highlight);
		message.append(zoneName);
		message.append(info);
		message.append(", owned by ");
		message.append(highlight);
		message.append(owner);
		message.append(info);
		message.append(".");

		return message.toString();
	}



	public String Create_3DZone_InPrimary_Only()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(error);
		message.append("You can only create 3D sub-zones in a primary zone.");

		return message.toString();
	}

	public String Demolish_Quote(int locationY, int blockCount, int price)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(info);
		message.append("Demolish at Height: ");
		message.append(highlight);
		message.append(locationY);
		message.append(info);
		message.append(" and above, a total of ");
		message.append(highlight);
		message.append(blockCount);
		message.append(info);
		message.append(" blocks, costing a total of: ");
		message.append(highlight);
		message.append(price);

		return message.toString();
	}

	public String Demolish_Started(int locationY, SafeCityZone zone, SafeCitySubZone subZone)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(success);
		message.append("Demolishing ");
		message.append(highlight);

		if (subZone != null)
		{
			message.append(subZone.getName());
		}
		else
		{
			message.append(zone.getName());
		}

		message.append(success);
		message.append(" to height: ");
		message.append(highlight);
		message.append(locationY);
		message.append(success);
		message.append(".");

		return message.toString();
	}

	public String Demolish_Completed(int blockCount, int price)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(success);
		message.append("Demolished ");
		message.append(highlight);
		message.append(blockCount);
		message.append(success);
		message.append(" blocks, costing a total of: ");
		message.append(highlight);
		message.append(context.currencySingular()).append(price);

		return message.toString();
	}

	public String Filler_Quote(int locationY, int blockCount, int price, Material material)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(info);
		message.append("Fill with ");
		message.append(highlight);
		message.append(material.toString());
		message.append(info);
		message.append(" to height: ");
		message.append(highlight);
		message.append(locationY);
		message.append(info);
		message.append(" and below, a total of ");
		message.append(highlight);
		message.append(blockCount);
		message.append(info);
		message.append(" blocks, costing a total of: ");
		message.append(highlight);
		message.append(price);

		return message.toString();
	}

	public String Filler_Started(int locationY, SafeCityZone zone, SafeCitySubZone subZone)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(success);
		message.append("Filling ");
		message.append(highlight);

		if (subZone != null)
		{
			message.append(subZone.getName());
		}
		else
		{
			message.append(zone.getName());
		}

		message.append(success);
		message.append(" to height: ");
		message.append(highlight);
		message.append(locationY);
		message.append(success);
		message.append(".");

		return message.toString();
	}

	public String Filler_Completed(int blockCount, int price)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(success);
		message.append("Filled ");
		message.append(highlight);
		message.append(blockCount);
		message.append(success);
		message.append(" blocks, costing a total of: ");
		message.append(highlight);
		message.append(context.currencySingular()).append(price);

		return message.toString();
	}

	public String Displaying_Visual()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(success);
		message.append("Displaying local zone boundaries...");

		return message.toString();
	}

	public String Zone_Too_Small()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(error);
		message.append("Zone must be at least 5x5 in size.");

		return message.toString();
	}

	public String SubZone_Too_Small()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(error);
		message.append("Sub-zone must be at least 5x5 in size.");

		return message.toString();
	}

	public String SubZone3D_Too_Small()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(error);
		message.append("Sub-zone must be at least 5x5 in size and 3 blocks high.");

		return message.toString();
	}



	public String Filler_Insufficient_Funds()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(error);
		message.append("Filling stopped. Insufficient funds.");

		return message.toString();
	}

	public String Filler_Busy()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(error);
		message.append("You can only fill one area at a time.");

		return message.toString();
	}

	public String Filler_Stopped()
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("Filler stopped.")
			.toString();
	}

	public String Filler_NotBusy()
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("Unable to stop filler because it is not busy.")
			.toString();
	}



	public String Demolished_Insufficient_Funds()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(error);
		message.append("Demolition stopped. Insufficient funds.");

		return message.toString();
	}

	public String Demolisher_Busy()
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(error)
			.append("You can only demolish one area at a time.")
			.toString();
	}

	public String Demolisher_Stopped()
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("Demolisher stopped.")
			.toString();
	}

	public String Demolisher_NotBusy()
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("Unable to stop demolisher because it is not busy.")
			.toString();
	}



	public String Unknown_Material(String material)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(info);
		message.append("Unknown material: ");
		message.append(highlight);
		message.append(material);
		message.append(info);
		message.append(".");

		return message.toString();
	}

	public String Invalid_Arguments()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(error);
		message.append("Invalid arguments specified.");

		return message.toString();
	}

	public String Invalid_Page()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(error);
		message.append("Invalid page number specified.");

		return message.toString();
	}

	public String Item_Purchased(double amount)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(highlight);
		message.append(context.currencySingular()).append(amount);
		message.append(info);
		message.append(" has been taken from your balance.");

		return message.toString();
	}

	public String Error_Zone_Permission_MisMatch(String existingStatus, String potentialStatus)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(error);
		message.append("This zone is marked ");
		message.append(highlight);
		message.append(existingStatus);
		message.append(error);
		message.append(", and therefore cannot be ");
		message.append(highlight);
		message.append(potentialStatus);
		message.append(error);
		message.append(".");

		return message.toString();
	}

	public String Player_BlocksLeft(int amount)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(info);
		message.append("You have ");
		message.append(highlight);
		message.append(amount);
		message.append(info);
		message.append(" blocks remaining.");

		return message.toString();
	}

	public String EntryMessage_Set()
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(success);
		message.append("Entry message has been set.");

		return message.toString();
	}

	public String Zone3D_Only()
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(error)
			.append("Only 3D sub-zones can be whitelisted.")
			.toString();
	}

	public String Whitelist_Added(String playername, String zoneName)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("Player ")
			.append(highlight)
			.append(playername)
			.append(success)
			.append(" has been whitelisted in zone ")
			.append(highlight)
			.append(zoneName)
			.append(success)
			.append(".")
			.toString();
	}

	public String Whitelist_Removed(String playername, String zoneName)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("Player ")
			.append(highlight)
			.append(playername)
			.append(success)
			.append(" has been removed from the whitelist in zone ")
			.append(highlight)
			.append(zoneName)
			.append(success)
			.append(".")
			.toString();
	}

	public String Zone3dTool_Only()
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(error)
			.append("You can only resize 3D zones with the ")
			.append(highlight)
			.append(SafeCityTool.ZoneTool3d.friendlyName())
			.append(error)
			.append(".")
			.toString();
	}



	public String Zone_Spawn_Set()
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(success)
			.append("Spawn location has been set.")
			.toString();
	}

	public String Invalid_ZoneId(String id)
	{
		StringBuilder message = new StringBuilder();
		message.append(PluginPrefix);

		message.append(error);
		message.append("Invalid Zone ID specified: ");
		message.append(highlight);
		message.append(id);

		return message.toString();
	}



	public String Spawn_Not_Public_Town(String zoneName)
	{
		return new StringBuilder()
		.append(PluginPrefix)
		.append(error)
		.append("You cannot teleport to ")
		.append(highlight)
		.append(zoneName)
		.append(error)
		.append(" because it does not have public teleporting enabled.")
		.toString();
	}

	public String Spawn_Already_Public(String zoneName)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(info)
			.append("Telporting for ")
			.append(highlight)
			.append(zoneName)
			.append(info)
			.append(" is already ")
			.append(highlight)
			.append("public")
			.append(info)
			.append(".")
			.toString();
	}

	public String Spawn_Already_Private(String zoneName)
	{
		return new StringBuilder()
		.append(PluginPrefix)
		.append(info)
		.append("Telporting for ")
		.append(highlight)
		.append(zoneName)
		.append(info)
		.append(" is already ")
		.append(highlight)
		.append("private")
		.append(info)
		.append(".")
		.toString();
	}

	public String Spawn_Now_Public(String zoneName)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(info)
			.append("Telporting for ")
			.append(highlight)
			.append(zoneName)
			.append(info)
			.append(" is now ")
			.append(highlight)
			.append("public")
			.append(info)
			.append(".")
			.toString();
	}

	public String Spawn_Now_Private(String zoneName)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(info)
			.append("Telporting for ")
			.append(highlight)
			.append(zoneName)
			.append(info)
			.append(" is now ")
			.append(highlight)
			.append("private")
			.append(info)
			.append(".")
			.toString();
	}

	public String Spawn_Town_Not_Public(String zoneName)
	{
		return new StringBuilder()
		.append(PluginPrefix)
		.append(info)
		.append("The zone ")
		.append(highlight)
		.append(zoneName)
		.append(info)
		.append(" must be public to enable public teleporting.")
		.toString();
	}



	public String Block_Purchase_Success(int blockAmount, double price)
	{
		return new StringBuilder()
		.append(PluginPrefix)
		.append(info)
		.append("Successfully purchased ")
		.append(highlight)
		.append(blockAmount)
		.append(info)
		.append(" for ")
		.append(highlight)
		.append(context.currencySingular())
		.append(price)
		.append(info)
		.append(".")
		.toString();
	}

	public String Block_Purchase_Quote(int blockAmount, double price)
	{
		return new StringBuilder()
		.append(PluginPrefix)
		.append(info)
		.append("Quote: Purchase ")
		.append(highlight)
		.append(blockAmount)
		.append(info)
		.append(" for ")
		.append(highlight)
		.append(context.currencySingular())
		.append(price)
		.append(info)
		.append(".")
		.toString();
	}


	public String Permission_PrimaryZone_Only(ZonePermissionType permission)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(error)
			.append("Permission ")
			.append(highlight)
			.append(permission.name().toLowerCase(Locale.getDefault()))
			.append(error)
			.append(" can only be applied to a primary zone.")
			.toString();
	}

	public String Permission_RequiresOwner(ZonePermissionType permission)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(error)
			.append("Permission ")
			.append(highlight)
			.append(permission.name().toLowerCase(Locale.getDefault()))
			.append(error)
			.append(" can only be given by owners.")
			.toString();
	}



    public String Pvp_Enabled(String zoneName)
	{
		return new StringBuilder()
		.append(PluginPrefix)
		.append(error)
		.append("PvP is now enabled for zone ")
		.append(highlight)
		.append(zoneName)
		.append(error)
		.append(".")
		.toString();
	}

    public String Pvp_Disabled(String zoneName)
	{
		return new StringBuilder()
		.append(PluginPrefix)
		.append(error)
		.append("PvP is now disabled for zone ")
		.append(highlight)
		.append(zoneName)
		.append(error)
		.append(".")
		.toString();
	}

	public String Pvp_Already_Enabled(String zoneName)
	{
		return new StringBuilder()
		.append(PluginPrefix)
		.append(error)
		.append("PvP is already enabled for zone ")
		.append(highlight)
		.append(zoneName)
		.append(error)
		.append(".")
		.toString();
	}

	public String Pvp_Already_Disabled(String zoneName)
	{
		return new StringBuilder()
		.append(PluginPrefix)
		.append(error)
		.append("PvP is already disabled for zone ")
		.append(highlight)
		.append(zoneName)
		.append(error)
		.append(".")
		.toString();
	}



    public String Mob_Enabled(String zoneName)
	{
		return new StringBuilder()
		.append(PluginPrefix)
		.append(error)
		.append("Mob spawning is now enabled for zone ")
		.append(highlight)
		.append(zoneName)
		.append(error)
		.append(".")
		.toString();
	}

    public String Mob_Disabled(String zoneName)
	{
		return new StringBuilder()
		.append(PluginPrefix)
		.append(error)
		.append("Mob spawning is now disabled for zone ")
		.append(highlight)
		.append(zoneName)
		.append(error)
		.append(".")
		.toString();
	}

    public String Mob_Already_Enabled(String zoneName)
	{
		return new StringBuilder()
		.append(PluginPrefix)
		.append(error)
		.append("Mob Spawning is already enabled for zone ")
		.append(highlight)
		.append(zoneName)
		.append(error)
		.append(".")
		.toString();
	}

	public String Mob_Already_Disabled(String zoneName)
	{
		return new StringBuilder()
		.append(PluginPrefix)
		.append(error)
		.append("Mob Spawning is already disabled for zone ")
		.append(highlight)
		.append(zoneName)
		.append(error)
		.append(".")
		.toString();
	}



	public String Permission_Cannot_SetPublic(ZonePermissionType permission)
	{
		return new StringBuilder()
			.append(PluginPrefix)
			.append(error)
			.append("Permission ")
			.append(highlight)
			.append(permission.name().toLowerCase(Locale.getDefault()))
			.append(error)
			.append(" cannot be set as public.")
			.toString();
	}

    public String Portal_Resident_Only(String zoneName)
    {
        return new StringBuilder()
                .append(error)
                .append("You must be a resident of ")
                .append(highlight)
                .append(zoneName)
                .append(error)
                .append(" to use this portal")
                .toString();
    }

    public String PortalCreate_Owners_Only()
    {
        return new StringBuilder()
                .append(PluginPrefix)
                .append(error)
                .append("Only zone owners can create portals.")
                .toString();
    }

    public String vineGrowth(String zoneName, boolean value)
    {
        return new StringBuilder()
                .append(PluginPrefix)
                .append(success)
                .append("Vine growth for zone ")
                .append(highlight)
                .append(zoneName)
                .append(success)
                .append(" is ")
                .append(highlight)
                .append(value ? "on" : "off")
                .toString();
    }

}
