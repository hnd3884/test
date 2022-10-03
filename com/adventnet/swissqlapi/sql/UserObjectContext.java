package com.adventnet.swissqlapi.sql;

public interface UserObjectContext
{
    Object getEquivalent(final Object p0);
    
    Object getMappedDatatype(final String p0, final String p1, final String p2);
}
