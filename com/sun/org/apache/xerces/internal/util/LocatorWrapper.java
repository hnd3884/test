package com.sun.org.apache.xerces.internal.util;

import org.xml.sax.Locator;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;

public class LocatorWrapper implements XMLLocator
{
    private final Locator locator;
    
    public LocatorWrapper(final Locator _loc) {
        this.locator = _loc;
    }
    
    @Override
    public int getColumnNumber() {
        return this.locator.getColumnNumber();
    }
    
    @Override
    public int getLineNumber() {
        return this.locator.getLineNumber();
    }
    
    @Override
    public String getBaseSystemId() {
        return null;
    }
    
    @Override
    public String getExpandedSystemId() {
        return this.locator.getSystemId();
    }
    
    @Override
    public String getLiteralSystemId() {
        return this.locator.getSystemId();
    }
    
    @Override
    public String getPublicId() {
        return this.locator.getPublicId();
    }
    
    @Override
    public String getEncoding() {
        return null;
    }
    
    @Override
    public int getCharacterOffset() {
        return -1;
    }
    
    @Override
    public String getXMLVersion() {
        return null;
    }
}
