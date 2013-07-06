package me.jayfella.SafeCity.Core;

public enum PluginPermission
{
    Create_City("safecity.public.create"),
    Delete_City("safecity.public.delete"),
    Staff_Override("safecity.staff");

    private String permissionNode;

    private PluginPermission(String permissionNode)
    {
        this.permissionNode = permissionNode;
    }

    public String permissionNode() { return this.permissionNode; }

    @Override
    public String toString() { return this.permissionNode; }

}
