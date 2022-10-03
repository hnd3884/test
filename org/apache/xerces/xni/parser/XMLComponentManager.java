package org.apache.xerces.xni.parser;

public interface XMLComponentManager
{
    boolean getFeature(final String p0) throws XMLConfigurationException;
    
    Object getProperty(final String p0) throws XMLConfigurationException;
}
