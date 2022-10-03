package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.FeatureState;

public interface XMLComponentManager
{
    boolean getFeature(final String p0) throws XMLConfigurationException;
    
    boolean getFeature(final String p0, final boolean p1);
    
    Object getProperty(final String p0) throws XMLConfigurationException;
    
    Object getProperty(final String p0, final Object p1);
    
    FeatureState getFeatureState(final String p0);
    
    PropertyState getPropertyState(final String p0);
}
