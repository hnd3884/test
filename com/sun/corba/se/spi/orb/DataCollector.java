package com.sun.corba.se.spi.orb;

import java.util.Properties;

public interface DataCollector
{
    boolean isApplet();
    
    boolean initialHostIsLocal();
    
    void setParser(final PropertyParser p0);
    
    Properties getProperties();
}
