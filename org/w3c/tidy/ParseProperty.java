package org.w3c.tidy;

public interface ParseProperty
{
    Object parse(final String p0, final String p1, final Configuration p2);
    
    String getType();
    
    String getOptionValues();
    
    String getFriendlyName(final String p0, final Object p1, final Configuration p2);
}
