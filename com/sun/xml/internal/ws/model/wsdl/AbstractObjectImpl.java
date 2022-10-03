package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.Locator;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLObject;

abstract class AbstractObjectImpl implements WSDLObject
{
    private final int lineNumber;
    private final String systemId;
    
    AbstractObjectImpl(final XMLStreamReader xsr) {
        final Location loc = xsr.getLocation();
        this.lineNumber = loc.getLineNumber();
        this.systemId = loc.getSystemId();
    }
    
    AbstractObjectImpl(final String systemId, final int lineNumber) {
        this.systemId = systemId;
        this.lineNumber = lineNumber;
    }
    
    @NotNull
    @Override
    public final Locator getLocation() {
        final LocatorImpl loc = new LocatorImpl();
        loc.setSystemId(this.systemId);
        loc.setLineNumber(this.lineNumber);
        return loc;
    }
}
