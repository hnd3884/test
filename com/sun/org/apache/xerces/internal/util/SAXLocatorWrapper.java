package com.sun.org.apache.xerces.internal.util;

import org.xml.sax.ext.Locator2;
import org.xml.sax.Locator;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;

public final class SAXLocatorWrapper implements XMLLocator
{
    private Locator fLocator;
    private Locator2 fLocator2;
    
    public SAXLocatorWrapper() {
        this.fLocator = null;
        this.fLocator2 = null;
    }
    
    public void setLocator(final Locator locator) {
        this.fLocator = locator;
        if (locator instanceof Locator2 || locator == null) {
            this.fLocator2 = (Locator2)locator;
        }
    }
    
    public Locator getLocator() {
        return this.fLocator;
    }
    
    @Override
    public String getPublicId() {
        if (this.fLocator != null) {
            return this.fLocator.getPublicId();
        }
        return null;
    }
    
    @Override
    public String getLiteralSystemId() {
        if (this.fLocator != null) {
            return this.fLocator.getSystemId();
        }
        return null;
    }
    
    @Override
    public String getBaseSystemId() {
        return null;
    }
    
    @Override
    public String getExpandedSystemId() {
        return this.getLiteralSystemId();
    }
    
    @Override
    public int getLineNumber() {
        if (this.fLocator != null) {
            return this.fLocator.getLineNumber();
        }
        return -1;
    }
    
    @Override
    public int getColumnNumber() {
        if (this.fLocator != null) {
            return this.fLocator.getColumnNumber();
        }
        return -1;
    }
    
    @Override
    public int getCharacterOffset() {
        return -1;
    }
    
    @Override
    public String getEncoding() {
        if (this.fLocator2 != null) {
            return this.fLocator2.getEncoding();
        }
        return null;
    }
    
    @Override
    public String getXMLVersion() {
        if (this.fLocator2 != null) {
            return this.fLocator2.getXMLVersion();
        }
        return null;
    }
}
