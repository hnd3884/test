package com.adventnet.persistence.migration;

import com.adventnet.persistence.DataObject;

public interface DOXMLChangeListener
{
    public static final int OVER_WRITE = 1;
    public static final int IGNORE = 2;
    
    boolean preInvoke(final String p0, final XmlChangeNotifyObject p1) throws Exception;
    
    void postInvoke(final String p0, final DataObject p1) throws Exception;
    
    int getPreference(final String p0);
    
    boolean skipXML(final String p0) throws Exception;
}
