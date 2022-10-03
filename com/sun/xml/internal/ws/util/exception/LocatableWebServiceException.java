package com.sun.xml.internal.ws.util.exception;

import javax.xml.stream.Location;
import org.xml.sax.helpers.LocatorImpl;
import com.sun.xml.internal.ws.resources.UtilMessages;
import com.sun.istack.internal.NotNull;
import java.util.Arrays;
import java.util.List;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;
import javax.xml.ws.WebServiceException;

public class LocatableWebServiceException extends WebServiceException
{
    private final Locator[] location;
    
    public LocatableWebServiceException(final String message, final Locator... location) {
        this(message, (Throwable)null, location);
    }
    
    public LocatableWebServiceException(final String message, final Throwable cause, final Locator... location) {
        super(appendLocationInfo(message, location), cause);
        this.location = location;
    }
    
    public LocatableWebServiceException(final Throwable cause, final Locator... location) {
        this(cause.toString(), cause, location);
    }
    
    public LocatableWebServiceException(final String message, final XMLStreamReader locationSource) {
        this(message, new Locator[] { toLocation(locationSource) });
    }
    
    public LocatableWebServiceException(final String message, final Throwable cause, final XMLStreamReader locationSource) {
        this(message, cause, new Locator[] { toLocation(locationSource) });
    }
    
    public LocatableWebServiceException(final Throwable cause, final XMLStreamReader locationSource) {
        this(cause, new Locator[] { toLocation(locationSource) });
    }
    
    @NotNull
    public List<Locator> getLocation() {
        return Arrays.asList(this.location);
    }
    
    private static String appendLocationInfo(final String message, final Locator[] location) {
        final StringBuilder buf = new StringBuilder(message);
        for (final Locator loc : location) {
            buf.append('\n').append(UtilMessages.UTIL_LOCATION(loc.getLineNumber(), loc.getSystemId()));
        }
        return buf.toString();
    }
    
    private static Locator toLocation(final XMLStreamReader xsr) {
        final LocatorImpl loc = new LocatorImpl();
        final Location in = xsr.getLocation();
        loc.setSystemId(in.getSystemId());
        loc.setPublicId(in.getPublicId());
        loc.setLineNumber(in.getLineNumber());
        loc.setColumnNumber(in.getColumnNumber());
        return loc;
    }
}
