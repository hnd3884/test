package org.apache.poi.xdgf.exceptions;

import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLException;

public class XDGFException
{
    public static POIXMLException error(final String message, final Object o) {
        return new POIXMLException(o + ": " + message);
    }
    
    public static POIXMLException error(final String message, final Object o, final Throwable t) {
        return new POIXMLException(o + ": " + message, t);
    }
    
    public static POIXMLException wrap(final POIXMLDocumentPart part, final POIXMLException e) {
        return new POIXMLException(part.getPackagePart().getPartName() + ": " + e.getMessage(), (e.getCause() == null) ? e : e.getCause());
    }
    
    public static POIXMLException wrap(final String where, final POIXMLException e) {
        return new POIXMLException(where + ": " + e.getMessage(), (e.getCause() == null) ? e : e.getCause());
    }
}
