package org.jfree.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.io.Serializable;

public interface Configuration extends Serializable
{
    Iterator findPropertyKeys(final String p0);
    
    Enumeration getConfigProperties();
    
    String getConfigProperty(final String p0);
    
    String getConfigProperty(final String p0, final String p1);
}
