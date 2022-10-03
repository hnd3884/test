package com.sun.corba.se.spi.orb;

import java.util.Properties;

public interface ParserData
{
    String getPropertyName();
    
    Operation getOperation();
    
    String getFieldName();
    
    Object getDefaultValue();
    
    Object getTestValue();
    
    void addToParser(final PropertyParser p0);
    
    void addToProperties(final Properties p0);
}
