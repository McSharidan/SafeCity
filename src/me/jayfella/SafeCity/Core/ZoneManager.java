package me.jayfella.SafeCity.Core;

import me.jayfella.SafeCity.SafeCityPlayer;
import me.jayfella.SafeCity.SafeCitySubZone;
import me.jayfella.SafeCity.SafeCityZone;

public final class ZoneManager
{
    private final SafeCityPlayer player;

    private SafeCityZone currentZone;
    private SafeCitySubZone currentSubZone;
    private SafeCitySubZone[] currentZoneChildren = new SafeCitySubZone[0];

    private boolean isCreatingZone = false;
    private boolean isCreatingSubZone = false;
    private SafeCityZone subZoneCreationParent;

    private boolean isResizing = false;
    private SafeCityZone resizeZone = null;
    private SafeCitySubZone resizeSubZone = null;
    private ThinLocation resizeCornerChanged = null;

    private ThinLocation newZoneLoc1 = null;
    private ThinLocation newZoneLoc2 = null;

    private int liquidSpreadZoneId = 0;

    public ZoneManager(SafeCityPlayer player) { this.player = player; }
    public SafeCityZone getCurrentZone() { return currentZone; }

    public void setCurrentZone(SafeCityZone zone)
    {
        currentZone = zone;

        if (zone != null)
        {
            setCurrentZoneChildren(zone.getChildren());
        }
    }

    public SafeCitySubZone getCurrentSubZone() { return currentSubZone; }
    public void setCurrentSubZone(SafeCitySubZone subZone) { currentSubZone = subZone; }
    public SafeCitySubZone[] getCurrentZoneChildren() { return currentZoneChildren; }
    private void setCurrentZoneChildren(SafeCitySubZone[] children) { currentZoneChildren = children; }

    public SafeCitySubZone getCurrentZoneChild(ThinLocation location)
    {
        for (int i = 0; i < currentZoneChildren.length; i++)
        {
            SafeCitySubZone subZone = currentZoneChildren[i];

            if (subZone.isInsideZone(location, currentZone.getWorld(), true))
            {
                return subZone;
            }
        }

        return null;
    }

    public boolean isCreatingZone() { return isCreatingZone; }
    public void setIsCreatingZone(boolean value) { isCreatingZone = value; }
    public boolean isCreatingSubZone() { return isCreatingSubZone; }
    public void setIsCreatingSubZone(boolean value) { isCreatingSubZone = value; }
    public ThinLocation getNewZoneLocation1() { return newZoneLoc1; }
    public void setNewZoneLocation1(ThinLocation location) { newZoneLoc1 = location; }
    public ThinLocation getNewZoneLocation2() {	return newZoneLoc2; }
    public void setNewZoneLocation2(ThinLocation location) { newZoneLoc2 = location; }
    public boolean isResizing()	{ return isResizing; }
    public void setIsResizing(boolean value) { isResizing = value; }
    public SafeCityZone getResizeZone()	{ return resizeZone; }
    public void setResizeZone(SafeCityZone zone) { resizeZone = zone; }
    public SafeCitySubZone getResizeSubZone() {	return resizeSubZone; }
    public void setResizeSubZone(SafeCitySubZone subZone) { resizeSubZone = subZone; }
    public ThinLocation getResizeCornerChanged() { return resizeCornerChanged; }
    public void setResizeCornerChanged(ThinLocation location) {	resizeCornerChanged = location;	}
    public SafeCityZone getSubZoneCreationParent() { return this.subZoneCreationParent; }
    public void setSubZoneCreationParent(SafeCityZone parent) { this.subZoneCreationParent = parent; }
    public int getLiquidSpreadZoneId() { return liquidSpreadZoneId; }
    public void setLiquidSpreadZoneId(int id) { liquidSpreadZoneId = id; }

    public void clearCreationData()
    {
        setIsCreatingZone(false);
        setIsCreatingSubZone(false);

        setNewZoneLocation1(null);
        setNewZoneLocation2(null);
    }

    public void clearResizeData()
    {
        setIsResizing(false);
        setResizeZone(null);
        setResizeSubZone(null);
        setResizeCornerChanged(null);
    }

}

