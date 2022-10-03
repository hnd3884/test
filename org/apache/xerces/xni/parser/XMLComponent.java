package org.apache.xerces.xni.parser;

public interface XMLComponent
{
    void reset(final XMLComponentManager p0) throws XMLConfigurationException;
    
    String[] getRecognizedFeatures();
    
    void setFeature(final String p0, final boolean p1) throws XMLConfigurationException;
    
    String[] getRecognizedProperties();
    
    void setProperty(final String p0, final Object p1) throws XMLConfigurationException;
    
    Boolean getFeatureDefault(final String p0);
    
    Object getPropertyDefault(final String p0);
}
