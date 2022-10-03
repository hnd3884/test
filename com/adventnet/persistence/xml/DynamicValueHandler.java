package com.adventnet.persistence.xml;

import java.util.Properties;

public interface DynamicValueHandler extends StateHolder
{
    Object getColumnValue(final String p0, final String p1, final Properties p2, final String p3) throws DynamicValueHandlingException;
    
    String getAttributeValue(final String p0, final String p1, final Properties p2, final Object p3) throws DynamicValueHandlingException;
}
