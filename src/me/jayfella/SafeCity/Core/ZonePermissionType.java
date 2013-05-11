package me.jayfella.SafeCity.Core;

import me.jayfella.SafeCity.Core.Permissions.AccessPermission;
import me.jayfella.SafeCity.Core.Permissions.BuildPermission;
import me.jayfella.SafeCity.Core.Permissions.BuyerPermission;
import me.jayfella.SafeCity.Core.Permissions.FarmerPermission;
import me.jayfella.SafeCity.Core.Permissions.LandlordPermission;
import me.jayfella.SafeCity.Core.Permissions.OwnerPermission;
import me.jayfella.SafeCity.Core.Permissions.RecruiterPermission;
import me.jayfella.SafeCity.Core.Permissions.RenterPermission;
import me.jayfella.SafeCity.Core.Permissions.ZonerPermission;

public enum ZonePermissionType
{
    Access(new AccessPermission()),
    Buyer(new BuyerPermission()),
    Build(new BuildPermission()),
    Farmer(new FarmerPermission()),
    Landlord(new LandlordPermission()),
    Owner(new OwnerPermission()),
    Recruiter(new RecruiterPermission()),
    Renter(new RenterPermission()),
    Zoner(new ZonerPermission());

    private ZonePermissionInterface type;

    ZonePermissionType(ZonePermissionInterface type)
    {
        this.type = type;
    }

    public ZonePermissionInterface getPermissions()
    {
        return this.type;
    }



}
