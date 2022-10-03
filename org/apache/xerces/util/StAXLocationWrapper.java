package org.apache.xerces.util;

import javax.xml.stream.Location;
import org.apache.xerces.xni.XMLLocator;

public final class StAXLocationWrapper implements XMLLocator
{
    private Location fLocation;
    
    public StAXLocationWrapper() {
        this.fLocation = null;
    }
    
    public void setLocation(final Location fLocation) {
        this.fLocation = fLocation;
    }
    
    public Location getLocation() {
        return this.fLocation;
    }
    
    public String getPublicId() {
        if (this.fLocation != null) {
            return this.fLocation.getPublicId();
        }
        return null;
    }
    
    public String getLiteralSystemId() {
        if (this.fLocation != null) {
            return this.fLocation.getSystemId();
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
        if (this.fLocation != null) {
            return this.fLocation.getLineNumber();
        }
        return -1;
    }
    
    public int getColumnNumber() {
        if (this.fLocation != null) {
            return this.fLocation.getColumnNumber();
        }
        return -1;
    }
    
    public int getCharacterOffset() {
        if (this.fLocation != null) {
            return this.fLocation.getCharacterOffset();
        }
        return -1;
    }
    
    public String getEncoding() {
        return null;
    }
    
    public String getXMLVersion() {
        return null;
    }
}
