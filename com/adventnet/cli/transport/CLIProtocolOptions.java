package com.adventnet.cli.transport;

import java.util.Properties;

public interface CLIProtocolOptions
{
    Object getID();
    
    Object clone();
    
    String getInitialMessage();
    
    void setInitialMessage(final String p0);
    
    void setProperties(final Properties p0);
    
    Properties getProperties();
}
