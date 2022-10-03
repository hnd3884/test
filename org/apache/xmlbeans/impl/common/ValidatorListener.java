package org.apache.xmlbeans.impl.common;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import org.apache.xmlbeans.XmlCursor;

public interface ValidatorListener
{
    public static final int BEGIN = 1;
    public static final int END = 2;
    public static final int TEXT = 3;
    public static final int ATTR = 4;
    public static final int ENDATTRS = 5;
    
    void nextEvent(final int p0, final Event p1);
    
    public interface Event extends PrefixResolver
    {
        public static final int PRESERVE = 1;
        public static final int REPLACE = 2;
        public static final int COLLAPSE = 3;
        
        XmlCursor getLocationAsCursor();
        
        Location getLocation();
        
        String getXsiType();
        
        String getXsiNil();
        
        String getXsiLoc();
        
        String getXsiNoLoc();
        
        QName getName();
        
        String getText();
        
        String getText(final int p0);
        
        boolean textIsWhitespace();
    }
}
