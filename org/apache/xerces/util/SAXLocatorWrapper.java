package org.apache.xerces.util;

import org.xml.sax.ext.Locator2;
import org.xml.sax.Locator;
import org.apache.xerces.xni.XMLLocator;

public final class SAXLocatorWrapper implements XMLLocator
{
    private Locator fLocator;
    private Locator2 fLocator2;
    
    public SAXLocatorWrapper() {
        this.fLocator = null;
        this.fLocator2 = null;
    }
    
    public void setLocator(final Locator fLocator) {
        this.fLocator = fLocator;
        if (fLocator instanceof Locator2 || fLocator == null) {
            this.fLocator2 = (Locator2)fLocator;
        }
    }
    
    public Locator getLocator() {
        return this.fLocator;
    }
    
    public String getPublicId() {
        if (this.fLocator != null) {
            return this.fLocator.getPublicId();
        }
        return null;
    }
    
    public String getLiteralSystemId() {
        if (this.fLocator != null) {
            return this.fLocator.getSystemId();
        }
        return null;
    }
    
    public String getBaseSystemId() {
        return null;
    }
    
    public String getExpandedSystemId() {
        return this.getLiteralSystemId();
    }
    
    public int getLineNumber() {
        if (this.fLocator != null) {
            return this.fLocator.getLineNumber();
        }
        return -1;
    }
    
    public int getColumnNumber() {
        if (this.fLocator != null) {
            return this.fLocator.getColumnNumber();
        }
        return -1;
    }
    
    public int getCharacterOffset() {
        return -1;
    }
    
    public String getEncoding() {
        if (this.fLocator2 != null) {
            return this.fLocator2.getEncoding();
        }
        return null;
    }
    
    public String getXMLVersion() {
        if (this.fLocator2 != null) {
            return this.fLocator2.getXMLVersion();
        }
        return null;
    }
}
