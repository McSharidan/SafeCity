package me.jayfella.SafeCity.Core;

import java.util.HashSet;

public final class StringHashSet extends HashSet<String>
{
    public boolean containsIgnoreCase(String string)
    {
        for (String s : this)
        {
            if (s.equalsIgnoreCase(string))
            {
                return true;
            }
        }
        
        return false;
    }
}
